package org.wso2.carbon.appmgt.sampledeployer.main;

import org.apache.log4j.Logger;
import org.wso2.carbon.appmgt.sampledeployer.appcontroller.ApplicationController;
import org.wso2.carbon.appmgt.sampledeployer.configuration.ConfigureWebApplication;
import org.wso2.carbon.appmgt.sampledeployer.deploy.DeployWebApplication;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceException;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;

import java.io.IOException;
import java.rmi.RemoteException;

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
    private static String backEndUrl = "https://localhost:9443";
    //private static String appmPath = "/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT";
    private static String appmPath = "../../..";

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore",appmPath+"/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        try {
            DeployWebApplication.copyFileUsingFileStreams("plan-your-trip-1.0.war");
            DeployWebApplication.copyFileUsingFileStreams("travel-booking-1.0.war");
            log.info("web application deployed in tomcat");
        } catch (IOException e) {
           log.error(e.getMessage());
        }
        ConfigureWebApplication configureWebApplication = null;
        try {
           configureWebApplication  = new ConfigureWebApplication(backEndUrl);
        } catch (RemoteException e) {
            log.error(e.getMessage());
        } catch (LoginAuthenticationExceptionException e) {
            log.error(e.getMessage());
        }

        try {
            configureWebApplication.addClaimMapping();
            log.info("claim mapping added successfuly");
            configureWebApplication.setClaimValues();
            log.info("claim values updated successfuly");
        } catch (RemoteException e) {
            log.error(e.getMessage());
        } catch (ClaimManagementServiceException e) {
            log.error(e.getMessage());
        } catch (RemoteUserStoreManagerServiceUserStoreExceptionException e) {
            log.error(e.getMessage());
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ApplicationController applicationController =  null;
        try {
            applicationController = new ApplicationController("admin","admin",0,"10.100.4.102");
            applicationController.manageWebApplication();
            try {
                applicationController.manageMobilebApplication();
            } catch (InterruptedException e) {
               log.error(e.getMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (RegistryException e) {
            log.error(e.getMessage());
        } catch (LoginAuthenticationExceptionException e) {
            log.error(e.getMessage());
        }

    }
}