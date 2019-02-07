package it.cnr.iit.test.pipreadresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import iit.cnr.it.pipreader.PIPReadResources;
import iit.cnr.it.ucsinterface.pip.exception.PIPException;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class CoverageTest {
	
	
	private RequestType requestType = new RequestType();
	private PIPReadResources subjectAttributePip;
	private PIPReadResources resourceAttributePip;
	private PIPReadResources actionAttributePip;
	private PIPReadResources environmentAttributePip;
	private PIPReadResources fault;
	private Attribute subjectAttribute = new Attribute();
	private Attribute resourceAttribute = new Attribute();
	private Attribute actionAttribute = new Attribute();
	private Attribute environmentAttribute = new Attribute();
	private Properties properties;
	private Logger LOGGER = Logger.getLogger(CoverageTest.class.getName());

	public CoverageTest() {
		try {
			properties = new Properties();
			InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("application.properties");
			properties.load(resourceStream);
			requestType = JAXBUtility.unmarshalToObject(RequestType.class, properties.get("requestType").toString());
			subjectAttributePip = new PIPReadResources(properties.get("subjectPip").toString());
			resourceAttributePip = new PIPReadResources(properties.get("resourcePip").toString());
			actionAttributePip = new PIPReadResources(properties.get("actionPip").toString());
			environmentAttributePip = new PIPReadResources(properties.get("environmentPip").toString());
			assertTrue(subjectAttributePip.initialized);
			subjectAttribute.createAttributeId("urn:oasis:names:tc:xacml:1.0:subject:role");
			resourceAttribute.createAttributeId("urn:oasis:names:tc:xacml:1.0:resource:class");
			actionAttribute.createAttributeId("urn:oasis:names:tc:xacml:1.0:resource:type");
			environmentAttribute.createAttributeId("urn:oasis:names:tc:xacml:3.0:environment:temperature");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInitialization() {
		fault = new PIPReadResources(null);
		fault = new PIPReadResources("");
		fault = new PIPReadResources(properties.get("missingCategory").toString());
		assertEquals(fault.initialized, false);
		fault = new PIPReadResources(properties.get("missingAttributeId").toString());
		assertEquals(fault.initialized, false);
		fault = null;
		LOGGER.info("---------BEGIN FAILURE TESTS----------");
		fault = new PIPReadResources(properties.get("missingExpectedCategory").toString());
		assertEquals(fault.initialized, false);
		fault = new PIPReadResources(properties.get("missingDataType").toString());
		assertEquals(fault.initialized, false);
		fault = new PIPReadResources(properties.get("missingFilePath").toString());
		assertEquals(fault.initialized, false);
		LOGGER.info("---------END FAILURE TESTS----------");
	}

	@Test
	public void removeRetrievalTest() {
		String value = testRetrieveAttribute(subjectAttribute, subjectAttributePip);
		assertTrue(value.equals("IIT"));
		value = testRetrieveAttribute(resourceAttribute, resourceAttributePip);
		assertTrue(value.equals("SECRET"));
		value = testRetrieveAttribute(actionAttribute, actionAttributePip);
		assertTrue(value.equals("ANALYZE"));
		value = testRetrieveAttribute(environmentAttribute, environmentAttributePip);
		assertTrue(value.equals("30.0"));
		value = testRetrieveAttribute(subjectAttribute, fault);
	}

	private String testRetrieveAttribute(Attribute attribute, PIPReadResources pipReader) {
		try {
			String value = pipReader.retrieve(attribute);
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
	}

	@Test
	public void localTest() throws Exception {
		testRetrieveAndEnrichment(requestType, subjectAttributePip);
		assertTrue(verifyRequest(requestType, subjectAttribute).equals("[IIT]"));
		testRetrieveAndEnrichment(requestType, resourceAttributePip);
		assertTrue(verifyRequest(requestType, resourceAttribute).equals("[SECRET]"));
		testRetrieveAndEnrichment(requestType, actionAttributePip);
		assertTrue(verifyRequest(requestType, actionAttribute).equals("[ANALYZE]"));
		testRetrieveAndEnrichment(requestType, environmentAttributePip);
		assertTrue(verifyRequest(requestType, environmentAttribute).equals("[30.0]"));
		Attribute dummySubjectAttribute = new Attribute();
		dummySubjectAttribute.createAttributeId("subjectId");
		LOGGER.info("---------BEGIN FAILURE TESTS----------");
		testRetrieveAndEnrichment(requestType, fault);
		testRetrieveAndEnrichment(null, fault);
		assertEquals(verifyRequest(requestType, dummySubjectAttribute), null);
		assertFalse(verifyRequest(requestType, environmentAttribute).equals("40.0"));
		LOGGER.info("---------END FAILURE TESTS----------");
	}

	private void testRetrieveAndEnrichment(RequestType requestType, PIPReadResources pipReader) {
		try {
			pipReader.retrieve(requestType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	private String verifyRequest(RequestType requestType, Attribute attribute) {
		for(AttributesType attributeTypes : requestType.getAttributes()) {
			for(AttributeType attributeType : attributeTypes.getAttribute()) {
				if(attributeType.getAttributeId().equals(attribute.getAttributeId().toString())) {
					for(AttributeValueType attributeValue : attributeType.getAttributeValue()) {
						return attributeValue.getContent().toString();
					}

				}
			}
		}
		return null;
	}
}
