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

import java.text.DecimalFormat;

/**
 * A class that is used to convert between CVSSv2 vector Strings and their
 * scores etc.
 *
 * @author cornerpirate
 */
public class CVSSVector {

    private String base_vector;
    private String temporal_vector;
    private String environmental_vector;
    private double base_score;
    private double temporal_score;
    private double environmental_score;

    public CVSSVector(String vector) {

        if (vector == null || vector.equals("NULL")) {
            return;
        } else {
            this.base_vector = getBaseVector(vector);
            this.temporal_vector = getTemporalVector(vector);
            this.environmental_vector = getEnvironmentalVector(vector);
        }
    }

    public CVSSVector() {
        String vector = "AV:L/AC:H/Au:M/C:N/I:N/A:N"; // A string that causes a score of 0.0
        this.base_vector = getBaseVector(vector);
        this.temporal_vector = getTemporalVector(vector);
        this.environmental_vector = getEnvironmentalVector(vector);
    }

    public double getRiskScore() {
        if (this.getCVSSVector().equals("") == true) {
            return 0.0;
        }
        double score = (this.getTemporalScore(this.getBaseScore()) != this.getEnvironmentalScore()) ? this.getEnvironmentalScore() : this.getTemporalScore(this.getBaseScore());
        if (score == 0.0) {
            score = Round(this.getBaseScore(), 1);
        }
        return score;
    }

    public String getCVSSVector() {
        String vec = this.base_vector + "/" + getTemporalVector() + "/" + getEnvironmentalVector(); 
        
        
        //System.out.println("TEMP:" + this.temporal_vector) ;
        //System.out.println("ENV:" + this.environmental_vector) ;
        /*
        if (!this.temporal_vector.equals("")) {
            vec = vec + "/" + this.temporal_vector;
        }
        if (!this.environmental_vector.equals("")) {
            vec = vec + "/" + this.environmental_vector;
        }
        */
        return vec;
    }

    public String getBaseVector() {
        return this.base_vector;
    }

    public String getBaseVector(String v) {
        String bv = "";
        if (v.length() != 0) {
            String[] vectorized = v.split("/");
            bv = vectorized[0] + "/" + vectorized[1] + "/" + vectorized[2] + "/" + vectorized[3] + "/" + vectorized[4] + "/" + vectorized[5];
        }
        return bv;
    }

    public String getTemporalVector(String v) {
        String tv = "";
        if (v.length() != 0) {
            if (v.indexOf("/E:") != -1) {
                try {
                    tv = v.substring(v.indexOf("/E:") + 1, v.indexOf("/CDP:"));
                } catch (StringIndexOutOfBoundsException ex) {
                    ;
                }
            }
        }
        return tv;
    }

    public String getEnvironmentalVector(String v) {
        String ev = "";
        if (v.length() != 0) {
            if (v.indexOf("/CDP:") != -1) {
                try {
                    ev = v.substring(v.indexOf("/CDP:") + 1);
                } catch (StringIndexOutOfBoundsException ex) {
                    ;
                }
            }
        }
        return ev;
    }

    public String getTemporalVector() {
        if (this.temporal_vector == null || this.temporal_vector.equals("")) {
            // set the vector to default, all not defined
            this.temporal_vector = "E:ND/RL:ND/RC:ND" ;
        }
        return this.temporal_vector;
    }

    public String getEnvironmentalVector() {
        if (this.environmental_vector == null || this.environmental_vector.equals("")) {
            // set the vector to default, all not defined
            this.environmental_vector = "CDP:ND/TD:ND/CR:ND/IR:ND/AR:ND" ;
        }
        return this.environmental_vector;
    }

    /**
     * Returns the base_vector string formatted as a double
     *
     * @return double Base Score
     */
    public double getBaseScore() {
        String[] vectorized = this.base_vector.split("/");

        double access_vec = 0;
        String av = vectorized[0].split(":")[1];

        /*
         AccessVector     = case AccessVector of
         requires local access: 0.395
         adjacent network accessible: 0.646
         network accessible: 1.0
         */
        if (av.equals("L")) {
            access_vec = 0.395;
        }
        if (av.equals("A")) {
            access_vec = 0.646;
        }
        if (av.equals("N")) {
            access_vec = 1.0;
        }

        double access_com = 0;
        String ac = vectorized[1].split(":")[1];

        /*
         AccessComplexity = case AccessComplexity of
         high: 0.35
         medium: 0.61
         low: 0.71
         */
        if (ac.equals("H")) {
            access_com = 0.35;
        }
        if (ac.equals("M")) {
            access_com = 0.61;
        }
        if (ac.equals("L")) {
            access_com = 0.71;
        }

        double authentication = 0;
        String au = vectorized[2].split(":")[1];

        /*
         Authentication   = case Authentication of
         requires multiple instances of authentication: 0.45
         requires single instance of authentication: 0.56
         requires no authentication: 0.704
         */
        if (au.equals("M")) {
            authentication = 0.45;
        }
        if (au.equals("S")) {
            authentication = 0.56;
        }
        if (au.equals("N")) {
            authentication = 0.704;
        }

        double conf_impact = 0;
        String ci = vectorized[3].split(":")[1];

        /*
         ConfImpact       = case ConfidentialityImpact of
         none:             0.0
         partial:          0.275
         complete:         0.660
         */
        if (ci.equals("N")) {
            conf_impact = 0.0;
        }
        if (ci.equals("P")) {
            conf_impact = 0.275;
        }
        if (ci.equals("C")) {
            conf_impact = 0.660;
        }

        double integ_impact = 0;
        String ii = vectorized[4].split(":")[1];
        /*
         IntegImpact      = case IntegrityImpact of
         none:             0.0
         partial:          0.275
         complete:         0.660
         */
        if (ii.equals("N")) {
            integ_impact = 0.0;
        }
        if (ii.equals("P")) {
            integ_impact = 0.275;
        }
        if (ii.equals("C")) {
            integ_impact = 0.660;
        }

        double avail_impact = 0;
        String ai = vectorized[5].split(":")[1];

        /*
         AvailImpact      = case AvailabilityImpact of
         none:             0.0
         partial:          0.275
         complete:         0.660
         */
        if (ai.equals("N")) {
            avail_impact = 0.0;
        }
        if (ai.equals("P")) {
            avail_impact = 0.275;
        }
        if (ai.equals("C")) {
            avail_impact = 0.660;
        }

        double Impact_Score = 10.41 * (1 - (1 - conf_impact) * (1 - integ_impact) * (1 - avail_impact));
        double Exploitability_Score = 20 * access_vec * access_com * authentication;
        double is = (Impact_Score == 0) ? 0 : 1.176;
        double Base_Score = (((0.6 * Impact_Score) + (0.4 * Exploitability_Score) - 1.5) * is);
        Base_Score = (Base_Score == -0.0) ? 0.0 : Base_Score;
        return Base_Score;
    }

    /**
     * Returns the temporal_vector formatted as a double
     *
     * @return double Temporal Score
     */
    public double getTemporalScore(double bs) {
        double TemporalScore = 0;

        if (!this.getTemporalVector().equals("")) {

            String[] vectorized = this.getTemporalVector().split("/");
            double Exploitability = 0.0;
            String exp = vectorized[0].split(":")[1];
            if (exp.equals("U")) {
                Exploitability = 0.85;
            }
            if (exp.equals("P")) {
                Exploitability = 0.9;
            }
            if (exp.equals("F")) {
                Exploitability = 0.95;
            }
            if (exp.equals("H")) {
                Exploitability = 1.00;
            }
            if (exp.equals("ND")) {
                Exploitability = 1.00;
            }

            double RemediationLevel = 0.0;
            String rl = vectorized[1].split(":")[1];
            if (rl.equals("O")) {
                RemediationLevel = 0.87;
            }
            if (rl.equals("T")) {
                RemediationLevel = 0.90;
            }
            if (rl.equals("W")) {
                RemediationLevel = 0.95;
            }
            if (rl.equals("U")) {
                RemediationLevel = 1.00;
            }
            if (rl.equals("ND")) {
                RemediationLevel = 1.00;
            }

            double ReportConfidence = 0.0;
            String rc = vectorized[2].split(":")[1];
            if (rc.equals("UC")) {
                ReportConfidence = 0.90;
            }
            if (rc.equals("UR")) {
                ReportConfidence = 0.95;
            }
            if (rc.equals("C")) {
                ReportConfidence = 1.00;
            }
            if (rc.equals("ND")) {
                ReportConfidence = 1.00;
            }

            double answer = 0.0;

            /*
             TemporalScore = round_to_1_decimal(BaseScore*Exploitability
             RemediationLevel*ReportConfidence)
             */
            double Base_Score = bs;
            TemporalScore = Base_Score * Exploitability * RemediationLevel * ReportConfidence;
        }
        return TemporalScore;
    }

    /**
     * Returns the environmental_vector formatted as a
     *
     * @return
     */
    public double getEnvironmentalScore() {
        double EnvironmentalScore = 0.0;
        if (!this.getEnvironmentalVector().equals("")) {

            String[] basescorevectorarray = this.getBaseVector().split("/");
            String[] environmentalvectorarray = this.getEnvironmentalVector().split("/");

            double access_vec = 0;
            String av = basescorevectorarray[0].split(":")[1];


            /*
             AccessVector     = case AccessVector of
             requires local access: 0.395
             adjacent network accessible: 0.646
             network accessible: 1.0
             */
            if (av.equals("L")) {
                access_vec = 0.395;
            }
            if (av.equals("A")) {
                access_vec = 0.646;
            }
            if (av.equals("N")) {
                access_vec = 1.0;
            }

            double access_com = 0;
            String ac = basescorevectorarray[1].split(":")[1];

            /*
             AccessComplexity = case AccessComplexity of
             high: 0.35
             medium: 0.61
             low: 0.71
             */
            if (ac.equals("H")) {
                access_com = 0.35;
            }
            if (ac.equals("M")) {
                access_com = 0.61;
            }
            if (ac.equals("L")) {
                access_com = 0.71;
            }

            double authentication = 0;
            String au = basescorevectorarray[2].split(":")[1];

            /*
             Authentication   = case Authentication of
             requires multiple instances of authentication: 0.45
             requires single instance of authentication: 0.56
             requires no authentication: 0.704
             */
            if (au.equals("M")) {
                authentication = 0.45;
            }
            if (au.equals("S")) {
                authentication = 0.56;
            }
            if (au.equals("N")) {
                authentication = 0.704;
            }

            double conf_impact = 0;
            String ci = basescorevectorarray[3].split(":")[1];

            /*
             ConfImpact       = case ConfidentialityImpact of
             none:             0.0
             partial:          0.275
             complete:         0.660
             */
            if (ci.equals("N")) {
                conf_impact = 0.0;
            }
            if (ci.equals("P")) {
                conf_impact = 0.275;
            }
            if (ci.equals("C")) {
                conf_impact = 0.660;
            }

            double integ_impact = 0;
            String ii = basescorevectorarray[4].split(":")[1];
            /*
             IntegImpact      = case IntegrityImpact of
             none:             0.0
             partial:          0.275
             complete:         0.660
             */

            if (ii.equals("N")) {
                integ_impact = 0.0;
            }
            if (ii.equals("P")) {
                integ_impact = 0.275;
            }
            if (ii.equals("C")) {
                integ_impact = 0.660;
            }

            double avail_impact = 0;
            String ai = basescorevectorarray[5].split(":")[1];

            /*
             AvailImpact      = case AvailabilityImpact of
             none:             0.0
             partial:          0.275
             complete:         0.660
             */
            if (ai.equals("N")) {
                avail_impact = 0.0;
            }
            if (ai.equals("P")) {
                avail_impact = 0.275;
            }
            if (ai.equals("C")) {
                avail_impact = 0.660;
            }
            /*
             CollateralDamagePotential = case CollateralDamagePotential of
             none:            0
             low:             0.1
             low-medium:      0.3
             medium-high:     0.4
             high:            0.5
             not defined:     0
             */
            double CollateralDamagePotential = 0;
            String cdp = environmentalvectorarray[0].split(":")[1];
            // CDP: N = None, L = Low, LM = Low-Medium, MH = Medium-High, H = High, ND = Not Defined
            if (cdp.equals("N")) {
                CollateralDamagePotential = 0;
            }
            if (cdp.equals("L")) {
                CollateralDamagePotential = 0.1;
            }
            if (cdp.equals("LM")) {
                CollateralDamagePotential = 0.3;
            }
            if (cdp.equals("MH")) {
                CollateralDamagePotential = 0.4;
            }
            if (cdp.equals("H")) {
                CollateralDamagePotential = 0.5;
            }
            if (cdp.equals("ND")) {
                CollateralDamagePotential = 0;
            }

            /*
             TargetDistribution        = case TargetDistribution of
             none:            0
             low:             0.25
             medium:          0.75
             high:            1.00
             not defined:     1.00
             */
            double TargetDistribution = 0;
            String td = environmentalvectorarray[1].split(":")[1];
            // TD : N = None (0%), L = Low (1-25%), M = Medium (26-75%), H = High (76-100%), ND = Not Defined
            if (td.equals("N")) {
                TargetDistribution = 0;
            }
            if (td.equals("L")) {
                TargetDistribution = 0.25;
            }
            if (td.equals("M")) {
                TargetDistribution = 0.75;
            }
            if (td.equals("H")) {
                TargetDistribution = 1.00;
            }
            if (td.equals("ND")) {
                TargetDistribution = 1.00;
            }

            double ConfReq = 0;
            String cr = environmentalvectorarray[2].split(":")[1];
            /*
             ConfReq 	         = case ConfReq of
             low:              0.5
             medium:           1.0
             high:             1.51
             not defined:      1.0
             */
            if (cr.equals("L")) {
                ConfReq = 0.5;
            }
            if (cr.equals("M")) {
                ConfReq = 1.0;
            }
            if (cr.equals("H")) {
                ConfReq = 1.51;
            }
            if (cr.equals("ND")) {
                ConfReq = 1.0;
            }

            double IntegReq = 0;
            String ir = environmentalvectorarray[3].split(":")[1];
            /*
             IntegReq         = case IntegReq of
             low:              0.5
             medium:           1.0
             high:             1.51
             not defined:      1.0
             */
            if (ir.equals("L")) {
                IntegReq = 0.5;
            }
            if (ir.equals("M")) {
                IntegReq = 1.0;
            }
            if (ir.equals("H")) {
                IntegReq = 1.51;
            }
            if (ir.equals("ND")) {
                IntegReq = 1.0;
            }

            double AvailReq = 0;
            String ar = environmentalvectorarray[4].split(":")[1];

            /*
             AvailReq         = case AvailReq of
             low:              0.5
             medium:           1.0
             high:             1.51
             not defined:      1.0
             */
            if (ar.equals("L")) {
                AvailReq = 0.5;
            }
            if (ar.equals("M")) {
                AvailReq = 1.0;
            }
            if (ar.equals("H")) {
                AvailReq = 1.51;
            }
            if (ar.equals("ND")) {
                AvailReq = 1.0;
            }
            DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
            double Exploitability_Score = 20 * access_vec * access_com * authentication;
            double AdjustedImpact = Math.min(10, 10.41 * (1 - (1 - conf_impact * ConfReq) * (1 - integ_impact * IntegReq) * (1 - avail_impact * AvailReq)));
            double is = (AdjustedImpact == 0) ? 0 : 1.176;
            double Adjusted_Base_Score = (((0.6 * AdjustedImpact) + (0.4 * Exploitability_Score) - 1.5) * is);
            double Adjusted_Temporal = getTemporalScore(Adjusted_Base_Score);
            Adjusted_Temporal = Double.parseDouble(oneDigit.format(Adjusted_Temporal));
            EnvironmentalScore = (Adjusted_Temporal + (10 - Adjusted_Temporal) * CollateralDamagePotential) * TargetDistribution;
        }
        return Round(EnvironmentalScore, 1);
    }

    public double Round(double number, int decimalPlaces) {
        double modifier = Math.pow(10.0, decimalPlaces);
        return Math.round(number * modifier) / modifier;
    }

    @Override
    public String toString() {
        return this.getCVSSVector();
    }
}
