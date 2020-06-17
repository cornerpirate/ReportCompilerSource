/* 
 * Copyright 2015 cornerpirate.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cornerpirate.reportcompiler.Importers;

import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import com.cornerpirate.reportcompiler.Utils.Helper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author cornerpirate
 */
public class SurecheckImporter implements ImporterInterface {

    @Override
    public boolean isValid(File file) {

        try {
            String valid_string = "<surecheck-report>";
            boolean valid = new Helper().fileContainsString(file, valid_string);
            return valid;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public DefaultMutableTreeNode readFile(File importFile) {
        System.out.println("==SurecheckImporter=readFile: " + importFile.getAbsolutePath());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(importFile);
            doc.normalize();

            Node subject_node = doc.getElementsByTagName("subject").item(0);
            if (subject_node == null) {
                System.out.println("This surecheck file didn't have a subject node? We should never happen.");
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(null, "This surecheck file didn't have a subject node? We should never happen.");
                    }
                });
                t.start();
                return null; // can't carry on under these circumstances!
            }

            // Setup the host object. SureCheck XML has one target per file
            Host host = new Host();
            host.setIp_address("1.1.1.1");
            host.setHostname(subject_node.getTextContent());
            host.setPortnumber("0");
            host.setProtocol("tcp");

            NodeList findingList = doc.getElementsByTagName("finding");
            for (int i = 0; i < findingList.getLength(); i++) {
                Node findingNode = findingList.item(i);
                NodeList findingNodeChildren = findingNode.getChildNodes();
                
                // Take a copy of the host. We are going to butcher it a little.
                Host cloned = host.clone();

                // Create a vulnerability for this finding.
                Vulnerability vuln = new Vulnerability();
                vuln.setImport_tool("Surecheck");
                vuln.setPortnumber(host.getPortnumber());
                vuln.setProtocol(host.getProtocol());

                vuln.setIs_custom_risk(true); // default to true, if a CVSS vector is hit it will be modified

                for (int k = 0; k < findingNodeChildren.getLength(); k++) {
                    Node thisnode = findingNodeChildren.item(k);
                    if (thisnode.getNodeType() == Node.ELEMENT_NODE) {
                        String nodeName = thisnode.getNodeName();
                        String nodeValue = thisnode.getTextContent();
                        //System.out.println("nodeName:" + nodeName) ;

                        // Check node names. I do this cause SureCheck is might not have
                        // consistent node positions. It seems to but I have been burned in the past!
                        //if (nodeName.equalsIgnoreCase("name")) { // its like a nessus plugin name, not required.
                        //} else 
                        
                        if (nodeName.equalsIgnoreCase("title")) {

                            vuln.setTitle(nodeValue);
                            // This identifier will be permanent for the lifetime of the vuln
                            vuln.setIdentifier();

                        } else if (nodeName.equalsIgnoreCase("severity")) {
                            // Surecheck only does custom risks with 'High', 'Medium', or 'Low' being the values
                            vuln.setRisk_category(thisnode.getChildNodes().item(3).getTextContent());

                        } else if (nodeName.equalsIgnoreCase("content")) {
                            
                            // Deep breath. This is one of the most broken bits of XML in history. Hate tackling this bit.
                            // Three bits of data hidden inside one node with custom markers instead of using three XML nodes.
                            
                            // Three variables
                            String description = "" ;
                            String recommendation = "" ;
                            //String notes = "" ; // Surecheck doesn't have any notes
                            
                            // Lets wrench the appropriate bits from the bossom of this awful XML.
                            String start_description = "== Description ==\\r\\n" ;
                            String end_description = "\\r\\n\\r\\n== Recommendation ==\\r\\n" ;
                            description = nodeValue.substring(nodeValue.indexOf(start_description)+start_description.length(),nodeValue.indexOf(end_description)) ;
                            description = description.replaceAll("\\\\r\\\\n", "\n"); // Replacing shady SureCheck formatting.
                            
                            String start_recommendation = end_description ;
                            recommendation = nodeValue.substring(nodeValue.indexOf(start_recommendation)+start_recommendation.length()) ;
                            recommendation = recommendation.replaceAll("\\\\r\\\\n", "\n"); // Replacing shady SureCheck formatting.

                            // Set them into the vulnerability
                            vuln.setDescription(description);
                            vuln.setRecommendation(recommendation);
                            //cloned.setNotes(new Note(notes));
                        } 
                    }

                }
                
                // Add the clone to the affected hosts.
                vuln.addAffectedHost(cloned);
                // Add this vuln to the tree
                root.add(new DefaultMutableTreeNode(vuln));
            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return root;
    }

}
