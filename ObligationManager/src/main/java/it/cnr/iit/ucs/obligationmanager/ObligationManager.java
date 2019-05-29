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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.ucs.pip.PIPOMInterface;
import it.cnr.iit.ucs.properties.components.ObligationManagerProperties;
import it.cnr.iit.utility.JsonUtility;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This class represents a possible implementation of the ObligationManager
 * <p>
 * The obligation manager is the component in charge of handling the various
 * obligations we can have in the policy. Since an obligation manager can handle
 * two different types of obligations, one directed to the PEP and the other
 * directed to the PIPs {attribute update operations}, this component will
 * behave as follows:
 * <ol>
 * <li>Upon receiving the result of the PDP:</li>
 * <li>Asks to the PIPs to perform the obligations directed to them (in this
 * case basically we follow the same approach of the attribute retrieval)</li>
 * <li>finally removes the sent obligations from the list of pending
 * obligations</li>
 * <li>Remained obligations will be sent to the PEP along with the result of the
 * evaluation</li>
 * </ol>
 *
 * </p>
 *
 * @author antonio
 *
 */
public final class ObligationManager implements ObligationManagerInterface {

    private final Logger log = Logger.getLogger( ObligationManager.class.getName() );

    private static final String MSG_ERR_UNMARSHAL = "Error unmarshalling json : {0}";
    private static final String MSG_ERR_DECODE_OBLIGATION = "Error decoding obligation : {0}";

    private List<PIPOMInterface> pipList;
    private ObligationManagerProperties properties; // NOSONAR

    public ObligationManager( ObligationManagerProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;
    }

    @Override
    public final boolean setPIPs( List<PIPOMInterface> pips ) {
        if( ( pips == null || pips.isEmpty() ) ) {
            log.log( Level.SEVERE,
                "Invalid provided PIPS : pips_null {0}\t"
                        + "pips_empty {1}\t",
                new Object[] {
                    pips == null,
                    pips != null ? pips.isEmpty() : Boolean.FALSE
                } );
            return false;
        }

        pipList = pips;
        return true;
    }

    /**
     * Translates the obligation from String to an Object that can be passed to a
     * PIP
     *
     * @param evaluation
     *          the evaluation returned by the PDP
     * @param status
     *          the status of the given session
     *          obligation to the PIP
     */
    @Override
    public PDPEvaluation translateObligations( PDPEvaluation evaluation, String status ) {
        Reject.ifNull( evaluation );
        Reject.ifNull( evaluation.getSessionId() );
        Reject.ifNull( status );
        Reject.ifTrue( pipList == null || pipList.isEmpty() );

        List<String> obligationsList = evaluation.getObligations();
        if( obligationsList == null ) {
            return null;
        }

        StringBuilder pipName = new StringBuilder();
        HashMap<String, ObligationInterface> obligationMap = new HashMap<>();
        for( String obligationString : obligationsList ) {
            ObligationInterface obligation = (ObligationInterface) createObjectFromString( obligationString, pipName );
            if( obligation != null ) {
                obligation.setSessionId( evaluation.getSessionId() );
                obligation.setStep( status );
                if( obligation.getAttributeId() != null ) {
                    obligationMap.put( obligation.getAttributeId(),
                        obligation );
                } else {
                    obligationMap.put( pipName.toString(), obligation );
                }
            }
        }

        for( PIPOMInterface pip : pipList ) {
            pip.performObligation( obligationMap.get( pipName.toString() ) );
        }

        return null;
    }

    /**
     * Given the string representing the whole obligation, this function extracts
     * the name of the class and, from there, it will create the related object
     *
     * @param obligation
     *          the obligation in string format
     * @return an object representing the obligation the PIP has to perform
     */
    private Object createObjectFromString( String obligation,
            StringBuilder classNameBuilder ) {
        String className = extractClassName( obligation );
        Class<?> clazz;
        try {
            // TODO UCS-32 NOSONAR
            clazz = Class.forName( className );
            classNameBuilder.append( className );
            if( clazz.isInstance( ObligationInterface.class ) ) {
                throw new IllegalArgumentException( "Invalid class provided: " + className );
            }
        } catch( ClassNotFoundException e ) {
            log.severe( String.format( MSG_ERR_UNMARSHAL, e.getMessage() ) );
            return null;
        }
        String json = getJson( obligation );
        return JsonUtility.loadObjectFromJsonString( json, clazz );
    }

    private String extractClassName( String obligation ) {
        String className = obligation.split( "=" )[0];
        return "it.cnr.iit.ucs.obligationmanager.obligationobjects."
                + className;
    }

    /**
     * Given the string in the obligation, it decodes it in order to obtain a
     * string that can be converted into a JSON format
     *
     * @param obligation
     *          the obligation to be performed
     * @return a String that can be converted into JSON
     */
    private String getJson( String obligation ) {
        try {
            return URLDecoder.decode( obligation.split( "=" )[1], "UTF-8" );
        } catch( UnsupportedEncodingException e ) {
            log.severe( String.format( MSG_ERR_DECODE_OBLIGATION, e.getMessage() ) );
            return null;
        }
    }

}
