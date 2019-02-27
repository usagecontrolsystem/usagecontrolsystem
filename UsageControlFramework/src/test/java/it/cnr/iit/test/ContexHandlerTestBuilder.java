package it.cnr.iit.test;

import iit.cnr.it.ucsinterface.pap.PAPInterface;
import iit.cnr.it.ucsinterface.pdp.PDPInterface;
import iit.cnr.it.ucsinterface.pip.PIPRetrieval;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;

public class ContexHandlerTestBuilder {
	private ContextHandlerLC contextHandler;
	
	public ContexHandlerTestBuilder(Configuration ucsConfiguration) {
		super();
		contextHandler =  new ContextHandlerLC(ucsConfiguration.getCh());;
	}
	
	public ContextHandlerLC build() {
		return contextHandler;
	}
	
	//contextHandler.setRequestManagerToChInterface(getMockedRequestManagerToChInterface());
	//contextHandler.setSessionManagerInterface(getSessionManagerForStatus("", "", "", ContextHandlerLC.TRY_STATUS));
	//contextHandler.setForwardingQueue(getMockedForwardingQueueToCHInterface());
	//contextHandler.setObligationManager(getMockedObligationManager());
	
	public ContexHandlerTestBuilder setPdpInterface(PDPInterface pdp) {
		contextHandler.setPdpInterface(pdp);
		return this;
	}
	
	public ContexHandlerTestBuilder setPapInterface(PAPInterface pap) {
		contextHandler.setPapInterface(pap);
		return this;
	}
	
	public ContexHandlerTestBuilder setPapInterface(PIPRetrieval pipRetrieval) {
		contextHandler.setPIPRetrieval(pipRetrieval);
		return this;
	}
	
}
