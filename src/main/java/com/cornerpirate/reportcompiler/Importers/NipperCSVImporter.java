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
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author cornerpirate
 */
public class NipperCSVImporter implements ImporterInterface {

    @Override
    public boolean isValid(File file) {

        try {
            // String that seems to be reliably in any CSV output from nipper
            // This is a bit dodgy and is why XML is better generally
            String valid_string = "Nipper Studio";
            boolean valid = new Helper().fileContainsString(file, valid_string);
            return valid;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public DefaultMutableTreeNode readFile(File importFile) {
        System.out.println("==NipperCSVImporter=readFile: " + importFile.getAbsolutePath());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");

        try {

            FileReader in = new FileReader(importFile);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

            Host host = null;

            for (CSVRecord record : records) {
                String title = record.get("Issue Title");
                title = title.substring(title.indexOf(" ")+1);
                String affected = record.get("Devices");
                String risk = record.get("Rating");
                if (risk.equals("Informational")) {
                    risk = "Info" ;
                }
                String description = record.get("Finding");
                String impact = record.get("Impact");
                String ease = record.get("Ease");
                String recommendation = record.get("Recommendation");
                
                //System.out.println(title) ;
                //System.out.println(affected) ;
                //System.out.println(risk) ;
                //System.out.println(description) ;
                //System.out.println(impact) ;
                //System.out.println(ease) ;
                //System.out.println(recommendation) ;
                
                if (host == null) {
                    host = new Host();
                    host.setIp_address("1.1.1.1");
                    host.setHostname(affected);
                    host.setPortnumber("0");
                    host.setProtocol("tcp");
                }

                // basic setup for a custom risk 
                Vulnerability vuln = new Vulnerability();
                vuln.setImport_tool("Nipper");
                vuln.setPortnumber(host.getPortnumber());
                vuln.setProtocol(host.getProtocol());
                vuln.setIs_custom_risk(true); // default to true, if a CVSS vector is hit it will be modified
                
                // set data values
                vuln.setTitle(title);
                vuln.setRisk_category(risk);
                vuln.setIdentifier();
                vuln.setDescription(description + "\n=== IMPACT ===\n" + impact + "\n\n=== EASE ===\n" + ease);
                vuln.setRecommendation(recommendation);
                
                // add affected host
                vuln.addAffectedHost(host);
                root.add(new DefaultMutableTreeNode(vuln));
            }

        } catch (Exception ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*
        // Setup the host object. SureCheck XML has one target per file


        // Create a vulnerability for this finding.

         */

 /*

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
                            String description = "";
                            String recommendation = "";
                            //String notes = "" ; // Surecheck doesn't have any notes

                            // Lets wrench the appropriate bits from the bossom of this awful XML.
                            String start_description = "== Description ==\\r\\n";
                            String end_description = "\\r\\n\\r\\n== Recommendation ==\\r\\n";
                            description = nodeValue.substring(nodeValue.indexOf(start_description) + start_description.length(), nodeValue.indexOf(end_description));
                            description = description.replaceAll("\\\\r\\\\n", "\n"); // Replacing shady SureCheck formatting.

                            String start_recommendation = end_description;
                            recommendation = nodeValue.substring(nodeValue.indexOf(start_recommendation) + start_recommendation.length());
                            recommendation = recommendation.replaceAll("\\\\r\\\\n", "\n"); // Replacing shady SureCheck formatting.

                            // Set them into the vulnerability
                            vuln.setDescription(description);
                            vuln.setRecommendation(recommendation);
                            //cloned.setNotes(new Note(notes));
                        }
                    }

                }
         */
        // Add the clone to the affected hosts.
        //vuln.addAffectedHost(cloned);
        // Add this vuln to the tree
        //  root.add(new DefaultMutableTreeNode(vuln));
        // }
        return root;
    }

}
