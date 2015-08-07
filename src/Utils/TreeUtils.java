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
package Utils;

import Models.Host;
import Models.Vulnerability;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author pritchie
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
     * @returnt
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

        //DefaultMutableTreeNode newdatastructure = this.getMergeTreeDataStructure(existing_root) ;
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
                /*
                 if (oldvuln.containsIdentifier(personalvuln.getIdentifiers()) == true) {
                 // We have a merge operation to do. Remove the oldvuln from the tree
                 existing_vulns.remove(oldvuln) ; // Delete it.
                    
                 if(vulns_to_merge.containsKey(personalvuln.getTitle())) {
                 // It already exists so we need to add old vuln to the map
                 Vector vulns = (Vector)vulns_to_merge.remove(personalvuln.getTitle()) ; // remove it
                 vulns.add(oldvuln); // Add oldvuln to the vector
                 vulns_to_merge.put(personalvuln.getTitle(), vulns) ; // put it back in the HashMap
                 System.out.println("== Found additional vuln to merge added that:\n\t" + vulns) ;
                        
                 } else {
                 // It doesn't exist so we add both the old vuln and the personal vuln
                 Vector vulns = new Vector() ; // Create new vector
                 vulns.add(personalvuln) ; // add the two vulns
                 vulns.add(oldvuln);
                 vulns_to_merge.put(personalvuln.getTitle(), vulns) ; // put it in the HashMap.
                 System.out.println("== Found a vuln to merge, added it to the list:\n\t" + vulns) ;
                 }
                 }
                 */
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
            //System.out.println("Personal: " + personal) ;
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
    /*

     HashMap new_tree = getTreeAsHashMap(new_root);

     Iterator it = new_tree.keySet().iterator();
     while (it.hasNext()) {
     Object key = it.next();
     String key_str = (String) key;

     if (existing_tree.containsKey(key_str) == false) {
     // This vuln is news to the existing tree so add the vuln to the hashmap
     Vulnerability vuln = (Vulnerability) new_tree.get(key);
     existing_tree.put(key_str, vuln);
     } else {
     // This vuln already existed so we need to merge the affected hosts

     // remove the vuln from existing tree because we need to update it and put it back
     Vulnerability existing = (Vulnerability) existing_tree.remove(key_str);
     // use new_tree.get to prevent a java.util.ConcurrentModificationException 
     Vulnerability newvuln = (Vulnerability) new_tree.get(key_str);

     // add the affected hosts from new vuln to existing
     existing.addAffectedHosts(newvuln.getAffectedHosts());

     // add existing back to existing_tree
     existing_tree.put(key, existing);
     }

     }

     // Convert HashMap back to DefaultMutableTreeNode
     DefaultMutableTreeNode answer = convertHashMapToTree(existing_tree);
     // sort the answer and return it
     return this.sortVulns(answer);
     //return new_root;
     }
     */

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
     * hostname
     *
     * @param root
     * @param previous
     * @param modified
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

    public DefaultMutableTreeNode convertVulnViewToHostView(DefaultMutableTreeNode vulnRoot) {
        DefaultMutableTreeNode hostRoot = new DefaultMutableTreeNode("hosts");
        // id = ip; value=vector of Vulnerabilities
        HashMap hosts = new HashMap();
        // Vector subnets
        Vector subnets = new Vector() ;
        
        // Convert vulns to hash map of hosts with issues
        Enumeration enums = vulnRoot.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                
                Vector affected = vuln.getAffectedHosts();
                Enumeration aff_enums = affected.elements();
                while (aff_enums.hasMoreElements()) {
                    Host hst = (Host) aff_enums.nextElement();
                    if(subnets.contains(hst.getSubnet())==false) {
                        System.out.println("Added Subnet: " + hst.getSubnet()) ;
                        subnets.add(hst.getSubnet()) ;
                    }
                    
                    if (hosts.containsKey(hst.getIp_address())) {
                        // already contains the host so get the vector of vulns and then add it
                        Vector vulns = (Vector) hosts.remove(hst.getIp_address());
                        vulns.add(vuln);
                        // put it back in then!
                        hosts.put(hst.getIp_address(), vulns);
                    } else {
                        // host doesn't exist so add it
                        Vector vulns = new Vector();
                        vulns.add(vuln);
                        hosts.put(hst.getIp_address(), vulns);
                    }
                }
            }
        }
        
        // sort the subnets
        //InetAddressComparator comparator = new InetAddressComparator() ;
        Collections.sort(subnets);
        //subnets.sort(ipComparator);
        
        // Put the subnets into the tree.
        Enumeration subnets_enum = subnets.elements() ;
        while(subnets_enum.hasMoreElements()) {
            String subnet = (String)subnets_enum.nextElement() ;
            DefaultMutableTreeNode subnet_node = new DefaultMutableTreeNode(subnet);
            hostRoot.add(subnet_node);
        }
        
        
//        // Convert HashMap to tree for the answer
//        Iterator it = hosts.keySet().iterator() ;
//        while(it.hasNext()) {
//            String id = (String)it.next();
//            Vector vulns = (Vector)hosts.get(id);
//            DefaultMutableTreeNode hostNode = new DefaultMutableTreeNode(id) ;
//            enums = vulns.elements() ; // reusing variable name since we are done with the previous values
//            while(enums.hasMoreElements()) {
//                Vulnerability vuln = (Vulnerability)enums.nextElement();
//                DefaultMutableTreeNode vulnNode = new DefaultMutableTreeNode(vuln) ;
//                hostNode.add(vulnNode);
//            }
//            hostRoot.add(hostNode);
//        }

        return hostRoot;
    }
    
    public HashMap getAllAffectedHosts(DefaultMutableTreeNode vulnTreeRoot) {
        
        // key=port:protocol, value=vector(hosts)
        HashMap hosts = new HashMap() ;
        Enumeration enums = vulnTreeRoot.children() ;
        while(enums.hasMoreElements()) {
            Object obj = enums.nextElement() ;
            if(obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability)obj;
                Enumeration hosts_enum = vuln.getAffectedHosts().elements() ;
                while(hosts_enum.hasMoreElements()){
                    Object obj2 = hosts_enum.nextElement() ;
                    if(obj2 instanceof Host) {
                        Host host = (Host)obj2 ;
                        String service = host.getPortnumber() + "/" + host.getProtocol() ;
                        if(hosts.containsKey(service)) {
                            // Already exists, remove the vector
                            Vector vec = (Vector)hosts.remove(service) ;
                            vec.add(host) ;
                            hosts.put(service, vec);
                        } else {
                            // Doesn't exist, just fire away matey.
                            hosts.put(service, new Vector().add(host));
                        }
                        
                    }
                }
            }
        }
        return hosts ;
    }
    
}
