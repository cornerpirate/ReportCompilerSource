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
package com.cornerpirate.reportcompiler.Models;

/**
 *
 * @author cornerpirate
 */
public class Note {
    protected String note_text;
    protected String note_style;
    
    public Note(String text) {
        this.note_text = text;
    }

    public String getNote_text() {
        return note_text;
    }

    public void setNote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getNote_style() {
        return note_style;
    }

    public void setNote_style(String note_style) {
        this.note_style = note_style;
    }
    
    public void mergeNotes(String new_note) {
        this.note_text = this.note_text + "\n" + new_note;
    }
    
}
