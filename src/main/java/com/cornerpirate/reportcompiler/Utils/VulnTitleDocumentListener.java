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
package com.cornerpirate.reportcompiler.Utils;

import com.cornerpirate.reportcompiler.GUI.PersonalVulnsWindow;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author cornerpirate
 */
public class VulnTitleDocumentListener implements DocumentListener {

    JTextField titleTextField;
    JTree vulnTree;
    PersonalVulnsWindow mainframe;

    public VulnTitleDocumentListener(JTextField title, JTree tree, PersonalVulnsWindow mf) {
        super();
        this.titleTextField = title;
        this.vulnTree = tree;
        this.mainframe = mf;
    }

    public VulnTitleDocumentListener(JTextField title, JTree tree) {
        super();
        this.titleTextField = title;
        this.vulnTree = tree;
        this.mainframe = null;
    }

    public void changedUpdate(DocumentEvent e) {
        saveTitle();
    }

    public void removeUpdate(DocumentEvent e) {
        saveTitle();
    }

    public void insertUpdate(DocumentEvent e) {
        saveTitle();
    }

    public void saveTitle() {
        // Get the modified text
        String new_title = "" + titleTextField.getText();
        // Get the selected vuln node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) vulnTree.getLastSelectedPathComponent();
        if (node != null) {
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                
                Vulnerability vuln = (Vulnerability) obj;
                vuln.setTitle(new_title);
                System.out.println("Vuln Import Tool: " + vuln.getImport_tool()) ;
                
                if(vuln.getImport_tool().equalsIgnoreCase("ReportCompiler")) {
                    vuln.changeReportCompilerID() ;
                }
                
                ((DefaultTreeModel) vulnTree.getModel()).nodeChanged(node);
                if (this.mainframe != null) {
                    mainframe.dirty = true;
                }
            }
        }
    }

}
