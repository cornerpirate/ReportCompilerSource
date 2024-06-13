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

import com.cornerpirate.reportcompiler.Exporters.ExportToWord;
import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.Note;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import com.cornerpirate.reportcompiler.Utils.Helper;
import com.cornerpirate.reportcompiler.Utils.MessageConsole;
import com.cornerpirate.reportcompiler.Utils.TreeUtils;
import com.cornerpirate.reportcompiler.Views.VulnerabilityViewTreeCellRenderer;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import jsyntaxpane.DefaultSyntaxKit;
import org.python.util.PythonInterpreter;

/**
 *
 * @author cornerpirate
 */
public class ShowNotesWindow extends javax.swing.JFrame {

    JTree vulnTree;
    protected String start_dir;
    final Helper helper = new Helper();
    
    // Added to change from default Java logo.
    ImageIcon logo = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));

    /**
     * Creates new form ShowNotesWindow with a particular host selected
     */
    public ShowNotesWindow(java.awt.Frame parent, JTree vulnTree, boolean modal, DefaultMutableTreeNode node, Host selectedHost, String start_dir) {
        //super(parent, modal);
        initComponents();

        // set the icon 
        this.setIconImage(logo.getImage());        
        
        DefaultSyntaxKit.initKit();
        PythonText.setContentType("text/python");

        // Centre the frame
        this.setLocationRelativeTo(parent);

        // Redirect standard out and err to the pythonOutput textpane
        MessageConsole console = new MessageConsole(this.PythonOutput);
        console.redirectOut();
        console.redirectErr(Color.RED, null);

        Dimension min_dim = new Dimension(1000, 800);
        this.setMinimumSize(min_dim);
        this.vulnTree = vulnTree;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Notes");

        NotesTree.setCellRenderer(new VulnerabilityViewTreeCellRenderer(false));

        DefaultMutableTreeNode copy = new DefaultMutableTreeNode(node.getUserObject());
        DefaultMutableTreeNode selected_host_node = null;

        Vulnerability vuln = (Vulnerability) copy.getUserObject();
        this.setTitle(this.getTitle() + " - " + vuln.getTitle());
        Vector hosts = vuln.getAffectedHosts();
        Enumeration enums = hosts.elements();
        while (enums.hasMoreElements()) {
            Host host = (Host) enums.nextElement();

            if (selectedHost != null && host.equals(selectedHost) == true) {
                System.out.println("Selected Host was not null: " + selectedHost.getIdentifier());
                selected_host_node = new DefaultMutableTreeNode(host);
                copy.add(selected_host_node);
            } else {
                copy.add(new DefaultMutableTreeNode(host));
            }
        }

        root.add(copy);

        DefaultTreeModel dtm = (DefaultTreeModel) NotesTree.getModel();
        dtm.setRoot(root);

        if (selected_host_node == null) {
            // this means that the calling was without a specific host being selected so get the first child
            selected_host_node = (DefaultMutableTreeNode) copy.getFirstChild();
        }

        //System.out.println("SelectedHostNode: " + sele) ;
        TreePath path = new TreePath(selected_host_node.getPath());
        this.NotesTree.setSelectionPath(path);

        new TreeUtils().expandAll(NotesTree);
        this.start_dir = start_dir;

        // get users preferred font size if they have it. Sets to 12 if they dont.
        Font currentfont = this.ViewNotesTextPanel.getFont();
        helper.setFontSize(currentfont, this.getRootPane());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        StyleToolbar = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        NotesTree = new javax.swing.JTree();
        CardsPanel = new javax.swing.JPanel();
        notesPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        CompileNotesButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        ViewNotesTextPanel = new javax.swing.JEditorPane();
        gatherNotesPanel = new javax.swing.JPanel();
        TopPanel = new javax.swing.JPanel();
        RunButton = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        PythonOutput = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        PythonText = new javax.swing.JEditorPane();
        BottomPanel = new javax.swing.JPanel();
        ScrapeButton = new javax.swing.JButton();

        javax.swing.GroupLayout StyleToolbarLayout = new javax.swing.GroupLayout(StyleToolbar);
        StyleToolbar.setLayout(StyleToolbarLayout);
        StyleToolbarLayout.setHorizontalGroup(
            StyleToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 584, Short.MAX_VALUE)
        );
        StyleToolbarLayout.setVerticalGroup(
            StyleToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 49, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ReportCompiler - Show Notes Screen");
        setMinimumSize(new java.awt.Dimension(1415, 749));
        setPreferredSize(new java.awt.Dimension(1415, 749));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jSplitPane1.setDividerLocation(400);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(700, 580));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        NotesTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        NotesTree.setRootVisible(false);
        NotesTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                NotesTreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(NotesTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        CardsPanel.setLayout(new java.awt.CardLayout());

        notesPanel.setLayout(new java.awt.BorderLayout());

        CompileNotesButton.setText("Compile Notes");
        CompileNotesButton.setEnabled(false);
        CompileNotesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CompileNotesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(896, Short.MAX_VALUE)
                .addComponent(CompileNotesButton)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CompileNotesButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        notesPanel.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jScrollPane2.setViewportView(ViewNotesTextPanel);

        notesPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        CardsPanel.add(notesPanel, "notesViewCard");

        gatherNotesPanel.setLayout(new java.awt.BorderLayout());

        RunButton.setText("Run");
        RunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TopPanelLayout = new javax.swing.GroupLayout(TopPanel);
        TopPanel.setLayout(TopPanelLayout);
        TopPanelLayout.setHorizontalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addContainerGap(946, Short.MAX_VALUE)
                .addComponent(RunButton)
                .addContainerGap())
        );
        TopPanelLayout.setVerticalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RunButton)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        gatherNotesPanel.add(TopPanel, java.awt.BorderLayout.PAGE_START);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jScrollPane4.setViewportView(PythonOutput);

        jSplitPane2.setRightComponent(jScrollPane4);

        PythonText.setContentType(""); // NOI18N
        jScrollPane5.setViewportView(PythonText);

        jSplitPane2.setLeftComponent(jScrollPane5);

        gatherNotesPanel.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        ScrapeButton.setText("Scrape");
        ScrapeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ScrapeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BottomPanelLayout = new javax.swing.GroupLayout(BottomPanel);
        BottomPanel.setLayout(BottomPanelLayout);
        BottomPanelLayout.setHorizontalGroup(
            BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BottomPanelLayout.createSequentialGroup()
                .addContainerGap(932, Short.MAX_VALUE)
                .addComponent(ScrapeButton)
                .addContainerGap())
        );
        BottomPanelLayout.setVerticalGroup(
            BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BottomPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ScrapeButton)
                .addContainerGap())
        );

        gatherNotesPanel.add(BottomPanel, java.awt.BorderLayout.PAGE_END);

        CardsPanel.add(gatherNotesPanel, "gatherNotesCard");

        jSplitPane1.setRightComponent(CardsPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NotesTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_NotesTreeValueChanged

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.NotesTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        // Get the card layout
        CardLayout cl = (CardLayout) (this.CardsPanel.getLayout());

        Object obj = node.getUserObject();
        if (obj instanceof Vulnerability) {
            // Disable the tool bar
            ViewNotesTextPanel.setText("Notes are stored for each affected service, highlight a child node to view the current notes");

            String start_script = "";
            // create the dictionaries for the services.
            String services_dictionary = getServicesDictionary();

            String import_statments = "import commonutils as cu\n\n";

            String for_loop = "\n\nfor service in services:\n"
                    + "    ip = service[0]\n"
                    + "    port = service[1]\n"
                    + "    protocol = service[2]\n"
                    + "    #### The next line is used as a place marker for the start of text for a service\n"
                    + "    print '====HOST' + ip + ' - ' + port + '/' +  protocol + '===='\n\n"
                    + "    doWork(service) # this function will implement your logic\n"
                    + "    #### The next line is used as a place marker for the end of text for a service\n\n"
                    + "    print '====CLOSEHOST===='\n";

            String do_work = "#### User defined function that does custom logic\n"
                    + "def doWork(service):\n"
                    + "    ip = service[0]\n"
                    + "    port = int(service[1])\n"
                    + "    protocol = service[2]\n"
                    + "    #### add code below ";

            start_script = import_statments + services_dictionary + do_work + for_loop;

            //start_script.replaceAll("\t", "    ");
            this.PythonText.setText(start_script);

            // Show correct card    
            cl.show(this.CardsPanel, "gatherNotesCard");

        } else if (obj instanceof Host) {

            // Show correct card    
            cl.show(this.CardsPanel, "notesViewCard");

            Host host = (Host) obj;
            Note note = host.getNotes();
            if (note == null) {
                ViewNotesTextPanel.setText("");
            } else {
                ViewNotesTextPanel.setText(note.getNote_text());
            }

            ViewNotesTextPanel.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    saveNote();
                }

                public void removeUpdate(DocumentEvent e) {
                    saveNote();
                }

                public void insertUpdate(DocumentEvent e) {
                    saveNote();
                }

                public void saveNote() {
                    String text = ViewNotesTextPanel.getText();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) NotesTree.getLastSelectedPathComponent();
                    Object obj = node.getUserObject();
                    if (obj instanceof Host) {
                        Host host = (Host) obj;
                        Note note = host.getNotes();
                        if (note != null) {
                            note.setNote_text(text);
                        } else {
                            Note n = new Note(text);
                            host.setNotes(n);
                        }
                    }

                }
            });

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    jScrollPane2.getVerticalScrollBar().setValue(0);
                }
            });
        }
    }//GEN-LAST:event_NotesTreeValueChanged

    private void CompileNotesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CompileNotesButtonActionPerformed

        JFileChooser importFileChooser = new JFileChooser();
        importFileChooser.setCurrentDirectory(new File(this.start_dir));

        // Prompt the user to select a file
        int returnVal = importFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            
            File save_file = importFileChooser.getSelectedFile();
            DefaultTreeModel dtm = (DefaultTreeModel) NotesTree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getFirstChild();
            Vulnerability vuln = (Vulnerability) node.getUserObject();

            ExportToWord etw = new ExportToWord();
            etw.compileNotes(save_file, vuln);
        }

    }//GEN-LAST:event_CompileNotesButtonActionPerformed

    private void RunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunButtonActionPerformed
        this.PythonOutput.setText("");
        String python = this.PythonText.getText();
        PythonInterpreter interp = new PythonInterpreter();
        interp.setIn(System.in);
        interp.setOut(System.out);
        interp.exec(python);
    }//GEN-LAST:event_RunButtonActionPerformed

    private void ScrapeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ScrapeButtonActionPerformed
        String answers_text = PythonOutput.getText();
        String start = "====HOST";
        String end = "====CLOSEHOST====";
        String[] answers = answers_text.split(start);

        for (int i = 0; i < answers.length; i++) {
            String answer = answers[i];
            if (answer.length() > 0) {
                String host_str = answer.substring(0, answer.indexOf("===="));
                String[] host_array = host_str.split(" ");
                // Get the ip, port, and protocol from the string
                String ip = host_array[0];
                String port_proto = host_array[2];
                String port = port_proto.split("/")[0];
                String protocol = port_proto.split("/")[1];
                // Get the 'output' text
                String output = answer.substring(answer.indexOf("====")+4, answer.indexOf(end));
                // Create host
                Host thishost = new Host();
                thishost.setIp_address(ip);
                thishost.setProtocol(protocol);
                thishost.setPortnumber(port);
                thishost.setNotes(new Note(output));

                // Armed with both. Lets update the notes for each service
                DefaultMutableTreeNode root = ((DefaultMutableTreeNode) NotesTree.getModel().getRoot());
                DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) root.getFirstChild();
                Object obj = firstChild.getUserObject();
                if (obj instanceof Vulnerability) {
                    Vulnerability vuln = (Vulnerability) obj;
                    vuln.replaceNotes(thishost);
                }
            }
        }
    }//GEN-LAST:event_ScrapeButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        System.out.println(evt.getKeyCode()) ;
    }//GEN-LAST:event_formKeyPressed

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
            java.util.logging.Logger.getLogger(ShowNotesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ShowNotesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ShowNotesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShowNotesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JPanel CardsPanel;
    private javax.swing.JButton CompileNotesButton;
    private javax.swing.JTree NotesTree;
    private javax.swing.JTextPane PythonOutput;
    private javax.swing.JEditorPane PythonText;
    private javax.swing.JButton RunButton;
    private javax.swing.JButton ScrapeButton;
    private javax.swing.JPanel StyleToolbar;
    private javax.swing.JPanel TopPanel;
    private javax.swing.JEditorPane ViewNotesTextPanel;
    private javax.swing.JPanel gatherNotesPanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel notesPanel;
    // End of variables declaration//GEN-END:variables

    private String getServicesDictionary() {

        String answer = "services = [ ";

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) NotesTree.getModel().getRoot()).getFirstChild();
        if (root == null) {
            return "";
        }

        Enumeration enums = root.children();
        while (enums.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enums.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof Host) {
                Host host = (Host) obj;
                String ip = host.getIp_address();
                String port = host.getPortnumber();
                String protocol = host.getProtocol();
                String ans = "['" + ip + "', '" + port + "', '" + protocol + "']";
                answer = answer + ans + ",";
            }
        }
        answer = answer.substring(0, answer.length() - 1);
        answer = answer + "]";

        return answer;
    }
}
