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
package com.cornerpirate.reportcompiler.Importers;

import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 *
 * @author cornerpirate
 */
public class ExcelImporter implements ImporterInterface {

    @Override
    public boolean isValid(File file) {
        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
        if (ext.equalsIgnoreCase("xls")) {
            return true;
        }
        return false;
    }

    @Override
    public DefaultMutableTreeNode readFile(File file) {
        System.out.println("==ExcelImporter=readFile: " + file.getAbsolutePath());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("vulns");
        try {

            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0; // No of columns
            int tmp = 0;

            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }

            for (int r = 1; r < rows; r++) {
                row = sheet.getRow(r);
                if (row != null) {

                    // Create a new vuln
                    Vulnerability vuln = new Vulnerability();
                    vuln.setTitle("NEW");
                    vuln.setIs_custom_risk(true);
                    vuln.setRisk_category("None");

                    for (int c = 0; c < cols; c++) {
                        cell = row.getCell(c);
                        if (cell != null) {
                            // Your code here
                            String value = cell.getStringCellValue();
                            switch (c) {
                                case 1:// title
                                    vuln.setTitle(value);
                                    break;
                                case 2: // Risk
                                    CellStyle style = cell.getCellStyle();
                                    short colorIdx = style.getFillForegroundColor();
                                    HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
                                    HSSFColor color = palette.getColor(colorIdx);
                                    String cc = color.getHexString();
                                    System.out.println(cc);
                                    if (cc.equalsIgnoreCase("8080:8080:0")) {
                                        vuln.setRisk_category("Critical");
                                    } else if (cc.equalsIgnoreCase("FFFF:0:0")) {
                                        vuln.setRisk_category("High");
                                    } else if (cc.equalsIgnoreCase("FFFF:6666:0")) {
                                        vuln.setRisk_category("Medium");
                                    } else if (cc.equalsIgnoreCase("F2F2:ECEC:0")) {
                                        vuln.setRisk_category("Low");
                                    } else if (cc.equalsIgnoreCase("0:0:FFFF")) {
                                        vuln.setRisk_category("Info");
                                    }

                                    break;
                                case 3:// cvss string
                                    System.out.println(value);
                                    if (value.equalsIgnoreCase("No CVSS Vector")) {
                                        vuln.setIs_custom_risk(true);
                                    } else {
                                        vuln.setIs_custom_risk(false);
                                        vuln.setCvss_vector_string("CVSS2#" + value);
                                    }
                                    break;
                                case 4://Description
                                    vuln.setDescription(value);
                                    break;
                                case 5://Recommendation
                                    vuln.setRecommendation(value);
                                    break;
                                case 6://Affected Hosts
                                    try {
                                        String[] lines = value.split("\n");

                                        for (String line : lines) {
                                            String[] bits = line.split(" ");
                                            Host host = new Host();
                                            host.setIp_address(bits[0]);
                                            String portprotocol = bits[2];
                                            host.setPortnumber(portprotocol.split("/")[0]);
                                            host.setProtocol(portprotocol.split("/")[1]);
                                            vuln.addAffectedHost(host);
                                        }
                                    } catch (Exception ex) {;
                                    }
                                    break;
                            }

                        }
                    }
                    System.out.println(vuln);

                    root.add(new DefaultMutableTreeNode(vuln));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return root;
    }

}
