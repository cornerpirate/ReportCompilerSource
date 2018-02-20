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
package Exporters;

import Models.Host;
import Models.Vulnerability;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.WritableHyperlink;

/**
 *
 * @author cornerpirate
 */
public class ExportToExcel {

    public void writeExcel(File outfile, DefaultMutableTreeNode root) throws IOException, WriteException {

        // open a new workbook5
        WritableWorkbook workbook = Workbook.createWorkbook(outfile);
        workbook.setColourRGB(jxl.format.Colour.YELLOW, 242, 236, 0);

        // create a sheet (a 'tab')
        WritableSheet sheet = workbook.createSheet("Vulnerability List", 0);
        // set default widths for the columns
        sheet.setColumnView(0, 10); // index
        sheet.setColumnView(1, 30); // title (default value)
        sheet.setColumnView(2, 10);  // Risk Score
        sheet.setColumnView(3, 35); // CVSS Vector
        sheet.setColumnView(4, 100); // description
        sheet.setColumnView(5, 100); // recommendations
        sheet.setColumnView(6, 40); // affected hosts

        // Format data for header row
        WritableFont wfobj = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.WHITE);
        WritableCellFormat header_row_format = new WritableCellFormat(wfobj);
        header_row_format.setBackground(jxl.format.Colour.BLUE);
        // Format data for large text cells
        WritableCellFormat data_format = new WritableCellFormat();
        data_format.setWrap(true);
        data_format.setAlignment(jxl.format.Alignment.JUSTIFY);
        data_format.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
        // Format data for Critical Risk
        WritableCellFormat critical_risk = new WritableCellFormat(wfobj);
        critical_risk.setBackground(jxl.format.Colour.DARK_PURPLE);
        critical_risk.setAlignment(jxl.format.Alignment.CENTRE);
        critical_risk.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
        // Format data for High Risk
        WritableCellFormat high_risk = new WritableCellFormat(wfobj);
        high_risk.setBackground(jxl.format.Colour.RED);
        high_risk.setAlignment(jxl.format.Alignment.CENTRE);
        high_risk.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
        // Format data for Medium Risk
        WritableCellFormat medium_risk = new WritableCellFormat(wfobj);
        medium_risk.setBackground(jxl.format.Colour.ORANGE);
        medium_risk.setAlignment(jxl.format.Alignment.CENTRE);
        medium_risk.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
        // Format data for Low Risk
        WritableCellFormat low_risk = new WritableCellFormat(wfobj);
        low_risk.setBackground(jxl.format.Colour.YELLOW);
        low_risk.setAlignment(jxl.format.Alignment.CENTRE);
        low_risk.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
        // Format data for Info
        WritableCellFormat info_risk = new WritableCellFormat(wfobj);
        info_risk.setBackground(jxl.format.Colour.BLUE);
        info_risk.setAlignment(jxl.format.Alignment.CENTRE);
        info_risk.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
        // Format data for Good Practices.
        WritableCellFormat good_risk = new WritableCellFormat(wfobj);
        good_risk.setBackground(jxl.format.Colour.GREEN);
        good_risk.setAlignment(jxl.format.Alignment.CENTRE);
        good_risk.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);

        // build header row [Index, Vuln Title, Risk Score, CVSS Vector, Description, Recommendations, Affected Hosts]
        Label label = new Label(0, 0, "Index", header_row_format);
        sheet.addCell(label);

        Label label2 = new Label(1, 0, "Vuln Title", header_row_format);
        sheet.addCell(label2);

        Label label3 = new Label(2, 0, "Risk Score", header_row_format);
        sheet.addCell(label3);

        Label label4 = new Label(3, 0, "CVSS Vector", header_row_format);
        sheet.addCell(label4);

        Label label5 = new Label(4, 0, "Description", header_row_format);
        sheet.addCell(label5);

        Label label6 = new Label(5, 0, "Recommendations", header_row_format);
        sheet.addCell(label6);

        Label label7 = new Label(6, 0, "Affected Hosts", header_row_format);
        sheet.addCell(label7);

        Enumeration enums = root.children();

        int index = 1;
        int row = 1;
        int longest_title = 30;

        while (enums.hasMoreElements()) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();

            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;

                //Index,
                Label index_label = new Label(0, row, "" + index, data_format);
                sheet.addCell(index_label);

                //Vuln Title, 
                Label title_label = new Label(1, row, vuln.getTitle(), data_format);
                sheet.addCell(title_label);

                //Risk Score, 
                WritableCellFormat cvss_format = null;
                double cvss = (vuln.getRiskScore());
                if (cvss >= Double.parseDouble("9.0")) {
                    cvss_format = critical_risk;
                } else if (cvss >= Double.parseDouble("7.0") && cvss <= Double.parseDouble("8.9")) {
                    cvss_format = high_risk;
                } else if (cvss >= Double.parseDouble("4.0") && cvss <= Double.parseDouble("6.9")) {
                    cvss_format = medium_risk;
                } else if (cvss >= Double.parseDouble("1.0") && cvss <= Double.parseDouble("3.9")) {
                    cvss_format = low_risk;
                } else if (cvss >= Double.parseDouble("0") && cvss <= Double.parseDouble("0.9")) {
                    cvss_format = info_risk;
                }

                Label score_label = new Label(2, row, "" + vuln.getRiskScore(), cvss_format);
                sheet.addCell(score_label);

                //CVSS Vector,
                Label cvss_label = new Label(3, row, vuln.getCvss_vector_string(), data_format);
                sheet.addCell(cvss_label);
                //Description, 
                Label description_label = new Label(4, row, vuln.getDescription(), data_format);
                sheet.addCell(description_label);
                //Recommendations]
                Label recommendations_label = new Label(5, row, vuln.getRecommendation(), data_format);
                sheet.addCell(recommendations_label);
                //Affected Hosts, 

                WritableSheet hosts_sheet = workbook.createSheet(index + "", index);
                index = index + 1;

                // There is a max length for excel cells.
                // If you have one vuln on hundreds of hosts then you will hit it.
                // So I have made the affected hosts go into their own sheet.
                int r = 0;
                Label title = new Label(0, r, vuln.getTitle(), data_format);
                hosts_sheet.addCell(title);
                hosts_sheet.setColumnView(0, 60);
                Enumeration hosts = vuln.getAffectedHosts().elements();
                while (hosts.hasMoreElements()) {
                    r = r + 1;
                    Host h = (Host) hosts.nextElement();
                    Label hst_label = new Label(0, r, h.getHostForExcel(), data_format);
                    hosts_sheet.addCell(hst_label) ;
                }

                //Label hosts_label = new Label(6, row, vuln.getAffectedHostsForExcel(), data_format);
                // Insert Link to the affected host
                WritableHyperlink link = new WritableHyperlink(6, row, "Link to Affected", hosts_sheet, 0,0) ;
                sheet.addHyperlink(link);
                
                //sheet.addCell(hosts_label);
                row = row + 1;
                // dynamically fix the width of the title column
                if (longest_title <= vuln.getTitle().length()) {
                    longest_title = vuln.getTitle().length() + 2;
                }

            }
        }

        // if longest_title has changed update the column width
        if (longest_title > 30) {
            sheet.setColumnView(1, longest_title); // title (default value)
        }
        // All sheets and cells added. Now write out the workbook
        workbook.write();
        workbook.close();
        // Give feedback that the file was created.
        JOptionPane.showMessageDialog(null, "Export to XLS successful", "Saved your file ok.", JOptionPane.INFORMATION_MESSAGE);

        try {
            // try to open the file if we can.
            Desktop.getDesktop().open(outfile);
        } catch (Exception ex) {
            System.err.println("Some problem opening the XLS file.");
            ex.printStackTrace();
            // if we get here it is likely that the user is not on Windows or doesn't have a default XLS viewer
        }
    }
}
