/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.ucs.configuration.pip;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ucs.configuration.session_manager.Table;

/**
 * PIP conf
 *
 * @author antonio
 *
 */
public final class PipProperties {

    private String id;
    private String className;
    private String retrieval;
    private Table table;
    private String connection;
    private boolean multiattribute = false;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private String journalDir;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public String getRetrieval() {
        return retrieval;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes( List<Attribute> attributes ) {
        if( attributes == null ) {
            throw new IllegalArgumentException( "List of passed attributes is null" );
        }
        if( this.attributes == null ) {
            this.attributes = new ArrayList<>();
        }
        this.attributes.addAll( attributes );
    }

    public Table getTable() {
        return table;
    }

    public String getConnection() {
        return connection;
    }

    public boolean isMultiattribute() {
        return multiattribute;
    }

    public void setMultiattribute( boolean multiattribute ) {
        this.multiattribute = multiattribute;
    }

    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }
}
