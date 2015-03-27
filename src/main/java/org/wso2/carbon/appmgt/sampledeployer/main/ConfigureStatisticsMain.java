package org.wso2.carbon.appmgt.sampledeployer.main;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.wso2.carbon.appmgt.sampledeployer.xmlhandler.XMLHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

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

public class ConfigureStatisticsMain {

    final static Logger log = Logger.getLogger(ApplicationPublisher.class.getName());
    private static String appmPath = "/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT";
    private static String bamPath = "/home/ushan/Shell_Script_Test/BAM/wso2bam-2.5.0";
    private static String offset ="3";
    private static String ipAddress ="localhost";
    private static int tcpPort = 7611;

    public static void main(String[] args) {
        appmPath = args[0];
        bamPath = args[1];
        offset = args[2];
        ipAddress =  args[3];
        tcpPort += Integer.parseInt(offset);
        log.info("initialising properties");
        log.info("APPM path : "+appmPath);
        log.info("BAM path : "+bamPath);
        log.info("Offset : "+offset);
        log.info("IP Address : "+ipAddress);
        statisticsConfiguration();
    }

    private static void statisticsConfiguration(){
        try {
            log.info("Configuring app-manager.xml");
            XMLHandler.configuredAPPMXML(appmPath + "/repository/conf/app-manager.xml",tcpPort+"",ipAddress);
            log.info("Configuring master-datasources.xml");
            Node node = XMLHandler.configuredDataSourceXML(appmPath + "/repository/conf/datasources/master-datasources.xml"
                    ,"jdbc:h2:"+bamPath + "/repository/database/APIMGTSTATS_DB;AUTO_SERVER=TRUE");
            log.info("Configuring carbon.xml");
            XMLHandler.updateOffset(bamPath + "/repository/conf/carbon.xml", offset);
            log.info("Configuring bam-datasources.xml");
            XMLHandler.updateBamDatasource(bamPath + "/repository/conf/datasources/bam-datasources.xml", node);
            log.info("Configuring hector-config.xml");
            XMLHandler.updateHectorConfigXML(bamPath + "/repository/conf/etc/hector-config.xml", "localhost:9160");
            log.info("Configuration Complete");
        } catch (ParserConfigurationException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (SAXException e) {
            log.error(e.getMessage());
        } catch (TransformerException e) {
            log.error(e.getMessage());
        }
    }
}
