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
package com.cornerpirate.reportcompiler.Exporters;

import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.Note;
import com.cornerpirate.reportcompiler.Models.Reference;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author cornerpirate
 */
public class SaveFileExporter {

    public String getNow() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //get current date time with Date()
        Date date = new Date();

        return dateFormat.format(date);
    }

    public Element getVulnAsElement(Vulnerability vuln, Document doc) {

        Base64 b64 = new Base64();

        Element vulnElement = doc.createElement("vuln");
        if (vuln.isIs_custom_risk()==false) {
            vulnElement.setAttribute("custom-risk", "false");
            vulnElement.setAttribute("cvss", vuln.getCvss_vector_string());
        } else {
            vulnElement.setAttribute("custom-risk", "true");
        }
        vulnElement.setAttribute("category", vuln.getRisk_category());
        vulnElement.setAttribute("risk-score", "" + vuln.getRiskScore());

        Element vulnTitle = doc.createElement("title");
        vulnTitle.appendChild(doc.createTextNode(b64.encodeAsString(vuln.getTitle().getBytes())));
        
        Element identifiersElement = doc.createElement("identifiers") ;
        HashMap identifiersMap = vuln.getIdentifiers() ;
        Iterator it = identifiersMap.keySet().iterator() ;
        while(it.hasNext()) {
            String hashed_title = (String)it.next();
            String import_tool = (String)identifiersMap.get(hashed_title) ;
            // add a tag mofo!
            Element identifier = doc.createElement("identifier") ;
            identifier.setAttribute("hash", hashed_title);
            identifier.setAttribute("import_tool", import_tool);
            identifiersElement.appendChild(identifier) ;
        }

        Element vulnDescription = doc.createElement("description");
        vulnDescription.appendChild(doc.createTextNode(b64.encodeAsString(vuln.getDescription().getBytes())));

        Element vulnRecommendation = doc.createElement("recommendation");
        vulnRecommendation.appendChild(doc.createTextNode(b64.encodeAsString(vuln.getRecommendation().getBytes())));

        //TODO Fill out <References> tag
        Element vulnReferences = doc.createElement("references") ;
        Enumeration refs_enums = vuln.getReferences().elements() ;
        while(refs_enums.hasMoreElements()) {
            Reference ref = (Reference)refs_enums.nextElement() ;
            Element vulnReference = doc.createElement("reference") ;
            vulnReference.setAttribute("description", ref.getDescription()); 
            vulnReference.appendChild(doc.createTextNode(ref.getUrl())) ;
            vulnReferences.appendChild(vulnReference);
        }
        
        Element affectedHosts = doc.createElement("affected-hosts");
        Enumeration enums = vuln.getAffectedHosts().elements();
        while (enums.hasMoreElements()) {
            Host host = (Host) enums.nextElement();

            // Create all the lovely element nodes
            Element affectedHost = doc.createElement("host");
            Element ipAddress = doc.createElement("ip-address");
            if (host.getIp_address() != null) {
                ipAddress.appendChild(doc.createTextNode(host.getIp_address()));
            }
            Element hostname = doc.createElement("hostname");
            if (host.getHostname() != null) {
                hostname.appendChild(doc.createTextNode(host.getHostname()));
            }
            Element netbios = doc.createElement("netbios-name");
            if (host.getNetbios_name() != null) {
                netbios.appendChild(doc.createTextNode(host.getNetbios_name()));
            }
            Element os = doc.createElement("operating-system");
            if (host.getOperating_system() != null) {
                os.appendChild(doc.createTextNode(host.getOperating_system()));
            }
            Element macAddress = doc.createElement("mac-address");
            if (host.getMac_address() != null) {
                macAddress.appendChild(doc.createTextNode(host.getMac_address()));
            }
            Element portnumber = doc.createElement("portnumber");
            if (host.getPortnumber() != null) {
                portnumber.appendChild(doc.createTextNode(host.getPortnumber()));
            }
            Element protocol = doc.createElement("protocol");
            if (host.getProtocol() != null) {
                protocol.appendChild(doc.createTextNode(host.getProtocol()));
            }

            Element note = doc.createElement("note");
            if (host.getNotes() != null) {
                Note n = host.getNotes();
                note.appendChild(doc.createTextNode(b64.encodeAsString(n.getNote_text().getBytes())));
            }

            // Append them to the affected Host node
            affectedHost.appendChild(ipAddress);
            affectedHost.appendChild(hostname);
            affectedHost.appendChild(netbios);
            affectedHost.appendChild(os);
            affectedHost.appendChild(macAddress);
            affectedHost.appendChild(portnumber);
            affectedHost.appendChild(protocol);
            affectedHost.appendChild(note);

            // Add that host to the affected hosts node
            affectedHosts.appendChild(affectedHost);
        }

        vulnElement.appendChild(vulnTitle);
        vulnElement.appendChild(identifiersElement) ;
        vulnElement.appendChild(vulnDescription);
        vulnElement.appendChild(vulnRecommendation);
        vulnElement.appendChild(vulnReferences);
        vulnElement.appendChild(affectedHosts);
        return vulnElement;
    }

    public void save(File outfile, DefaultMutableTreeNode root) throws Exception {
        System.out.println("===SaveFileExporter==save:" + outfile.getAbsolutePath());

        // Create new XML document
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Create the root element
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("reportCompiler");
        rootElement.setAttribute("date", getNow()); // Debugging to say when saved
        // TODO - figure out a universal means to get the current version of the generating tool
        // Initial thoughts would be to create a convenience class in Utils package with some
        // final variables that can be updated when issuing a release.
        doc.appendChild(rootElement);

        Element vulnerabilitiesElement = doc.createElement("vulnerabilities");

        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                Element vulnElement = getVulnAsElement(vuln, doc);
                if (vulnElement != null) {
                    vulnerabilitiesElement.appendChild(vulnElement);
                }
            }
        }

        rootElement.appendChild(vulnerabilitiesElement);

        // now write the XML file
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(outfile);

        // Output to console for testing
        //StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);

        System.out.println("File saved!");

    }
}
