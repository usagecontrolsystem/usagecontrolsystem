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
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import iit.cnr.it.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import iit.cnr.it.utility.RESTUtils;

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
    if (message.getPurpose() == PURPOSE.TRYACCESS) {
      RESTUtils.asyncPost(buildUrl(tryAccess), message);
    }
    if (message.getPurpose() == PURPOSE.STARTACCESS) {
      RESTUtils.asyncPost(buildUrl(startAccess), message);
    }
    if (message.getPurpose() == PURPOSE.ENDACCESS) {
      RESTUtils.asyncPost(buildUrl(endAccess), message);
    }
    return null;
  }

  private String buildUrl(String api) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("http://" + url + ":");
    stringBuilder.append(port + "/");
    stringBuilder.append(api);
    return stringBuilder.toString();
  }
}
