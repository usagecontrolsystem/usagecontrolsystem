package it.cnr.iit.eitdemo.pep;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;

import iit.cnr.it.ucsinterface.message.MEAN;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.pep.PEPInterface;
import iit.cnr.it.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import iit.cnr.it.utility.Utility;
import it.cnr.iit.peprest.configuration.Configuration;
import it.cnr.iit.peprest.configuration.PEPConf;
import it.cnr.iit.peprest.proxy.ProxyRequestManager;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;

public class PEPEit implements PEPInterface {

	private Configuration configuration;

	private String ip;
	private String tomcatPort;
	private String revokeType;
	// required to give the PEP an endpoint in case of on going notifications
	private String onGoingEvaluationInterface;
	private String serverPort;

	protected static final Logger LOGGER = Logger.getLogger(OldPEPEit.class.getName());

	private RequestManagerToExternalInterface requestManager;
	// private ContextHandlerInterface contextHandler;

	// map of unanswered messages, the key is the id of the message
	private HashMap<String, Message> unanswered = new HashMap<>();
	private ConcurrentHashMap<String, Message> responses = new ConcurrentHashMap<>();
	private HashMap<String, String> sessionPerConnection = new HashMap<>();

	private volatile boolean initialized = false;

	private Object mutex = new Object();

	private ExecutorService listener = Executors.newFixedThreadPool(1);

	private DatagramSocket serverSocket;

	// TEMPORARY PART
	private String POLICY_PATH;

	private String REQUEST_PATH;

	public PEPEit() {
		System.out.println("\t*********************+STARTING");
		if ((configuration = retrieveConfiguration()) == null) {
			return;
		}
		try {
			PEPConf pepConf = configuration.getPepConf();
			System.out.println("\t*********CREATION********\n");
			this.ip = pepConf.getIp();
			this.tomcatPort = pepConf.getTomcatPort();
			this.revokeType = pepConf.getRevoke();
			this.serverPort = pepConf.getServerPort();
			this.onGoingEvaluationInterface = pepConf.getStatusChanged();
			this.requestManager = new ProxyRequestManager(configuration.getRMConf());
			System.out.println("\t*********CREATION********\n");
			serverSocket = new DatagramSocket(Integer.parseInt(serverPort));
			listener.submit(new ListenerClass(serverSocket));
			POLICY_PATH = pepConf.getPolicyPath();
			REQUEST_PATH = pepConf.getRequestPath();
			initialized = true;
			System.out.println("\t*********CREATION********\n");
		} catch (Exception exception) {
			exception.printStackTrace();
			initialized = false;
			System.exit(-1);
		}
	}

	private Configuration retrieveConfiguration() {
		try {
			String xml = "";
			InputStream stream = OldPEPEit.class.getClassLoader().getResourceAsStream("conf.xml");
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
			String line = "";

			while ((line = buffer.readLine()) != null) {
				xml += line;
			}
			buffer.close();
			stream.close();
			Configuration configuration = JAXBUtility.unmarshalToObject(Configuration.class, xml);
			return configuration;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private class ListenerClass implements Runnable {

		private DatagramSocket socket;
		private int length = 1024;
		byte[] buffer;

		ListenerClass(DatagramSocket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			boolean stop = false;
			while (!stop) {
				try {
					buffer = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
					socket.receive(receivePacket);
					String result = tryAccess("");
					TryAccessResponse tryAccessResponse = (TryAccessResponse) waitForResponse(result);
					if (!tryAccessResponse.getPDPEvaluation().getResponse().contains("Permit")) {
						sendMessage(receivePacket.getAddress(), receivePacket.getPort(), "TERMINATE");
					} else {
						String sessionId = tryAccessResponse.getSessionId();
						System.out.println("PERMIT " + sessionId);
						result = startAccess(sessionId);
						StartAccessResponse startAccessResponse = (StartAccessResponse) waitForResponse(result);
						if (!startAccessResponse.getPDPEvaluation().getResponse().contains("Permit")) {
							sendMessage(receivePacket.getAddress(), receivePacket.getPort(), "TERMINATE");
						} else {
							System.out.println("STARTACCESS PERMIT");
							sessionPerConnection.put(sessionId,
									receivePacket.getAddress().getHostAddress() + "," + receivePacket.getPort());
							sendMessage(receivePacket.getAddress(), receivePacket.getPort(), "PERMIT");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	private synchronized void sendMessage(InetAddress destinationIP, int destinationPort, String message) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), destinationIP,
					destinationPort);
			serverSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message reevaluationReceived(ReevaluationResponse chPepMessage) {
		try {
			String session = chPepMessage.getPDPEvaluation().getSessionId();
			String destination = sessionPerConnection.get(session);
			InetAddress ipAddress = InetAddress.getByName(destination.split(",")[0]);
			int port = Integer.parseInt(destination.split(",")[1]);
			if (revokeType.equals("HARD")) {
				sendMessage(ipAddress, port, "DENY");
				System.out.println("[TIME] sending endacces " + System.currentTimeMillis());
				EndAccessMessage endAccess = new EndAccessMessage(configuration.getPepConf().getId(),
						configuration.getPepConf().getIp());
				endAccess.setCallback(null, MEAN.REST);
				endAccess.setSessionId(chPepMessage.getPDPEvaluation().getSessionId());
				requestManager.sendMessageToCH(endAccess);

				try {
					Message message = waitForResponse(endAccess.getID());
					System.out.println("[TIME] endacces END" + System.currentTimeMillis());
					sendMessage(ipAddress, port, "TERMINATE");
					return message;
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (chPepMessage.getPDPEvaluation().getResponse().contains("Permit")) {
					sendMessage(ipAddress, port, "PERMIT");
					LOGGER.log(Level.INFO, "RESUME EXECUTION");
				}
				if (chPepMessage.getPDPEvaluation().getResponse().contains("Deny")) {
					sendMessage(ipAddress, port, "DENY");
					LOGGER.log(Level.INFO, "STOP EXECUTION");
				}
			}
			return chPepMessage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String tryAccess(String fileName) {
		String request;
		String policy;
		int random = 0;
		policy = readPolicy(fileName);
		request = createRequest(fileName);

		TryAccessMessageBuilder tryAccessBuilder = new TryAccessMessageBuilder(configuration.getPepConf().getId(),
				configuration.getPepConf().getIp());
		tryAccessBuilder.setPepUri(buildOnGoingEvaluationInterface()).setPolicy(policy).setRequest(request);
		TryAccessMessage tryAccessMessage = tryAccessBuilder.build();
		tryAccessMessage.setCallback(buildResponseInterface("tryAccessResponse"), MEAN.REST);
		unanswered.put(tryAccessMessage.getID(), tryAccessMessage);
		// Gson gson = new Gson();
		// String s = gson.toJson(tryAccessMessage);
		// System.out.println(s);
		System.out.println("[TIME] TRYACCESS " + System.currentTimeMillis());
		Message message = requestManager.sendMessageToCH(tryAccessMessage);
		return tryAccessMessage.getID();

		// return (TryAccessResponse)
		// contextHandler.tryAccess(tryAccessMessage);
	}

	public String startAccess(String sessionId) {
		StartAccessMessage startAccessMessage = new StartAccessMessage(configuration.getPepConf().getId(),
				configuration.getPepConf().getIp());
		startAccessMessage.setSessionId(sessionId);
		startAccessMessage.setCallback(buildResponseInterface("startAccessResponse"), MEAN.REST);
		unanswered.put(startAccessMessage.getID(), startAccessMessage);
		try {
			System.out.println("[TIME] STARTACCESS " + System.currentTimeMillis());
			Message message = requestManager.sendMessageToCH(startAccessMessage);

			// return (StartAccessResponse) contextHandler
			// .startAccess(startAccessMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return startAccessMessage.getID();
	}

	private String endAccess(String sessionId) {
		EndAccessMessage endAccessMessage = new EndAccessMessage(configuration.getPepConf().getId(),
				configuration.getPepConf().getIp());
		endAccessMessage.setSessionId(sessionId);
		endAccessMessage.setCallback(buildResponseInterface("endAccessResponse"), MEAN.REST);
		unanswered.put(endAccessMessage.getID(), endAccessMessage);
		try {
			System.out.println("[TIME] ENDACCESS " + System.currentTimeMillis());
			Message message = requestManager.sendMessageToCH(endAccessMessage);
			message = waitForResponse(endAccessMessage.getID());
			System.out.println("[TIME] ENDACCESS END" + System.currentTimeMillis());
			System.exit(0);
			// return (StartAccessResponse) contextHandler
			// .startAccess(startAccessMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return endAccessMessage.getID();
	}

	/*
	 * public void setCHInterface(ContextHandlerInterface
	 * contextHandlerInterface) { this.contextHandler = contextHandlerInterface;
	 * }
	 */

	public void setRequestManagerInterface(RequestManagerToExternalInterface requestManager) {
		this.requestManager = requestManager;
	}

	@Override
	@Async
	public Message onGoingEvaluation(Message message) {
		// BEGIN parameter checking
		if (message == null || !(message instanceof ReevaluationResponse)) {
			System.err.println("Message not valid");
			return null;
		}
		if (!initialized) {
			System.err.println("Cannot answer the message");
			return null;
		}
		// END parameter checking

		System.out.println("[TIME] ON_GOING_EVAL " + System.currentTimeMillis());

		ReevaluationResponse chPepMessage = (ReevaluationResponse) message;
		reevaluationReceived(chPepMessage);

		// contextHandler.endAccess(endAccess);
		message.setMotivation("OK");

		return null;
	}

	@Override
	@Async
	public void receiveResponse(Message message) {
		responses.put(message.getID(), message);
		synchronized (mutex) {
			mutex.notifyAll();
		}
	}

	private Message waitForResponse(String id) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		TaskBody taskBody = new TaskBody(id);
		return executor.submit(taskBody).get();
	}

	private final String buildResponseInterface(String name) {
		StringBuilder response = new StringBuilder();
		response.append("http://" + ip + ":");
		response.append(tomcatPort + "/");
		response.append(name);
		return response.toString();
	}

	private String buildOnGoingEvaluationInterface() {
		StringBuilder response = new StringBuilder();
		response.append("http://" + ip + ":");
		response.append(tomcatPort + "/");
		response.append(onGoingEvaluationInterface);
		return response.toString();
	}

	public void end(String sessionId) throws InterruptedException, ExecutionException {
		LOGGER.log(Level.INFO, "[TIME] Sending endAccess " + System.currentTimeMillis());
		String id = endAccess(sessionId);
		EndAccessResponse endAccessResponse = (EndAccessResponse) waitForResponse(id);
		LOGGER.log(Level.INFO, "[TIME] END ACCESS RESPONSE: " + System.currentTimeMillis());
	}

	private class TaskBody implements Callable<Message> {

		private String id;

		public TaskBody(String id) {
			this.id = id;
		}

		@Override
		public Message call() {
			try {
				while (!responses.containsKey(id)) {
					synchronized (mutex) {
						mutex.wait();
					}
				}
				return responses.remove(id);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 *
	 * @return
	 */
	private String readPolicy(String fileName) {
		return Utility.readFileAbsPath(POLICY_PATH);
	}

	/**
	 *
	 * @param fileName
	 * @return
	 */
	private String createRequest(String fileName) {
		return Utility.readFileAbsPath(REQUEST_PATH);
	}

}
