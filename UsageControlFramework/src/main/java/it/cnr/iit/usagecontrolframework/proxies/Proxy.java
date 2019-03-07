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
package it.cnr.iit.usagecontrolframework.proxies;

/**
 * This is the proxy abstract object.
 * <p>
 * With this object we basically provide a skeleton for a proxy. A proxy is
 * basically a wrapper around the real implementation of the object and helps us
 * in dealing with the real object. <br>
 * The only functionality a Proxy abstract object <b>MUST</b> provide is a ping
 * function. The ping function is used to understand if the wrapped object has
 * been correctly initalized and if the wrapped object is ready.
 * </p>
 *
 * @author antonio
 *
 */
public abstract class Proxy {
    abstract public boolean ping();
}
