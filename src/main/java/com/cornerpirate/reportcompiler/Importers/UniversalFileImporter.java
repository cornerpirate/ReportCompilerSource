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

import com.cornerpirate.reportcompiler.Utils.TreeUtils;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The beating heart of ReportCompiler is this sexyish importing class. Users
 * can select one or more files to import at a time and this will work out if
 * the file is supported by ReportCompiler. If not it will warn the user
 * Otherwise it does what it says on the tin, it will import and add to the
 * tree.
 *
 * @author cornerpirate
 */
public class UniversalFileImporter {

    final NessusV2XMLImporter nv2xml = new NessusV2XMLImporter();
    final ExcelImporter excelim = new ExcelImporter();
    final ImportReportCompiler importrc = new ImportReportCompiler();
    final BurpImporter burpim = new BurpImporter();
    final SurecheckImporter surecheckim = new SurecheckImporter();
    final NipperCSVImporter nipperCSVim = new NipperCSVImporter();

    public String file_type = null;

    /**
     * Called before importing to check for the file type. This is where it is
     * determined as importable or not
     *
     * @param importingFile
     * @return String - representing the tool name or "Unknown"
     */
    public String getFileType(File importingFile) {

        String fileType = "Unknown";

        if (nv2xml.isValid(importingFile)) {
            System.out.println("Is a NESSUS file");
            file_type = "NessusV2XML";
            return file_type;
        } else if (excelim.isValid(importingFile)) {
            System.out.println("Is an xls file");
            file_type = "ExcelXLS";
            return file_type;
        } else if (importrc.isValid(importingFile)) {
            System.out.println("Is a ReportCompiler save file");
            file_type = "ReportCompiler";
            return file_type;
        } else if (burpim.isValid(importingFile)) {
            System.out.println("Is a Burp Report XML file");
            file_type = "Burp";
            return file_type;
        } else if (surecheckim.isValid(importingFile)) {
            System.out.println("Is a Surecheck XML file");
            file_type = "Surecheck";
            return file_type;
        } else if (nipperCSVim.isValid(importingFile)) {
            System.out.println("Is a Nipper CSV file");
            file_type = "NipperCSV";
            return file_type;
        }

        String message = "Did not understand that file type, cannot import that.";
        String title = "Cannot Import File";
        // go through all the importers we have and see if we can cater for it

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

        System.out.println("==getFileType():" + title);
        return fileType;
    }

    /**
     * This is responsible for calling the correct readFile method.
     *
     * @param importingFile
     * @return DefaultMutableTreeNode - representing the tree of vulns in the
     * file
     */
    public DefaultMutableTreeNode readFile(File importingFile) {

        DefaultMutableTreeNode root = null;

        String ftype = this.getFileType(importingFile);
        if (ftype.equalsIgnoreCase("NessusV2XML")) {
            // Read the nessus file
            root = nv2xml.readFile(importingFile);
        } else if (ftype.equalsIgnoreCase("ExcelXLS")) {
            // Read the XLS file
            root = excelim.readFile(importingFile);
        } else if (ftype.equalsIgnoreCase("ReportCompiler")) {
            root = importrc.readFile(importingFile);
        } else if (ftype.equalsIgnoreCase("Burp")) {
            root = burpim.readFile(importingFile);
        } else if (ftype.equalsIgnoreCase("Surecheck")) {
            root = surecheckim.readFile(importingFile);
        } else if (ftype.equalsIgnoreCase("NipperCSV")) {
            root = nipperCSVim.readFile(importingFile);
        }

        // Optionally we need to check for existing items in the VulnTree 
        // and merge them in here but for now one file at a time.
        // sort the tree and return it
        if (root != null) {
            return new TreeUtils().sortVulns(root);
        }
        return new DefaultMutableTreeNode("vulns");
    }

}
