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

import com.cornerpirate.reportcompiler.Exporters.ExportToExcel;
import com.cornerpirate.reportcompiler.Exporters.SaveFileExporter;
import com.cornerpirate.reportcompiler.Importers.ImportReportCompiler;
import com.cornerpirate.reportcompiler.Importers.UniversalFileImporter;
import com.cornerpirate.reportcompiler.Models.CVE;
import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.ImportFile;
import com.cornerpirate.reportcompiler.Models.Reference;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import com.cornerpirate.reportcompiler.Utils.AffectedHostsTableModel;
import com.cornerpirate.reportcompiler.Utils.CVEUtils;
import com.cornerpirate.reportcompiler.Utils.Helper;
import com.cornerpirate.reportcompiler.Utils.TreeUtils;
import com.cornerpirate.reportcompiler.Utils.VulnDescriptionDocumentListener;
import com.cornerpirate.reportcompiler.Utils.VulnRecommendationDocumentListener;
import com.cornerpirate.reportcompiler.Utils.VulnTitleDocumentListener;
import com.cornerpirate.reportcompiler.Views.VulnerabilityViewTreeCellRenderer;
import com.cornerpirate.reportcompiler.WorkerThreads.ImportScanTask;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/** 
 *
 * @author cornerpirate
 */
public class MainWindow extends javax.swing.JFrame {

    final JFileChooser fileChooser = new JFileChooser();
    private Properties properties; // Used to store user preferences
    final File properties_file = new File(new File((getClass().getProtectionDomain().getCodeSource().getLocation()).getFile()).getParent() + File.separator + "UserPreferences.properties");
    final Helper helper = new Helper();
    final UniversalFileImporter universal_file_importer = new UniversalFileImporter();
    public DefaultListModel importingListModel = new DefaultListModel();
    public int font_size = 12;
    // An undo manager for all our undo needs
    public UndoManager undo_manager = new UndoManager();
    protected File save_file = null;
    protected PersonalVulnsWindow personal_vulns_window = null;
    // Document Listeners
    protected VulnTitleDocumentListener titleDocumentListener;
    protected VulnDescriptionDocumentListener descriptionDocumentListener;
    protected VulnRecommendationDocumentListener recommendationDocumentListener;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        fileChooser.setMultiSelectionEnabled(true);

        // Create the document Listeners
        this.titleDocumentListener = new VulnTitleDocumentListener(VulnTitleTextField, VulnTree);
        this.descriptionDocumentListener = new VulnDescriptionDocumentListener(VulnDescriptionTextPane, VulnTree);
        this.recommendationDocumentListener = new VulnRecommendationDocumentListener(VulnRecommendationTextPane, VulnTree);

        //Setup tool tips for the vuln tree
        ToolTipManager.sharedInstance().registerComponent(VulnTree);

        // Check for saved preferences and handle them appropriately
        handleUserPreferences();
        // Now create the personal vulns window and populate it with current vulns if possible
        setupPersonalVulnsWindow();

        // get users preferred font size if they have it. Sets to 12 if they dont.
        Font currentfont = this.VulnTitleTextField.getFont();
        helper.setFontSize(currentfont, this.getRootPane());
        //helper.setFontSize(currentfont, this.MergeVulnScreen.getRootPane());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ImportScanScreen = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ImportFile = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        FileType = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        FileSize = new javax.swing.JTextField();
        ProgressBar = new javax.swing.JProgressBar();
        VulnTreeContextMenu = new javax.swing.JPopupMenu();
        MergeButton = new javax.swing.JMenuItem();
        LookupCVE = new javax.swing.JMenuItem();
        AddToPersonalVulns = new javax.swing.JMenuItem();
        ClearHash = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        DeleteButton = new javax.swing.JMenuItem();
        VulnAffectedHostsContextMenu = new javax.swing.JPopupMenu();
        AddHostsButton = new javax.swing.JMenuItem();
        EditHostname = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        DeleteHost = new javax.swing.JMenuItem();
        VulnReferencesContextMenu = new javax.swing.JPopupMenu();
        InsertReference = new javax.swing.JMenuItem();
        EditReferenceOption = new javax.swing.JMenuItem();
        LaunchInBrowser = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        DeleteReferenceOption = new javax.swing.JMenuItem();
        ManageAffectedHosts = new javax.swing.JDialog();
        jScrollPane5 = new javax.swing.JScrollPane();
        ListOfHosts = new javax.swing.JList();
        jScrollPane9 = new javax.swing.JScrollPane();
        ListOfOpenPorts = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        MainScreenBottomPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        VulnTreeFilter = new javax.swing.JTextField();
        ExtraInfoLabel = new javax.swing.JLabel();
        jSplitPane2 = new javax.swing.JSplitPane();
        ViewModeTabPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        VulnTree = new javax.swing.JTree();
        jScrollPane3 = new javax.swing.JScrollPane();
        HostTree = new javax.swing.JTree();
        RightPanelCardLayout = new javax.swing.JPanel();
        RightPanelVulnView = new javax.swing.JPanel();
        VulnerabilityTopPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        VulnTitleTextField = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        VulnCVSSVectorTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        VulnRiskCategory = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        VulnScore = new javax.swing.JTextField();
        EditRiskButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        VulnRecommendationsPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        VulnRecommendationTextPane = new javax.swing.JTextPane();
        jSplitPane4 = new javax.swing.JSplitPane();
        VulnReferencesPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        VulnReferencesList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        VulnAffectedHostsTable = new javax.swing.JTable();
        VulnDescriptionPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        VulnDescriptionTextPane = new javax.swing.JTextPane();
        RightPanelHostsView = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        exitButton = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        increaseFont = new javax.swing.JMenuItem();
        decreaseFont = new javax.swing.JMenuItem();

        ImportScanScreen.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        ImportScanScreen.setTitle("Report Compiler - Import Scan Screen");
        ImportScanScreen.setMinimumSize(new java.awt.Dimension(382, 220));
        ImportScanScreen.setModal(true);
        ImportScanScreen.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                ImportScanScreenWindowActivated(evt);
            }
        });

        jLabel1.setText("File Name:");

        ImportFile.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "One" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ImportFile.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ImportFile.setEnabled(false);
        jScrollPane2.setViewportView(ImportFile);

        jLabel2.setText("File Type:");

        FileType.setEnabled(false);

        jLabel3.setText("File Size:");

        FileSize.setEnabled(false);

        ProgressBar.setIndeterminate(true);

        javax.swing.GroupLayout ImportScanScreenLayout = new javax.swing.GroupLayout(ImportScanScreen.getContentPane());
        ImportScanScreen.getContentPane().setLayout(ImportScanScreenLayout);
        ImportScanScreenLayout.setHorizontalGroup(
            ImportScanScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ImportScanScreenLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(ImportScanScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(FileType, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(FileSize, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        ImportScanScreenLayout.setVerticalGroup(
            ImportScanScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ImportScanScreenLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addComponent(FileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel3)
                .addGap(6, 6, 6)
                .addComponent(FileSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(ProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        MergeButton.setText("Merge");
        MergeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MergeButtonActionPerformed(evt);
            }
        });
        VulnTreeContextMenu.add(MergeButton);

        LookupCVE.setText("Lookup CVE(s)");
        LookupCVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LookupCVEActionPerformed(evt);
            }
        });
        VulnTreeContextMenu.add(LookupCVE);

        AddToPersonalVulns.setText("Add to Personal Vulns");
        AddToPersonalVulns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddToPersonalVulnsActionPerformed(evt);
            }
        });
        VulnTreeContextMenu.add(AddToPersonalVulns);

        ClearHash.setText("Clear Hash(s)");
        ClearHash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearHashActionPerformed(evt);
            }
        });
        VulnTreeContextMenu.add(ClearHash);
        VulnTreeContextMenu.add(jSeparator1);

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });
        VulnTreeContextMenu.add(DeleteButton);

        VulnAffectedHostsContextMenu.setMinimumSize(new java.awt.Dimension(20, 20));

        AddHostsButton.setText("Add Host");
        AddHostsButton.setActionCommand("AddHost");
        AddHostsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddHostsButtonActionPerformed(evt);
            }
        });
        VulnAffectedHostsContextMenu.add(AddHostsButton);

        EditHostname.setText("Edit Hostname");
        EditHostname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditHostnameActionPerformed(evt);
            }
        });
        VulnAffectedHostsContextMenu.add(EditHostname);
        VulnAffectedHostsContextMenu.add(jSeparator2);

        DeleteHost.setText("Delete Host ('del' is hotkey)");
        DeleteHost.setActionCommand("DeleteHost");
        DeleteHost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteHostActionPerformed(evt);
            }
        });
        VulnAffectedHostsContextMenu.add(DeleteHost);

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

        ManageAffectedHosts.setTitle("Report Compiler - Manage Affected Hosts");
        ManageAffectedHosts.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                ManageAffectedHostsWindowOpened(evt);
            }
        });

        jScrollPane5.setViewportView(ListOfHosts);

        jScrollPane9.setViewportView(ListOfOpenPorts);

        jLabel5.setText("Hosts");

        jLabel13.setText("Ports");

        javax.swing.GroupLayout ManageAffectedHostsLayout = new javax.swing.GroupLayout(ManageAffectedHosts.getContentPane());
        ManageAffectedHosts.getContentPane().setLayout(ManageAffectedHostsLayout);
        ManageAffectedHostsLayout.setHorizontalGroup(
            ManageAffectedHostsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManageAffectedHostsLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(ManageAffectedHostsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(ManageAffectedHostsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ManageAffectedHostsLayout.setVerticalGroup(
            ManageAffectedHostsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManageAffectedHostsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManageAffectedHostsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel13))
                .addGap(13, 13, 13)
                .addGroup(ManageAffectedHostsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                    .addComponent(jScrollPane9))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Report Compiler - Main Window");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setText("Tree Filter:");

        VulnTreeFilter.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                VulnTreeFilterCaretUpdate(evt);
            }
        });

        ExtraInfoLabel.setFont(ExtraInfoLabel.getFont().deriveFont(ExtraInfoLabel.getFont().getStyle() | java.awt.Font.BOLD));
        ExtraInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout MainScreenBottomPanelLayout = new javax.swing.GroupLayout(MainScreenBottomPanel);
        MainScreenBottomPanel.setLayout(MainScreenBottomPanelLayout);
        MainScreenBottomPanelLayout.setHorizontalGroup(
            MainScreenBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainScreenBottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(VulnTreeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 354, Short.MAX_VALUE)
                .addComponent(ExtraInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        MainScreenBottomPanelLayout.setVerticalGroup(
            MainScreenBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainScreenBottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MainScreenBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(MainScreenBottomPanelLayout.createSequentialGroup()
                        .addComponent(ExtraInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainScreenBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20)
                        .addComponent(VulnTreeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(MainScreenBottomPanel, java.awt.BorderLayout.SOUTH);

        jSplitPane2.setDividerLocation(200);
        jSplitPane2.setDividerSize(20);
        jSplitPane2.setOneTouchExpandable(true);

        ViewModeTabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ViewModeTabPaneStateChanged(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("NOT IMPLEMENTED");
        VulnTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        VulnTree.setCellRenderer(new VulnerabilityViewTreeCellRenderer(true));
        VulnTree.setRootVisible(false);
        VulnTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VulnTreeMouseClicked(evt);
            }
        });
        VulnTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                VulnTreeValueChanged(evt);
            }
        });
        VulnTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                VulnTreeKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(VulnTree);

        ViewModeTabPane.addTab("Vuln View", jScrollPane1);

        jScrollPane3.setEnabled(false);

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        HostTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        HostTree.setRootVisible(false);
        jScrollPane3.setViewportView(HostTree);

        ViewModeTabPane.addTab("Host View", jScrollPane3);

        jSplitPane2.setLeftComponent(ViewModeTabPane);

        RightPanelCardLayout.setLayout(new java.awt.CardLayout());

        RightPanelVulnView.setLayout(new java.awt.BorderLayout());

        VulnerabilityTopPanel.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setLabelFor(VulnTitleTextField);
        jLabel9.setText("Title: ");
        jPanel8.add(jLabel9);

        VulnTitleTextField.setColumns(80);
        jPanel8.add(VulnTitleTextField);

        VulnerabilityTopPanel.add(jPanel8, java.awt.BorderLayout.NORTH);

        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("CVSS:");
        jPanel9.add(jLabel10);

        VulnCVSSVectorTextField.setEditable(false);
        VulnCVSSVectorTextField.setColumns(81);
        jPanel9.add(VulnCVSSVectorTextField);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Category:");
        jPanel9.add(jLabel11);

        VulnRiskCategory.setEditable(false);
        VulnRiskCategory.setColumns(8);
        jPanel9.add(VulnRiskCategory);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Score:");
        jPanel9.add(jLabel12);

        VulnScore.setEditable(false);
        VulnScore.setColumns(4);
        jPanel9.add(VulnScore);

        EditRiskButton.setText("Edit Risk");
        EditRiskButton.setToolTipText("Click here to see the Risk Calculator where scores can be modified");
        EditRiskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditRiskButtonActionPerformed(evt);
            }
        });
        jPanel9.add(EditRiskButton);

        VulnerabilityTopPanel.add(jPanel9, java.awt.BorderLayout.CENTER);

        RightPanelVulnView.add(VulnerabilityTopPanel, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(20);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);

        jSplitPane3.setDividerLocation(200);
        jSplitPane3.setDividerSize(20);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setOneTouchExpandable(true);

        VulnRecommendationsPanel.setLayout(new java.awt.BorderLayout());

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Recommendation");
        VulnRecommendationsPanel.add(jLabel8, java.awt.BorderLayout.PAGE_START);

        jScrollPane7.setViewportView(VulnRecommendationTextPane);

        VulnRecommendationsPanel.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        jSplitPane3.setLeftComponent(VulnRecommendationsPanel);

        jSplitPane4.setDividerSize(20);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setOneTouchExpandable(true);

        VulnReferencesPanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Affected Hosts");
        VulnReferencesPanel.add(jLabel4, java.awt.BorderLayout.PAGE_END);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("References");
        VulnReferencesPanel.add(jLabel7, java.awt.BorderLayout.PAGE_START);

        VulnReferencesList.setModel(new DefaultListModel() );
        VulnReferencesList.setToolTipText("Right click on this area to see options for references.");
        VulnReferencesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VulnReferencesListMouseClicked(evt);
            }
        });
        VulnReferencesList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                VulnReferencesListKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(VulnReferencesList);

        VulnReferencesPanel.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        jSplitPane4.setTopComponent(VulnReferencesPanel);

        jScrollPane4.setToolTipText("Right click on this area to insert new affected hosts. Select one or more and press 'del' to delete or use the right click 'delete' option.");
        jScrollPane4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane4MouseClicked(evt);
            }
        });
        jScrollPane4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane4KeyPressed(evt);
            }
        });

        VulnAffectedHostsTable.setAutoCreateRowSorter(true);
        VulnAffectedHostsTable.setModel(new AffectedHostsTableModel());

        /*new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "IP Address", "Hostname", "Portnumber", "Protocol"
            }
        ) {
            Class[] types = new Class[]{
                Host.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        }*///);
        VulnAffectedHostsTable.setToolTipText("");
        VulnAffectedHostsTable.setCellSelectionEnabled(true);
        VulnAffectedHostsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VulnAffectedHostsTableMouseClicked(evt);
            }
        });
        VulnAffectedHostsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                VulnAffectedHostsTableKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(VulnAffectedHostsTable);
        VulnAffectedHostsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jSplitPane4.setRightComponent(jScrollPane4);

        jSplitPane3.setRightComponent(jSplitPane4);

        jSplitPane1.setBottomComponent(jSplitPane3);

        VulnDescriptionPanel.setMinimumSize(new java.awt.Dimension(0, 50));
        VulnDescriptionPanel.setLayout(new java.awt.BorderLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Description");
        VulnDescriptionPanel.add(jLabel6, java.awt.BorderLayout.PAGE_START);

        jScrollPane8.setViewportView(VulnDescriptionTextPane);

        VulnDescriptionPanel.add(jScrollPane8, java.awt.BorderLayout.CENTER);

        jSplitPane1.setTopComponent(VulnDescriptionPanel);

        RightPanelVulnView.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        RightPanelCardLayout.add(RightPanelVulnView, "vulnView");

        javax.swing.GroupLayout RightPanelHostsViewLayout = new javax.swing.GroupLayout(RightPanelHostsView);
        RightPanelHostsView.setLayout(RightPanelHostsViewLayout);
        RightPanelHostsViewLayout.setHorizontalGroup(
            RightPanelHostsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1103, Short.MAX_VALUE)
        );
        RightPanelHostsViewLayout.setVerticalGroup(
            RightPanelHostsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );

        RightPanelCardLayout.add(RightPanelHostsView, "hostView");

        jSplitPane2.setRightComponent(RightPanelCardLayout);

        getContentPane().add(jSplitPane2, java.awt.BorderLayout.CENTER);

        jMenu1.setMnemonic('F');
        jMenu1.setText("File");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("New (Clear Tree)");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Open");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Save");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem11.setText("Save As");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jMenu1.add(exitButton);

        jMenuBar1.add(jMenu1);

        jMenu2.setMnemonic('V');
        jMenu2.setText("Vulnerabilities");
        jMenu2.setToolTipText("All vulnerability related operations. Import from a tool, create an entirely new one etc");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Import from Tool");
        jMenuItem1.setToolTipText("Select one or more files to import simultaneously. ");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Create New Vulnerability");
        jMenuItem2.setToolTipText("Add a new vulnerability to your test. When finished you can save it to your Personal Vulnerability database by right clicking on the issue in the tree");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setText("Manage Personal Vulns");
        jMenuItem7.setToolTipText("Allows you to delete or edit the text for vulnerabilities in your Personal Database");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem10.setText("Auto Merge");
        jMenuItem10.setToolTipText("Use this to automatically replace the title, description, recommendation, references, and risk score with vulnerabilities in your personal database.");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);

        jMenuBar1.add(jMenu2);

        jMenu4.setText("Hosts");

        jMenuItem9.setText("Import Hosts by Nmap");
        jMenuItem9.setEnabled(false);
        jMenu4.add(jMenuItem9);

        jMenuBar1.add(jMenu4);

        jMenu3.setMnemonic('E');
        jMenu3.setText("Export");

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setText("Excel XLS Format");
        jMenuItem8.setToolTipText("This can be used to send a high level debrief to clients in a spreadsheet format. Report Compiler also imports vulnerabilities back from these excel files if necessary.");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuBar1.add(jMenu3);

        jMenu5.setText("Options");

        increaseFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.CTRL_MASK));
        increaseFont.setText("Increase Font");
        increaseFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                increaseFontActionPerformed(evt);
            }
        });
        jMenu5.add(increaseFont);

        decreaseFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.CTRL_MASK));
        decreaseFont.setText("Decrease Font");
        decreaseFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decreaseFontActionPerformed(evt);
            }
        });
        jMenu5.add(decreaseFont);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void doImport() {
        // Get the file selector popup
        int returnVal = this.fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // Clear previous importing list
            this.importingListModel = new DefaultListModel();
            File[] importing_files = fileChooser.getSelectedFiles();
            for (File file : importing_files) {
                ImportFile impFile = new ImportFile(file.getAbsolutePath());

                this.properties.setProperty("Last_Accessed_Directory", impFile.getParent());
                System.out.println("Last_Accessed_Directory Updated: " + impFile.getParent());

                DefaultListModel dlm = new DefaultListModel();
                dlm.addElement(impFile);
                ImportFile.setModel(dlm);
                String fileType = universal_file_importer.getFileType(file);
                if (fileType.equalsIgnoreCase("Unknown")) {
                    String message = "Did not understand that file type, cannot import that.";
                    String title = "Cannot Import File";

                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
                    System.out.println(title);
                    System.out.println("User informed by popup, cleanly ignoring the file");
                } else {
                    FileType.setText(fileType);
                    FileSize.setText("" + new Helper().getFileSizeInMB(impFile));
                    ImportScanScreen.setLocationRelativeTo(this);
                    ImportScanScreen.setVisible(true);
                }
            }

        }
    }

    private void ImportScanScreenWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ImportScanScreenWindowActivated

        Object obj = ImportFile.getModel().getElementAt(0);
        if (obj != null && obj instanceof ImportFile) {
            ImportFile imFile = (ImportFile) obj;
            System.out.println("Importing File: " + imFile.getAbsolutePath());
            ProgressBar.setIndeterminate(true);

            ImportScanTask ist = new ImportScanTask(ProgressBar, imFile, ImportScanScreen);
            ist.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    if ("progress".equals(e.getPropertyName())) {
                        ProgressBar.setIndeterminate(false);
                        ProgressBar.setValue((Integer) e.getNewValue());
                        System.out.println("**: " + e.getNewValue());
                    }
                }
            });
            ist.execute();

            try {
                DefaultMutableTreeNode new_root = ist.get();
                System.out.println("Import Finished");
                DefaultMutableTreeNode existing_root = (DefaultMutableTreeNode) VulnTree.getModel().getRoot();
                if (existing_root.getChildCount() == 0) {
                    // The tree was empty so simply set the importe one into the model
                    VulnTree.setModel(new DefaultTreeModel(new_root));
                } else {
                    // The tree had existing children so we need to merge them
                    VulnTree.setModel(new DefaultTreeModel(new TreeUtils().mergeTrees(existing_root, new_root)));
                }

            } catch (InterruptedException ex) {
                //Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getStackTrace());
            } catch (ExecutionException ex) {
                //Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getStackTrace());
            }

        }
    }//GEN-LAST:event_ImportScanScreenWindowActivated

    /**
     * The vulnTree has had a selection event fire. This will find the last
     * selected node and display that on the right pane. It will also update the
     * count of selected node label at the bottom if more than one has been
     * selected.
     *
     * @param evt
     */
    private void VulnTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_VulnTreeValueChanged

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.VulnTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (node.isLeaf() && obj instanceof Vulnerability) {
            // this is a vulnerability we should update the UI to show the contents
            showVulnerability((Vulnerability) obj);
        }

        int number_of_nodes = this.VulnTree.getSelectionCount();
        this.ExtraInfoLabel.setText("Number of nodes selected: " + number_of_nodes);

    }//GEN-LAST:event_VulnTreeValueChanged

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        doImport();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

        // Create a new vuln
        Vulnerability vuln = new Vulnerability();
        vuln.setTitle("NEW");
        vuln.setIs_custom_risk(true);
        vuln.setRisk_category("None");
        vuln.setImport_tool("ReportCompiler");
        vuln.setIdentifier();
        // Add it to the tree
        DefaultTreeModel dtm = (DefaultTreeModel) VulnTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        DefaultMutableTreeNode new_vuln = new DefaultMutableTreeNode(vuln);
        root.add(new_vuln);
        // Refresh the GUI
        dtm.reload();
        // Now select 
        TreePath path = new TreePath(new_vuln.getPath());
        VulnTree.setSelectionPath(path);
        // Set focus on the title field
        this.VulnTitleTextField.requestFocus();
        this.VulnTitleTextField.selectAll();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void VulnTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_VulnTreeKeyPressed

        int pressed = evt.getKeyCode();
        if (pressed == KeyEvent.VK_DELETE) {

            doDelete();

        } else if (pressed == KeyEvent.VK_ENTER) {
            showNotes();
        }
    }//GEN-LAST:event_VulnTreeKeyPressed

    private void showNotes() {
        DefaultTreeModel dtm = (DefaultTreeModel) VulnTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) VulnTree.getLastSelectedPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof Vulnerability) {
            ShowNotesWindow shownotes = new ShowNotesWindow(this, this.VulnTree, true, node, null, this.fileChooser.getCurrentDirectory().getAbsolutePath());
            shownotes.setVisible(true);
        }
    }


    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        System.out.println("Save test button pressed");
        SaveTest();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        System.out.println("New Test Selected, Clear the tree and GUI");
        // Clear the tree
        DefaultTreeModel dtm = ((DefaultTreeModel) this.VulnTree.getModel());
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        root.removeAllChildren();
        dtm.reload();
        // Clear the GUI
        clearGUI();
        // Clear save file
        this.save_file = null;
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void VulnTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VulnTreeMouseClicked

        // Do nothing 
        Object obj = VulnTree.getLastSelectedPathComponent();

        if (obj == null) // No vulns selected so no interaction required
        {
            return;
        }

        if (evt.getButton() == MouseEvent.BUTTON3) {

            // At least one vuln was selected so we need to see how many and modify the context menu
            if (VulnTree.getSelectionCount() == 1) { // User cannot merge because they don't have two selected
                MergeButton.setEnabled(false);
            } else {
                MergeButton.setEnabled(true);
            }
            VulnTreeContextMenu.show(VulnTree, evt.getX(), evt.getY());
        } else if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            showNotes();
        }
    }//GEN-LAST:event_VulnTreeMouseClicked

    private void MergeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MergeButtonActionPerformed

        MergeVulnerabilitiesWindow mergeWindow = new MergeVulnerabilitiesWindow(this.VulnTree);
        mergeWindow.setVisible(true);


    }//GEN-LAST:event_MergeButtonActionPerformed

    private void VulnTreeFilterCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_VulnTreeFilterCaretUpdate
        String search_term = VulnTreeFilter.getText();

        DefaultTreeModel dtm = (DefaultTreeModel) VulnTree.getModel();
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
    }//GEN-LAST:event_VulnTreeFilterCaretUpdate

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        // TODO add your handling code here:
        doDelete();

    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void VulnAffectedHostsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VulnAffectedHostsTableMouseClicked

        handleAffectedHosts(evt);
    }//GEN-LAST:event_VulnAffectedHostsTableMouseClicked

    private void VulnAffectedHostsTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_VulnAffectedHostsTableKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            showNotesForSpecificHost();
        } else if (evt.getKeyChar() == KeyEvent.VK_DELETE) {
            deleteAffectedHosts();
        } else if (evt.getKeyCode() == KeyEvent.VK_INSERT) {
            addAffectedHost();
        }

    }//GEN-LAST:event_VulnAffectedHostsTableKeyPressed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        System.out.println("Export to excel clicked");
        DefaultTreeModel dtm = (DefaultTreeModel) this.VulnTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        System.out.println("Child Count: " + root.getChildCount());
        if (root.getChildCount() == 0) {
            System.out.println("No vulns in tree. Halting the export");
            return;
        }

        // If we get here then there are some vulns.
        // Prompt for an export file.
        boolean proceed = true;
        int returnVal = this.fileChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File save_file = fileChooser.getSelectedFile();
            System.out.println(save_file);

            if (save_file.exists() == true) {

                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to replace existing file?", "Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    proceed = false;
                }
            }

            if (proceed == true) {

                ExportToExcel excelOut = new ExportToExcel();
                try {
                    excelOut.writeExcel(save_file, root);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex);
                } 

            }

        }


    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void EditRiskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditRiskButtonActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) VulnTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (obj instanceof Vulnerability) {
            // Show Edit Risk Window
            CVSSv2Calculator riskWindow = new CVSSv2Calculator(this, true, this.VulnTree, node);
            riskWindow.setVisible(true);
        }

    }//GEN-LAST:event_EditRiskButtonActionPerformed

    public void addAffectedHost() {

        System.out.println("==addAffectedHost");

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.VulnTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        Vulnerability vuln = (Vulnerability) node.getUserObject();
        //ManageAffectedHosts.setVisible(true) ;

        // Old way of doing it
        JTextField ip_address = new JTextField();
        JTextField hostname = new JTextField();
        JTextField port_number = new JTextField();
        port_number.setText("0");
        JTextField protocol = new JTextField();
        protocol.setText("tcp");

        Object[] message = {
            "IP Address:", ip_address, "Hostname:", hostname, "Port Number:", port_number, "Protocol:", protocol
        };

        String new_ip = null;
        String new_hostname = null;
        String new_port_number = null;
        String new_protocol = null;

        while (new_ip == null || new_hostname == null || new_port_number == null || new_protocol == null) {
            int option = JOptionPane.showConfirmDialog(null, message, "Add affected host", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {

                new_ip = ip_address.getText();
                new_hostname = hostname.getText();
                new_port_number = port_number.getText();
                new_protocol = protocol.getText();
                Host host = new Host();
                host.setIp_address(new_ip);
                host.setHostname(new_hostname);
                host.setPortnumber(new_port_number);
                host.setProtocol(new_protocol);
                vuln.addAffectedHost(host);
                DefaultTableModel dtm = (DefaultTableModel) this.VulnAffectedHostsTable.getModel();
                dtm.addRow(host.getAsVector());
                dtm.fireTableDataChanged();

            } else {
                return; // End the infinite loop
            }

        }

    }
    private void AddHostsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddHostsButtonActionPerformed
        addAffectedHost();
    }//GEN-LAST:event_AddHostsButtonActionPerformed

    private void DeleteHostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteHostActionPerformed

        deleteAffectedHosts();

    }//GEN-LAST:event_DeleteHostActionPerformed

    private void EditHostnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditHostnameActionPerformed

        System.out.println("Edit Hostname Selected");
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.VulnTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        Object vuln_obj = node.getUserObject();
        if (!(vuln_obj instanceof Vulnerability)) {
            return;
        }

        Vulnerability current = (Vulnerability) vuln_obj;

        int row = this.VulnAffectedHostsTable.getSelectedRow();
        row = this.VulnAffectedHostsTable.convertRowIndexToModel(row);
        Object obj = this.VulnAffectedHostsTable.getModel().getValueAt(row, 0);
        if (obj instanceof Host) {
            Host previous = (Host) obj;

            JTextField hostname = new JTextField();
            hostname.setText(previous.getHostname());
            Object[] message = {
                "Hostname:", hostname
            };

            String new_hostname = null;
            while (new_hostname == null) {
                int option = JOptionPane.showConfirmDialog(null, message, "Modify Hostname", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    new_hostname = hostname.getText();
                    Host modified = previous.clone(); // Clone the previous one
                    modified.setHostname(new_hostname);
                    new TreeUtils().modifyHostname((DefaultMutableTreeNode) this.VulnTree.getModel().getRoot(), previous, modified);
                    //current.modifyAffectedHost(previous, modified); // Update the Vulnerability object
                    // Update the affected hosts table
                    DefaultTableModel dtm = (DefaultTableModel) this.VulnAffectedHostsTable.getModel();
                    // Clear the existing table
                    dtm.setRowCount(0);

                    // Set affected hosts into table
                    Enumeration enums = current.getAffectedHosts().elements();
                    while (enums.hasMoreElements()) {
                        Object obj2 = enums.nextElement();
                        if (obj instanceof Host) {
                            Host host = (Host) obj2;
                            Vector row2 = host.getAsVector(); // Gets the first two columns from the host
                            dtm.addRow(row2);
                        }
                    }

                } else {
                    return; // user cancelled or closed the prompt
                }
            }

        }


    }//GEN-LAST:event_EditHostnameActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        System.out.println("Window Closing");
        // Save preferences to disk
        savePreferences();

    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        System.out.println("Manage Personal Vulns Selected");
        this.personal_vulns_window.show();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void AddToPersonalVulnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddToPersonalVulnsActionPerformed

        System.out.println("Add to personal vulns section");

        DefaultTreeModel dtm = (DefaultTreeModel) VulnTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        TreePath[] paths = VulnTree.getSelectionPaths();

        // If there are no paths then no nodes have been selected, ignore the request
        if (paths == null) {
            return;
        }

        DefaultTreeModel personaldtm = (DefaultTreeModel) this.personal_vulns_window.PersonalVulnsTree.getModel();
        DefaultMutableTreeNode personalroot = (DefaultMutableTreeNode) personaldtm.getRoot();

        // For each selected node
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i]; // Get the node
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object obj = node.getUserObject();
            // Make sure it has an object of type Vulnerability
            if (obj != null && obj instanceof Vulnerability) {
                Vulnerability vuln = (Vulnerability) obj;
                if (vuln.containsIdentifier("24d459a81449d721c8f9a86c2913034", "ReportCompiler")) {
                    JOptionPane.showMessageDialog(null, "Attempting to add a personal vuln with the title 'NEW' has a dangerous identifier, please change the title first\n" + vuln.getTitle());
                    return;
                }
                // Clone that vulnerability so it isn't the same object that is in the main tree
                Vulnerability newvuln = new Vulnerability();
                newvuln.setTitle(vuln.getTitle());
                newvuln.setRecommendation(vuln.getRecommendation());
                newvuln.setDescription(vuln.getDescription());
                newvuln.cloneReferences(vuln);
                newvuln.cloneRisk(vuln);
                newvuln.cloneIdentifiers(vuln);
                // Add the clone to the personal vulns database
                // TODO do something clever where a vuln exists that has an ID in this one.
                if (new TreeUtils().OkToaddToPersonalVulns(personalroot, newvuln)) {
                    personalroot.add(new DefaultMutableTreeNode(newvuln));
                    System.out.println("Added to personal vulns: " + newvuln);
                    System.out.println("Hosts: " + newvuln.getAffectedHosts().size());
                } else {
                    JOptionPane.showMessageDialog(null, "A personal vuln exists that may match that already");
                }

            }
        }

        // Sort the tree
        personalroot = new TreeUtils().sortVulns(personalroot);
        // Update the model
        personaldtm.setRoot(personalroot);

        try {

            // Save the tree again
            new SaveFileExporter().save(this.personal_vulns_window.person_vulns_file, personalroot);
            System.out.println("Personal Vulns Saved: " + this.personal_vulns_window.person_vulns_file.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_AddToPersonalVulnsActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        System.out.println("Trying to clash the current tree against personal vulns to auto merge");
        DefaultTreeModel dtm_vulntree = (DefaultTreeModel) this.VulnTree.getModel();
        DefaultMutableTreeNode existing_root = (DefaultMutableTreeNode) dtm_vulntree.getRoot();

        DefaultTreeModel dtm_personal_vulns = (DefaultTreeModel) this.personal_vulns_window.PersonalVulnsTree.getModel();
        DefaultMutableTreeNode new_root = (DefaultMutableTreeNode) dtm_personal_vulns.getRoot();
        DefaultMutableTreeNode answer = new TreeUtils().autoMergePersonalVulns(existing_root, new_root);

        dtm_vulntree.setRoot(answer);
        dtm_vulntree.reload();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void VulnReferencesListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_VulnReferencesListKeyPressed

        int pressed = evt.getKeyCode();
        if (pressed == KeyEvent.VK_DELETE) {

            deleteReferences(VulnTree, VulnReferencesList);

        } else if (pressed == KeyEvent.VK_INSERT) {
            addReference(VulnTree, VulnReferencesList, null);
        }

    }//GEN-LAST:event_VulnReferencesListKeyPressed

    private void VulnReferencesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VulnReferencesListMouseClicked

        int clicked = evt.getButton();
        if (clicked == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            // A double click. Lets see if there is already a link or if we are creating a new one
            Object obj = VulnReferencesList.getSelectedValue();
            if (obj == null) {
                // We should simply show the 'add reference' prompy
            } else if (obj instanceof Reference) {
                // User probably wants to edit the existing reference
                Reference ref = (Reference) obj;
                addReference(VulnTree, VulnReferencesList, ref);
            }
        } else if (clicked == MouseEvent.BUTTON3) {
            // A right click. Show the context menu
            //int selected = VulnReferencesList.getSelectedIndex();
            List l = VulnReferencesList.getSelectedValuesList();
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
    }//GEN-LAST:event_VulnReferencesListMouseClicked

    private void jScrollPane4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane4MouseClicked
        handleAffectedHosts(evt);
    }//GEN-LAST:event_jScrollPane4MouseClicked

    private void EditReferenceOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditReferenceOptionActionPerformed

        Object obj = VulnReferencesList.getSelectedValue();
        if (obj == null) {
            // We should simply show the 'add reference' prompy
        } else if (obj instanceof Reference) {
            // User probably wants to edit the existing reference
            Reference ref = (Reference) obj;
            addReference(VulnTree, VulnReferencesList, ref);
        }

    }//GEN-LAST:event_EditReferenceOptionActionPerformed

    private void InsertReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InsertReferenceActionPerformed

        addReference(VulnTree, VulnReferencesList, null);

    }//GEN-LAST:event_InsertReferenceActionPerformed

    private void LaunchInBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LaunchInBrowserActionPerformed
        System.out.println("Launch References in Browser Selected");
        launchSelectedReferences(VulnReferencesList);
    }//GEN-LAST:event_LaunchInBrowserActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        System.out.println("Save As pressed");
        this.save_file = null; // Making this null means the file prompt will be shown
        SaveTest();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jScrollPane4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane4KeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_INSERT) {
            addAffectedHost();
        }

    }//GEN-LAST:event_jScrollPane4KeyPressed

    /**
     * This increases the font size up to a range of 30. It was smaller but then
     * I saw a 4k monitor and found that java does *not* scale well at all. Best
     * solution seemed to be to simply allow a more generous text size.
     *
     * This also is saved in the properties file and persists on next run
     *
     * @param evt
     */
    private void increaseFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_increaseFontActionPerformed
        System.out.println("Increase Font Selected");
        // Get the current size from the title.
        Font currentfont = this.VulnTitleTextField.getFont();
        int currentsize = currentfont.getSize();
        System.out.println("The current font size was: " + currentsize);
        int newsize = currentsize + 2;
        System.out.println("The new size looks a bit like: " + newsize);
        if (newsize <= 30) {

            // Save this into the preferences
            this.properties.setProperty("Font_Size", newsize + "");
            savePreferences();
            // Update GUI
            helper.setFontSize(currentfont, this.getRootPane());

        } else {
            System.out.println("Maximum font size is 30");
        }

    }//GEN-LAST:event_increaseFontActionPerformed

    /**
     * Reduces the font size to a minimum of 10.
     *
     * This is also saved in the properties file and persists on next run
     *
     * @param evt
     */
    private void decreaseFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decreaseFontActionPerformed
        System.out.println("Decrease Font Selected");
        // Get the current size from the title.
        Font currentfont = this.VulnTitleTextField.getFont();
        int currentsize = currentfont.getSize();
        System.out.println("The current font size was: " + currentsize);
        int newsize = currentsize - 2;
        System.out.println("The new size looks a bit like: " + newsize);
        if (newsize >= 10) {

            // Save this into the preferences
            this.properties.setProperty("Font_Size", newsize + "");
            savePreferences();
            // Update GUI
            helper.setFontSize(currentfont, this.getRootPane());

        } else {
            System.out.println("Minimum font size is 10");
        }
    }//GEN-LAST:event_decreaseFontActionPerformed

    private void DeleteReferenceOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteReferenceOptionActionPerformed
        deleteReferences(this.VulnTree, this.VulnReferencesList);
    }//GEN-LAST:event_DeleteReferenceOptionActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        System.out.println("file -> Open clicked");

        this.fileChooser.setMultiSelectionEnabled(false);
        int returnVal = this.fileChooser.showOpenDialog(this); // Disable people should only import RC Files here

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File impFile = this.fileChooser.getSelectedFile();
            // Update the properties files
            this.properties.setProperty("Last_Accessed_Directory", impFile.getParent());
            System.out.println("Last_Accessed_Directory Updated: " + impFile.getParent());
            ImportReportCompiler imprc = new ImportReportCompiler();
            if (imprc.isValid(impFile)) {
                System.out.println("Valid RC XML file: " + impFile.getAbsoluteFile());
                DefaultMutableTreeNode newroot = imprc.readFile(impFile);
                DefaultTreeModel dtm = (DefaultTreeModel) this.VulnTree.getModel();
                DefaultMutableTreeNode currentroot = (DefaultMutableTreeNode) dtm.getRoot();
                if (currentroot.getChildCount() == 0) {
                    // No existing nodes so don't merge.
                    dtm.setRoot(newroot);
                } else {

                }
            } else {
                JOptionPane.showMessageDialog(null, "The file -> Open menu only accepts ReportCompiler XML files. If you want to import scans try Vulnerabilities -> Import");
            }
        }
        this.fileChooser.setMultiSelectionEnabled(true);

    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void ViewModeTabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ViewModeTabPaneStateChanged
        JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        String title = sourceTabbedPane.getTitleAt(index);
        System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));

        Object layout = RightPanelCardLayout.getLayout();
        if (layout instanceof java.awt.CardLayout) {
            java.awt.CardLayout cl = (java.awt.CardLayout) layout;
            if (title.equalsIgnoreCase("Vuln View")) {
                // Show the vuln view card
                cl.show(RightPanelCardLayout, "vulnView");

            } else if (title.equalsIgnoreCase("Host View")) {
                DefaultMutableTreeNode hostRoot = new TreeUtils().convertVulnViewToHostView((DefaultMutableTreeNode) this.VulnTree.getModel().getRoot());
                DefaultTreeModel dtm = ((DefaultTreeModel) this.HostTree.getModel());
                dtm.setRoot(hostRoot);
                dtm.reload(hostRoot);
                // Expand all nodes to make everything visible
                new TreeUtils().expandAll(this.HostTree);
                // Show the host view card
                cl.show(RightPanelCardLayout, "hostView");
            }
        }

        //cardLayout.show(cardPanel, "CardToShow");
        //java.awt.CardLayout cl = (java.awt.CardLayout)(RightPanelCardLayout.getLayout()) ;
//        

    }//GEN-LAST:event_ViewModeTabPaneStateChanged

    private void ClearHashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearHashActionPerformed

        DefaultMutableTreeNode node = ((DefaultMutableTreeNode) VulnTree.getLastSelectedPathComponent());
        if (node == null) {
            return;
        }

        Object obj = node.getUserObject();
        if (!(obj instanceof Vulnerability)) {
            return; // here be monsters, most likely in the merge tree
        }

        Vulnerability vuln = (Vulnerability) obj;
        vuln.deleteAllIds();

    }//GEN-LAST:event_ClearHashActionPerformed

    private void ManageAffectedHostsWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ManageAffectedHostsWindowOpened

        System.out.println("==ManageAffectedHostsWindowOpened");
        // Scrape through the vuln tree to find a unique list of hosts and ports.


    }//GEN-LAST:event_ManageAffectedHostsWindowOpened

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        // TODO add your handling code here:
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    /**
     * Writes a row of data to a CSV file. For use with exporting CVE data
     * mainly.
     *
     * @param file
     * @param data
     * @param recordSeparator
     * @throws Exception
     */
    public void writeCSVLine(File file, String[] data) throws Exception {
        FileWriter writer = new FileWriter(file, true);
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(System.getProperty("line.separator"));
        CSVPrinter csvFilePrinter = new CSVPrinter(writer, csvFileFormat);
        csvFilePrinter.printRecord(data);
        writer.flush();
        writer.close();
        csvFilePrinter.close();
    }

    private void LookupCVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LookupCVEActionPerformed

        File save_file = null;
        // Ask user where they want to save file
        boolean proceed = true;
        int returnVal = this.fileChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            save_file = fileChooser.getSelectedFile();
            System.out.println("==LookupCVEActionPerformed(): " + save_file);

            if (save_file.exists() == true) {

                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to replace existing file?", "Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    proceed = false;
                }
            }

            if (proceed == true) {

                save_file.delete(); // wipe original
                handleCVELookup(save_file);

            }

        }
    }//GEN-LAST:event_LookupCVEActionPerformed

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
            java.util.logging.Logger.getLogger(MainWindow.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // Create right click context menu for most Text Components
        final JPopupMenu copypaste = new JPopupMenu();

        JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        copypaste.add(cut);

        JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copypaste.add(copy);

        JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        copypaste.add(paste);

        // Taken from here. It succinctly added a right click context menu to every text component.
        // http://stackoverflow.com/questions/19424574/adding-a-context-menu-to-all-swing-text-components-in-application
        javax.swing.UIManager.addAuxiliaryLookAndFeel(new LookAndFeel() {
            private final UIDefaults defaults = new UIDefaults() {
                public javax.swing.plaf.ComponentUI getUI(JComponent c) {
                    if (c instanceof javax.swing.text.JTextComponent) {
                        if (c.getClientProperty(this) == null) {
                            c.setComponentPopupMenu(copypaste);
                            c.putClientProperty(this, Boolean.TRUE);
                        }
                    }
                    return null;
                }
            };

            @Override
            public UIDefaults getDefaults() {
                return defaults;
            }

            ;
    @Override
            public String getID() {
                return "TextContextMenu";
            }

            @Override
            public String getName() {
                return getID();
            }

            @Override
            public String getDescription() {
                return getID();
            }

            @Override
            public boolean isNativeLookAndFeel() {
                return false;
            }

            @Override
            public boolean isSupportedLookAndFeel() {
                return true;
            }
        });

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AddHostsButton;
    private javax.swing.JMenuItem AddToPersonalVulns;
    private javax.swing.JMenuItem ClearHash;
    private javax.swing.JMenuItem DeleteButton;
    private javax.swing.JMenuItem DeleteHost;
    private javax.swing.JMenuItem DeleteReferenceOption;
    private javax.swing.JMenuItem EditHostname;
    private javax.swing.JMenuItem EditReferenceOption;
    private javax.swing.JButton EditRiskButton;
    private javax.swing.JLabel ExtraInfoLabel;
    private javax.swing.JTextField FileSize;
    private javax.swing.JTextField FileType;
    private javax.swing.JTree HostTree;
    private javax.swing.JList ImportFile;
    private javax.swing.JDialog ImportScanScreen;
    private javax.swing.JMenuItem InsertReference;
    private javax.swing.JMenuItem LaunchInBrowser;
    private javax.swing.JList ListOfHosts;
    private javax.swing.JList ListOfOpenPorts;
    private javax.swing.JMenuItem LookupCVE;
    private javax.swing.JPanel MainScreenBottomPanel;
    private javax.swing.JDialog ManageAffectedHosts;
    private javax.swing.JMenuItem MergeButton;
    private javax.swing.JProgressBar ProgressBar;
    private javax.swing.JPanel RightPanelCardLayout;
    private javax.swing.JPanel RightPanelHostsView;
    private javax.swing.JPanel RightPanelVulnView;
    private javax.swing.JTabbedPane ViewModeTabPane;
    private javax.swing.JPopupMenu VulnAffectedHostsContextMenu;
    private javax.swing.JTable VulnAffectedHostsTable;
    private javax.swing.JTextField VulnCVSSVectorTextField;
    private javax.swing.JPanel VulnDescriptionPanel;
    private javax.swing.JTextPane VulnDescriptionTextPane;
    private javax.swing.JTextPane VulnRecommendationTextPane;
    private javax.swing.JPanel VulnRecommendationsPanel;
    private javax.swing.JPopupMenu VulnReferencesContextMenu;
    private javax.swing.JList VulnReferencesList;
    private javax.swing.JPanel VulnReferencesPanel;
    private javax.swing.JTextField VulnRiskCategory;
    private javax.swing.JTextField VulnScore;
    private javax.swing.JTextField VulnTitleTextField;
    private javax.swing.JTree VulnTree;
    private javax.swing.JPopupMenu VulnTreeContextMenu;
    private javax.swing.JTextField VulnTreeFilter;
    private javax.swing.JPanel VulnerabilityTopPanel;
    private javax.swing.JMenuItem decreaseFont;
    private javax.swing.JMenuItem exitButton;
    private javax.swing.JMenuItem increaseFont;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    // End of variables declaration//GEN-END:variables

    private void showVulnerability(Vulnerability vulnerability) {

        // Unmap document listeners IF at all possible
        removeDocumentListeners();

        this.VulnTitleTextField.setText(vulnerability.getTitle());
        if (vulnerability.isIs_custom_risk() == true) {
            this.VulnCVSSVectorTextField.setText("NO CVSS VECTOR");
        } else {
            this.VulnCVSSVectorTextField.setText(vulnerability.getCvss_vector_string());
        }
        // Set vuln category
        this.VulnRiskCategory.setText(vulnerability.getRisk_category());
        this.VulnScore.setText("" + vulnerability.getRiskScore());
        this.VulnDescriptionTextPane.setText(vulnerability.getDescription());
        this.VulnRecommendationTextPane.setText(vulnerability.getRecommendation());

        // clear old references
        DefaultListModel ref_model = (DefaultListModel) this.VulnReferencesList.getModel();
        ref_model.removeAllElements();
        // add in the new references
        Enumeration ref_enums = vulnerability.getReferences().elements();
        while (ref_enums.hasMoreElements()) {
            Reference ref = (Reference) ref_enums.nextElement();
            ref_model.addElement(ref);
        }

        // Now setup undo listeners
        setupUndoListeners();
        setupDocumentListeners();

        DefaultTableModel dtm = (DefaultTableModel) this.VulnAffectedHostsTable.getModel();
        // Clear the existing table
        dtm.setRowCount(0);

        // Set affected hosts into table
        Enumeration enums = vulnerability.getAffectedHosts().elements();
        while (enums.hasMoreElements()) {
            Object obj = enums.nextElement();
            if (obj instanceof Host) {
                Host host = (Host) obj;
                Vector row = host.getAsVector(); // Gets the first two columns from the host
                dtm.addRow(row);
            }
        }

    }

    private void setupUndoListeners() {

        undo_manager.setLimit(-1);
        this.VulnTitleTextField.getDocument().addUndoableEditListener(this.undo_manager);
        this.VulnDescriptionTextPane.getDocument().addUndoableEditListener(this.undo_manager);
        this.VulnRecommendationTextPane.getDocument().addUndoableEditListener(this.undo_manager);

        // Add actions into the Action Map.
        VulnDescriptionTextPane.getActionMap().put("Undo",
                new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo_manager.canUndo()) {
                        undo_manager.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
        });

        VulnDescriptionTextPane.getActionMap().put("Redo",
                new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo_manager.canRedo()) {
                        undo_manager.redo();
                    }
                } catch (CannotRedoException e) {
                }
            }
        });

        VulnRecommendationTextPane.getActionMap().put("Undo",
                new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo_manager.canUndo()) {
                        undo_manager.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
        });

        VulnRecommendationTextPane.getActionMap().put("Redo",
                new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo_manager.canRedo()) {
                        undo_manager.redo();
                    }
                } catch (CannotRedoException e) {
                }
            }
        });

        VulnTitleTextField.getActionMap().put("Undo",
                new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo_manager.canUndo()) {
                        undo_manager.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
        });

        VulnTitleTextField.getActionMap().put("Redo",
                new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo_manager.canRedo()) {
                        undo_manager.redo();
                    }
                } catch (CannotRedoException e) {
                }
            }
        });

        // Add the key mappings
        VulnDescriptionTextPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        VulnDescriptionTextPane.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        VulnRecommendationTextPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        VulnRecommendationTextPane.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        VulnTitleTextField.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        VulnTitleTextField.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

    }

    private void removeDocumentListeners() {
        this.VulnTitleTextField.getDocument().removeDocumentListener(this.titleDocumentListener);
        this.VulnDescriptionTextPane.getDocument().removeDocumentListener(this.descriptionDocumentListener);
        this.VulnRecommendationTextPane.getDocument().removeDocumentListener(this.recommendationDocumentListener);

    }

    private void setupDocumentListeners() {
        this.VulnTitleTextField.getDocument().addDocumentListener(this.titleDocumentListener);
        this.VulnDescriptionTextPane.getDocument().addDocumentListener(this.descriptionDocumentListener);
        this.VulnRecommendationTextPane.getDocument().addDocumentListener(this.recommendationDocumentListener);
    }

    /**
     * This wipes out the UNDO list and clears the interface from previous
     * selections. It should be called when no vulns are in the tree.
     */
    private void clearGUI() {

        undo_manager.setLimit(-1);
        this.VulnTitleTextField.setText("");
        this.VulnDescriptionTextPane.setText("");
        this.VulnRecommendationTextPane.setText("");
        this.VulnCVSSVectorTextField.setText("");
        this.VulnScore.setText("");
        this.VulnRiskCategory.setText("");
        // TODO clear references.
        DefaultListModel dlm = (DefaultListModel) this.VulnReferencesList.getModel();
        dlm.removeAllElements();
        DefaultTableModel dtm = (DefaultTableModel) this.VulnAffectedHostsTable.getModel();
        // Clear the existing table
        dtm.setRowCount(0);

    }

    private void SaveTest() {

        if (this.save_file == null) {
            // Prompt the user to select a file
            int returnVal = this.fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                this.save_file = fileChooser.getSelectedFile();

                try {
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultTreeModel) this.VulnTree.getModel()).getRoot();
                    // Try the save
                    new SaveFileExporter().save(save_file, root);
                } catch (Exception ex) {
                    // Handle all exceptions
                    ex.printStackTrace();   // to the terminal log                 
                    JOptionPane.showMessageDialog(null, ex); // show user
                }

            }
        } else {
            // We have the file so save it.
            try {
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultTreeModel) this.VulnTree.getModel()).getRoot();
                // Try the save
                new SaveFileExporter().save(save_file, root);
            } catch (Exception ex) {
                // Handle all exceptions
                ex.printStackTrace();   // to the terminal log                 
                JOptionPane.showMessageDialog(null, ex); // show user
            }
        }

    }

    private void mergeScreenDisplayVuln(Vulnerability vulnerability) {

        /*
         this.MergeVulnTitleTextField.setText(vulnerability.getTitle());
         this.MergeVulnCVSSVectorTextField.setText(vulnerability.getCvss_vector_string());
         // Set vuln category
         this.MergeVulnRiskCategory.setText(vulnerability.getRisk_category());
         this.MergeVulnScore.setText("" + vulnerability.getRiskScore());
         this.MergeVulnDescriptionTextPane.setText(vulnerability.getDescription());
         this.MergeVulnRecommendationTextPane.setText(vulnerability.getRecommendation());
         // TODO do references

         DefaultTableModel dtm = (DefaultTableModel) this.MergeVulnAffectedHostsTable.getModel();
         // Clear the existing table
         dtm.setRowCount(0);

         // Set affected hosts into table
         Enumeration enums = vulnerability.getAffectedHosts().elements();
         while (enums.hasMoreElements()) {
         Object obj = enums.nextElement();
         if (obj instanceof Host) {
         Host host = (Host) obj;
         Vector row = host.getAsVector(); // Gets the first two columns from the host
         dtm.addRow(row);
         }
         }
         */
    }

    private void doDelete() {

        DefaultTreeModel dtm = (DefaultTreeModel) VulnTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
        TreePath[] paths = VulnTree.getSelectionPaths();

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
                //System.out.println("previous:" + previous);

                // If it is null here we have no nodes above it, we get the next sibling below
                if (previous == null) {
                    previous = (DefaultMutableTreeNode) node.getNextSibling();
                }

                // If it is still null here there are no nodes in the tree. Point to the root. Avoids NullPointerException
                if (previous == null) {
                    previous = root;
                }

                TreePath p = new TreePath(previous.getPath());
                VulnTree.setSelectionPath(p);
            }

            if (node.getParent() != null) {
                dtm.removeNodeFromParent(node);
            }
        }

        if (root.getChildCount() == 0) {
            clearGUI();
        }

    }

    private void showNotesForSpecificHost() {
        System.out.println("==showNotesForSpecificHost");
        int row = this.VulnAffectedHostsTable.convertRowIndexToModel(this.VulnAffectedHostsTable.getSelectedRow());
        System.out.println("Selected Row: " + row);
        Object obj = this.VulnAffectedHostsTable.getModel().getValueAt(row, 0);
        if (obj instanceof Host) {
            Host host = (Host) obj;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) VulnTree.getLastSelectedPathComponent();
            ShowNotesWindow shownotes = new ShowNotesWindow(this, this.VulnTree, true, node, host, this.fileChooser.getCurrentDirectory().getAbsolutePath());
            shownotes.setVisible(true);
        }

    }

    private void deleteAffectedHosts() {

        System.out.println("==deleteAffectedHost");
        // Get reference to visible vulnerability
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) VulnTree.getLastSelectedPathComponent();
        Vulnerability vuln = (Vulnerability) node.getUserObject();

        // Build the list of hosts to delete
        int[] rows = this.VulnAffectedHostsTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            int row = this.VulnAffectedHostsTable.convertRowIndexToModel(rows[i]);
            Object obj = this.VulnAffectedHostsTable.getModel().getValueAt(row, 0);
            if (obj instanceof Host) {
                Host host = (Host) obj;
                System.out.println("To Delete: " + host + ":" + host.getPortnumber());
                vuln.deleteAffectedHost(host);
            }
        }

        // update the GUI
        DefaultTableModel dtm = (DefaultTableModel) this.VulnAffectedHostsTable.getModel();
        // Clear the existing table
        dtm.setRowCount(0);

        // Set affected hosts into table
        Enumeration enums = vuln.getAffectedHosts().elements();
        while (enums.hasMoreElements()) {
            Object obj2 = enums.nextElement();
            if (obj2 instanceof Host) {
                Host host2 = (Host) obj2;
                Vector vecrow = host2.getAsVector(); // Gets the first two columns from the host
                dtm.addRow(vecrow);
            }
        }
    }

    private void handleUserPreferences() {

        // Check for presense of user preferences file
        if (this.properties == null) {
            this.properties = new Properties();
        }

        try {

            InputStream in = new FileInputStream(this.properties_file);
            properties.load(in);
            System.out.println("User preferences loaded from: " + VulnerabilityViewTreeCellRenderer.class
                    .getResource("/Properties/UserPreferences.properties"));
            System.out.println(
                    "Number of preferenes: " + properties.size());
            // Check for last accessed directory
            if (properties.containsKey(
                    "Last_Accessed_Directory")) {
                // We should update the JFileChooser to use this as a baseline
                String last_accessed_directory = properties.getProperty("Last_Accessed_Directory");
                this.fileChooser.setCurrentDirectory(new File(last_accessed_directory));
                System.out.println("last_accessed_directory: " + last_accessed_directory);
            }

        } catch (java.io.FileNotFoundException fnfex) {
            System.out.println("User does not have any preferences file yet");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupPersonalVulnsWindow() {
        this.personal_vulns_window = new PersonalVulnsWindow(this, true, this.VulnTree);
        try {

            boolean success = this.personal_vulns_window.readPersonalVulns();
            if (success == true) {
                // nicely done people
            } else {
                // garr problems
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        //this.personal_vulns_window.show();
    }

    private void handleAffectedHosts(MouseEvent evt) {

        Object obj = this.VulnTree.getLastSelectedPathComponent();
        if (obj == null) {
            return;
        }

        int row = VulnAffectedHostsTable.getSelectedRow();
        if (row == -1) // No vulns selected
        {
            // Setup the context menu as required
            EditHostname.setEnabled(false);
            DeleteHost.setEnabled(false);
        } else { // A vuln is selected
            // Setup the context menu as required
            EditHostname.setEnabled(true);
            DeleteHost.setEnabled(true);
        }

        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            // this was a double click on  the try
            showNotesForSpecificHost();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {

            VulnAffectedHostsContextMenu.show(VulnAffectedHostsTable, evt.getX(), evt.getY());
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

    private void savePreferences() {
        try {
            OutputStream out = new FileOutputStream(this.properties_file);
            this.properties.store(out, null);
            System.out.println("Updated preferences to: " + this.properties_file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleCVELookup(File save_file) {

        final File sf = save_file;
        // Best to do this as a background task it'll take time
        Runnable r = new Runnable() {
            public void run() {
                HashSet cves = new HashSet();
                // Find all selected vulns in the tree.
                TreePath[] paths = VulnTree.getSelectionPaths();
                for (int i = 0; i < paths.length; i++) {
                    // Loop through them and merge all CVEs into the cves HashSet
                    TreePath path = paths[i];
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Object obj = node.getUserObject();
                    if (obj instanceof Vulnerability) {
                        Vulnerability vuln = (Vulnerability) obj;
                        // add these references to the HashSet
                        cves.addAll(vuln.getCVEReferences());
                    }
                }

                // Get the answers from our local CSV file
                CVEUtils cveu = new CVEUtils();
                Vector answers = cveu.getCVEs(cves);

                try {
                    String[] headerrow = {"CVE ID", "Risk Score", "Summary"};
                    // Write header column to file
                    writeCSVLine(sf, headerrow);
                    // Now get all the details and make a CSV for the user.
                    Enumeration enums = answers.elements();
                    while (enums.hasMoreElements()) {
                        CVE c = (CVE) enums.nextElement();
                        System.out.println(c.getCveId() + ":" + c.getRiskScore());
                        writeCSVLine(sf, c.toStringArray());
                    }

                    // Open file in user's default programme
                    Desktop.getDesktop().open(sf);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        };

        new Thread(r).start();
    }

}
