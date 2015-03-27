package org.wso2.carbon.appmgt.sampledeployer.appm;

import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

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

public class WSRegistryService_Client {
    private Registry wsRegistryServiceClient;

    public WSRegistryService_Client(String backEndUrl, String cookie) throws RegistryException {
        wsRegistryServiceClient = new WSRegistryServiceClient(backEndUrl + "/services/", cookie);
    }

    public String getUUID(String path) throws RegistryException {
        Resource resource = wsRegistryServiceClient.get(path);
        return resource.getUUID();

    }


}
