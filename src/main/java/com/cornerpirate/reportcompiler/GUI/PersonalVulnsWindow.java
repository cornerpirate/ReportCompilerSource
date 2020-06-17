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
package com.cornerpirate.reportcompiler.GUI;

import com.cornerpirate.reportcompiler.Exporters.SaveFileExporter;
import com.cornerpirate.reportcompiler.Importers.ImportReportCompiler;
import com.cornerpirate.reportcompiler.Models.Reference;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import com.cornerpirate.reportcompiler.Utils.Helper;
import com.cornerpirate.reportcompiler.Utils.TreeUtils;
import com.cornerpirate.reportcompiler.Utils.VulnDescriptionDocumentListener;
import com.cornerpirate.reportcompiler.Utils.VulnRecommendationDocumentListener;
import com.cornerpirate.reportcompiler.Utils.VulnTitleDocumentListener;
import com.cornerpirate.reportcompiler.Views.VulnerabilityViewTreeCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author cornerpirate
 */
public class PersonalVulnsWindow extends javax.swing.JFrame {

    public final File person_vulns_file = new File(new File((getClass().getProtectionDomain().getCodeSource().getLocation()).getFile()).getParent() + File.separator + "PersonalVulns.xml");
    protected JTree vulntree;
    final Helper helper = new Helper();
    public boolean dirty = false;
    protected VulnTitleDocumentListener titleDocumentListener;
    protected VulnDescriptionDocumentListener descriptionDocumentListener;
    protected VulnRecommendationDocumentListener recommendationDocumentListener;

    /**
     * Creates new form PersonalVulnsWindow
     */
    public PersonalVulnsWindow(java.awt.Frame parent, boolean modal, JTree vulntree) {
        //super(parent, modal);
        initComponents();

        // Create the document Listeners
        this.titleDocumentListener = new VulnTitleDocumentListener(PersonalVulnsTitle, PersonalVulnsTree, this);
        this.descriptionDocumentListener = new VulnDescriptionDocumentListener(PersonalVulnDescription, PersonalVulnsTree, this);
        this.recommendationDocumentListener = new VulnRecommendationDocumentListener(PersonalVulnRecommendation, PersonalVulnsTree, this);

        //Setup tool tips for the vuln tree
        ToolTipManager.sharedInstance().registerComponent(PersonalVulnsTree);

        this.vulntree = vulntree;
        this.setLocationRelativeTo(null); // this centres the dialog on the screen

        // get users preferred font size if they have it. Sets to 12 if they dont.
        Font currentfont = this.PersonalVulnsTitle.getFont();
        helper.setFontSize(currentfont, this.getRootPane());
    }

    private void getFontSize() {

        Properties properties = new Properties();
        File properties_file = new File(new File((getClass().getProtectionDomain().getCodeSource().getLocation()).getFile()).getParent() + File.separator + "UserPreferences.properties");

        try {

            InputStream in = new FileInputStream(properties_file);
            properties.load(in);

            if (properties.containsKey("Font_Size")) {
                // Lets set the font size
                String users_font_size = properties.getProperty("Font_Size");
                try {
                    this.setFontSize(Integer.parseInt(users_font_size));
                } catch (Exception ex) {
                    ex.printStackTrace(); // if we get here somebody tampered with their preferences file
                }
            }

        } catch (java.io.FileNotFoundException fnfex) {
            System.out.println("User does not have any preferences file yet");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void setFontSize(int newsize) {
        Font currentfont = this.PersonalVulnsTitle.getFont();
        Component[] components = this.getComponents();
        for (int i = 0; i < components.length; i++) {
            Object obj = components[i];
            System.out.println(obj.getClass());
        }
    }

    public boolean readPersonalVulns() throws Exception {
        // Check for the personal vulns file

        if (person_vulns_file.exists() == false) {
            throw new Exception("Personal Vulns Database Doesn't Exist: " + person_vulns_file.getAbsolutePath());
        }

        final ImportReportCompiler imp = new ImportReportCompiler();

        // If we get here then the file exists. Invoke a thread to read them in the background and quietly prepare the 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (imp.isValid(person_vulns_file)) {
                    DefaultMutableTreeNode root = imp.readFile(person_vulns_file);
                    DefaultTreeModel dtm = (DefaultTreeModel) PersonalVulnsTree.getModel();
                    dtm.setRoot(root);
                }
            }
        });

        System.out.println("person_vulns_file: " + person_vulns_file);

        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VulnReferencesContextMenu = new javax.swing.JPopupMenu();
        InsertReference = new javax.swing.JMenuItem();
        EditReferenceOption = new javax.swing.JMenuItem();
        LaunchInBrowser = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        DeleteReferenceOption = new javax.swing.JMenuItem();
        BottomPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        PersonalTreeFilter = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        PersonalVulnsTree = new javax.swing.JTree();
        RightPanel = new javax.swing.JPanel();
        VulnerabilityTopPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        PersonalVulnsTitle = new javax.swing.JTextField();
        AddButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        PersonalVulnsCvssVector = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        PersonalVulnRiskCategory = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        PersonalVulnScore = new javax.swing.JTextField();
        PersonalEditRiskButton = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        DescriptionPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        PersonalVulnDescription = new javax.swing.JTextPane();
        jSplitPane4 = new javax.swing.JSplitPane();
        RecommendationPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        scrollypaney = new javax.swing.JScrollPane();
        PersonalVulnRecommendation = new javax.swing.JTextPane();
        ReferencesPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        PersonalVulnReferences = new javax.swing.JList();

        InsertReference.setText("Insert Reference");
        InsertReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InsertReferenceActionPerformed(evt);
            }
        });
        VulnReferencesContextMenu.add(InsertReference);

        EditReferenceOption.setText("Edit Reference");
        EditReferenceOption.setToolTipText("");
        EditReferenceOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditReferenceOptionActionPerformed(evt);
            }
        });
        VulnReferencesContextMenu.add(EditReferenceOption);

        LaunchInBrowser.setText("Launch in Browser");
        LaunchInBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LaunchInBrowserActionPerformed(evt);
            }
        });
        VulnReferencesContextMenu.add(LaunchInBrowser);
        VulnReferencesContextMenu.add(jSeparator3);

        DeleteReferenceOption.setText("Delete Reference");
        DeleteReferenceOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteReferenceOptionActionPerformed(evt);
            }
        });
        VulnReferencesContextMenu.add(DeleteReferenceOption);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PersonalVulns Management Screen");
        setMinimumSize(new java.awt.Dimension(262, 266));
        setPreferredSize(new java.awt.Dimension(700, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setText("Tree Filter:");

        PersonalTreeFilter.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                PersonalTreeFilterCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout BottomPanelLayout = new javax.swing.GroupLayout(BottomPanel);
        BottomPanel.setLayout(BottomPanelLayout);
        BottomPanelLayout.setHorizontalGroup(
            BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PersonalTreeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(988, Short.MAX_VALUE))
        );
        BottomPanelLayout.setVerticalGroup(
            BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(PersonalTreeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(BottomPanel, java.awt.BorderLayout.PAGE_END);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(20);
        jSplitPane1.setOneTouchExpandable(true);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("PersonalVulns");
        PersonalVulnsTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        PersonalVulnsTree.setToolTipText("T");
        PersonalVulnsTree.setCellRenderer(new VulnerabilityViewTreeCellRenderer(true));
        PersonalVulnsTree.setRootVisible(false);
        PersonalVulnsTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                PersonalVulnsTreeValueChanged(evt);
            }
        });
        PersonalVulnsTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PersonalVulnsTreeKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(PersonalVulnsTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        RightPanel.setLayout(new java.awt.BorderLayout());

        VulnerabilityTopPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Title: ");
        jPanel1.add(jLabel15);
        jPanel1.add(PersonalVulnsTitle);

        AddButton.setText("Add");
        AddButton.setToolTipText("Click here to add one or more selected vulnerabilities to your current test.");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });
        jPanel1.add(AddButton);

        VulnerabilityTopPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("CVSS:");
        jPanel2.add(jLabel16);

        PersonalVulnsCvssVector.setEditable(false);
        PersonalVulnsCvssVector.setColumns(82);
        jPanel2.add(PersonalVulnsCvssVector);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Category:");
        jPanel2.add(jLabel17);

        PersonalVulnRiskCategory.setEditable(false);
        PersonalVulnRiskCategory.setColumns(8);
        jPanel2.add(PersonalVulnRiskCategory);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Score:");
        jPanel2.add(jLabel18);

        PersonalVulnScore.setEditable(false);
        PersonalVulnScore.setColumns(4);
        jPanel2.add(PersonalVulnScore);

        PersonalEditRiskButton.setText("Edit Risk");
        PersonalEditRiskButton.setToolTipText("Click here to see the Risk Calculator where scores can be modified");
        PersonalEditRiskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PersonalEditRiskButtonActionPerformed(evt);
            }
        });
        jPanel2.add(PersonalEditRiskButton);

        VulnerabilityTopPanel.add(jPanel2, java.awt.BorderLayout.SOUTH);

        RightPanel.add(VulnerabilityTopPanel, java.awt.BorderLayout.NORTH);

        jSplitPane2.setDividerLocation(200);
        jSplitPane2.setDividerSize(20);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setOneTouchExpandable(true);

        jSplitPane3.setDividerLocation(200);
        jSplitPane3.setDividerSize(20);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setOneTouchExpandable(true);
        jSplitPane2.setTopComponent(jSplitPane3);

        DescriptionPanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Description:");
        DescriptionPanel.add(jLabel4, java.awt.BorderLayout.PAGE_START);

        jScrollPane2.setViewportView(PersonalVulnDescription);

        DescriptionPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane2.setTopComponent(DescriptionPanel);

        jSplitPane4.setDividerLocation(200);
        jSplitPane4.setDividerSize(20);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setOneTouchExpandable(true);

        RecommendationPanel.setLayout(new java.awt.BorderLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Recommendation:");
        RecommendationPanel.add(jLabel5, java.awt.BorderLayout.PAGE_START);

        scrollypaney.setViewportView(PersonalVulnRecommendation);

        RecommendationPanel.add(scrollypaney, java.awt.BorderLayout.CENTER);

        jSplitPane4.setTopComponent(RecommendationPanel);

        ReferencesPanel.setLayout(new java.awt.BorderLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("References");
        ReferencesPanel.add(jLabel6, java.awt.BorderLayout.PAGE_START);

        PersonalVulnReferences.setModel(new DefaultListModel() );
        PersonalVulnReferences.setToolTipText("Right click on this area to see options for references.");
        PersonalVulnReferences.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PersonalVulnReferencesMouseClicked(evt);
            }
        });
        PersonalVulnReferences.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PersonalVulnReferencesKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(PersonalVulnReferences);

        ReferencesPanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jSplitPane4.setRightComponent(ReferencesPanel);

        jSplitPane2.setRightComponent(jSplitPane4);

        RightPanel.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(RightPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PersonalVulnsTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_PersonalVulnsTreeValueChanged

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) PersonalVulnsTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        // Unmap document listeners IF at all possible
        removeDocumentListeners();

        Object obj = node.getUserObject();
        if (obj != null && obj instanceof Vulnerability) {
            Vulnerability vuln = (Vulnerability) obj;

            this.PersonalVulnsTitle.setText(vuln.getTitle());
            this.PersonalVulnsCvssVector.setText(vuln.getCvss_vector_string());
            this.PersonalVulnScore.setText("" + vuln.getRiskScore());
            this.PersonalVulnRiskCategory.setText(vuln.getRisk_category());
            this.PersonalVulnDescription.setText(vuln.getDescription());
            this.PersonalVulnRecommendation.setText(vuln.getRecommendation());

            // clear old references
            DefaultListModel ref_model = (DefaultListModel) this.PersonalVulnReferences.getModel();
            ref_model.removeAllElements();
            // add in the new references
            Enumeration ref_enums = vuln.getReferences().elements();
            while (ref_enums.hasMoreElements()) {
                Reference ref = (Reference) ref_enums.nextElement();
                ref_model.addElement(ref);
            }

            setupDocumentListeners();

        }

    }//GEN-LAST:event_PersonalVulnsTreeValueChanged

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        System.out.println("Personal Vuln Window: Add button pressed");

        DefaultTreeModel dtm = (DefaultTreeModel) PersonalVulnsTree.getModel();
        DefaultTreeModel vulntreemodel = (DefaultTreeModel) this.vulntree.getModel();
        DefaultMutableTreeNode vulntreeroot = (DefaultMutableTreeNode) vulntreemodel.getRoot();
        DefaultMutableTreeNode answerroot = new DefaultMutableTreeNode("answer_root");

        // Remove the existing nodes
        TreePath[] paths = PersonalVulnsTree.getSelectionPaths();
        if (paths == null) {
            return;
        }

        // Get each node and then add it to the vuln tree model
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Vulnerability vuln = (Vulnerability) node.getUserObject();
            System.out.println("Added: " + vuln);
            answerroot.add(new DefaultMutableTreeNode(vuln));
        }

        // Sort the answer
        TreeUtils tu = new TreeUtils();
        answerroot = tu.mergeTrees(answerroot, vulntreeroot);
        vulntreeroot = tu.sortVulns(answerroot);

        // Update the view
        vulntreemodel.setRoot(vulntreeroot);
        vulntreemodel.reload();
    }//GEN-LAST:event_AddButtonActionPerformed

    private void PersonalVulnsTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PersonalVulnsTreeKeyPressed

        int pressed = evt.getKeyCode();
        if (pressed == KeyEvent.VK_DELETE) {

            doDelete();

        }
    }//GEN-LAST:event_PersonalVulnsTreeKeyPressed

    private void InsertReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InsertReferenceActionPerformed

        addReference(PersonalVulnsTree, PersonalVulnReferences, null);
    }//GEN-LAST:event_InsertReferenceActionPerformed

    private void EditReferenceOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditReferenceOptionActionPerformed

        Object obj = PersonalVulnReferences.getSelectedValue();
        if (obj == null) {
            // We should simply show the 'add reference' prompy
        } else if (obj instanceof Reference) {
            // User probably wants to edit the existing reference
            Reference ref = (Reference) obj;
            addReference(PersonalVulnsTree, PersonalVulnReferences, ref);
        }
    }//GEN-LAST:event_EditReferenceOptionActionPerformed

    private void LaunchInBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LaunchInBrowserActionPerformed
        System.out.println("Launch References in Browser Selected");
        launchSelectedReferences(PersonalVulnReferences);
    }//GEN-LAST:event_LaunchInBrowserActionPerformed

    private void PersonalVulnReferencesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PersonalVulnReferencesMouseClicked

        int clicked = evt.getButton();
        if (clicked == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            // A double click. Lets see if there is already a link or if we are creating a new one
            Object obj = PersonalVulnReferences.getSelectedValue();
            if (obj == null) {
                // We should simply show the 'add reference' prompy
            } else if (obj instanceof Reference) {
                // User probably wants to edit the existing reference
                Reference ref = (Reference) obj;
                addReference(PersonalVulnsTree, PersonalVulnReferences, ref);
            }
        } else if (clicked == MouseEvent.BUTTON3) {
            // A right click. Show the context menu
            //int selected = VulnReferencesList.getSelectedIndex();
            List l = PersonalVulnReferences.getSelectedValuesList();
            int selected = l.size();

            System.out.println("selected: " + selected);
            // None are selected enable insert and disable all the rest
            InsertReference.setEnabled(true);
            EditReferenceOption.setEnabled(false);
            LaunchInBrowser.setEnabled(false);
            DeleteReferenceOption.setEnabled(false);

            if (selected == 1) {
                // One was selected to enable the right options
                InsertReference.setEnabled(true);
                EditReferenceOption.setEnabled(true);
                LaunchInBrowser.setEnabled(true);
                DeleteReferenceOption.setEnabled(true);
            } else if (selected >= 2) {
                // One was selected to enable the right options
                InsertReference.setEnabled(true);
                EditReferenceOption.setEnabled(false);
                LaunchInBrowser.setEnabled(true);
                DeleteReferenceOption.setEnabled(true);
            }

            VulnReferencesContextMenu.show((Component) evt.getSource(), evt.getX(), evt.getY());
        }

    }//GEN-LAST:event_PersonalVulnReferencesMouseClicked

    private void DeleteReferenceOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteReferenceOptionActionPerformed
        deleteReferences(this.PersonalVulnsTree, this.PersonalVulnReferences);
    }//GEN-LAST:event_DeleteReferenceOptionActionPerformed

    private void PersonalVulnReferencesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PersonalVulnReferencesKeyPressed

        int pressed = evt.getKeyCode();
        if (pressed == KeyEvent.VK_DELETE) {

            deleteReferences(this.PersonalVulnsTree, this.PersonalVulnReferences);

        } else if (pressed == KeyEvent.VK_INSERT) {
            addReference(this.PersonalVulnsTree, this.PersonalVulnReferences, null);
        }

    }//GEN-LAST:event_PersonalVulnReferencesKeyPressed

    private void PersonalEditRiskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PersonalEditRiskButtonActionPerformed

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.PersonalVulnsTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (obj instanceof Vulnerability) {
            // Show Edit Risk Window
            CVSSv2Calculator riskWindow = new CVSSv2Calculator(this, true, this.PersonalVulnsTree, node);
            riskWindow.setVisible(true);
        }

    }//GEN-LAST:event_PersonalEditRiskButtonActionPerformed

    private void PersonalTreeFilterCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_PersonalTreeFilterCaretUpdate

        String search_term = PersonalTreeFilter.getText();
        System.out.println(search_term);

        DefaultTreeModel dtm = (DefaultTreeModel) this.PersonalVulnsTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        TreeUtils tu = new TreeUtils();

        if (search_term == null || search_term.equals("")) {
            // Clear the highlighted attribute on all nodes
            tu.clearHighlighting(root);

        } else {
            // Loop through the tree and mark nodes that are highlighted.
            tu.searchTree(root, search_term);
        }
        root = tu.sortVulns(root);
        dtm.setRoot(root);

    }//GEN-LAST:event_PersonalTreeFilterCaretUpdate

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        System.out.println("Personal Vulns Window Closing");
        if (dirty == true) {
            // User has edited their database in some way
            int accept = JOptionPane.showConfirmDialog(null, "You edited your vulnerabilities. Do you want to save those changes?");
            if (accept == JOptionPane.OK_OPTION) {
                System.out.println("User wanted to save their vulns");

                try {
                    DefaultTreeModel dtm = (DefaultTreeModel) this.PersonalVulnsTree.getModel();
                    DefaultMutableTreeNode personalroot = (DefaultMutableTreeNode) dtm.getRoot();
                    // Save the tree again
                    new SaveFileExporter().save(this.person_vulns_file, personalroot);
                    System.out.println("Personal Vulns Saved: " + this.person_vulns_file.getAbsolutePath());
                    dirty = false;
                    this.setVisible(false);
                } catch (Exception ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_formWindowClosing

    public void deleteReferences(JTree tree, JList list) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        Object obj = node.getUserObject();
        if (!(obj instanceof Vulnerability)) {
            return;
        }

        // Get currently selected vulnerability
        Vulnerability vuln = (Vulnerability) obj;
        DefaultListModel dlm = (DefaultListModel) list.getModel();
        List selected = list.getSelectedValuesList();
        for (Object ref_obj : selected) {
            if (ref_obj instanceof Reference) {
                Reference ref = (Reference) ref_obj;
                vuln.deleteReference(ref);
                dlm.removeElement(ref_obj);
                System.out.println("Deleted Reference: " + ref);
            } else {
                System.out.println("Somehow the references list contained a non-Regerence object");
            }
        }
    }

    private void launchSelectedReferences(JList referencesList) {
        List selected = referencesList.getSelectedValuesList();
        Iterator it = selected.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Reference) {
                try {
                    Reference ref = (Reference) obj;
                    this.helper.openWebpage(new URL(ref.getUrl()));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void addReference(JTree tree, JList list, Reference current) {
        DefaultMutableTreeNode node = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
        if (node == null) {
            return;
        }

        Object obj = node.getUserObject();
        if (!(obj instanceof Vulnerability)) {
            return; // here be monsters, most likely in the merge tree
        }
        Vulnerability vuln = (Vulnerability) obj;
        DefaultListModel dlm = (DefaultListModel) list.getModel();
        // Build Input Field and display it
        JTextField description = new JTextField();
        JTextField url = new JTextField();

        // If current is not null then pre-set the description and risk
        if (current != null) {
            description.setText(current.getDescription());
            url.setText(current.getUrl());
        }

        JLabel error = new JLabel("A valid URL needs to be supplied including the protocol i.e. http://www.github.com");
        error.setForeground(Color.red);
        Object[] message = {
            "Description:", description,
            "URL:", url
        };

        String url_string = null;
        Reference newref = null;
        while (url_string == null) {
            int option = JOptionPane.showConfirmDialog(null, message, "Add Reference", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                System.out.println("User clicked ok, validating data");
                String ref_desc = description.getText();
                String ref_url = url.getText();
                if (!ref_desc.equals("") || !ref_url.equals("")) {
                    // Both have values
                    // Try to validate URL
                    try {

                        URL u = new URL(url.getText());
                        u.toURI();
                        url_string = url.getText(); // Causes loop to end with a valid url

                    } catch (MalformedURLException ex) {
                        url_string = null;
                        //ex.printStackTrace();
                    } catch (URISyntaxException ex) {
                        url_string = null;
                        //ex.printStackTrace();
                    }

                }

            } else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                System.out.println("User clicked cancel/close");
                return; // ends the loop without making any chages
            }

            if (url_string == null) {
                // We need to show an error saying that the url failed to parse
                Object[] message2 = {
                    error, "Description:", description, "URL:", url
                };
                message = message2;

            }
        }

        // If you get here there is a valid reference URL and description
        Reference ref = new Reference(description.getText(), url.getText());

        if (current == null) {
            // Add it to the vuln
            vuln.addReference(ref);
            // Add it to the GUI 
            dlm.addElement(ref);
            System.out.println("Valid reference added: " + ref);
        } else {
            // Modify it in the vuln
            vuln.modifyReference(current, ref);
            // Update the GUI
            dlm.removeElement(current);
            dlm.addElement(ref);
            System.out.println("Valid reference modified: " + ref);
        }
    }

    private void doDelete() {

        DefaultTreeModel dtm = (DefaultTreeModel) PersonalVulnsTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        TreePath[] paths = PersonalVulnsTree.getSelectionPaths();

        if (paths == null) {
            return;
        }

        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (i == 0) {
                // This is the first delete operation
                DefaultMutableTreeNode previous = (DefaultMutableTreeNode) node.getPreviousSibling();
                // Consider saving deleted vulns into a scratchpad file TODO

                // If it is null here we have no nodes above it, we get the next sibling below
                if (previous == null) {
                    previous = (DefaultMutableTreeNode) node.getNextSibling();
                }

                // If it is still null here there are no nodes in the tree. Point to the root. Avoids NullPointerException
                if (previous == null) {
                    previous = root;
                }

                TreePath p = new TreePath(previous.getPath());
                PersonalVulnsTree.setSelectionPath(p);
            }

            if (node.getParent() != null) {
                dtm.removeNodeFromParent(node);
            }
        }

        if (root.getChildCount() == 0) {
            clearGUI();
            // also delete personal vulns file. User has emptied their entire database
            this.person_vulns_file.delete();
            System.out.println("Personal Vulns Emptied, Deleted file: " + this.person_vulns_file.getAbsolutePath());
        } else {
            // save the file again because some still exist

            try {
                // Try the save
                new SaveFileExporter().save(this.person_vulns_file, root);
            } catch (Exception ex) {
                // Handle all exceptions
                ex.printStackTrace();   // to the terminal log                 
                JOptionPane.showMessageDialog(null, ex); // show user
            }

        }

    }

    public void clearGUI() {
        this.PersonalVulnsTitle.setText("");
        this.PersonalVulnsCvssVector.setText("");
        this.PersonalVulnScore.setText("");
        this.PersonalVulnRiskCategory.setText("");
        this.PersonalVulnDescription.setText("");
        this.PersonalVulnRecommendation.setText("");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PersonalVulnsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PersonalVulnsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PersonalVulnsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PersonalVulnsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JMenuItem DeleteReferenceOption;
    private javax.swing.JPanel DescriptionPanel;
    private javax.swing.JMenuItem EditReferenceOption;
    private javax.swing.JMenuItem InsertReference;
    private javax.swing.JMenuItem LaunchInBrowser;
    private javax.swing.JButton PersonalEditRiskButton;
    private javax.swing.JTextField PersonalTreeFilter;
    private javax.swing.JTextPane PersonalVulnDescription;
    private javax.swing.JTextPane PersonalVulnRecommendation;
    private javax.swing.JList PersonalVulnReferences;
    private javax.swing.JTextField PersonalVulnRiskCategory;
    private javax.swing.JTextField PersonalVulnScore;
    private javax.swing.JTextField PersonalVulnsCvssVector;
    private javax.swing.JTextField PersonalVulnsTitle;
    public javax.swing.JTree PersonalVulnsTree;
    private javax.swing.JPanel RecommendationPanel;
    private javax.swing.JPanel ReferencesPanel;
    private javax.swing.JPanel RightPanel;
    private javax.swing.JPopupMenu VulnReferencesContextMenu;
    private javax.swing.JPanel VulnerabilityTopPanel;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JScrollPane scrollypaney;
    // End of variables declaration//GEN-END:variables

    private void setupDocumentListeners() {
        this.PersonalVulnsTitle.getDocument().addDocumentListener(this.titleDocumentListener);
        this.PersonalVulnDescription.getDocument().addDocumentListener(this.descriptionDocumentListener);
        this.PersonalVulnRecommendation.getDocument().addDocumentListener(this.recommendationDocumentListener);
    }

    private void removeDocumentListeners() {
        this.PersonalVulnsTitle.getDocument().removeDocumentListener(this.titleDocumentListener);
        this.PersonalVulnDescription.getDocument().removeDocumentListener(this.descriptionDocumentListener);
        this.PersonalVulnRecommendation.getDocument().removeDocumentListener(this.recommendationDocumentListener);
    }

}
