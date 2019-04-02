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
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import it.cnr.iit.ucs.configuration.PapProperties;
import it.cnr.iit.ucsinterface.pap.PAPInterface;
import it.cnr.iit.utility.JAXBUtility;

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

    private Logger LOGGER = Logger.getLogger( PolicyAdministrationPoint.class.getName() );

    private PapProperties properties;

    private static final String POLICY_FILE_EXTENSION = ".pol";

    private static final String MSG_ERR_POLICY_READ = "Error reading policy file : {0} -> {1}";
    private static final String MSG_ERR_POLICY_WRITE = "Error writing policy file : {0} -> {1}";
    private static final String MSG_ERR_POLICY_INVALID = "Invalid policy contents : {0}";
    private static final String MSG_WARN_POLICY_EXISTS = "Policy file already existent";

    private volatile boolean initialized = false;

    /**
     * Constructor for the policy administration point
     *
     * @param properties
     *          the properties that describes this PAP
     */
    public PolicyAdministrationPoint( PapProperties properties ) {
        // BEGIN parameter checking
        if( properties == null ) {
            // TODO throw exception
            return;
        }
        // END parameter checking
        this.properties = properties;
        if( properties.getPath() != null &&
                Paths.get( properties.getPath() ).toFile().isDirectory() ) {
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
        if( initialized == false ||
                policyId == null || policyId.equals( "" ) ) {
            return null;
        }
        // END parameter checking

        Path path = getPolicyPath( policyId );
        try {
            return new String( Files.readAllBytes( path ) );
        } catch( Exception e ) {
            LOGGER.severe( String.format( MSG_ERR_POLICY_READ, path, e.getMessage() ) );
            // TODO throw exception
        }

        return null;
    }

    /**
     * Adds a new policy
     *
     * @param policy
     *          the policy to be added
     * @return true if everything goes OK, false otherwise
     */
    @Override
    public boolean addPolicy( String policy ) {
        // BEGIN parameter checking
        if( initialized == false ) {
            return false;
        }

        Optional<PolicyType> optPolicyType = getXACMLPolicyFromString( policy );
        if( !optPolicyType.isPresent() ) {
            return false;
        }
        PolicyType policyType = optPolicyType.get();

        if( getPolicyPath( policyType.getPolicyId() ).toFile().exists() ) {
            LOGGER.warning( MSG_WARN_POLICY_EXISTS );
            return true;
        }
        // END parameter checking

        return writePolicy( policyType.getPolicyId(), policy );
    }

    private boolean writePolicy( String policyId, String policy ) {
        String path = getPolicyPath( policyId ).toString();

        try (FileOutputStream fos = new FileOutputStream( path )) {
            fos.write( policy.getBytes() );
        } catch( Exception e ) {
            LOGGER.severe( String.format( MSG_ERR_POLICY_WRITE, path, e.getMessage() ) );
            return false;
        }

        return true;
    }

    private Path getPolicyPath( String policyId ) {
        return Paths.get( properties.getPath(), policyId, POLICY_FILE_EXTENSION );
    }

    private Optional<PolicyType> getXACMLPolicyFromString( String policy ) {
        try {
            PolicyType policyType = JAXBUtility.unmarshalToObject( PolicyType.class, policy );
            return Optional.of( policyType );
        } catch( Exception e ) {
            LOGGER.severe( MSG_ERR_POLICY_INVALID );
        }
        return Optional.empty();
    }

    /**
     * Retrieves the IDs of the policy stored
     *
     * @return the list of the IDs of the policies stored
     */
    @Override
    public List<String> listPolicies() {
        File directory = new File( properties.getPath() );
        File[] files = directory.listFiles( ( dir, name ) -> name.toLowerCase().endsWith( POLICY_FILE_EXTENSION ) );
        return Arrays.asList( files ).parallelStream()
            .map( file -> file.getName() ).collect( Collectors.toList() );
    }

}
