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
package it.cnr.iit.xacmlutilities.constants;

/**
 * List of constant tags in the XACML syntax
 * 
 * @author antonio
 *
 */
public final class XACMLTags {
	
	public static final String	ATTRIBUTE_ID					= "AttributeId";
	public static final String	RESOURCE							= "resource";
	public static final String	ACTION								= "action";
	public static final String	SUBJECT								= "subject";
	public static final String	ENVIRONMENT						= "environment";
	public static final String	CONDITION							= "Condition";
	public static final String	CATEGORY							= "Category";
	public static final String	DATA_TYPE							= "DataType";
	public static final String	ATTRIBUTE_DESIGNATOR	= "AttributeDesignator";
	public static final String	DECISION_TIME					= "DecisionTime";
	public static final String	RULE									= "Rule";
	public static final String	PRE										= "pre";
	public static final String	POST									= "post";
	public static final String	ONGOING								= "ongoing";
	public static final String	EFFECT								= "Effect";
	public static final String	RULE_ID								= "RuleId";
	public static final String	POLICY_ID							= "PolicyId";
	public static final String	RULE_COMBINING				= "RuleCombiningAlgId";
	public static final String	POLICY								= "Policy";
	public static final String	POLICY_SET_ID					= "PolicySetId";
	public static final String	POLICY_SET						= "PolicySet";
	public static final String	POLICY_COMBINING			= "PolicyCombiningAlgId";
	public static final String	VERSION								= "Version";
	
	private XACMLTags() {
	};
	
}
