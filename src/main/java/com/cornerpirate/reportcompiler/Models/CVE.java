/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cornerpirate.reportcompiler.Models;

/**
 * This object models a 'CVE' vulnerability. It is a POJO which
 * is literally only in existence to enable sorting of a list of 
 * CVEs by risk score. It was too tricky without this but now given
 * the Comparable interface and "compareTo" we have a useful tool.
 * @author cornerpirate
 */
public class CVE implements Comparable {
    private String cveId;
    private String riskScore;
    private String summary;

    public String getCveId() {
        return cveId;
    }

    public void setCveId(String cveId) {
        this.cveId = cveId;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * A useful string representation of the CVE for quick debugging.
     * Is not used in anger by the system but you might see it hitting
     * your STDOUT as that is where I log things.
     * @return String - format; cveId + "," + riskScore + "," + summary
     */
    @Override
    public String toString() {
        return cveId + "," + riskScore + "," + summary ;
    }
    
    /**
     * Reformats this CVE as a String array called when writing to a CSV file
     * @return String[] formatted; 0=CVE-ID, 1=RiskScore, 2=Summary
     */
    public String[] toStringArray() {
        String[] answer = { this.getCveId(), this.getRiskScore(), this.getSummary() } ;
        return answer ;
    }

    /**
     * This is called when sorting a list of CVEs. It orders by the Risk Score only.
     * If you want to then order by the CVE-ID because of your OCD you can do that in excel
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Object o) {
        CVE other = (CVE)o;
        
        if(other.getRiskScore().equalsIgnoreCase("UNKNOWN") || this.getRiskScore().equalsIgnoreCase("UNKNOWN")) {
            return -1;
        } else {
            if (Double.parseDouble(this.getRiskScore()) < Double.parseDouble(other.getRiskScore())) return -1 ;
            if (Double.parseDouble(this.getRiskScore()) > Double.parseDouble(other.getRiskScore())) return 1 ;
        }
        
        return 0;    }
    
}
