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
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author cornerpirate
 */
public class ExportToExcel {

    public void writeExcel(File outfile, DefaultMutableTreeNode root) {

        //javax.swing.JOptionPane.showMessageDialog(null, "Not implemented, replacing the export dependency library");
        // open a new workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // create a sheet (a 'tab')
        XSSFSheet sheet = workbook.createSheet("Vulnerability List");

        // set default widths for the columns
        sheet.setColumnWidth(0, 10 * 256); // index
        sheet.setColumnWidth(1, 30 * 256); // title (default value)
        sheet.setColumnWidth(2, 10 * 256);  // Risk Score
        sheet.setColumnWidth(3, 35 * 256); // CVSS Vector
        sheet.setColumnWidth(4, 100 * 256); // description
        sheet.setColumnWidth(5, 100 * 256); // recommendations
        sheet.setColumnWidth(6, 40 * 256); // affected hosts

        // Create format data for header row
        XSSFFont headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);

        XSSFCellStyle header_row_style = workbook.createCellStyle();
        header_row_style.setFont(headerFont);
        header_row_style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        header_row_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        short rowid = 0;

        // add heading row
        XSSFRow header_row = sheet.createRow(rowid++);
        String[] header_strings = {"Index", "Vuln Title", "Risk Score", "CVSS Vector", "Description", "Recommendations", "Affected Hosts"};
        int columnCount = 0;
        for (String header : header_strings) {
            XSSFCell cell = header_row.createCell(columnCount++);
            cell.setCellValue((String) header);
            cell.setCellStyle(header_row_style);
        }

        // Format data for large text cells
        XSSFCellStyle large_text_style = workbook.createCellStyle();
        large_text_style.setWrapText(true);
        large_text_style.setAlignment(HorizontalAlignment.JUSTIFY);
        large_text_style.setVerticalAlignment(VerticalAlignment.TOP);

        // Format for hyperlinks
        XSSFFont hyperlink_font = workbook.createFont();
        hyperlink_font.setColor(IndexedColors.BLUE.getIndex());
        hyperlink_font.setBold(true);
        hyperlink_font.setUnderline(FontUnderline.SINGLE);

        XSSFCellStyle hyperlink_style = workbook.createCellStyle();
        hyperlink_style.setAlignment(HorizontalAlignment.LEFT);
        hyperlink_style.setVerticalAlignment(VerticalAlignment.TOP);
        hyperlink_style.setFont(hyperlink_font);

        // Format data for Critical Risk
        XSSFCellStyle critical_style = workbook.createCellStyle();
        critical_style.setVerticalAlignment(VerticalAlignment.TOP);
        critical_style.setAlignment(HorizontalAlignment.CENTER);
        critical_style.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
        critical_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Format data for High Risk
        XSSFCellStyle high_style = workbook.createCellStyle();
        high_style.setVerticalAlignment(VerticalAlignment.TOP);
        high_style.setAlignment(HorizontalAlignment.CENTER);
        high_style.setFillForegroundColor(IndexedColors.RED.getIndex());
        high_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Format data for Medium Risk
        XSSFCellStyle medium_style = workbook.createCellStyle();
        medium_style.setVerticalAlignment(VerticalAlignment.TOP);
        medium_style.setAlignment(HorizontalAlignment.CENTER);
        medium_style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        medium_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Format data for Low Risk
        XSSFCellStyle low_style = workbook.createCellStyle();
        low_style.setVerticalAlignment(VerticalAlignment.TOP);
        low_style.setAlignment(HorizontalAlignment.CENTER);
        low_style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        low_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Format data for Info
        XSSFCellStyle info_style = workbook.createCellStyle();
        info_style.setVerticalAlignment(VerticalAlignment.TOP);
        info_style.setAlignment(HorizontalAlignment.CENTER);
        info_style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        info_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Format data for Good Practices.
        XSSFCellStyle good_style = workbook.createCellStyle();
        good_style.setVerticalAlignment(VerticalAlignment.TOP);
        good_style.setAlignment(HorizontalAlignment.CENTER);
        good_style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        good_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Aligned to top.
        XSSFCellStyle top_aligned_style = workbook.createCellStyle();
        top_aligned_style.setVerticalAlignment(VerticalAlignment.TOP);

        // Write vulns
        Enumeration enums = root.children();

        int index = 1;
        short row_index = 0;
        int longest_title = 30;

        while (enums.hasMoreElements()) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();

            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                System.out.println(vuln);
                System.out.println("rowid(b4):" + rowid);

                XSSFRow row = sheet.createRow(rowid++);
                System.out.println("rowid(at):" + rowid);

                XSSFCell id_cell = row.createCell(0);
                id_cell.setCellValue((String) "" + (rowid - 1));
                id_cell.setCellStyle(top_aligned_style);

                XSSFCell title_cell = row.createCell(1);
                title_cell.setCellValue((String) vuln.getTitle());
                title_cell.setCellStyle(top_aligned_style);

                XSSFCell score_cell = row.createCell(2);
                score_cell.setCellValue((double) vuln.getRiskScore());

                double cvss = (vuln.getRiskScore());
                if (cvss >= Double.parseDouble("9.0")) {
                    score_cell.setCellStyle(critical_style);
                } else if (cvss >= Double.parseDouble("7.0") && cvss <= Double.parseDouble("8.9")) {
                    score_cell.setCellStyle(high_style);
                } else if (cvss >= Double.parseDouble("4.0") && cvss <= Double.parseDouble("6.9")) {
                    score_cell.setCellStyle(medium_style);
                } else if (cvss >= Double.parseDouble("1.0") && cvss <= Double.parseDouble("3.9")) {
                    score_cell.setCellStyle(low_style);
                } else if (cvss >= Double.parseDouble("0") && cvss <= Double.parseDouble("0.9")) {
                    score_cell.setCellStyle(info_style);
                }

                XSSFCell vector_cell = row.createCell(3);
                vector_cell.setCellValue((String) vuln.getCvss_vector_string());
                vector_cell.setCellStyle(top_aligned_style);

                XSSFCell description_cell = row.createCell(4);
                description_cell.setCellValue((String) vuln.getDescription());
                description_cell.setCellStyle(large_text_style);

                XSSFCell recommendations_cell = row.createCell(5);
                recommendations_cell.setCellValue((String) vuln.getRecommendation());
                recommendations_cell.setCellStyle(large_text_style);

                // Deal with affected hosts
                XSSFSheet hosts_sheet = workbook.createSheet(index + "");
                XSSFRow hosts_header_row = hosts_sheet.createRow(0);
                XSSFCell hosts_title_cell = hosts_header_row.createCell(0);
                hosts_title_cell.setCellStyle(header_row_style);
                hosts_title_cell.setCellValue((String) vuln.getTitle());
                hosts_sheet.setColumnWidth(0, 120 * 256);

                // Write each affected host one per cell
                int host_row_index = 1;
                Enumeration hosts = vuln.getAffectedHosts().elements();
                while (hosts.hasMoreElements()) {
                    Host h = (Host) hosts.nextElement();
                    XSSFRow host_row = hosts_sheet.createRow(host_row_index++);
                    XSSFCell host_cell = host_row.createCell(0);
                    host_cell.setCellValue((String) h.getHostForExcel());
                }

                // now cross link from sheet 1 to the sheet we just made
                XSSFCell affected_hosts_cell = row.createCell(6);
                XSSFCreationHelper helper = (XSSFCreationHelper) workbook.getCreationHelper();

                XSSFHyperlink link2 = helper.createHyperlink(HyperlinkType.DOCUMENT);
                link2.setAddress("'" + index++ + "'!A1");
                affected_hosts_cell.setCellValue((String) "Link to Affected Hosts");
                affected_hosts_cell.setHyperlink(link2);
                affected_hosts_cell.setCellStyle(hyperlink_style);

                // dynamically fix the width of the title column
                if (longest_title <= vuln.getTitle().length()) {
                    longest_title = vuln.getTitle().length() + 2;
                }
            }
        }

        // if longest_title has changed update the column width
        if (longest_title > 30) {
            sheet.setColumnWidth(1, longest_title * 256); // title (default value)
        }

        // write the file
        try (FileOutputStream outputStream = new FileOutputStream(outfile)) {
            workbook.write(outputStream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExportToExcel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportToExcel.class.getName()).log(Level.SEVERE, null, ex);
        }

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
