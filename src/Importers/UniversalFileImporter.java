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
package Importers;

import Utils.TreeUtils;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author pritchie
 */
public class UniversalFileImporter {

    final NessusV2XMLImporter nv2xml = new NessusV2XMLImporter();
    final ExcelImporter excelim = new ExcelImporter();
    final ImportReportCompiler importrc = new ImportReportCompiler();
    final BurpImporter burpim = new BurpImporter() ;
    final SurecheckImporter surecheckim = new SurecheckImporter() ;
    final QualysCSVImporter qualyscsvim = new QualysCSVImporter() ;
    
    public String file_type = null;

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
        }  else if (surecheckim.isValid(importingFile)) {
            System.out.println("Is a Surecheck XML file");
            file_type = "Surecheck";
            return file_type;
        } else if (qualyscsvim.isValid(importingFile)) {
            System.out.println("Qualys CSV file");
            file_type = "QualysCsv";
            return file_type;
        } 

        String message = "Did not understand that file type, cannot import that.";
        String title = "Cannot Import File";
        // go through all the importers we have and see if we can cater for it
        /*
        JOptionPane.showMessageDialog(null,
         message,
         title,
         JOptionPane.ERROR_MESSAGE);
         */

        System.out.println(title);
        return fileType;
    }

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
        } else if (ftype.equalsIgnoreCase("QualysCsv")) {
            root = qualyscsvim.readFile(importingFile);
        } 

        // Optionally we need to check for existing items in the VulnTree 
        // and merge them in here but for now one file at a time.
        // sort the tree and return it
        if (root != null) {
            return new TreeUtils().sortVulns(root);
        }
        return new DefaultMutableTreeNode("vulns");
        //return root;
    }

}
