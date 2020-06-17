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
package com.cornerpirate.reportcompiler.Views;

import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.Reference;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author cornerpirate
 */
public class MergedTreeSelectionListener implements TreeSelectionListener {

    public HashMap gui;
    public JTree toMergeTree;
    public JTree MergedTree;

    public MergedTreeSelectionListener(HashMap gui, JTree ToMergeTree, JTree MergedTree) {
        super();
        this.gui = gui;
        this.toMergeTree = ToMergeTree;
        this.MergedTree = MergedTree;

    }

    public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.MergedTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        if (this.toMergeTree.getSelectionCount() != 0) {
            this.toMergeTree.clearSelection();
        }

        Object obj = node.getUserObject();
        if (obj instanceof Vulnerability) {
            Vulnerability vuln = (Vulnerability) obj;
            System.out.println("MergedTreeSelectionListener==" + vuln);
            updateGui(vuln);
        } else {
            // Somebody clicked on the root node, clear the GUI and disable editing
            clearGui();
        }
    }

    /*
     gui.put("title", MergeVulnTitleTextField) ;
     gui.put("cvss", MergeVulnCVSSVectorTextField) ;
     gui.put("score", MergeVulnScore);
     gui.put("category", MergeVulnRiskCategory) ;
     gui.put("description", MergeVulnDescriptionTextPane);
     gui.put("recommendation", MergeVulnRecommendationTextPane);
     gui.put("references", MergeVulnReferencesList);
     gui.put("affected", MergeVulnAffectedHostsTable);
     */
    private void updateGui(Vulnerability vuln) {

        ((JTextField) gui.get("title")).setEnabled(true);
        ((JTextField) gui.get("title")).setText(vuln.getTitle());
        ((JTextField) gui.get("cvss")).setEnabled(true);
        ((JTextField) gui.get("cvss")).setText(vuln.getCvss_vector_string());
        ((JTextField) gui.get("score")).setEnabled(true);
        ((JTextField) gui.get("score")).setText("" + vuln.getRiskScore());
        ((JTextField) gui.get("category")).setEnabled(true);
        ((JTextField) gui.get("category")).setText(vuln.getRisk_category());
        ((JTextPane) gui.get("description")).setEnabled(true);
        ((JTextPane) gui.get("description")).setText(vuln.getDescription());
        ((JTextPane) gui.get("recommendation")).setEnabled(true);
        ((JTextPane) gui.get("recommendation")).setText(vuln.getRecommendation());
        ((JButton)gui.get("editrisk")).setEnabled(true);

        JList refs = (JList) gui.get("references");
        refs.setEnabled(true);
        ListModel model = refs.getModel();
        DefaultListModel dlm = (DefaultListModel) model;
        dlm.removeAllElements();
        Enumeration ref_enums = vuln.getReferences().elements();
        while (ref_enums.hasMoreElements()) {
            Reference ref = (Reference) ref_enums.nextElement();
            dlm.addElement(ref);
        }

        JTable affected = (JTable) gui.get("affected") ;
        affected.setEnabled(true);
        DefaultTableModel dtm = (DefaultTableModel) affected.getModel();
        // Clear the existing table
        dtm.setRowCount(0);

        // Set affected hosts into table
        Enumeration enums = vuln.getAffectedHosts().elements();
        while (enums.hasMoreElements()) {
            Object obj = enums.nextElement();
            if (obj instanceof Host) {
                Host host = (Host) obj;
                Vector row = host.getAsVector(); // Gets the first two columns from the host
                dtm.addRow(row);
            }
        }

    }

    private void clearGui() {
        // Clear and disable text fields
        ((JTextField) gui.get("title")).setEnabled(false);
        ((JTextField) gui.get("title")).setText("");
        ((JTextField) gui.get("cvss")).setEnabled(false);
        ((JTextField) gui.get("cvss")).setText("");
        ((JTextField) gui.get("score")).setEnabled(false);
        ((JTextField) gui.get("score")).setText("");
        ((JTextField) gui.get("category")).setEnabled(false);
        ((JTextField) gui.get("category")).setText("");
        ((JTextPane) gui.get("description")).setEnabled(false);
        ((JTextPane) gui.get("description")).setText("");
        ((JTextPane) gui.get("recommendation")).setEnabled(false);
        ((JTextPane) gui.get("recommendation")).setText("");

        // Clear and disable the references
        JList refs = (JList) gui.get("references");
        refs.setEnabled(false);
        ListModel model = refs.getModel();
        DefaultListModel dlm = (DefaultListModel) model;
        dlm.removeAllElements();
        
        JTable affected = (JTable) gui.get("affected") ;
        DefaultTableModel dtm = (DefaultTableModel) affected.getModel();
        // Clear the existing table
        dtm.setRowCount(0);
        affected.setEnabled(false);
    }
}
