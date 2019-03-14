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
package it.cnr.iit.usagecontrolframework.obligationmanager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;

import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLObligationManager;

import it.cnr.iit.ucsinterface.obligationmanager.ObligationInterface;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.pip.PIPOMInterface;

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
final public class ObligationManager implements ObligationManagerInterface {
    private final Logger LOGGER = Logger.getLogger( ObligationManager.class.getName() );

    // list of pips
    private List<PIPOMInterface> pipList;
    // pip retrieval
    private PIPOMInterface pipRetrieval;
    // xml describing the obligation manager
    private XMLObligationManager configuration;
    // states if the obligation manager has been correctly initialized
    private volatile boolean initialized = false;

    /**
     *
     * @param obligationManager
     */
    public ObligationManager( XMLObligationManager obligationManager ) {
        // BEGIN parameter checking
        if( obligationManager == null ) {
            return;
        }
        // END parameter checking
        configuration = obligationManager;
        initialized = true;
    }

    /**
     * Checks if the object is correctly initialized
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the PIPs with which
     */
    @Override
    final public boolean setPIPs( List<PIPOMInterface> pips, PIPOMInterface pipRetrieval ) {
        // BEGIN parameter checking
        if( ( ( pips == null || pips.size() == 0 ) && pipRetrieval == null )
                || !isInitialized() ) {
            LOGGER.severe( "Invalid provided PIPS: " + ( pips == null ) + "\t" + ( ( pips != null ? pips.size() == 0 : 0 ) )
                    + "\t" + ( pipRetrieval == null ) + "\t" + ( configuration == null ) );
            return false;
        }
        // END parameter checking
        pipList = pips;
        this.pipRetrieval = pipRetrieval;
        initialized = true;
        return true;
    }

    /**
     * Translates the obligation from String to an Object that can be passed to a
     * PIP
     *
     * @param pdpEvaluation
     *          the evaluation returned by the PDP
     * @param sessionId
     *          the id of the session
     * @param status
     *          the status of the given session FIXME: find a better way to link
     *          obligation to the PIP
     */
    @Override
    public PDPEvaluation translateObligations( PDPEvaluation pdpEvaluation,
            String sessionId, String status ) {
        // BEGIN parameter checking
        if( !isInitialized() || pdpEvaluation == null || sessionId == null
                || status == null ) {
            return null;
        }
        // END parameter checking
        ArrayList<String> obligationsString = pdpEvaluation.getObligations();
        if( obligationsString == null ) {
            return null;
        }
        StringBuilder pipName = new StringBuilder();
        HashMap<String, ObligationInterface> obligationMap = new HashMap<>();
        for( int index = 0; index < obligationsString.size(); ) {
            String obligation = obligationsString.get( index );

            Object obl = createObjectFromString( obligation, pipName );

            ObligationInterface obligationInterface = (ObligationInterface) obl;
            obligationInterface.setSessionId( sessionId );
            obligationInterface.setStep( status );
            // checks if the obligation is meant to be used by the PIP or by the PEP
            if( obligationInterface.getAttributeId() != null ) {
                obligationMap.put( obligationInterface.getAttributeId(),
                    obligationInterface );
                obligationsString.remove( index );
            } else {
                obligationMap.put( pipName.toString(), obligationInterface );
                index++;
            }

        }
        for( PIPOMInterface pip : pipList ) {
            // pip.performObligation(obligationMap.get(pip.getAttributeId()));
            pip.performObligation( obligationMap.get( pipName.toString() ) );
        }
        if( pipRetrieval != null ) {
            pipRetrieval.performObligation( obligationMap.get( "?" ) );
        }
        return null;

    }

    /**
     * Given the string representing the whole obligation, this function extracts
     * the name of the class and, from there, it will create the related object
     *
     * @param obligation
     *          the obligtion in string format
     * @return an object representing the obligation the PIP has to perform
     */
    private Object createObjectFromString( String obligation,
            StringBuilder classNameBuilder ) {
        String className = extractClassName( obligation );
        Class<?> clazz;
        try {
            clazz = Class.forName( className );
            classNameBuilder.append( className );
        } catch( ClassNotFoundException exception ) {
            exception.printStackTrace();
            return null;
        }
        String json = getJson( obligation );
        return new Gson().fromJson( json, clazz );
    }

    private String extractClassName( String obligation ) {
        String className = obligation.split( "=" )[0];
        // FIXME
        return "it.cnr.iit.ucsinterface.obligationmanager.obligationobjects."
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
            e.printStackTrace();
            return null;
        }
    }

}
