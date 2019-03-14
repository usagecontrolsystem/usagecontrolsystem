package it.cnr.iit.pipsocket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerPIPInterface;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.message.remoteretrieval.PipChContent;
import it.cnr.iit.ucsinterface.pip.exception.PIPException;
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
	private Logger													LOGGER	= Logger
	    .getLogger(PRSubscriberTimer.class.getName());
	
	// the path of the file to be read
	private DatagramSocket									clientSocket;
	
	// the queue of attributes that have been subscribed
	private final BlockingQueue<Attribute>	subscriptions;
	
	// the interface to communicate with the context handler
	private ContextHandlerPIPInterface			contextHandler;
	
	private String													destinationIPAddress;
	private String													destinationPort;
	
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
	PRSubscriberTimer(ContextHandlerPIPInterface contextHandler,
	    BlockingQueue<Attribute> map, DatagramSocket clientSocket,
	    String destinationAddress, String port) {
		subscriptions = map;
		this.clientSocket = clientSocket;
		this.destinationIPAddress = destinationAddress;
		this.destinationPort = port;
	}
	
	@Override
	public void run() {
		for (Attribute entry : subscriptions) {
			// System.out.println("[PipFile] Subscribe iteration");
			Category category = entry.getCategory();
			String newValue = "";
			if (category == Category.ENVIRONMENT) {
				newValue = read();
			} else {
				newValue = read(entry.getAdditionalInformations());
			}
			
			// if the attribute has not changed
			if (entry.getAttributeValues(entry.getAttributeDataType()).get(0)
			    .equals(newValue))
				;
			
			// if the value of the attribute has changed notify the context handler
			else {
				LOGGER.log(Level.INFO,
				    "[TIME] value of the attribute changed at "
				        + System.currentTimeMillis() + "\t" + newValue + "\t"
				        + entry.getAdditionalInformations());
				entry.setValue(entry.getAttributeDataType(), newValue);
				PipChContent pipChContent = new PipChContent();
				pipChContent.addAttribute(entry);
				MessagePipCh messagePipCh = new MessagePipCh(PART.PIP.toString(),
				    PART.CH.toString());
				messagePipCh.setMotivation(pipChContent);
				contextHandler.attributeChanged(messagePipCh);
			}
		}
		return;
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
	private String read() {
		try {
			byte receive[] = new byte[1024];
			String read = "READ";
			DatagramPacket sendPacket = new DatagramPacket(read.getBytes(),
			    read.getBytes().length, InetAddress.getByName(destinationIPAddress),
			    Integer.parseInt(destinationPort));
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receive,
			    receive.length);
			clientSocket.receive(receivePacket);
			String line = new String(receivePacket.getData());
			line = line.trim();
			receive = null;
			return line;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String read(String filter) {
		try {
			byte receive[] = new byte[1024];
			String read = "READ\t" + filter;
			DatagramPacket sendPacket = new DatagramPacket(read.getBytes(),
			    read.getBytes().length, InetAddress.getByName(destinationIPAddress),
			    Integer.parseInt(destinationPort));
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receive,
			    receive.length);
			clientSocket.receive(receivePacket);
			String line = new String(receivePacket.getData());
			receive = null;
			return line;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Sets the context handler interface
	 * 
	 * @param contextHandler
	 */
	public void setContextHandlerInterface(
	    ContextHandlerPIPInterface contextHandler) {
		// BEGIN parameter checking
		if (contextHandler == null) {
			LOGGER.log(Level.SEVERE, "Context handler is null");
			return;
		}
		// END parameter checking
		this.contextHandler = contextHandler;
	}
	
	public ContextHandlerPIPInterface getContextHandler() {
		return contextHandler;
	}
	
}
