package org.wso2.carbon.appmgt.sampledeployer.configuration;

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
public class Configuration {

    static {
        /*AppManagerConfiguration config = ServiceReferenceHolder.getInstance().
                getAPIManagerConfigurationService().getAPIManagerConfiguration();*/
    }

    public static String getUserName(){
        return "admin";
    }

    public static String getPassword(){
        return "admin";
    }

    public static String getHttpsUrl(){
        return "https://localhost:9443";
    };

    public static String getHttpUrl(){
        return "http://localhost:9763";
    };

    public static int getOffset(){return 0;}
}
