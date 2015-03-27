package org.wso2.carbon.appmgt.sampledeployer.main;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.conn.Wire;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.wso2.carbon.appmgt.sampledeployer.appm.ClaimManagementServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.LoginAdminServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.RemoteUserStoreManagerServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.WSRegistryService_Client;
import org.wso2.carbon.appmgt.sampledeployer.bean.AppCreateRequest;
import org.wso2.carbon.appmgt.sampledeployer.bean.MobileApplicationBean;
import org.wso2.carbon.appmgt.sampledeployer.commandlinehandler.CommandLine;
import org.wso2.carbon.appmgt.sampledeployer.http.HttpHandler;
import org.wso2.carbon.appmgt.sampledeployer.javascriptwrite.InvokeStatistcsJavascriptBuilder;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceException;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;

import java.io.IOException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

public class ApplicationPublisher {
    final static Logger log = Logger.getLogger(ApplicationPublisher.class.getName());
    private static WSRegistryService_Client wsRegistryService_client;
    private static InvokeStatistcsJavascriptBuilder invokeStatistcsJavascriptBuilder;
    private static String backEndNonSecureUrl = "http://localhost:9763";
    private static String backEndUrl = "https://localhost:9443";
    private static HttpHandler httpHandler = new HttpHandler();
    private static String tomcatPort = "8080";
    private static String ipAddress = "10.100.4.102";
    private static String tomcatPath = "/home/ushan/Shell_Script_Test/Tomcat/apache-tomcat-7.0.59";
    private static String lampPath = "/opt/lampp";
    private static String appmPath = "/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT";
    private static String username = "admin";
    private static String password = "admin";
    private static ConcurrentHashMap<String, String> trackingCodes;
    private static int hitCount = 10;
    public static Wire HEADER_WIRE = new Wire(LogFactory.getLog("httpclient.wire.header"));
    public static Wire CONTENT_WIRE = new Wire(LogFactory.getLog("httpclient.wire.content"));

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", appmPath + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        log.info("initialising properties");
        log.info("Tomcat port : "+tomcatPort);
        log.info("APPM path : "+appmPath);
        log.info("Username : "+username);
        log.info("Password : "+password);
        log.info("Tomcat path : "+tomcatPath);
        log.info("LAMP path : "+lampPath);
        log.info("IP Address : "+ipAddress);
        log.info("Hit count : "+hitCount);
        trackingCodes = new ConcurrentHashMap<String, String>();
        configure();
        //startServers();
        try {
            log.info("Starting Servers.....");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*trackingCodes.put("/notifi","AM_995364166653157224");
        trackingCodes.put("/travelBooking","AM_14359451055881852910");
        trackingCodes.put("/travel","AM_10574531036724348945");*/
        for (String key : trackingCodes.keySet()) {
            accsesWebPages(key, trackingCodes.get(key), hitCount);
        }
    }



    private static void configure() {
        LoginAdminServiceClient login = null;
        try {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            login = new LoginAdminServiceClient(backEndUrl);
            String normal_session = login.authenticate(username, password);
            ClaimManagementServiceClient claimManagementServiceClient =
                    new ClaimManagementServiceClient(normal_session, backEndUrl);
            RemoteUserStoreManagerServiceClient remoteUserStoreManagerServiceClient =
                    new RemoteUserStoreManagerServiceClient(normal_session, backEndUrl);
            //add claim dialects
            log.info("Add claim mapping");
            claimManagementServiceClient.addClaim("FrequentFlyerID", "http://wso2.org/ffid", true);
            claimManagementServiceClient.addClaim("zipcode", "http://wso2.org/claims/zipcode", true);
            claimManagementServiceClient.addClaim("Credit card number", "http://wso2.org/claims/card_number", true);
            claimManagementServiceClient.addClaim("Credit cArd Holder Name", "http://wso2.org/claims/card_holder"
                    , true);
            claimManagementServiceClient.addClaim("Credit card expiration date", "http://wso2.org/claims/expiration_date"
                    , true);
            //set values for claims
            log.info("Updating claim values");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/ffid", "12345151");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/streetaddress", "21/5");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/zipcode", "GL");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/card_number"
                    , "001012676878");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/card_holder", "Admin");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/telephone", "091222222");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/givenname", "Sachith");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/lastname", "Ushan");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/emailaddress", "wso2@wso2.com");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/country", "SriLanka");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/expiration_date"
                    , "31/12/2015");
            //publish application
            log.info("Creating application DTO");
            try {
                String publisher_session = httpHandler.doPostHttps(backEndUrl + "/publisher/api/authenticate",
                        "username=" + username + "&password=" + password + "&action=login", ""
                        , "application/x-www-form-urlencoded");
                String store_session = httpHandler.doPostHttp(backEndNonSecureUrl + "/store/apis/user/login",
                        "{\"username\":\"admin\"" +
                                ",\"password\":\"admin\"}", "header", "application/json");
                AppCreateRequest appCreateRequest = new AppCreateRequest();
                //requesting policy ID
                String policyIDResponce = httpHandler.doPostHttps(backEndUrl + "/publisher/api/entitlement/policy/partial" +
                                "/policyGroup/save", "anonymousAccessToUrlPattern=false&policyGroupName" +
                                "=test&throttlingTier=Unlimited&objPartialMappings=[]&policyGroupDesc=null&userRoles=",
                        publisher_session, "application/x-www-form-urlencoded; charset=UTF-8").split(":")[3];
                String policyId = policyIDResponce.substring(1, (policyIDResponce.length() - 2)).trim();
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
                appCreateRequest.setOverview_webAppUrl("http://localhost:" + tomcatPort + "/plan-your-trip-1.0/");
                String payload = appCreateRequest.generateRequestParameters();
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session,
                        publisher_session, appCreateRequest, "application/x-www-form-urlencoded", "");
                httpHandler.doPostHttps(backEndUrl + "/store/resources/webapp/v1/subscription/app",
                        "apiName=" + appCreateRequest.getOverview_name() + "" +
                                "&apiVersion=" + appCreateRequest.getOverview_version() + "&apiTier=" +
                                appCreateRequest.getOverview_tier()
                                + "&subscriptionType=INDIVIDUAL&apiProvider=admin&appName=DefaultApplication"
                        , store_session, "application/x-www-form-urlencoded; charset=UTF-8");
                //publishing travel booking application
                log.info("publishing travleWebapp");
                appCreateRequest.setOverview_name("TravelBooking");
                appCreateRequest.setOverview_displayName("TravelBooking");
                appCreateRequest.setOverview_context("/travelBooking");
                appCreateRequest.setOverview_version("1.0.0");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setOverview_trackingCode(appCreateRequest.generateTrackingID());
                appCreateRequest.setClaimPropertyName0("http://wso2.org/claims/givenname,http://wso2.org/claims/lastname" +
                        ",http://wso2.org/claims/emailaddress,http://wso2.org/claims/streetaddress" +
                        ",http://wso2.org/claims/zipcode,http://wso2.org/claims/country" +
                        ",http://wso2.org/claims/card_number,http://wso2.org/claims/card_holder" +
                        ",http://wso2.org/claims/expiration_date");
                appCreateRequest.setClaimPropertyCounter("9");
                appCreateRequest.setOverview_webAppUrl("http://localhost:" + tomcatPort + "/travel-booking-1.0/");
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session,
                        publisher_session, appCreateRequest, "application/x-www-form-urlencoded", "");
                httpHandler.doPostHttps(backEndUrl + "/store/resources/webapp/v1/subscription/app",
                        "apiName=" + appCreateRequest.getOverview_name() + "" +
                                "&apiVersion=" + appCreateRequest.getOverview_version() + "&apiTier=" +
                                appCreateRequest.getOverview_tier()
                                + "&subscriptionType=INDIVIDUAL&apiProvider=admin&appName=DefaultApplication"
                        , store_session, "application/x-www-form-urlencoded; charset=UTF-8");
                log.info("publishing notifi web application");
                //publishing notifi webapplication
                appCreateRequest.setOverview_name("notifi");
                appCreateRequest.setOverview_context("/notifi");
                appCreateRequest.setOverview_displayName("notifi");
                appCreateRequest.setOverview_version("1.0.0");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setClaimPropertyName0("http://wso2.org/claims/card_number");
                appCreateRequest.setClaimPropertyCounter("1");
                appCreateRequest.setOverview_trackingCode(appCreateRequest.generateTrackingID());
                appCreateRequest.setOverview_webAppUrl("http://localhost/notifi/");
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session,
                        publisher_session, appCreateRequest, "application/x-www-form-urlencoded", "");
                httpHandler.doPostHttps(backEndUrl + "/store/resources/webapp/v1/subscription/app",
                        "apiName=" + appCreateRequest.getOverview_name() + "" +
                                "&apiVersion=" + appCreateRequest.getOverview_version() + "&apiTier=" +
                                appCreateRequest.getOverview_tier()
                                + "&subscriptionType=INDIVIDUAL&apiProvider=admin&appName=DefaultApplication"
                        , store_session, "application/x-www-form-urlencoded; charset=UTF-8");
                //publish mobile application clean calc
                log.info("publishing CleanCalc mobile application");
                MobileApplicationBean mobileApplicationBean = new MobileApplicationBean();
                mobileApplicationBean.setApkFile(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/CleanCalc.apk");
                String appMeta = httpHandler.doPostMultiData(backEndUrl + "/publisher/api/mobileapp/upload",
                        "upload", mobileApplicationBean, publisher_session);
                mobileApplicationBean.setVersion("1.0.0");
                mobileApplicationBean.setProvider("1WSO2Mobile");
                mobileApplicationBean.setMarkettype("Enterprise");
                mobileApplicationBean.setPlatform("android");
                mobileApplicationBean.setName("CleanCalc");
                mobileApplicationBean.setDescription("this is a clean calucultor");
                mobileApplicationBean.setBannerFilePath(appmPath + "/repository/resources/mobileapps/" +
                        "CleanCalc/Resources/banner.png");
                mobileApplicationBean.setIconFile(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/icon.png");
                mobileApplicationBean.setScreenShot1File(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/screen1.png");
                mobileApplicationBean.setScreenShot2File(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/screen2.png");
                mobileApplicationBean.setScreenShot3File(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/screen3.png");
                mobileApplicationBean.setMobileapp("mobileapp");
                mobileApplicationBean.setAppmeta(appMeta);
                String ID = httpHandler.doPostMultiData(backEndUrl + "/publisher/api/asset/mobileapp", "none",
                        mobileApplicationBean, publisher_session);
                publishApplication("", "mobileapp", normal_session, publisher_session, null, "", ID);
                //wso2Con mobile application
                log.info("publishing WSO2Con mobile application");
                mobileApplicationBean.setAppmeta("{\"package\":\"com.wso2.wso2con.mobile\",\"version\":\"1.0.0\"}");
                mobileApplicationBean.setVersion("1.0.0");
                mobileApplicationBean.setProvider("1WSO2Mobile");
                mobileApplicationBean.setMarkettype("Market");
                mobileApplicationBean.setPlatform("android");
                mobileApplicationBean.setName("Wso2Con");
                mobileApplicationBean.setDescription("WSO2Con mobile app provides the agenda and meeting tool for " +
                        "WSO2Con. Get the app to follow the agenda, find out about the sessions and speakers, provide " +
                        "feedback on the sessions, and get more information on the venue and sponsors. Join us at " +
                        "WSO2Con, where we will place emerging technology, best practices, and WSO2 product features " +
                        "in the perspective of accelerating development and building a connected business. We hope you " +
                        "enjoy WSO2Con!");
                mobileApplicationBean.setBannerFilePath(appmPath + "/repository/resources/mobileapps/WSO2Con" +
                        "/Resources/banner.png");
                mobileApplicationBean.setIconFile(appmPath + "/repository/resources/mobileapps/WSO2Con" +
                        "/Resources/icon.png");
                mobileApplicationBean.setScreenShot1File(appmPath + "/repository/resources/mobileapps/WSO2Con" +
                        "/Resources/screen1.png");
                mobileApplicationBean.setScreenShot2File(appmPath + "/repository/resources/mobileapps" +
                        "/WSO2Con/Resources/screen2.png");
                mobileApplicationBean.setMobileapp("mobileapp");
                ID = httpHandler.doPostMultiData(backEndUrl + "/publisher/api/asset/mobileapp", "none"
                        , mobileApplicationBean, publisher_session);
                publishApplication("", "mobileapp", normal_session, publisher_session, null, "", ID);
                //MyTrack mobile application
                log.info("publishing MyTrack mobile application");
                mobileApplicationBean.setAppmeta("{\"package\":\"com.google.android.maps.mytracks\",\"version\":\"1.0" +
                        ".0\"}");
                mobileApplicationBean.setVersion("1.0.0");
                mobileApplicationBean.setProvider("1WSO2Mobile");
                mobileApplicationBean.setMarkettype("Market");
                mobileApplicationBean.setPlatform("android");
                mobileApplicationBean.setName("MyTracks");
                mobileApplicationBean.setDescription("My Tracks records your path, speed, distance, and elevation while " +
                        "you walk, run, bike, or do anything else outdoors. While recording, you can view your data live" +
                        ", annotate your path, and hear periodic voice announcements of your progress.\n" +
                        "With My Tracks, you can sync and share your tracks via Google Drive. For sharing, you can share" +
                        " tracks with friends, see the tracks your friends have shared with you, or make tracks public " +
                        "and share their URLs via Google+, Facebook, Twitter, etc. In addition to Google Drive, you can " +
                        "also export your tracks to Google My Maps, Google Spreadsheets, or external storage.\n" +
                        "My Tracks also supports Android Wear. For watches with GPS, My Tracks can perform GPS recording" +
                        " of tracks without a phone and sync tracks to the phone. For watches without GPS, you can see " +
                        "your current distance and time, and control the recording of your tracks from your wrist.");
                mobileApplicationBean.setBannerFilePath(appmPath + "/repository/resources/" +
                        "mobileapps/MyTracks/Resources/banner.png");
                mobileApplicationBean.setIconFile(appmPath + "/repository/resources/mobileapps" +
                        "/MyTracks/Resources/icon.png");
                mobileApplicationBean.setScreenShot1File(appmPath + "/repository/resources/mobileapps/MyTracks" +
                        "/Resources/screen1.png");
                mobileApplicationBean.setScreenShot2File(appmPath + "/repository/resources/mobileapps/MyTracks" +
                        "/Resources/screen2.png");
                mobileApplicationBean.setMobileapp("mobileapp");
                ID = httpHandler.doPostMultiData(backEndUrl + "/publisher/api/asset/mobileapp", "none"
                        , mobileApplicationBean, publisher_session);
                publishApplication("", "mobileapp", normal_session, publisher_session, null, "", ID);
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } catch (AxisFault axisFault) {
            configure();
        } catch (RemoteException e) {
            log.error(e.getMessage());
        } catch (LoginAuthenticationExceptionException e) {
            log.error(e.getMessage());
        } catch (ClaimManagementServiceException e) {
            log.error(e.getMessage());
        } catch (RemoteUserStoreManagerServiceUserStoreExceptionException e) {
            log.error(e.getMessage());
        }
        startServers();

    }

    private static void publishApplication(String applicationName, String applicationType, String session, String
            appm_session_id, AppCreateRequest appCreateRequest, String contentType, String id)
            throws Exception {
        String UUID = "";
        String appPath = "";
        if (!applicationType.equals("mobileapp")) {
            String payload = appCreateRequest.generateRequestParameters();
            httpHandler.doPostHttps(backEndUrl + "/publisher/asset/" + applicationType, payload
                    , appm_session_id, contentType);
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
            httpHandler.doPostHttps(backEndUrl + "/publisher/api/sso/addConfig", jsonPayload, appm_session_id
                    , "application/json; charset=UTF-8");
            wsRegistryService_client = new WSRegistryService_Client(backEndUrl, session);
            appPath = "/_system/governance/appmgt/applicationdata/provider/admin/" + applicationName + "/1.0.0/"
                    + applicationType;
            UUID = wsRegistryService_client.getUUID(appPath);
            String trackingIDResponse = httpHandler.doGet(backEndUrl + "/publisher/api/asset/webapp/trackingid/" + UUID
                    , "", appm_session_id, "").split(":")[1].trim();
            String trackingID = trackingIDResponse.substring(1, (trackingIDResponse.length() - 2));
            trackingCodes.put(appCreateRequest.getOverview_context(), trackingID);
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
            }
        } else {
            UUID = id;
        }
        //publishing application
        httpHandler.doPut(backEndUrl + "/publisher/api/lifecycle/Submit%20for%20Review/" + applicationType + "/" + UUID
                , appm_session_id);

        httpHandler.doPut(backEndUrl + "/publisher/api/lifecycle/Approve/" + applicationType + "/" + UUID
                , appm_session_id);

        httpHandler.doPut(backEndUrl + "/publisher/api/lifecycle/Publish/" + applicationType + "/" + UUID
                , appm_session_id);
        log.info(applicationName+" "+applicationType+" published and UUID is "+UUID);
    }

    private static void startServers() {
        log.info("starting servers......");
        CommandLine.executeCommand(new String[]{"bash", "-c", "sudo chmod 777 -R " + tomcatPath});
        //start tomcat
        log.info("Starting tomcat....");
        CommandLine.executeCommand(new String[]{"bash", "-c", "sh " + tomcatPath + "/bin/startup.sh"});
        //stoping apache servers
        CommandLine.executeCommand(new String[]{"bash", "-c", "sudo /etc/init.d/apache2 stop"});
        //start lamp
        log.info("Starting lamp");
        CommandLine.executeCommand(new String[]{"bash", "-c", "sudo " + lampPath + "/lampp start"});

    }

    private static void accsesWebPages(String webContext, String trackingCode, int hitCount) {
        String loginHtmlPage = null;
        String webAppurl = "http://" + ipAddress + ":8280" + webContext + "/1.0.0/";
        String responceHtml = null;
        try {
            loginHtmlPage = httpHandler.getHtml(webAppurl);
            Document html = Jsoup.parse(loginHtmlPage);
            Element something = html.select("input[name=sessionDataKey]").first();
            String sessionDataKey = something.val();
            responceHtml = httpHandler.doPostHttps(backEndUrl + "/commonauth"
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

    private static String appendPageToUrl(String pageName, String webAppUrl, boolean isAppendLastOne) {
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