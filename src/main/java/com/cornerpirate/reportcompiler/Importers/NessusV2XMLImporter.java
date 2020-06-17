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
import com.cornerpirate.reportcompiler.Models.Reference;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import com.cornerpirate.reportcompiler.Utils.Helper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author cornerpirate
 */
public class NessusV2XMLImporter implements ImporterInterface {

    private InputStream fXmlFile;

    /**
     * Pass this a File object and it will return true if it is a valid example
     * of this type of file false otherwise.
     *
     * @param file
     * @return
     */
    @Override
    public boolean isValid(File file) {

        try {
            String valid_string = "<NessusClientData_v2>";
            boolean valid = new Helper().fileContainsString(file, valid_string);
            return valid;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * This determines if the file is valid using isValid and then returns a
     * representation of the file as a DefaultMutableTreeNode. This will be null
     * if it is not a valid file.
     *
     * @param file
     * @return
     */
    @Override
    public DefaultMutableTreeNode readFile(File importFile) {
        System.out.println("==NessusV2XMLImporter=readFile: " + importFile.getAbsolutePath());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");

        // <ReportHost> - tag containing details of host. Sub tags <ReportItem> has vulnerabilities.
        // Get the document
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(importFile);
            doc.normalize();

            NodeList hosts_node_list = doc.getElementsByTagName("ReportHost");
            NodeList report_issues_list = doc.getElementsByTagName("ReportItem");
            System.out.println("# of vulns in report: " + report_issues_list.getLength());
            System.out.println("# of hosts in report:" + hosts_node_list.getLength());

            //Vector hosts = getHosts(hosts_node_list);
            Vector vulns = getVulns(hosts_node_list);

            // Quick convert to DefaultMutableTreeNodes
            Enumeration enums = vulns.elements();
            while (enums.hasMoreElements()) {
                Vulnerability vuln = (Vulnerability) enums.nextElement();
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

    private Vector getHosts(NodeList hosts_node_list) {
        Vector hosts = new Vector();

        for (int i = 0; i < hosts_node_list.getLength(); i++) {
            Node reportHostNode = hosts_node_list.item(i);
            // Lets work out the hostname and the IP address of this badboy
            Host host = getHost(reportHostNode);
        }

        return hosts;
    }

    private Host getHost(Node reportHostNode) {
        Host host = new Host();

        // reportHostNode is the <ReportHost> tag. The "<HostProperties>" Tag is the first child we hope.
        String possible_ip = reportHostNode.getAttributes().getNamedItem("name").getTextContent();
        // Default to this it will be over written if "host-ip" occurs later
        // If that tag does not occur then at least the user gets an IP.
        host.setIp_address(possible_ip);

        Node hostPropertiesNode = reportHostNode.getFirstChild();
        NodeList children = hostPropertiesNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String tag_name = child.getAttributes().getNamedItem("name").getTextContent();
                String tag_value = child.getTextContent();

                if (tag_name.equalsIgnoreCase("host-ip")) {
                    host.setIp_address(tag_value);
                } else if (tag_name.equalsIgnoreCase("host-fqdn")) {
                    host.setHostname(tag_value);
                } else if (tag_name.equalsIgnoreCase("operating-system")) {
                    host.setOperating_system(tag_value);
                } else if (tag_name.equalsIgnoreCase("netbios-name")) {
                    host.setNetbios_name(tag_value);
                } else if (tag_name.equalsIgnoreCase("mac-address")) {
                    host.setMac_address(tag_value);
                }

            }
        }
        return host;
    }

    private Vector getVulns(NodeList hosts_node_list) {
        Vector vulns = new Vector();
        // Key=Plugin ID, Value=Vulnerability Object
        HashMap vulnsMap = new HashMap();

        for (int i = 0; i < hosts_node_list.getLength(); i++) {
            Node reportHostNode = hosts_node_list.item(i);
            // Lets work out the hostname and the IP address of this badboy
            Host host = getHost(reportHostNode); // This host object doesn't have a vulnerable port/protocol data.

            Vector vulns_for_host = getVulnsForHost(host, reportHostNode);
            //System.out.println("# of vulns on host: " + vulns_for_host.size());
            Enumeration enums = vulns_for_host.elements();
            while (enums.hasMoreElements()) {
                Vulnerability vuln = (Vulnerability) enums.nextElement();
                Host copy = host.clone(); // take a clone so we can set port/protocol data to a clean object
                copy.setPortnumber(vuln.getPortnumber());
                copy.setProtocol(vuln.getProtocol());
                Note note = new Note(vuln.getNotes());
                copy.setNotes(note);

                if (vulnsMap.containsKey(vuln.getImport_tool_id()) == false) {
                    // just add the vuln to the hash map
                    vuln.addAffectedHost(copy);
                    vulnsMap.put(vuln.getImport_tool_id(), vuln);
                } else {
                    // take the existing one out and then put the affected host in and add it back to the map.
                    Vulnerability existing = (Vulnerability) vulnsMap.remove(vuln.getImport_tool_id());
                    existing.addAffectedHost(copy);
                    vulnsMap.put(existing.getImport_tool_id(), existing);
                }
            }
        }

        Iterator it = vulnsMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Vulnerability vuln = (Vulnerability) vulnsMap.get(key);
            vulns.add(vuln);
        }

        return vulns;
    }

    private Vector getVulnsForHost(Host host, Node reportHostNode) {

        Vector vulns = new Vector();
        NodeList children = reportHostNode.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equalsIgnoreCase("ReportItem")) {

                Vulnerability vuln = new Vulnerability();
                vuln.setImport_tool("Nessus");
                // the following info can come from attributes of the "ReportItem" note;
                NamedNodeMap attributes = child.getAttributes();
                vuln.setPortnumber(attributes.getNamedItem("port").getTextContent());
                vuln.setProtocol(attributes.getNamedItem("protocol").getTextContent());
                vuln.setImport_tool_id(attributes.getNamedItem("pluginID").getTextContent());
                vuln.setTitle(attributes.getNamedItem("pluginName").getTextContent());
                String pluginFamily = attributes.getNamedItem("pluginFamily").getTextContent();

                // This identifier will be permanent for the lifetime of the vuln
                vuln.setIdentifier();
                vuln.setIs_custom_risk(true); // default to true, if a CVSS vector is hit it will be modified

                // the rest of the info comes from children of "ReportItem" 
                NodeList reportItemChildren = child.getChildNodes();
                for (int k = 0; k < reportItemChildren.getLength(); k++) {
                    Node kidnode = reportItemChildren.item(k);
                    if (kidnode.getNodeType() == Node.ELEMENT_NODE) {
                        String nodeName = kidnode.getNodeName();
                        String nodeValue = kidnode.getTextContent();

                        if (nodeName.equalsIgnoreCase("description")) {
                            vuln.setDescription(nodeValue);
                        } else if (nodeName.equalsIgnoreCase("plugin_output")) {
                            vuln.setNotes(nodeValue);
                        } else if (nodeName.equalsIgnoreCase("cm:compliance-check-name")) {
                            vuln.setTitle("Compliance: " + nodeValue);
                            vuln.setIs_nessus_compliance_finding(true);

                            if (pluginFamily.equalsIgnoreCase("Policy Compliance")) {
                                vuln.setImport_tool_id(nodeValue);
                            }

                        } else if (nodeName.equalsIgnoreCase("cm:compliance-result")) {
                            vuln.setNessus_compliance_result(nodeValue);
                        } else if (nodeName.equalsIgnoreCase("risk_factor")) {
                            vuln.setRisk_category(nodeValue);
                        } else if (nodeName.equalsIgnoreCase("cvss_vector")) {
                            vuln.setIs_custom_risk(false); // modify it
                            vuln.setCvss_vector_string(nodeValue);
                        } else if (nodeName.equalsIgnoreCase("solution")) {
                            vuln.setRecommendation(nodeValue);
                        } //// These ones are all related to references 
                        else if (nodeName.equalsIgnoreCase("see_also")) {
                            if (nodeValue.contains("\n")) {
                                // this one has multiple URLs
                                String[] urls = nodeValue.split("\n");
                                for (String url : urls) {
                                    try {

                                        URL u = new URL(url);
                                        String hostname = u.getHost() + ":";
                                        Reference ref = new Reference(hostname, url);
                                        vuln.addReference(ref);

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else {
                                // this one just has one URL
                                Reference ref = new Reference("See also:", nodeValue);
                                vuln.addReference(ref);
                            }

                        } else if (nodeName.equalsIgnoreCase("cve")) {
                            Reference ref = new Reference(nodeValue + " :", "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + nodeValue);
                            vuln.addReference(ref);
                        } else if (nodeName.equalsIgnoreCase("bid")) {
                            Reference ref = new Reference("Security Focus:", "http://www.securityfocus.com/bid/" + nodeValue);
                        } else if (nodeName.equalsIgnoreCase("xref")) {
                            try {
                                String description = nodeValue.split(":")[0];
                                String url_part = nodeValue.split(":")[1];
                                String url = "UNKNOWN";
                                if (description.equalsIgnoreCase("OSVDB")) {
                                    description = "Open Source Vulnerability Database:";
                                    url = "http://osvdb.org/";
                                } else if (description.equalsIgnoreCase("Secunia")) {
                                    description = "Secunia Database:";
                                    url = "http://secunia.com/community/advisories/";
                                } else if (description.equalsIgnoreCase("CWE")) {
                                    description = "Common Weakness Enumeration Vulnerability Database";
                                    url = "http://cwe.mitre.org/data/definitions/";
                                } else if (description.equalsIgnoreCase("CERT")) {
                                    description = "Cert Vulnerability Database:";
                                    url = "http://www.kb.cert.org/vuls/id/";
                                } else if (description.equalsIgnoreCase("MSFT")) {
                                    description = "Microsoft Security Bulletin:";
                                    url = "https://technet.microsoft.com/en-us/library/security/";
                                } else if (description.equalsIgnoreCase("ICS-ALERT")) {
                                    description = "Industrial Control Systems Cyber Emergencry Response Team Advisory";
                                    url = "http://ics-cert.us-cert.gov/advisories/ICSA-";
                                }

                                if (url.equalsIgnoreCase("UNKNOWN") == false) {
                                    // This was a known external reference so add it
                                    Reference ref = new Reference(description, url + url_part);
                                    vuln.addReference(ref);
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                    }
                }

                if (vuln.isIs_nessus_compliance_finding() == true) {

                    // Set Categories
                    if (vuln.getNessus_compliance_result().equalsIgnoreCase("FAILED")) {
                        vuln.setRisk_category("high");
                    } else if (vuln.getNessus_compliance_result().equalsIgnoreCase("PASSED")) {
                        vuln.setRisk_category("none");
                    } else if (vuln.getNessus_compliance_result().equalsIgnoreCase("ERROR")) {
                        vuln.setRisk_category("info");
                    }
                    
                    // Clean up description
                    String all_text = vuln.getDescription() ;
                    String solution = all_text.substring(all_text.indexOf("Solution : ") + "Solution : ".length());
                    vuln.setRecommendation(solution);
                    

                }

                // We have to drop ones where the plugin was "0" because they don't have any child tags. Maybe useful for port scans.
                if (vuln.getImport_tool_id().equalsIgnoreCase("0") != true) {
                    vulns.addElement(vuln);
                }
            }
        }
        return vulns;
    }

}
