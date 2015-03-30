package org.wso2.carbon.appmgt.sampledeployer.appcontroller;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.wso2.carbon.appmgt.sampledeployer.appm.LoginAdminServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.WSRegistryService_Client;
import org.wso2.carbon.appmgt.sampledeployer.bean.AppCreateRequest;
import org.wso2.carbon.appmgt.sampledeployer.bean.MobileApplicationBean;
import org.wso2.carbon.appmgt.sampledeployer.http.HttpHandler;
import org.wso2.carbon.appmgt.sampledeployer.javascriptwrite.InvokeStatistcsJavascriptBuilder;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ushan on 3/27/15.
 */
public class ApplicationController {

     final static Logger log = Logger.getLogger(ApplicationController.class.getName());
     private String publisherSession;
     private String storeSession;
     private HttpHandler httpHandler;
     private String httpsBackEndUrl;
     private String httpBackEndUrl;
     private int httpPort = 9763;
     private int httpsPort = 9443;
     private WSRegistryService_Client wsRegistryService_client;
     private String webAppPath ="../../";
     private ConcurrentHashMap<String,String> trackingCodes;
     private InvokeStatistcsJavascriptBuilder invokeStatistcsJavascriptBuilder;
     private LoginAdminServiceClient loginAdminServiceClient;

     public  ApplicationController(String username,String password,int offset,String ipAddress) throws IOException, RegistryException, LoginAuthenticationExceptionException {
          httpPort+=offset;
          httpsPort+=offset;
          httpsBackEndUrl = "https://localhost:"+httpsPort;
          httpBackEndUrl = "https://localhost:"+httpPort;
          httpHandler = new HttpHandler();
          trackingCodes = new ConcurrentHashMap<String,String>();
          loginAdminServiceClient = new LoginAdminServiceClient(httpsBackEndUrl);
          String serviceSession =loginAdminServiceClient.authenticate(username, password);
          wsRegistryService_client = new WSRegistryService_Client(serviceSession,httpsBackEndUrl);
          publisherSession = httpHandler.doPostHttps(httpsBackEndUrl + "/publisher/api/authenticate",
                  "username=" + username + "&password=" + password + "&action=login", ""
                  , "application/x-www-form-urlencoded");
          storeSession = httpHandler.doPostHttp(httpBackEndUrl + "/store/apis/user/login",
                  "{\"username\":\"admin\"" +
                          ",\"password\":\"admin\"}", "header", "application/json");
     }

     public void createWebApplication(String serverPort) throws IOException {
          String policyIDResponce = httpHandler.doPostHttps(httpsBackEndUrl + "/publisher/api/entitlement/policy/partial" +
                          "/policyGroup/save", "anonymousAccessToUrlPattern=false&policyGroupName" +
                          "=test&throttlingTier=Unlimited&objPartialMappings=[]&policyGroupDesc=null&userRoles=",
                  publisherSession, "application/x-www-form-urlencoded; charset=UTF-8").split(":")[3];
          String policyId = policyIDResponce.substring(1, (policyIDResponce.length() - 2)).trim();
          AppCreateRequest appCreateRequest = new AppCreateRequest();
          appCreateRequest.setUritemplate_policyGroupIds("[" + policyId + "]");
          appCreateRequest.setUritemplate_policyGroupId4(policyId);
          appCreateRequest.setUritemplate_policyGroupId3(policyId);
          appCreateRequest.setUritemplate_policyGroupId2(policyId);
          appCreateRequest.setUritemplate_policyGroupId1(policyId);
          appCreateRequest.setUritemplate_policyGroupId0(policyId);
          appCreateRequest.setClaimPropertyName0("http://wso2.org/claims/streetaddress,http://wso2.org/ffid" +
                  ",http://wso2.org/claims/telephone");
          appCreateRequest.setClaimPropertyCounter("3");
          //publishing travelWebapp
          log.info("publishing travleWebapp");
          appCreateRequest.setOverview_name("travelWebapp");
          appCreateRequest.setOverview_displayName("travelWebapp");
          appCreateRequest.setOverview_context("/travel");
          appCreateRequest.setOverview_version("1.0.0");
          appCreateRequest.setOverview_trackingCode(appCreateRequest.generateTrackingID());
          appCreateRequest.setOverview_transports("http");
          appCreateRequest.setOverview_webAppUrl("http://localhost:" +serverPort + "/plan-your-trip-1.0/");
          String payload = appCreateRequest.generateRequestParameters();

     }

     private void deployWebApplication(AppCreateRequest appCreateRequest) throws IOException, RegistryException {
          String payload = appCreateRequest.generateRequestParameters();
          httpHandler.doPostHttps(httpsBackEndUrl + "/publisher/asset/webapp", payload
                  ,publisherSession, "application/x-www-form-urlencoded");
          String claims_ary = "[\"http://wso2.org/claims/givenname\"]";

          if (appCreateRequest.getClaimPropertyName0().contains(",")) {
               claims_ary = "[";
               String[] claims = appCreateRequest.getClaimPropertyName0().split(",");
               for (int i = 0; i < claims.length; i++) {
                    claims_ary += "\"" + claims[i] + "\"";
                    if (claims.length - 1 != i) {
                         claims_ary += ",";
                    }
               }
               claims_ary += "]";
          }
          String jsonPayload = "{\"provider\":\"wso2is-5.0.0\",\"logout_url\":\"\",\"claims\":" + claims_ary + "" +
                  ",\"app_name\":\"" + appCreateRequest.getOverview_name() + "\",\"app_verison\":\""
                  + appCreateRequest.getOverview_version() + "\",\"app_transport\":\"http\",\"app_context\":\""
                  + appCreateRequest.getOverview_context() + "\",\"app_provider\":\"admin\",\"app_allowAnonymous\":\"f" +
                  "alse\"}";
          httpHandler.doPostHttps(httpsBackEndUrl + "/publisher/api/sso/addConfig", jsonPayload,publisherSession
                  , "application/json; charset=UTF-8");

          String appPath = "/_system/governance/appmgt/applicationdata/provider/admin/" +
                  appCreateRequest.getOverview_name() + "/1.0.0/webapp";
          String UUID = wsRegistryService_client.getUUID(appPath);
          String trackingIDResponse = httpHandler.doGet(httpsBackEndUrl + "/publisher/api/asset/webapp/trackingid/" + UUID
                  , "", publisherSession, "").split(":")[1].trim();
          String trackingID = trackingIDResponse.substring(1, (trackingIDResponse.length() - 2));

         trackingCodes.put(appCreateRequest.getOverview_context(), trackingID);
         invokeStatistcsJavascriptBuilder = new InvokeStatistcsJavascriptBuilder
                 (trackingID,"");//ip address to be implement

          if (appCreateRequest.getOverview_name().equals("travelWebapp")) {
               invokeStatistcsJavascriptBuilder.buildInvokeStaticsJavascriptFile("webapppath"+//to be implement
                       "/webapps/plan-your-trip-1.0");
          } else if (appCreateRequest.getOverview_name().equals("TravelBooking")) {
               invokeStatistcsJavascriptBuilder.buildInvokeStaticsJavascriptFile("webapp path"+ //to be implement
                       "/webapps/travel-booking-1.0/js");
          }
     }

     public  void publishApplication(String applicationType,String applicationName,String UUID) throws IOException {
          httpHandler.doPut(httpsBackEndUrl + "/publisher/api/lifecycle/Submit%20for%20Review/" + applicationType + "/" + UUID
                  , publisherSession);

          httpHandler.doPut(httpsBackEndUrl + "/publisher/api/lifecycle/Approve/" + applicationType + "/" + UUID
                  , publisherSession);

          httpHandler.doPut(httpsBackEndUrl + "/publisher/api/lifecycle/Publish/" + applicationType + "/" + UUID
                  , publisherSession);
          log.info(applicationName+" "+applicationType+" published and UUID is "+UUID);
     }

     public  void subscribeApplication(AppCreateRequest appCreateRequest) throws IOException {
          httpHandler.doPostHttps(httpsBackEndUrl + "/store/resources/webapp/v1/subscription/app",
                  "apiName=" + appCreateRequest.getOverview_name() + "" +
                          "&apiVersion=" + appCreateRequest.getOverview_version() + "&apiTier=" +
                          appCreateRequest.getOverview_tier()
                          + "&subscriptionType=INDIVIDUAL&apiProvider=admin&appName=DefaultApplication"
                  , storeSession, "application/x-www-form-urlencoded; charset=UTF-8");
     }

     public  String createMobielAppliaction(MobileApplicationBean mobileApplicationBean) throws IOException {
          if(mobileApplicationBean.getName().equals("CleanCalc")){
               httpHandler.doPostMultiData(httpsBackEndUrl + "/publisher/api/mobileapp/upload",
                       "upload", mobileApplicationBean, publisherSession);
          }
          return httpHandler.doPostMultiData(httpsBackEndUrl + "/publisher/api/asset/mobileapp", "none",
                  mobileApplicationBean, publisherSession);
     }

    public void accsesWebPages(String webContext, String trackingCode, int hitCount,String ipAddress) {
        String loginHtmlPage = null;
        String webAppurl = "http://" + ipAddress + ":8280" + webContext + "/1.0.0/";
        String responceHtml = null;
        try {
            loginHtmlPage =  httpHandler .getHtml(webAppurl);
            Document html = Jsoup.parse(loginHtmlPage);
            Element something = html.select("input[name=sessionDataKey]").first();
            String sessionDataKey = something.val();
            responceHtml = httpHandler.doPostHttps(httpsBackEndUrl+ "/commonauth"
                    , "username=admin&password=admin&sessionDataKey=" + sessionDataKey
                    , "none"
                    , "application/x-www-form-urlencoded; charset=UTF-8");
            Document postHtml = Jsoup.parse(responceHtml);
            Element postHTMLResponse = postHtml.select("input[name=SAMLResponse]").first();
            String samlResponse = postHTMLResponse.val();
            String appmSamlSsoTokenId = httpHandler.doPostHttp(webAppurl,
                    "SAMLResponse=" + URLEncoder.encode(samlResponse, "UTF-8"), "appmSamlSsoTokenId",
                    "application/x-www-form-urlencoded; charset=UTF-8");
            for (int i = 0; i < hitCount; i++) {
                if (webContext.equals("/notifi")) {
                    if (i == hitCount / 5) {
                        webAppurl += "member/";
                    } else if (i == hitCount / 2) {
                        webAppurl = appendPageToUrl("admin", webAppurl, false);
                    }
                } else if (webContext.equals("/travelBooking")) {
                    if (i == hitCount / 5) {
                        webAppurl = appendPageToUrl("booking-step1.jsp", webAppurl, true);
                    } else if (i == hitCount / 2) {
                        webAppurl = appendPageToUrl("booking-step2.jsp", webAppurl, false);
                    }
                }
                httpHandler.doGet("http://" + ipAddress + ":8280/statistics/",
                        trackingCode, appmSamlSsoTokenId, webAppurl);
                log.info("Web Page : " + webAppurl + " Hit count : " + i );
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String appendPageToUrl(String pageName, String webAppUrl, boolean isAppendLastOne) {
        String elements[] = webAppUrl.split("/");
        StringBuilder newUrl = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            if (!elements[i].equals("")) {
                if (i == 0) {
                    newUrl.append(elements[i] + "//");
                } else if ((i == (elements.length - 1)) && isAppendLastOne) {
                    newUrl.append(elements[i] + "/");
                } else if (i != (elements.length - 1)) {
                    newUrl.append(elements[i] + "/");
                }
            }
        }
        newUrl.append(pageName + "/");
        return newUrl.toString();
    }

}
