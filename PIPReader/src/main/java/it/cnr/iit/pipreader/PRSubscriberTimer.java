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
package it.cnr.iit.pipreader;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerPIPInterface;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.message.remoteretrieval.PipChContent;
import it.cnr.iit.ucsinterface.pip.exception.PIPException;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;

/**
 * Subscriber timer for the PIPReader.
 * <p>
 * Basically the subscriber timer is in hcarge of performing the task of
 * refreshing periodically the value of a certain attribute, if that value
 * changes, then it has to update the value in the subscriptions queue.
 *
 * <p>
 * By removing the public attribute to this class we have allowed only classes
 * in the same package to create or instantiate such a class
 * </p>
 *
 * @author antonio
 *
 */
final class PRSubscriberTimer extends TimerTask {

    // logger to be used to log the actions
    private Logger LOGGER = Logger
        .getLogger( PRSubscriberTimer.class.getName() );

    // the path of the file to be read
    private String path = "";

    // the queue of attributes that have been subscribed
    private final BlockingQueue<Attribute> subscriptions;

    // the interface to communicate with the context handler
    private ContextHandlerPIPInterface contextHandler;

    /**
     * Constructor for a new Subscriber timer
     *
     * @param contextHandler
     *          the interface to the context handler
     * @param map
     *          the list of attributes that have been subscribed
     * @param path
     *          the path to the file to be read
     */
    PRSubscriberTimer( ContextHandlerPIPInterface contextHandler,
            BlockingQueue<Attribute> map, String path ) {
        subscriptions = map;
        this.path = path;
    }

    @Override
    public void run() {
        for( Attribute entry : subscriptions ) {
            // System.out.println("[PipFile] Subscribe iteration");
            Category category = entry.getCategory();
            String newValue = "";
            LOGGER.log( Level.INFO, "[TIME] polling on value of the attribute for change." );
            if( category == Category.ENVIRONMENT ) {
                newValue = read();
            } else {
                newValue = read( entry.getAdditionalInformations() );
            }

            // if the attribute has not changed
            if( entry.getAttributeValues( entry.getAttributeDataType() ).get( 0 )
                .equals( newValue ) ) {
                ;
            } else {
                LOGGER.log( Level.INFO,
                    "[TIME] value of the attribute changed at "
                            + System.currentTimeMillis() + "\t" + newValue + "\t"
                            + entry.getAdditionalInformations() );
                entry.setValue( entry.getAttributeDataType(), newValue );
                PipChContent pipChContent = new PipChContent();
                pipChContent.addAttribute( entry );
                MessagePipCh messagePipCh = new MessagePipCh( PART.PIP.toString(),
                    PART.CH.toString() );
                messagePipCh.setMotivation( pipChContent );
                contextHandler.attributeChanged( messagePipCh );
            }
        }
        return;
    }

    /**
     * Effective retrieval of the monitored value, before this retrieval many
     * checks may have to be performed
     *
     * @return the requested string
     * @throws PIPException
     */
    private String read() {
        return Utility.readFileAbsPath( path );
    }

    /**
     * Reads the file looking for the line containing the filter we are passing as
     * argument and the role stated as other parameter
     *
     * <br>
     * NOTE we suppose that in the file each line has the following structure:
     * filter\tattribute.
     *
     * @param filter
     *          the string to be used to search for the item we're interested into
     * @param role
     *          the role of the string
     * @return the string or null
     *
     *
     * @throws PIPException
     */
    private String read( String filter ) {
        try (
                Scanner fileInputStream = new Scanner( new File( path ) );) {
            // BufferedInputStream fileInputStream = new BufferedInputStream(
            // new FileInputStream(new File("/home/antonio/temperature.txt")));
            String line = "";
            while( fileInputStream.hasNextLine() ) {
                String tmp = fileInputStream.nextLine();
                if( tmp.contains( filter ) ) {
                    line = tmp;
                    break;
                }
            }
            // LOGGER.log(Level.INFO,
            // "[PIPReader] value read is " + line.split("\t")[1]);
            return line.split( "\t" )[1];
        } catch( IOException ioException ) {
            ioException.printStackTrace();
            return "";
        }
    }

    /**
     * Sets the context handler interface
     *
     * @param contextHandler
     */
    public void setContextHandlerInterface(
            ContextHandlerPIPInterface contextHandler ) {
        // BEGIN parameter checking
        if( contextHandler == null ) {
            LOGGER.log( Level.SEVERE, "Context handler is null" );
            return;
        }
        // END parameter checking
        this.contextHandler = contextHandler;
    }

    public ContextHandlerPIPInterface getContextHandler() {
        return contextHandler;
    }

}
