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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author cornerpirate
 */
public class ImportReportCompiler implements ImporterInterface {

    @Override
    public boolean isValid(File file) {

        try {
            String valid_string = "<reportCompiler";
            boolean valid = new Helper().fileContainsString(file, valid_string);
            return valid;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NessusV2XMLImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public DefaultMutableTreeNode readFile(File importFile) {
        System.out.println("==ImportReportCompiler=readFile");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(importFile);
            doc.normalize();

            NodeList vulns_list = doc.getElementsByTagName("vuln");
            for (int i = 0; i < vulns_list.getLength(); i++) {
                Node vuln_node = vulns_list.item(i);
                Vulnerability vuln = getVuln(vuln_node);
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

    private Vulnerability getVuln(Node vuln_node) {
        Base64 b64 = new Base64();
        Vulnerability vuln = new Vulnerability();

        String custom = "false";
        String category = "";
        String cvss_string = "";

        try {
            custom = vuln_node.getAttributes().getNamedItem("custom-risk").getTextContent();
        } catch (NullPointerException e) {;
        }
        try {
            category = vuln_node.getAttributes().getNamedItem("category").getTextContent();
        } catch (NullPointerException e) {;
        }
        try {
            cvss_string = vuln_node.getAttributes().getNamedItem("cvss").getTextContent();
        } catch (NullPointerException e) {;
        }

        // TODO read identifiers
        //vuln.setIdentifier(vuln_node.getAttributes().getNamedItem("id").getTextContent());
        vuln.setIs_custom_risk(custom.equalsIgnoreCase("true"));
        vuln.setRisk_category(category);
        vuln.setCvss_vector_string(cvss_string);

        NodeList children = vuln_node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                String value = node.getTextContent();
                //System.out.println(name + ":" + value);

                if (name.equalsIgnoreCase("title")) {
                    vuln.setTitle(new String(b64.decode(value)));
                } else if (name.equalsIgnoreCase("identifiers")) {
                    NodeList identifiers = node.getChildNodes();
                    for (int ii = 0; ii < identifiers.getLength(); ii++) {
                        Node idNode = identifiers.item(ii);
                        if (idNode.getNodeType() == Node.ELEMENT_NODE) {
                            String hash = idNode.getAttributes().getNamedItem("hash").getTextContent();
                            String import_tool = idNode.getAttributes().getNamedItem("import_tool").getTextContent();
                            vuln.setImport_tool(import_tool);
                            // this hash is a legacy broken from the before time
                            if (hash.equalsIgnoreCase("24d459a81449d721c8f9a86c2913034")) {
                                vuln.setIdentifier(); // this replaces the hash with the current MD5(vuln.getTitle());
                            } else {
                                vuln.setIdentifierFromSaveFile(hash, import_tool); // this trusts the source tool and hash.
                            }
                        }
                    }

                } else if (name.equalsIgnoreCase("description")) {
                    vuln.setDescription(new String(b64.decode(value)));
                } else if (name.equalsIgnoreCase("recommendation")) {
                    vuln.setRecommendation(new String(b64.decode(value)));
                } else if (name.equalsIgnoreCase("affected-hosts")) {
                    Vector affected_hosts = getHosts(node);
                    vuln.addAffectedHosts(affected_hosts);
                } else if (name.equalsIgnoreCase("references")) {

                    NodeList references = node.getChildNodes();
                    for (int n = 0; n < references.getLength(); n++) {
                        Node refNode = references.item(n);
                        if (refNode.getNodeType() == Node.ELEMENT_NODE) {
                            String description = refNode.getAttributes().getNamedItem("description").getTextContent();
                            String url = refNode.getTextContent();
                            Reference ref = new Reference(description, url);
                            vuln.addReference(ref);
                        }

                    }

                }

            }
        }

        // for original users with saved personal vulns saved without import tools
        if (vuln.getImport_tool() == "NULL") {
            vuln.setImport_tool("ReportCompiler");
            vuln.setIdentifier();
            System.out.println(vuln.getTitle() + ": hash: " + vuln.getIDsAsString());
        }

        return vuln;
    }

    private Vector getHosts(Node node) {
        Vector hosts = new Vector();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String name = n.getNodeName();
                String value = n.getTextContent();
                if (name.equalsIgnoreCase("host")) {
                    Host host = getHost(n);
                    hosts.addElement(host);
                }
            }
        }
        return hosts;
    }

    private Host getHost(Node n) {
        Host host = new Host();
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                String value = node.getTextContent();

                if (name.equalsIgnoreCase("ip-address")) {
                    host.setIp_address(value);
                } else if (name.equalsIgnoreCase("hostname")) {
                    host.setHostname(value);
                } else if (name.equalsIgnoreCase("netbios-name")) {
                    host.setNetbios_name(value);
                } else if (name.equalsIgnoreCase("operating-system")) {
                    host.setOperating_system(value);
                } else if (name.equalsIgnoreCase("mac-address")) {
                    host.setMac_address(value);
                } else if (name.equalsIgnoreCase("portnumber")) {
                    host.setPortnumber(value);
                } else if (name.equalsIgnoreCase("protocol")) {
                    host.setProtocol(value);
                } else if (name.equalsIgnoreCase("note")) {
                    Note note = new Note(new String(new Base64().decode(value)));
                    host.setNotes(note);
                }
            }
        }
        return host;
    }

}
