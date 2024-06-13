/* 
 * Copyright 2024 cornerpirate.
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
package com.cornerpirate.reportcompiler.WorkerThreads;

import com.cornerpirate.reportcompiler.Importers.UniversalFileImporter;
import com.cornerpirate.reportcompiler.Models.ImportFile;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author cornerpirate
 */
public class ImportScanTask extends SwingWorker<DefaultMutableTreeNode, String> {

    private final JProgressBar ProgressBar;
    private final ImportFile imFile;
    private final JDialog window;
    private final UniversalFileImporter universal_file_importer = new UniversalFileImporter();

    public ImportScanTask(JProgressBar pb, ImportFile im, JDialog w) {
        this.ProgressBar = pb;
        this.imFile = im;
        this.window = w;
    }

    @Override
    protected DefaultMutableTreeNode doInBackground() throws Exception {
        setProgress(1);
        return universal_file_importer.readFile(imFile);
    }

    @Override
    protected void done() {
        window.dispose();
    }

}
