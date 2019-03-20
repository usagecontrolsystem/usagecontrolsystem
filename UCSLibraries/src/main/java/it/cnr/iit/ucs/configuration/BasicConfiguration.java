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
package it.cnr.iit.ucs.configuration;

import java.security.InvalidParameterException;
import java.util.logging.Logger;

/**
 * This is the class in charge of storing basic configuration informations.
 * <p>
 * Basic configuration informations include:
 * <ul>
 * <li>IP of the node</li>
 * <li>PORT of the node</li>
 * </ul>
 * The are extracted from the Configuration class derived from the conf.xml
 * file. In this way many basic informations required by multiple components can
 * be shared very easily. <br>
 * This class follows the <i>Singleton Pattern</i> and MUST be initialized by a
 * UsageControlFramework class. <br>
 * </p>
 *
 * @author testucs
 *
 */
public final class BasicConfiguration {
    private static final Logger LOGGER = Logger.getLogger( BasicConfiguration.class.getName() );

    private static BasicConfiguration instance = null;

    private String ip;
    private String port;
    private boolean scheduler;

    private BasicConfiguration() {

    }

    public static BasicConfiguration getBasicConfiguration() {
        if( instance == null ) {
            synchronized( BasicConfiguration.class ) {
                if( instance == null ) {
                    instance = new BasicConfiguration();
                }
            }
        }
        return instance;
    }

    /**
     * Initialization of a Basic Configuration object.
     * <p>
     * We take the basic configuration parameters required from the configuration
     * object created after the conf.xml file. For the moment the only parameters
     * considered as basic configuration are the ip of the node and the port of
     * the node.
     * </p>
     *
     * @param configuration
     *          the configuration object created after the conf.xml file
     */
    public void configure( UCSConfiguration configuration ) {
        // BEGIN parameter checking
        if( configuration == null || configuration.getGeneral().getIp() == null
                || configuration.getGeneral().getIp().isEmpty()
                || configuration.getGeneral().getPort() == null
                || configuration.getGeneral().getPort().isEmpty() ) {
            LOGGER.severe( "Invalid configuration file passed as parameter" );
            throw new InvalidParameterException();
        }
        // END parameter checking
        ip = configuration.getGeneral().getIp();
        port = configuration.getGeneral().getPort();
        scheduler = configuration.getGeneral().isSchedulerEnabled();
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public boolean getScheduler() {
        return scheduler;
    }

}
