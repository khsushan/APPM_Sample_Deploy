package org.wso2.carbon.appmgt.sampledeployer.appcontroller;

import org.apache.log4j.Logger;
import org.wso2.carbon.appmgt.sampledeployer.appm.WSRegistryService_Client;
import org.wso2.carbon.appmgt.sampledeployer.bean.AppCreateRequest;
import org.wso2.carbon.appmgt.sampledeployer.bean.MobileApplicationBean;
import org.wso2.carbon.appmgt.sampledeployer.http.HttpHandler;
import org.wso2.carbon.appmgt.sampledeployer.javascriptwrite.InvokeStatistcsJavascriptBuilder;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import java.io.IOException;

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
     private String webAppPath;

     public  ApplicationController(String username,String password,int offset,String serviceSession) throws IOException, RegistryException {
          httpPort+=offset;
          httpsPort+=offset;
          httpsBackEndUrl = "https://localhost:"+httpsPort;
          httpBackEndUrl = "https://localhost:"+httpPort;
          httpHandler = new HttpHandler();
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
          /*trackingCodes.put(appCreateRequest.getOverview_context(), trackingID);
          invokeStatistcsJavascriptBuilder = new InvokeStatistcsJavascriptBuilder
                  (trackingID, ipAddress);
          if (applicationName.equals("travelWebapp")) {
               invokeStatistcsJavascriptBuilder.buildInvokeStaticsJavascriptFile(tomcatPath +
                       "/webapps/plan-your-trip-1.0");
          } else if (applicationName.equals("TravelBooking")) {
               invokeStatistcsJavascriptBuilder.buildInvokeStaticsJavascriptFile(tomcatPath +
                       "/webapps/travel-booking-1.0/js");
          } else if (applicationName.equals("notifi")) {
               invokeStatistcsJavascriptBuilder.buildInvokeStaticsJavascriptFile(lampPath +
                       "/htdocs/notifi/assets/js");
          }*/
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
}
