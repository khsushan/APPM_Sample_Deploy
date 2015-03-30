package org.wso2.carbon.appmgt.sampledeployer.deploy;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

public class DeployWebApplication {
    //private static String homePath = "/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT";
    private static String homePath = "../../..";

    public static void copyFileUsingFileStreams(String warFileName) throws IOException {
        File souceFile  = new File(homePath+"/samples/"+warFileName);
        File destinantionFile = new File(homePath+"/repository/deployment/server/webapps/"+warFileName);
        FileUtils.copyFile(souceFile,
                destinantionFile);
    }
}
