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
package it.cnr.iit.peprest.proxy;

import it.cnr.iit.peprest.configuration.RequestManagerProperties;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import it.cnr.iit.utility.RESTAsynchPostStatus;
import it.cnr.iit.utility.RESTUtils;
import it.cnr.iit.utility.Utility;

public class ProxyRequestManager implements RequestManagerToExternalInterface {

    private String port;
    private String url;
    private String startAccess;
    private String endAccess;
    private String tryAccess;

    public ProxyRequestManager( RequestManagerProperties properties ) {
        port = properties.getPort();
        url = properties.getIp();
        startAccess = properties.getStartAccess();
        endAccess = properties.getEndAccess();
        tryAccess = properties.getTryAccess();
    }

    @Override
    public Message sendMessageToCH( Message message ) {
        RESTAsynchPostStatus postStatus = RESTAsynchPostStatus.PENDING;
        String api = getApiNameFromPurpose( message.getPurpose() );

        postStatus = RESTUtils.asyncPost( Utility.buildUrl( url, port, api ), message );

        if( postStatus == RESTAsynchPostStatus.SUCCESS ) {
            message.setDeliveredToDestination( true );
        }
        return message;
    }

    private String getApiNameFromPurpose( PURPOSE purpose ) {
        switch( purpose ) {
            case TRYACCESS:
                return tryAccess;
            case STARTACCESS:
                return startAccess;
            case ENDACCESS:
                return endAccess;
            default:
                return "";
        }
    }
}
