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
package it.cnr.iit.ucs.obligationmanager.obligationobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.cnr.iit.ucs.obligationmanager.ObligationInterface;

public final class PIPHistoryObligation implements ObligationInterface {

    private List<Tran> trans = null;
    private Map<String, Object> additionalProperties = new HashMap<>();
    private String AttributeId;

    private String step;
    private String sessionId;

    public List<Tran> getTRANS() {
        return trans;
    }

    public void setTRANS( List<Tran> tRANS ) {
        this.trans = tRANS;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty( String name, Object value ) {
        this.additionalProperties.put( name, value );
    }

    @Override
    public String getAttributeId() {
        return AttributeId;
    }

    @Override
    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    @Override
    public void setStep( String step ) {
        this.step = step;
    }

    public String getStep() {
        return step;
    }

    public String getSessionId() {
        return sessionId;
    }

}
