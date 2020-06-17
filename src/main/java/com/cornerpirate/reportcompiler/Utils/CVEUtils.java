/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cornerpirate.reportcompiler.Utils;

import com.cornerpirate.reportcompiler.Models.CVE;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * This class is for interacting with the CVE database contained in the CSV
 * file. The raw file is generated and available in its own github here:
 *
 * https://github.com/cornerpirate/cve-offline
 *
 * The exact file comes from the URL below:
 *
 * https://raw.githubusercontent.com/cornerpirate/cve-offline/master/cve-summary.csv
 *
 * Keep your file update by downloading that and placing it in the "./CVES" directory.
 *
 * @author cornerpirate
 */
public class CVEUtils {

    private final File csvfile = new File("." + File.separator + "cves" + File.separator + "cve-summary.csv");

    //new File(new File((getClass().getProtectionDomain().getCodeSource().getLocation()).getFile())+ File.separator + "cves" + File.separator + "cve-summary.csv"); 
    /**
     * Find the CVE in the CSV file and return the relevant parts
     *
     * @param cveid
     * @return String[] with format { cveid, cvss_risk, summary } - If no cve
     * exits then this returns null
     */
    public String[] getCVE(String cveid) {
        String[] cve = new String[3];

        // get the id from the cveid
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',');
        try {
            CSVParser parser = new CSVParser(new FileReader(csvfile), format);
            for (CSVRecord record : parser) {

                String thiscve = record.get(0);
                if (thiscve.equalsIgnoreCase(cveid)) {
                    // we have found our cve, get all the details and return
                    cve[0] = record.get(0);
                    cve[1] = record.get(1);
                    cve[2] = record.get(2);
                    return cve;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // If we get here then there was no vuln with that ID, return null.
        return null;
    }

    /**
     * Lookup a load of CVEs at once.
     *
     * @param cves
     * @return a vector of String[] with format { cveid, cvss_risk, summary }
     */
    public Vector getCVEs(HashSet cves) {
        Vector answer = new Vector();
        ArrayList al = new ArrayList();

        Iterator it = cves.iterator();
        while (it.hasNext()) {
            String cve = (String) it.next();
            String[] cve_details = getCVE(cve);
            // If it is null then that vuln didn't exist.
            if (cve_details != null) {
                answer.add(cve_details);

                CVE c = new CVE();
                c.setCveId(cve_details[0]);
                c.setRiskScore(cve_details[1]);
                c.setSummary(cve_details[2]);

                al.add(c);

            } else {
                System.out.println("==CVEUtils=getCVEs: No local vuln for " + cve + ", consider updating");
            }
        }

        Collections.sort(al, Collections.reverseOrder());

        Vector actual_answer = new Vector();
        actual_answer.addAll(al);

        return actual_answer;
    }
}
