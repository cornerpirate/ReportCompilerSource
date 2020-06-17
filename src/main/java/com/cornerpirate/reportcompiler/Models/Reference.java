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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a POJO that allows me to map reference data which consists
 * of a description + URL pair. By mapping it as an object I have been
 * able to add the "isCVE" check which is useful and may support
 * more advanced things in the future.
 * @author cornerpirate
 */
public class Reference {

    protected String description;
    protected String url;

    public Reference(String desc, String url) {
        this.setDescription(desc);
        this.setUrl(url);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return this.description + " " + this.url;
    }

    /**
     * Checks if this reference is a CVE. It does so using a regular expression.
     * This regex takes into account the new CVE ID format which includes arbitrary 
     * length numbers after the year. 
     * @return boolean
     */
    public boolean isCVE() {

        String pattern = "CVE-\\d{4}-\\d+";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(this.getUrl());
        return m.find();
    }

}
