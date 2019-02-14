package it.cnr.iit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import iit.cnr.it.pipreader.PIPReader;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class CoverageTest {
	
	
	private RequestType requestType = new RequestType();
	private PIPReader subjectAttributePip;
	private PIPReader resourceAttributePip;
	private PIPReader actionAttributePip;
	private PIPReader environmentAttributePip;
	private PIPReader fault;
	private Attribute subjectAttribute = new Attribute();
	private Attribute resourceAttribute = new Attribute();
	private Attribute actionAttribute = new Attribute();
	private Attribute environmentAttribute = new Attribute();
	private Properties properties;

	public CoverageTest() {
		try {
			properties = new Properties();
			InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application-test.properties");
			properties.load(resourceStream);
			requestType = JAXBUtility.unmarshalToObject(RequestType.class, properties.get("requestType").toString());
			subjectAttributePip = new PIPReader(properties.get("subjectPip").toString());
			resourceAttributePip = new PIPReader(properties.get("resourcePip").toString());
			actionAttributePip = new PIPReader(properties.get("actionPip").toString());
			environmentAttributePip = new PIPReader(properties.get("environmentPip").toString());
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
		fault = new PIPReader(null);
		fault = new PIPReader("");
		System.out.println(properties.get("missingCategory").toString());
		fault = new PIPReader(properties.get("missingCategory").toString());
		assertEquals(fault.initialized, false);
		System.out.println(properties.get("missingAttributeId").toString());
		fault = new PIPReader(properties.get("missingAttributeId").toString());
		assertEquals(fault.initialized, false);
		fault = null;
		fault = new PIPReader(properties.get("missingExpectedCategory").toString());
		assertEquals(fault.initialized, false);
		fault = new PIPReader(properties.get("missingDataType").toString());
		assertEquals(fault.initialized, false);
		fault = new PIPReader(properties.get("missingFilePath").toString());
		assertEquals(fault.initialized, false);
		fault = new PIPReader(properties.get("malformedInput").toString());
		assertEquals(fault.initialized, false);
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

	private String testRetrieveAttribute(Attribute attribute, PIPReader pipReader) {
		try {
			String value = pipReader.retrieve(attribute);
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		testRetrieveAndEnrichment(requestType, fault);
		testRetrieveAndEnrichment(null, fault);
		assertEquals(verifyRequest(requestType, dummySubjectAttribute), null);
		assertFalse(verifyRequest(requestType, environmentAttribute).equals("40.0"));
	}

	private void testRetrieveAndEnrichment(RequestType requestType, PIPReader pipReader) {
		try {
			pipReader.retrieve(requestType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String verifyRequest(RequestType requestType, Attribute attribute) {
		for(AttributesType attributeTypes : requestType.getAttributes()) {
			for(AttributeType attributeType : attributeTypes.getAttribute()) {
				if(attributeType.getAttributeId().equals(attribute.getAttributeId().toString())) {
					for(AttributeValueType attributeValue : attributeType.getAttributeValue()) {
						System.out.println(attributeValue.getContent().toString());
						return attributeValue.getContent().toString();
					}

				}
			}
		}
		return null;
	}
}
