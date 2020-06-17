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
package com.cornerpirate.reportcompiler.Exporters;

import com.cornerpirate.reportcompiler.Models.Host;
import com.cornerpirate.reportcompiler.Models.Note;
import com.cornerpirate.reportcompiler.Models.Vulnerability;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

/**
 *
 * @author cornerpirate
 */
public class ExportToWord {

    private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<Object>();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement<?>) obj).getValue();
        }

        if (obj.getClass().equals(toSearch)) {
            result.add(obj);
        } else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }

        }
        return result;
    }

    private WordprocessingMLPackage getTemplate() throws Docx4JException, FileNotFoundException {
        System.out.println("==ExportToWord=getTemplate") ;
        InputStream instream = ExportToWord.class.getResourceAsStream("/Template.docx");
        WordprocessingMLPackage template = WordprocessingMLPackage.load(instream);
        return template;
    }

    private void replacePlaceholder(WordprocessingMLPackage template, String name, String placeholder) {
        List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

        for (Object text : texts) {
            Text textElement = (Text) text;
            if (textElement.getValue().equals(placeholder)) {
                textElement.setValue(name);
            }
        }
    }

    private void writeDocxToStream(WordprocessingMLPackage template, File outFile) throws IOException, Docx4JException {
        template.save(outFile);
    }

    private void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template, ContentAccessor addTo) {
        // 1. get the paragraph
        List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);

        P toReplace = null;
        for (Object p : paragraphs) {
            List<Object> texts = getAllElementFromObject(p, Text.class);
            for (Object t : texts) {
                Text content = (Text) t;
                if (content.getValue().equals(placeholder)) {
                    toReplace = (P) p;
                    break;
                }
            }
        }

        // we now have the paragraph that contains our placeholder: toReplace
        // 2. split into seperate lines
        String as[] = StringUtils.splitPreserveAllTokens(textToAdd, '\n');

        for (int i = 0; i < as.length; i++) {
            String ptext = as[i];

            // 3. copy the found paragraph to keep styling correct
            P copy = (P) XmlUtils.deepCopy(toReplace);

            // replace the text elements from the copy
            List texts = getAllElementFromObject(copy, Text.class);
            if (texts.size() > 0) {
                Text textToReplace = (Text) texts.get(0);
                textToReplace.setValue(ptext);
            }

            // add the paragraph to the document
            addTo.getContent().add(copy);
        }

        // 4. remove the original one
        ((ContentAccessor) toReplace.getParent()).getContent().remove(toReplace);

    }

    private void addParagraphsToRun(R r, String text) {
        org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
        String[] parts = text.split("\n");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            Text t = factory.createText();
            //P p = factory.createP() ;
            t.setValue(part);
            //p.getContent().add(t);
            r.getContent().add(t);
            Br br = factory.createBr();
            r.getContent().add(br);
        }
        r.getContent().remove(0);
    }

    public void compileNotes(File outfile, Vulnerability vuln) {
        System.out.println("==ExportToWord=compileNotes: " + outfile.getAbsolutePath()); 
        try {

            WordprocessingMLPackage template = getTemplate();
            MainDocumentPart bodyPart = template.getMainDocumentPart();

            // get and delete the vuln table from the document.
            //List<Object> Tables = getAllElementFromObject(bodyPart, XWPFTable.class);
            //XWPFTable table = (XWPFTable)Tables.get(0);
            /*
            List<Object> content = bodyPart.getContent();
            Iterator it = content.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                content.remove(obj);
            }
                    */

            bodyPart.addStyledParagraphOfText("Heading2", vuln.getTitle());

            Enumeration enums = vuln.getAffectedHosts().elements();
            while (enums.hasMoreElements()) {
                Host host = (Host) enums.nextElement();
                String header = host.getIp_address() + " - " + host.getHostname() + " - " + host.getPortnumber() + "/" + host.getProtocol();
                bodyPart.addStyledParagraphOfText("Heading3", header);
                Note note = host.getNotes();
                String[] paragraphs = note.getNote_text().split("\\n");
                for (String para : paragraphs) {
                    bodyPart.addStyledParagraphOfText("Code", para);
                }
            }

            if (outfile.exists() == false) {
                template.save(outfile);
            }

            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(outfile);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
