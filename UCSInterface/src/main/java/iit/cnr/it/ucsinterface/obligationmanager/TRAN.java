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
package iit.cnr.it.ucsinterface.obligationmanager;

import java.util.HashMap;
import java.util.Map;

public class TRAN {

    private Integer ACTION_TYPE;
    private String ST_FROM;
    private String ST_TO;
    private String RULE_ID;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getACTIONTYPE() {
        return ACTION_TYPE;
    }

    public void setACTIONTYPE( Integer aCTIONTYPE ) {
        this.ACTION_TYPE = aCTIONTYPE;
    }

    public String getSTFROM() {
        return ST_FROM;
    }

    public void setSTFROM( String sTFROM ) {
        this.ST_FROM = sTFROM;
    }

    public String getSTTO() {
        return ST_TO;
    }

    public void setSTTO( String sTTO ) {
        this.ST_TO = sTTO;
    }

    public String getRULEID() {
        return RULE_ID;
    }

    public void setRULEID( String rULEID ) {
        this.RULE_ID = rULEID;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty( String name, Object value ) {
        this.additionalProperties.put( name, value );
    }

}
