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
package com.cornerpirate.reportcompiler.Models;

import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author cornerpirate
 */
public class Host implements Comparator {

    protected String identifier;
    protected String hostname;
    protected String netbios_name;
    protected String ip_address;
    protected String operating_system;
    protected String mac_address;
    protected Note note = new Note("");
    // A slight fudge in that a "Host" is really a "Service" that has an issue.
    protected String portnumber;
    protected String protocol;
    protected Vector vulnerabilities;

    
    public void addVulnerability(Vulnerability vuln) {
        this.vulnerabilities.add(vuln) ;
    }
    public Vector getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(Vector vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public Note getNotes() {
        return note;
    }

    /**
     * Get a String representing the first three octets with "XXX" as the final.
     * Not the most advanced subnetting but neat and predictable enough.
     * @return String - i.e. 192.168.1.XXX
     */
    public String getSubnet() {
        String i = this.getIp_address();
        i = i.substring(0, i.lastIndexOf(".")+1) + "XXX";
        return i;
    }

    public void setNotes(Note notes) {
        this.note = notes;
    }

    public String getPortnumber() {
        return portnumber;
    }

    public void setPortnumber(String portnumber) {
        this.portnumber = portnumber;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Vector getAsVector() {
        Vector row_data = new Vector();
        row_data.add(this.clone());
        row_data.add(this.getHostname());
        row_data.add(this.getPortnumber());
        row_data.add(this.getProtocol());
        return row_data;
    }

    /**
     * The identifier is; IP+Hostname+Portnumber+Protocol. Basically the service
     * endpoint
     *
     * @return
     */
    public String getIdentifier() {
        return this.getIp_address() + this.getHostname() + this.getPortnumber() + this.getProtocol();
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getHostname() {
        if (this.hostname == null && this.netbios_name == null) {
            return "Unknown";
        } else if (this.hostname == null && this.netbios_name != null) {
            return this.getNetbios_name();
        }
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getNetbios_name() {
        return netbios_name;
    }

    public void setNetbios_name(String netbios_name) {
        this.netbios_name = netbios_name;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getOperating_system() {
        return operating_system;
    }

    public void setOperating_system(String operating_system) {
        this.operating_system = operating_system;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Host == false) {
            // Someone called equals on an object of a different type.. Not equal, not equal at all!
            return false;
        } else if (obj instanceof Host == true) {

            Host compareTo = (Host) obj;
            // IP + Hostname + Portnumber + Protocol
            return this.getIdentifier().equals(compareTo.getIdentifier());

        }
        // Default to false
        return false;
    }

    @Override
    public String toString() {
        //return "Host{" + "hostname=" + this.getHostname()+ ", ip_address=" + this.getIp_address() + '}';
        return this.getIp_address();
    }

    @Override
    public Host clone() {
        Host new_host = new Host();
        //new_host.setIdentifier(this.getIdentifier()); // Not needed it is derived from other values set below
        new_host.setHostname(this.getHostname());
        new_host.setIp_address(this.getIp_address());
        new_host.setMac_address(this.getMac_address());
        new_host.setNetbios_name(this.getNetbios_name());
        new_host.setOperating_system(this.getOperating_system());
        new_host.setPortnumber(this.getPortnumber());
        new_host.setProtocol(this.getProtocol());
        new_host.setNotes(this.getNotes());

        return new_host;
    }

    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private int[] getIPAsByteArray(String ip) {
        int[] ipv4 = new int[4];
        String[] stringarray = ip.split(".");
        for (int i = 0; i < stringarray.length; i++) {
            int b = Integer.parseInt(stringarray[i]);
        }
        return ipv4;
    }

    @Override
    public int compare(Object o1, Object o2) {
        System.out.println(o1.getClass() + ":" + o2.getClass());
        Host h1 = (Host) o1;
        Host h2 = (Host) o2;
        String ip1 = h1.getIp_address();
        String ip2 = h2.getIp_address();
        int[] ba1 = getIPAsByteArray(ip1);//ip1.getBytes();
        int[] ba2 = getIPAsByteArray(ip2);//ip2.getBytes();

        // general ordering: ipv4 before ipv6
        if (ba1.length < ba2.length) {
            return -1;
        }
        if (ba1.length > ba2.length) {
            return 1;
        }

        // we have 2 ips of the same type, so we have to compare each byte
        for (int i = 0; i < ba1.length; i++) {
            int b1 = ba1[i];
            int b2 = ba2[i];
            if (b1 == b2) {
                continue;
            }
            if (b1 < b2) {
                return -1;
            } else {
                return 1;
            }
        }
        return 0;
    }

    public String getHostForExcel() {
        return this.ip_address + " - " + this.getHostname() + " - " + this.getPortnumber() + "/" + this.getProtocol();
    }

}
