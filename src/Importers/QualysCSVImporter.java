/* 
 * Copyright 2015 pritchie.
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
package Importers;

import Models.Host;
import Models.Reference;
import Models.Vulnerability;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author pritchie
 */
public class QualysCSVImporter implements ImporterInterface {

    protected File import_file;

    @Override
    public boolean isValid(File file) {

        this.import_file = file;
        String ext = FilenameUtils.getExtension(file.getAbsolutePath());

        if (ext.equalsIgnoreCase("csv")) {
            // Great so it is a .csv. 
            // if the first cell, A1, saying "Scan Results", it is probably Qualys
            return checkCell("Scan Results");
        }
        return false;
    }

    public boolean checkCell(String value) {

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(this.import_file));
            String[] line1 = reader.readNext();
            System.out.println(line1);
            System.out.println(line1[0].contains(value));
            reader.close();
            return line1[0].contains(value);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }

        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public DefaultMutableTreeNode readFile(File file) {

        System.out.println("==QualysCSVImporter=readFile");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");
        // key=vulnid, value = Vulnerability object

        HashMap vulns = new HashMap();

        CSVReader reader;
        try {

            reader = new CSVReader(new FileReader(file));
            String[] nextLine;
            Iterator it = reader.iterator();
            int count = 1;
            boolean important = false;
            while (it.hasNext()) {
                String[] line = (String[]) it.next();

                if (count == 8) {
                    //System.out.println(line[0]);
                    if (line[0].contains("IP")) {
                        important = true;
                        System.out.println("Important == True");
                    }

                    /*
                     else {
                     JOptionPane.showMessageDialog(null, "Somehow cell A8 does not have 'IP' in it. Most likely NOT a valid Qualys CSV file");
                     System.out.println("Somehow cell A8 does not have 'IP' in it. Most likely NOT a valid Qualys CSV file");
                     return null;
                     }
                     */
                }

                if (important == true && count >= 9) {
                    // We are into the lines of vulns. Convert to a vuln.

                    //System.out.println(line.length);
                    //if (line.length != 25) {
                    //System.out.println(line[0]);
                    //}
                    if (line.length == 25) {

                        String qualys_type = line[7]; // this tells us if it is info, practice or vuln.
                        // Brian Kee asked for "Practice" and "Vuln"

                        if (qualys_type.equals("Vuln") || qualys_type.equals("Practice")) {

                            String vuln_title = line[6]; //

                            String qualys_threat = line[16];
                            String qualys_impact = line[17];

                            String vuln_description = qualys_threat + "\n" + qualys_impact; //
                            String severity = "Low";
                            //String cvss_vector = "CVSS2#" + line[5];
                            String ip_addy = line[0]; //
                            String hostname = line[1]; // this might be improved to pick netbios etc gracefully
                            if (hostname.equals("No registered hostname")) {
                                hostname = "UNKNOWN";
                            }
                            String protocol = line[10]; //
                            if (protocol.length() == 0) {
                                protocol = "tcp";
                            }
                            String port = line[9]; //
                            if (port.length() == 0) {
                                port = "0";
                            }
                            String remediation = line[18]; //

                            //String vendor_ref = line[14] ;
                            //String reference = line[10];
                            // Set it into a vulnerability object
                            Vulnerability vuln = new Vulnerability();
                            vuln.setIs_custom_risk(true);
                            //vuln.setCvss_vector_string(cvss_vector); // cause no CVSS vector                   
                            vuln.setRisk_category(severity.toLowerCase());
                            if(qualys_type.equals("Practice")) {
                                // then the risk should be "low" these are "potential" vulns
                                vuln.setRisk_category("Low");
                            } else if(qualys_type.equals("Vuln")) {
                                String qualys_severity = line[8] ;
                                int sev = Integer.parseInt(qualys_severity) ;
                                if(sev >= 4) {
                                    vuln.setRisk_category("High");
                                } else {
                                    vuln.setRisk_category("Medium");
                                }
                                      
                            }
                            vuln.setImport_tool("Qualys");
                            vuln.setImport_tool_id(vuln_title);
                            // setup an ID for the issue.
                            vuln.setIdentifier();
                            vuln.setTitle(vuln_title);
                            vuln.setDescription(vuln_description);
                            vuln.setRecommendation(remediation);

                            String cve = line[13];
                            if (cve.length() > 0) {
                                // this IS a cve reference.
                                String start = "http://web.nvd.nist.gov/view/vuln/detail?vulnId=";
                                String pattern = "CVE-....-....";
                                // Create a Pattern object
                                Pattern r = Pattern.compile(pattern);
                                // Now create matcher object.
                                Matcher m = r.matcher(cve);
                                while (m.find()) {
                                    String index = m.group() ;
                                    vuln.addReference(new Reference(index, start + index));
                                }

                            }

                            // Create an affected host
                            Host hst = new Host();
                            hst.setIp_address(ip_addy);
                            hst.setHostname(hostname);
                            hst.setPortnumber(port);
                            hst.setProtocol(protocol);

                            vuln.addAffectedHost(hst);

                            if (vulns.containsKey(vuln.getTitle())) {
                                // Remove existing vuln. Add host to it
                                Vulnerability tmp = (Vulnerability) vulns.remove(vuln.getTitle());
                                tmp.addAffectedHost(hst);
                                vulns.put(tmp.getTitle(), tmp);

                            } else {
                                // Just add it into the vuln table
                                vulns.put(vuln.getTitle(), vuln);
                            }

                        } else {
                            System.out.println("This line didn't have 25 columns and is therefore 'fucked'");
                        }
                    } // Else ignore importing the line it is an info

                }

                count++;
            }
            //reader.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Error A");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error B");
            ex.printStackTrace();
        }

        System.out.println("# of vulns: " + vulns.size());

        // Now loop through the vulns hash map and add to the tree
        Iterator it2 = vulns.keySet().iterator();
        while (it2.hasNext()) {
            String key = (String) it2.next();
            Object obj = vulns.get(key);
            if (obj instanceof Vulnerability) {
                root.add(new DefaultMutableTreeNode((Vulnerability) obj));
            }
        }

        return root;
    }

}
