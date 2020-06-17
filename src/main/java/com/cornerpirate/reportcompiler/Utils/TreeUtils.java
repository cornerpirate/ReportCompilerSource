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
package com.cornerpirate.reportcompiler.Utils;

import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class is used wherever significant operations to either the
 * vulnerability tree or host tree are needed.
 *
 * @author cornerpirate
 */
public class TreeUtils {

    public void searchTree(DefaultMutableTreeNode root, String search_term) {
        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                if (vuln.containsText(search_term)) {
                    vuln.setHighlighted(true);
                } else {
                    vuln.setHighlighted(false);
                }
            }
        }
    }

    public void expandAll(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    /**
     * Takes a root node with vulns inside it and then sorts it according to
     * risk.
     *
     * @param root
     * @return
     */
    public DefaultMutableTreeNode sortVulns(DefaultMutableTreeNode root) {

        Vector highlighted_vulns = new Vector();
        Vector vulns = new Vector();
        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Vulnerability vuln = (Vulnerability) node.getUserObject();
            if (vuln.isHighlighted() == true) {
                highlighted_vulns.add(vuln);
            } else {
                vulns.add(vuln);
            }
        }

        // sort the vulns vector
        Comparator reversedComparator = Collections.reverseOrder();
        Collections.sort(vulns, reversedComparator);
        Collections.sort(highlighted_vulns, reversedComparator);

        // now that it is sorted create a new Tree
        DefaultMutableTreeNode new_root = new DefaultMutableTreeNode(root.toString());
        Enumeration enums2 = highlighted_vulns.elements();
        while (enums2.hasMoreElements()) {
            new_root.add(new DefaultMutableTreeNode((Vulnerability) enums2.nextElement()));
        }

        Enumeration enums3 = vulns.elements();
        while (enums3.hasMoreElements()) {
            new_root.add(new DefaultMutableTreeNode((Vulnerability) enums3.nextElement()));
        }

        // return that new tree
        return new_root;
    }

    public HashMap getTreeAsHashMap(DefaultMutableTreeNode root) {
        HashMap answer = new HashMap();
        Enumeration new_root_enums = root.children();

        while (new_root_enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) new_root_enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                answer.put(vuln.getAnIdentifier(), vuln);
            }
        }
        return answer;
    }

    public DefaultMutableTreeNode getMergeTreeDataStructure(DefaultMutableTreeNode root) {
        DefaultMutableTreeNode answer = new DefaultMutableTreeNode("RootNode");
        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Vulnerability vuln = (Vulnerability) node.getUserObject();
            Iterator it = vuln.getIdentifiers().keySet().iterator();
            while (it.hasNext()) {
                String hash = (String) it.next();
                String tool = (String) vuln.getIdentifiers().get(hash);
                node.add(new DefaultMutableTreeNode(hash + ":" + tool));
            }
            answer.add(node);
        }
        return answer;
    }

    public Vector getTreeAsVector(DefaultMutableTreeNode root) {
        Vector answer = new Vector();
        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                answer.add(vuln);
            }
        }
        return answer;
    }

    public DefaultMutableTreeNode mergeTrees(DefaultMutableTreeNode existing_root, DefaultMutableTreeNode new_root) {
        System.out.println("==TreeUtils=mergeTrees 2.0, allowing multiple identifiers");
        DefaultMutableTreeNode answer = new DefaultMutableTreeNode("vulns");
        Vector existing_vulns = getTreeAsVector(existing_root);
        Vector new_vulns = getTreeAsVector(new_root);

        // for each item in the new_root, check if a vuln with the same ID exists in the existing_root
        // If not then add to the existing_root
        // If so then merge the vulns
        Enumeration enums = new_vulns.elements();
        while (enums.hasMoreElements()) {
            Vulnerability newvuln = (Vulnerability) enums.nextElement();
            Enumeration enums2 = existing_vulns.elements();
            boolean addvuln = true;
            while (enums2.hasMoreElements() && addvuln == true) {
                Vulnerability oldvuln = (Vulnerability) enums2.nextElement();
                if (oldvuln.containsIdentifier(newvuln.getIdentifiers()) == true) {
                    // merge affected hosts
                    oldvuln.addAffectedHosts(newvuln.getAffectedHosts());
                    System.out.println("Added hosts: " + oldvuln);
                    addvuln = false;

                }
            }
            // if you get here and addvuln==true, then we add it
            if (addvuln == true) {
                // add to answer
                answer.add(new DefaultMutableTreeNode(newvuln));
                System.out.println("Added newvuln to answer: " + newvuln);
            }
        }

        // put the existing vulns back into the answer
        enums = existing_vulns.elements();
        while (enums.hasMoreElements()) {
            Vulnerability vuln = (Vulnerability) enums.nextElement();
            answer.add(new DefaultMutableTreeNode(vuln));
            System.out.println("Added back existing vuln: " + vuln);
        }
        // sort by risk and return them
        return this.sortVulns(answer);
    }

    /**
     * A routine to clash users current tree against their personal vuln
     * database. When hashes are matched the version from users db will replace
     * the tool import.
     *
     * @param existing_root
     * @param personal_vulns_root
     * @return DefaultMutableTreeNode = root node of new version of the tree
     */
    public DefaultMutableTreeNode autoMergePersonalVulns(DefaultMutableTreeNode existing_root, DefaultMutableTreeNode personal_vulns_root) {
        System.out.println("==TreeUtils=autoMergePersonalVulns");

        DefaultMutableTreeNode answer = new DefaultMutableTreeNode("vulns");
        Vector existing_vulns = getTreeAsVector(existing_root);
        Vector new_vulns = getTreeAsVector(personal_vulns_root);
        Vector to_delete = new Vector();

        // ID = personal vuln id, value = Vector of vulns to merge
        HashMap vulns_to_merge = new HashMap();

        //DefaultMutableTreeNode newdatastructure = this.getMergeTreeDataStructure(existing_root) ;
        // for each item in the new_root, check if a vuln with the same ID exists in the existing_root
        // If not then add to the existing_root
        // If so then merge the vulns
        Enumeration enums = new_vulns.elements();
        while (enums.hasMoreElements()) {
            Vulnerability personalvuln = (Vulnerability) enums.nextElement();
            Enumeration enums2 = existing_vulns.elements();

            while (enums2.hasMoreElements()) {
                Vulnerability oldvuln = (Vulnerability) enums2.nextElement();

                if (oldvuln.containsIdentifier(personalvuln.getIdentifiers()) == true) {
                    //System.out.println("personalvuln:" + personalvuln);
                    //System.out.println("\toldvuln:" + oldvuln);
                    to_delete.add(oldvuln);

                    if (vulns_to_merge.containsKey(personalvuln.getTitle())) {
                        // We need to add this oldvuln to the vector
                        Vector vulns = (Vector) vulns_to_merge.get(personalvuln.getTitle());
                        vulns.add(oldvuln);
                        System.out.println("Merged by adding: " + vulns);

                    } else {
                        // we need to create the vector
                        Vector vulns = new Vector();
                        vulns.add(personalvuln);
                        vulns.add(oldvuln);
                        vulns_to_merge.put(personalvuln.getTitle(), vulns);
                        System.out.println("Merged by inserting: " + vulns);
                    }

                    //existing_vulns.remove(oldvuln);
                }
            }
        }

        // Merge the ones that need merging
        Iterator it = vulns_to_merge.keySet().iterator();
        //int count = 1 ;
        while (it.hasNext()) {
            String key = (String) it.next();

            Vector vulns = (Vector) vulns_to_merge.get(key);
            System.out.println("\tKey: " + key);
            System.out.println("\t" + vulns);
            Vulnerability personal = (Vulnerability) vulns.get(0);
            // For all other vulns in this merge operation we need to get the affected hosts
            for (int i = 1; i <= vulns.size() - 1; i++) {
                Vulnerability avuln = (Vulnerability) vulns.get(i);
                personal.addAffectedHosts(avuln.getAffectedHosts()); // Merge the hosts
            }
            // Having merged the affected hosts we need to put personal into the answer tree
            answer.add(new DefaultMutableTreeNode(personal));
            System.out.println("Merged: " + personal);
        }

        // Remove to_delete from existing_vulns
        existing_vulns.removeAll(to_delete);

        // put the existing vulns back into the answer
        enums = existing_vulns.elements();
        while (enums.hasMoreElements()) {
            Vulnerability vuln = (Vulnerability) enums.nextElement();
            answer.add(new DefaultMutableTreeNode(vuln));
            //System.out.println("Added back existing vuln: " + vuln);
        }
        // sort by risk and return them
        return this.sortVulns(answer);
    }

    public DefaultMutableTreeNode convertHashMapToTree(HashMap map) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Vulnerability vuln = (Vulnerability) map.get(key);
            root.add(new DefaultMutableTreeNode(vuln));
        }
        return root;
    }

    /**
     * Removes the red highlighting from every node in the tree. Highlighting is
     * added when a node matches a search done using the tree grep feature. To
     * call this routine the user has to remove the filter completely.
     *
     * @param root
     */
    public void clearHighlighting(DefaultMutableTreeNode root) {
        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                vuln.setHighlighted(false);
            }
        }
    }

    /**
     * Loops through the children in the tree and modifies all instances of the
     * hostname.
     *
     * @param root - tree to search
     * @param previous - hostname before change
     * @param modified - version to change to
     */
    public void modifyHostname(DefaultMutableTreeNode root, Host previous, Host modified) {
        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                vuln.modifyAffectedHost(previous, modified);
            }

        }
    }

    /**
     * Clashes the vulnerability IDs stored within vulnerability one and two. If
     * any ids match this returns true.
     *
     * @param one
     * @param two
     * @return
     */
    public boolean vulnerabilityContainsAnyIds(Vulnerability one, Vulnerability two) {
        HashMap hm1 = one.getIdentifiers();
        HashMap hm2 = two.getIdentifiers();
        Iterator it1 = hm1.keySet().iterator();
        while (it1.hasNext()) {
            String key1 = (String) it1.next();
            if (hm2.containsKey(key1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This checks if it is OK to add a vulnerability to the personal vulns
     * file. Specifically it is checking if a vuln in your database exists
     * already with a vulnerability identifier. They need to be unique in your
     * personal vulns.
     *
     * @param personal_vulns_root
     * @param vuln
     * @return true = Safe to go on, false = nope, nope, nope!
     */
    public boolean OkToaddToPersonalVulns(DefaultMutableTreeNode personal_vulns_root, Vulnerability vuln) {

        Enumeration enums = personal_vulns_root.children();
        while (enums.hasMoreElements()) {
            Object obj = enums.nextElement();
            if (obj instanceof Vulnerability) {
                Vulnerability current = (Vulnerability) obj;
                // Check if the current vuln has the same title as vuln or if it includes any of the same references
                if (current.getTitle().equalsIgnoreCase(vuln.getTitle())) {
                    System.out.println("A vuln with the same title already existed");
                    return false;
                } else if (vulnerabilityContainsAnyIds(current, vuln)) {
                    System.out.println("A vuln with one of the IDs");
                    return false;
                }

            }
        }

        // If you get to the end then it was ok to just add to the tree
        return true;
    }

    /**
     * This creates a copy of the vulnerability tree to display issues by host
     * instead. It returns a a DefaultMutableTreeNode which is the root node of
     * the host view tree.
     *
     * @param vulnRoot
     * @return DefaultMutableTreeNode - root node for hosts view
     */
    public DefaultMutableTreeNode convertVulnViewToHostView(DefaultMutableTreeNode vulnRoot) {
        DefaultMutableTreeNode hostRoot = new DefaultMutableTreeNode("hosts");

        Set<Vulnerability> vulns = new HashSet<>();
        Set<String> hosts = new HashSet<>();

        // Vector subnets
        Set<String> subnets = new TreeSet<>();

        // Loop through vulnerabilities tree and compile a list of subnets.
        Enumeration enums = vulnRoot.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                Vector affected_hosts = vuln.getAffectedHosts();
                Enumeration hosts_enum = affected_hosts.elements();
                while (hosts_enum.hasMoreElements()) {
                    Host host = (Host) hosts_enum.nextElement();
                    subnets.add(host.getSubnet());
                    // Add IP address to list of hosts.
                    hosts.add(host.getIp_address());
                }
            }
        }

        // Create a HashMap of Hosts
        Iterator it = subnets.iterator();
        while (it.hasNext()) {
            String snet = (String) it.next();
            DefaultMutableTreeNode subnet_node = new DefaultMutableTreeNode(snet);

            // loop through vulns and their affected hosts.
            // If affected host is in subnet add it and then all its vulnerabilities.
            Iterator it2 = hosts.iterator();
            while (it2.hasNext()) {
                String ip = (String) it2.next();
                Host tmp = new Host();
                tmp.setIp_address(ip);
                // Check to see if this IP was in this subnet
                if (tmp.getSubnet().equals(snet)) {
                    DefaultMutableTreeNode host_node = new DefaultMutableTreeNode(tmp);

                    Vector vulns_for_host = getVulnsForIP(ip, vulnRoot);
                    System.out.println("vulns_for_host: " + vulns_for_host.size());
                    Enumeration vulns_enum = vulns_for_host.elements();
                    while (vulns_enum.hasMoreElements()) {
                        Vulnerability vuln = (Vulnerability) vulns_enum.nextElement();
                        DefaultMutableTreeNode vuln_node = new DefaultMutableTreeNode(vuln);
                        host_node.add(vuln_node);
                    }
                    // Get all vulnerabilities for this host
                    // Add host_node to subnet_node
                    subnet_node.add(host_node);
                }

            }

            // Add to hosts tree
            hostRoot.add(subnet_node);
        }

        return hostRoot;
    }

    /**
     * Take a vuln tree and an IP and find Vulnerabilities affecting that host
     *
     * @param ip
     * @param vulnTreeRoot
     * @return
     */
    public Vector getVulnsForIP(String ip, DefaultMutableTreeNode vulnRoot) {
        Vector answers = new Vector();

        // Loop through vulnerabilities tree and compile a list of subnets.
        Enumeration enums = vulnRoot.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                Vector affected_hosts = vuln.getAffectedHosts();
                Enumeration hosts_enum = affected_hosts.elements();
                while (hosts_enum.hasMoreElements()) {
                    Host host = (Host) hosts_enum.nextElement();
                    // Check if this affected host is in the list
                    if (ip.equalsIgnoreCase(host.getIp_address())) {
                        //System.out.println(ip + ":" + vuln.toString());
                        answers.add(vuln);
                    }
                }
            }
        }

        return answers;
    }

    public HashMap getAllAffectedHosts(DefaultMutableTreeNode vulnTreeRoot) {

        // key=port:protocol, value=vector(hosts)
        HashMap hosts = new HashMap();
        Enumeration enums = vulnTreeRoot.children();
        while (enums.hasMoreElements()) {
            Object obj = enums.nextElement();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                Enumeration hosts_enum = vuln.getAffectedHosts().elements();
                while (hosts_enum.hasMoreElements()) {
                    Object obj2 = hosts_enum.nextElement();
                    if (obj2 instanceof Host) {
                        Host host = (Host) obj2;
                        String service = host.getPortnumber() + "/" + host.getProtocol();
                        if (hosts.containsKey(service)) {
                            // Already exists, remove the vector
                            Vector vec = (Vector) hosts.remove(service);
                            vec.add(host);
                            hosts.put(service, vec);
                        } else {
                            // Doesn't exist, just fire away matey.
                            hosts.put(service, new Vector().add(host));
                        }

                    }
                }
            }
        }
        return hosts;
    }

}
