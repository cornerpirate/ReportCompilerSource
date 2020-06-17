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
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author cornerpirate
 */
public class VulnDescriptionDocumentListener implements DocumentListener {

    JTextPane descriptionTextField;
    JTree vulnTree;
    PersonalVulnsWindow mainframe;

    public VulnDescriptionDocumentListener(JTextPane title, JTree tree, PersonalVulnsWindow mf) {
        super();
        this.descriptionTextField = title;
        this.vulnTree = tree;
        this.mainframe = mf;
    }

    public VulnDescriptionDocumentListener(JTextPane title, JTree tree) {
        super();
        this.descriptionTextField = title;
        this.vulnTree = tree;
        this.mainframe = null;
    }

    public void changedUpdate(DocumentEvent e) {
        saveDescription();
    }

    public void removeUpdate(DocumentEvent e) {
        saveDescription();
    }

    public void insertUpdate(DocumentEvent e) {
        saveDescription();
    }

    public void saveDescription() {
        // Get the modified text
        String new_description = "" + descriptionTextField.getText();
        // Get the selected vuln node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) vulnTree.getLastSelectedPathComponent();
        if (node != null) {
            Object obj = node.getUserObject();
            if (obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                vuln.setDescription(new_description);
                ((DefaultTreeModel) vulnTree.getModel()).nodeChanged(node);
                
                if (this.mainframe != null) {
                    mainframe.dirty = true;
                }
            }
        }
    }

}
