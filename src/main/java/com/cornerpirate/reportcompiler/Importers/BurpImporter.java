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
import com.cornerpirate.reportcompiler.Models.Note;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import com.cornerpirate.reportcompiler.Utils.Helper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class BurpImporter implements ImporterInterface {

    @Override
    public boolean isValid(File file) {

        try {
            String valid_string = "<issues burpVersion=";
            boolean valid = new Helper().fileContainsString(file, valid_string);
            return valid;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public DefaultMutableTreeNode readFile(File importFile) {
        System.out.println("==BurpImporter=readFile");

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(importFile);
            doc.normalize();

            HashMap vulns = new HashMap(); // id=vuln title, value=vuln object

            NodeList vulns_list = doc.getElementsByTagName("issue");
            for (int i = 0; i < vulns_list.getLength(); i++) {
                Node vuln_node = vulns_list.item(i);
                Vulnerability vuln = getVuln(vuln_node);
                if (vulns.containsKey(vuln.getTitle())) {
                    // Take the old one out
                    Vulnerability original = (Vulnerability) vulns.remove(vuln.getTitle());
                    original.addAffectedHost((Host) vuln.getAffectedHosts().elementAt(0));
                    vulns.put(original.getTitle(), original);
                } else {
                    // Just add it to the hashmap
                    vulns.put(vuln.getTitle(), vuln);
                }
            }

            // Now loop through the hashmap and add them to the tree
            Iterator it = vulns.keySet().iterator();
            while (it.hasNext()) {
                String key = "" + it.next();
                Vulnerability vuln = (Vulnerability) vulns.get(key);
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

    public String fixBurpHTMLInStrings(String in) {
        // some places logically need to be new lines.
        String out = in.replaceAll("<br>", "\n");
        out = out.replaceAll("<ul>", "\n");
        out = out.replaceAll("</li>", "\n");
        //String out = in;

        // some tags just need to be removed
        Vector removelist = new Vector();
        removelist.add("</ul>");
        removelist.add("<li>");
        removelist.add("<b>");
        removelist.add("</b>");
        removelist.add("<wbr>");
        Enumeration enums = removelist.elements();
        while (enums.hasMoreElements()) {
            String s = (String) enums.nextElement();
            out = out.replaceAll(s, "");
        }

        return out;
    }

    private Vulnerability getVuln(Node vuln_node) {
        Vulnerability vuln = new Vulnerability();
        vuln.setIs_custom_risk(true); // No CVSS risks here
        vuln.setImport_tool("Burp"); // this is the name of the tool!
        Host host = new Host(); // Each 'Issue' node has one affected host

        /*
         <name>Cross-domain script include</name>
         <host ip="46.20.224.186">http://www.btpensions.net</host>
         <path><![CDATA[/161/newsletters]]></path>
         <location><![CDATA[/161/newsletters]]></location>
         <severity>Information</severity>
         <confidence>Certain</confidence>
         <issueBackground></issueBackground>
         <remediationBackground></remediationBackground>
         <issueDetail></issueDetail>
         <requestresponse>
         */
        NodeList kids = vuln_node.getChildNodes();
        for (int i = 0; i < kids.getLength(); i++) {
            Node node = kids.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String node_name = node.getNodeName();
                String node_value = node.getTextContent();
                if (node_name.equalsIgnoreCase("name")) {
                    vuln.setTitle(node_value);
                    vuln.setIdentifier();
                } else if (node_name.equalsIgnoreCase("host")) {

                    try {

                        String ip = node.getAttributes().getNamedItem("ip").getTextContent();
                        host.setIp_address(ip);
                        URL url = new URL(node_value);
                        host.setHostname(url.getHost());

                        if (url.getPort() != -1) {
                            // If we get here the site has a non default port
                            host.setPortnumber("" + url.getPort());
                        } else {
                            // Otherwise guess 80 for http and 443 for https
                            if(url.getProtocol().equalsIgnoreCase("http")) {
                                host.setPortnumber("80");
                            } else if(url.getProtocol().equalsIgnoreCase("https")) {
                                host.setPortnumber("443");
                            }
                        }
                        host.setProtocol("tcp");

                    } catch (MalformedURLException ex) {
                        ex.printStackTrace(); // should not get here.
                    }

                } else if (node_name.equalsIgnoreCase("path")) {

                    Note note = host.getNotes();
                    host.setNotes(new Note(note.getNote_text() + "\n" + node_value)) ;
                    
                } else if (node_name.equalsIgnoreCase("severity")) {

                    if (node_value.equalsIgnoreCase("Information")) {
                        node_value = "Info";
                    }
                    vuln.setRisk_category(node_value);

                } else if (node_name.equalsIgnoreCase("issueBackground")) {

                    vuln.setDescription(fixBurpHTMLInStrings(node_value));

                } else if (node_name.equalsIgnoreCase("remediationBackground")) {

                    vuln.setRecommendation(fixBurpHTMLInStrings(node_value));

                } else if (node_name.equalsIgnoreCase("issueDetail")) {
                    // these are notes
                    host.setNotes(new Note(fixBurpHTMLInStrings(node_value)));
                }
            }
        }

        vuln.addAffectedHost(host);
        return vuln;
    }

}
