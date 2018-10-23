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
package iit.cnr.it.ucsinterface.message.remoteretrieval;

import java.util.List;

import com.google.gson.Gson;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PART;
import it.cnr.iit.xacmlutilities.Attribute;

/**
 * This is the message a PIP sends to a CH and viceversa.
 * <p>
 * We have defined a single class for both local and remote messages, in this
 * way it is a lot easier to pass messages from one source to the other. <br>
 * 
 * </p>
 * 
 * @author antonio
 *
 */
public final class MessagePipCh extends Message {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	public static final int		ERROR_CODE				= -10;
	
	private boolean						isInitialized			= false;
	
	private PipChContent			content						= new PipChContent();
	
	private ACTION						action;
	
	/**
	 * Message exchanged between PIP and CH
	 * 
	 * @param source
	 *          source of the message
	 * @param destination
	 *          destination of the message
	 * @param motivation
	 *          motivation of the message (most likely it is a json describing the
	 *          motivation)
	 */
	public MessagePipCh(String source, String destination) {
		super(source, destination);
		if (super.isInitialized()) {
			isInitialized = true;
		}
	}
	
	/**
	 * Message exchanged between PIP and CH
	 * 
	 * @param source
	 *          source of the message
	 * @param destination
	 *          destination of the message
	 * @param motivation
	 *          motivation of the message (most likely it is a json describing the
	 *          motivation)
	 */
	public MessagePipCh(PART source, PART destination, PipChContent motivation) {
		super(source.toString(), destination.toString());
		if (super.isInitialized()) {
			isInitialized = true;
			setMotivation(motivation);
		}
	}
	
	@Override
	public int compareTo(Message o) {
		if (!isInitialized) {
			return ERROR_CODE;
		}
		return 0;
	}
	
	/**
	 * Sets the motivation for the message. The motivation is a JSON in string
	 * format. Any other format would not be accepted
	 * 
	 * @param motivation
	 *          the motivation of the message
	 * @return true if everything goes ok, false otherwise
	 */
	/*
	 * private void setMotivation(String motivation) { if (!isInitialized) ; try {
	 * Gson gson = new Gson(); content = gson.fromJson(motivation,
	 * PipChContent.class); } catch (Exception e) { e.printStackTrace();
	 * isInitialized = false; } }
	 */
	
	public void setMotivation(PipChContent content) {
		if (content != null) {
			this.content = content;
			isInitialized = true;
		} else {
			System.err.println("NULL content");
			return;
		}
	}
	
	/**
	 * Adds a new attribute to the motivation of the message.
	 * 
	 * @param attribute
	 *          the attribute to be added
	 * @return true if everything goes ok, false otherwise
	 */
	public boolean addAttribute(Attribute attribute) {
		if (!isInitialized || attribute == null) {
			return false;
		}
		return content.addAttribute(attribute);
	}
	
	public List<Attribute> getAttributes() {
		if (!isInitialized) {
			System.err.println("Message not initialized");
			return null;
		}
		return content.getAttributes();
	}
	
	@Override
	public String getMotivation() {
		Gson gson = new Gson();
		return gson.toJson(content);
	}
	
	public void setAction(ACTION retrieve) {
		this.action = retrieve;
	}
	
	public ACTION getAction() {
		return action;
	}
	
}
