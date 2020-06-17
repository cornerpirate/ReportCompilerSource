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

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This interface specifies the requirements for a valid importing class.
 * @author cornerpirate
 */
public interface ImporterInterface {
    
    public boolean isValid(File file) ;
    public DefaultMutableTreeNode readFile(File file) ;
    
}
