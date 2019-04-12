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
package it.cnr.iit.xacmlutilities.policy;

import java.util.logging.Logger;

import it.cnr.iit.utility.JAXBUtility;

import oasis.names.tc.xacml.core.schema.wd_17.RuleType;

public class DefaultPermitRule {

    private static final Logger log = Logger.getLogger( DefaultPermitRule.class.getName() );

    private DefaultPermitRule() {}

    private static final String DEFAULT_PERMIT = "<Rule Effect=\"Permit\" RuleId=\"def-permit\"></Rule>";

    public static final RuleType getInstance() {
        try {
            return JAXBUtility.unmarshalToObject( RuleType.class,
                DEFAULT_PERMIT );
        } catch( Exception exception ) {
            log.severe( exception.getMessage() );
            return null;
        }
    }
}
