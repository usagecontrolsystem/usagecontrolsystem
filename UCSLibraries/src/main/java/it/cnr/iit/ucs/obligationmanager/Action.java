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
package it.cnr.iit.ucs.obligationmanager;

import java.util.HashMap;
import java.util.Map;

public class Action {

    private Integer actionType;
    private String stFrom;
    private String stTo;
    private String ruleId;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType( Integer actionType ) {
        this.actionType = actionType;
    }

    public String getStFrom() {
        return stFrom;
    }

    public void setStFrom( String stFrom ) {
        this.stFrom = stFrom;
    }

    public String getStTo() {
        return stTo;
    }

    public void setStTo( String stTo ) {
        this.stTo = stTo;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId( String ruleId ) {
        this.ruleId = ruleId;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperty( String name, Object value ) {
        additionalProperties.put( name, value );
    }

}
