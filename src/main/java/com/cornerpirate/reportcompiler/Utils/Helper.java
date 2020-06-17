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

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JRootPane;

/**
 * Various convenience features used throughout the application.
 *
 * @author cornerpirate
 */
public class Helper {

    /**
     * A grep within a file to check for the search term.
     *
     * @param file
     * @param searchTerm
     * @return
     * @throws FileNotFoundException
     */
    public boolean fileContainsString(File file, String searchTerm) throws FileNotFoundException {

        boolean result = false;
        Scanner in = null;
        try {
            in = new Scanner(new FileReader(file));
            while (in.hasNextLine() && !result) {
                result = in.nextLine().indexOf(searchTerm) >= 0;
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    public long getFileSizeInMB(File file) {
        // Get length of file in bytes
        long fileSizeInBytes = file.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;

        return fileSizeInMB;
    }

    public String MD5(String original) {
        StringBuffer sb = new StringBuffer();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            byte[] digest = md.digest();

            for (byte b : digest) {
                sb.append(Integer.toHexString((int) (b & 0xff)));
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private int getFontSize() {

        Properties properties = new Properties();
        File properties_file = new File(new File((getClass().getProtectionDomain().getCodeSource().getLocation()).getFile()).getParent() + File.separator + "UserPreferences.properties");

        try {

            InputStream in = new FileInputStream(properties_file);
            properties.load(in);

            if (properties.containsKey("Font_Size")) {
                // Lets set the font size
                String users_font_size = properties.getProperty("Font_Size");
                try {
                    int fs = Integer.parseInt(users_font_size) ;
                    return fs ;
                } catch (Exception ex) {
                    ex.printStackTrace(); // if we get here somebody tampered with their preferences file
                }
            }

        } catch (java.io.FileNotFoundException fnfex) {
            System.out.println("User does not have any preferences file yet");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
                
        return 12;
    }

    public void setFontSize(Font currentfont, JRootPane rootPane) {
        // Set the newsize into the GUI.
        //Font currentfont = this.VulnTitleTextField.getFont();
        int newsize = this.getFontSize() ;

        List components = this.getAllComponents(rootPane);
        Iterator it = components.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof javax.swing.JComponent) {
                javax.swing.JComponent comp = (javax.swing.JComponent) obj;
                comp.setFont(currentfont.deriveFont((float) newsize));
            } else if(obj instanceof javax.swing.JTree) {
                javax.swing.JTree tree = (javax.swing.JTree)obj ;
                tree.setFont(currentfont.deriveFont((float) newsize));
            } else if(obj instanceof javax.swing.JTable) {
                javax.swing.JTable table = (javax.swing.JTable)obj ;
                table.setFont(currentfont.deriveFont((float) newsize));
            }
        }
    }

    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

}
