package org.wso2.carbon.appmgt.sampledeployer.xmlhandler;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

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
public class XMLHandler {

    public XMLHandler(){

    }

    public static void configuredAPPMXML(String xmlPath,String port,String ipAddress) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlPath.trim());
        Node apiUsageTrackingNode= doc.getElementsByTagName("APIUsageTracking").item(0);
        if(apiUsageTrackingNode instanceof Element) {
            Element docElement = (Element)apiUsageTrackingNode;
            Node enabled = docElement.getElementsByTagName("Enabled").item(0);
            enabled.setTextContent("true");
            Node bamServerURl = docElement.getElementsByTagName("BAMServerURL").item(0);
            bamServerURl.setTextContent("tcp://"+ipAddress+":"+port);
        }
        Node dataSourceName = doc.createElement("DataSourceName");
        dataSourceName.appendChild(doc.createTextNode("jdbc/WSO2AM_STATS_DB"));
        apiUsageTrackingNode.appendChild(dataSourceName);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlPath).toURI().getPath());
        transformer.transform(source, result);
    }

    public static Node configuredDataSourceXML(String xmlPath,String url) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlPath.trim());
        Node datasourceNode = doc.getElementsByTagName("datasource").item(2);
        Node definitionNode = ((Element)datasourceNode).getElementsByTagName("definition").item(0);
        Node configurationNode = ((Element)definitionNode).getElementsByTagName("configuration").item(0);
        Node urlNode = ((Element)configurationNode).getElementsByTagName("url").item(0);
        urlNode.setTextContent(url);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlPath).toURI().getPath());
        transformer.transform(source, result);
        return  datasourceNode;

    }

    public  static void updateOffset(String carbonXMLPath,String offset) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(carbonXMLPath.trim());
        Node portNode = doc.getElementsByTagName("Ports").item(0);
        Node definitionNode = ((Element)portNode).getElementsByTagName("Offset").item(0);
        definitionNode.setTextContent(offset);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(carbonXMLPath).toURI().getPath());
        transformer.transform(source, result);
    }

    public  static void updateBamDatasource(String xmlFilePath,Node node) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlFilePath.trim());
        Node datasources = doc.getElementsByTagName("datasources").item(0);
        Element datasourceElements = (Element)datasources;
        Node datasource=doc.importNode(node,true);
        datasources.appendChild(datasource);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath).toURI().getPath());
        transformer.transform(source, result);
    }

    public static  void updateHectorConfigXML(String xmlFilePath,String url) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlFilePath.trim());
        Node hectorConfigurationXML = doc.getElementsByTagName("HectorConfiguration").item(0);
        Node definitionNode = ((Element)hectorConfigurationXML).getElementsByTagName("Cluster").item(0);
        Node configurationNode = ((Element)definitionNode).getElementsByTagName("Nodes").item(0);
        configurationNode.setTextContent(url);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath).toURI().getPath());
        transformer.transform(source, result);
    }
}
