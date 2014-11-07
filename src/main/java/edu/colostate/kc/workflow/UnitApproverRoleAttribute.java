/*
 * Copyright 2005-2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.colostate.kc.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.UnitService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.identity.Id;
import org.kuali.rice.kew.api.identity.PrincipalId;
import org.kuali.rice.kew.api.rule.RoleName;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.NodeState;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.GenericRoleAttribute;
import org.kuali.rice.kew.rule.QualifiedRoleName;
import org.kuali.rice.kew.rule.ResolvedQualifiedRole;

import edu.colostate.kc.infrastructure.CSUKeyConstants;


/**
 * @author chrisdenne
 * This class is a custom imlpementation to provide UnitApprovers specifically to the approval request in our 
 * dynamic proposalUnitHierarchy workflow.
 * 
 */
public class UnitApproverRoleAttribute extends GenericRoleAttribute {
    private static final long serialVersionUID = 2837085991084714072L;

    private static final String ROLE_NAME = "Unit Administrator";
    private static final List<RoleName> UNIT_ADMIN_ROLE_LIST;
    static{
    	UNIT_ADMIN_ROLE_LIST = new LinkedList<RoleName>();
    	RoleName.Builder roleNameBuilder = RoleName.Builder.create(UnitApproverRoleAttribute.class.getName(), ROLE_NAME, ROLE_NAME);
    	UNIT_ADMIN_ROLE_LIST.add(roleNameBuilder.build());
    }
    

    private ConfigurationService kualiConfigurationService;
    private List<String> approverCodeList;    


    public List<String> getQualifiedRoleNames(String roleName, DocumentContent documentContent) {
        List<String> qualifiedRoleNames = new ArrayList<String>();
        qualifiedRoleNames.add(ROLE_NAME);
        
        return qualifiedRoleNames;
    }

    public List<RoleName> getRoleNames() {
        return UNIT_ADMIN_ROLE_LIST;
    }

    @Override
    public Map<String, String> getProperties() {
        // intentionally unimplemented...not intending on using this attribute client-side
        return null;
    }

    private UnitService getUnitService() {
        return KraServiceLocator.getService(UnitService.class);
    }

    @Override
    protected ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, QualifiedRoleName qualifiedRoleName) {
        List<Id> recipients = resolveRecipients(routeContext, qualifiedRoleName);
        ResolvedQualifiedRole rqr = new ResolvedQualifiedRole(getLabelForQualifiedRoleName(qualifiedRoleName),
                                                              recipients
                                                              ); // default to no annotation...
        String unitNumber = null;
        unitNumber = ((NodeState)routeContext.getNodeInstance().getState().get(0)).getValue();
          if (StringUtils.isNotBlank(unitNumber)) {
        	  rqr.setAnnotation(unitNumber + " Proposal Units Hierarchy Approval");
          }
        return rqr;
    }
    
    @Override
    protected List<Id> resolveRecipients(RouteContext routeContext, QualifiedRoleName qualifiedRoleName) {
      List<Id> members = new ArrayList<Id>();  
      String unitNumber = null;
      unitNumber = ((NodeState)routeContext.getNodeInstance().getState().get(0)).getValue();
        if (StringUtils.isNotBlank(unitNumber)) {
            List<UnitAdministrator> unitAdministrators = getUnitService().retrieveUnitAdministratorsByUnitNumber(unitNumber);
            for ( UnitAdministrator unitAdministrator : unitAdministrators ) {
                if ( StringUtils.isNotBlank(unitAdministrator.getPersonId()) && 
                		unitAdministrator.getUnitAdministratorType() != null &&
                		getUnitApproverCodes().contains(unitAdministrator.getUnitAdministratorType().getUnitAdministratorTypeCode())) {
                    members.add(new PrincipalId(unitAdministrator.getPersonId()));
                }
            }
        }
        
        return members;
    }
    
    protected List<String> getUnitApproverCodes() {
    	if (approverCodeList==null) {	    	
	    	// TODO will change below to parameter instead of property accessed via keyConsts.
	    	String listString = getKualiConfigurationService().getPropertyValueAsString(CSUKeyConstants.UNIT_APPROVER_TYPE_CODE);
	    	String[] arrayString = StringUtils.split(listString, ',');
	    	approverCodeList = Arrays.asList(arrayString);
    	}
    	return approverCodeList;
    }
    
	protected ConfigurationService getKualiConfigurationService() {
		if ( kualiConfigurationService == null ) {
			kualiConfigurationService = KraServiceLocator.getService(ConfigurationService.class);
		}
		return this.kualiConfigurationService;
	}

}
