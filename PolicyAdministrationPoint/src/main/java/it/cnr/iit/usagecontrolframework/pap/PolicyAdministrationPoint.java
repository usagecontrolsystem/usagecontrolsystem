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
package it.cnr.iit.usagecontrolframework.pap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPap;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;

import iit.cnr.it.ucsinterface.pap.PAPInterface;

import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;

/**
 * This is one of the possible implementations of the PAP.
 * <p>
 * The PAP is basically a storage of policies. Its only task is to store policy
 * and give them to the ContextHandler or the PDP when they request them. In
 * this implementation the PAP is synchronous and does not use any thread to
 * perform its task. Of course it can be possible to have multithreading if we
 * want. <br>
 * By assumption all the policies the PAP can handle are stored inside the
 * resources folder, where the name of the file corresponds to the policy id.
 * </p>
 *
 * @author antonio
 *
 */
public class PolicyAdministrationPoint implements PAPInterface {
    private Logger LOGGER = Logger
        .getLogger( PolicyAdministrationPoint.class.getName() );

    private String policiesFilePath;

    private static final String policyExtension = ".pol"; // isn't it .xml ?

    private volatile boolean initialized = false;

    /**
     * Constructor for the policy administration point
     *
     * @param xmlPAP
     *          the xml that describes this PAP
     */
    public PolicyAdministrationPoint( XMLPap xmlPAP ) {
        // BEGIN parameter checking
        if( xmlPAP == null ) {
            return;
        }
        // END parameter checking
        policiesFilePath = xmlPAP.getPath();
        if( policiesFilePath != null && !policiesFilePath.equals( "" ) ) {
            initialized = true;
        }
    }

    /**
     * retrieves the policy that has as id the policyId passed as parameter
     *
     * @param the
     *          policyid to be used
     * @return the policy in string format
     */
    @Override
    public String retrievePolicy( String policyId ) {
        // BEGIN parameter checking
        if( initialized == false ) {
            return null;
        }
        if( policyId == null || policyId.equals( "" ) ) {
            return null;
        }
        // END parameter checking
        String policy = null;
        try {
            Path path = Paths.get( policiesFilePath, policyId + policyExtension );
            policy = new String( Files.readAllBytes( path ) );
        } catch( IOException e ) {
            LOGGER.severe( "error while reading policy file : " + e.getMessage() );
            e.printStackTrace();
            // TODO throw exception
        }

        return policy;
    }

    /**
     * Adds a new policy
     *
     * @param policy
     *          the policy to be added
     * @return true if everything goes ok, false otherwise
     */
    @Override
    public boolean addPolicy( String policy ) {

        // BEGIN parameter checking
        if( initialized == false ) {
            return false;
        }
        if( policy == null ) {
            return false;
        }
        PolicyType policyType;
        try {
            policyType = JAXBUtility.unmarshalToObject( PolicyType.class, policy );
        } catch( Exception e ) {
            e.printStackTrace();
            LOGGER.log( Level.SEVERE, "Invalid policy " + policy );
            return false;
        }
        if( policyType == null ) {
            return false;
        }
        if( ( new File( policiesFilePath + File.separator + policy + policyExtension ) ).exists() ) {
            return true;
        }
        // END parameter checking

        try {
            FileWriter fileWriter = new FileWriter(
                policiesFilePath + File.separator + policy + policyExtension );
            fileWriter.write( policy );
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch( IOException ioException ) {
            ioException.printStackTrace();
            LOGGER.log( Level.SEVERE, "IOException in writing to file " + policiesFilePath
                    + File.separator + policy + policyExtension );
        }
        return false;
    }

    /**
     * Retrieves the IDs of the policy stored
     *
     * @return the list of the IDs of the policies stored
     */
    @Override
    public List<String> listPolicies() {
        List<String> list = new ArrayList<>();
        File directory = new File( policiesFilePath );
        File[] files = directory.listFiles();
        for( File file : files ) {
            list.add( file.getName() );
        }
        return list;
    }

}
