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
package it.cnr.iit.ucsinterface.pip;

/**
 * This is the abstract class for a PIPRetrieval.
 * <p>
 * A PIP retrieval is a particular type of PIP which is in charge of querying
 * remote CH in order to retrieve some attributes. In fact in a distributed
 * environment it is possible that a CH does not have access to all the
 * attributes either for geographical reasons (some sensors may be too far from
 * the CH or physical the device doesn't have the proper antenna or protocol
 * etc). In order to query other CHs the PIPRetrieval needs their address and
 * also the attribute they're monitoring. This may be implemented in many
 * different ways and, since this is an abstract class, we only fix a remainder
 * for the developer to remind him to implement a way of accessing remote CHs.
 * </p>
 * 
 * /** This is the implementation of the PIPRetrievalCassandra.
 * <p>
 * This PIPRetrieval uses a Cassandra table to know where the various
 * ContextHandlers are, i.e, their ip address and also which is the attribute
 * they're monitoring and all the stuff needed to retrieve the attribute. This
 * PIP will work as follows: every time it is queried (since it is a
 * PIPRetrieval it won't be queried every time in order to save bandwidth and
 * resources on constrained devices) it will search which are the context
 * handlers that monitor the requested attribute id. [In most cases there will
 * be a single context handler in charge of monitoring that attribute, but for
 * generality we will also handle the case in which we have more than one
 * context handler that monitor the same attribute-id.] Once it has the IP
 * address of the context handler, via a rest method call it will query the
 * context handler in order to retrieve the value for that attribute. The remote
 * context handler, once triggered, will query all its PIPs in order to retrieve
 * the value for that(ose) attribute(s) and will return that list to the
 * PIPRetrieval. Now the only task left to the PIPRetrieval is to fatten the
 * request and return it to the CH.
 * </p>
 * <p>
 * The only additional attribute of this class is the CassandraTable object we
 * use to store the informations required by the PIP.
 * </p>
 * <p>
 * This particular type of PIP will implement only the retrieve and subscribe
 * functions in which it is passed to it the AttributeRetrieval object by the
 * CH, it will modify that structure by putting inside the value for the
 * requested attribute and by returning that value in form of String.
 * </p>
 * <p>
 * This PIP expects to receive as parameters the following items:
 * <ol>
 * <li>The object implementing the CassandraTable interface in order to allow it
 * to perform the various queries</li>
 * <li>The subscriptionsqueue</li>
 * <li>A string of additional parameters to help it know how to connect to the
 * Cassandra database and how to perform a query on the table. From this side it
 * need sto know the clustername, the keyspace and the name of the table to
 * perform a connection to the table. Additionally it also needs to know which
 * is the name of the field on which teh various queries will be executed. All
 * the paramters are passed in the following format: PARAMETER=VALUE\n, so
 * basically this is the format we expect:<br>
 * HOST=host<br>
 * CLUSTER=clustername<br>
 * KEYSPACE=keyspace<br>
 * TABLENAME=tablename<br>
 * FIELDNAME=fieldname<br>
 * </li>
 * </ol>
 * This class also expects that the table passed as argument has at least the
 * following fields: <b>attributeuri</b> and <b>attributecategory</b>.
 * </p>
 * 
 * @author antonio
 *
 */
public abstract class PIPRetrieval extends PIPBase implements PIPRMInterface {

    // ---------------------------------------------------------------------------
    // These are the additional attributes required from this particular PIP
    // ---------------------------------------------------------------------------
    // the table to which this pip has to connect to
    protected Object address;

    /**
     * Basic constructor for the PIPRetrieval
     * 
     * @param xmlPip
     *          the additional parameters required by the PIPRetrieval, preferably
     *          in XML format
     */
    public PIPRetrieval( String xmlPip ) {
        super( xmlPip );
    }

}
