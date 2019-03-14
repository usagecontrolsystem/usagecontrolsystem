/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package iit.cnr.it.peprest.proxy;

import iit.cnr.it.peprest.configuration.RequestManagerConf;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import it.cnr.iit.utility.RESTAsynchPostStatus;
import it.cnr.iit.utility.RESTUtils;

public class ProxyRequestManager implements RequestManagerToExternalInterface {
  private String port;
  private String url;
  private String startAccess;
  private String endAccess;
  private String tryAccess;

  public ProxyRequestManager(RequestManagerConf requestManagerConf) {
    this.port = requestManagerConf.getPort();
    this.url = requestManagerConf.getIp();
    this.startAccess = requestManagerConf.getStartAccess();
    this.endAccess = requestManagerConf.getEndAccess();
    this.tryAccess = requestManagerConf.getTryAccess();
  }

  @Override
  public Message sendMessageToCH(Message message) {
	RESTAsynchPostStatus postStatus = RESTAsynchPostStatus.PENDING;
    if (message.getPurpose() == PURPOSE.TRYACCESS) {
      postStatus = RESTUtils.asyncPost(buildUrl(tryAccess), message);
    }
    if (message.getPurpose() == PURPOSE.STARTACCESS) {
    	postStatus = RESTUtils.asyncPost(buildUrl(startAccess), message);
    }
    if (message.getPurpose() == PURPOSE.ENDACCESS) {
    	postStatus = RESTUtils.asyncPost(buildUrl(endAccess), message);
    }
    if ( postStatus == RESTAsynchPostStatus.SUCCESS ){
    	message.setDeliveredToDestination(true);
    }
    return message;
  }

  private String buildUrl(String api) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("http://" + url + ":");
    stringBuilder.append(port + "/");
    stringBuilder.append(api);
    return stringBuilder.toString();
  }
}
