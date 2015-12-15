/*
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kra.proposaldevelopment.service.impl;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.service.impl.KraAuthorizationServiceImpl;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.krad.service.DocumentService;

import edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants;
import edu.arizona.kra.proposaldevelopment.service.CustomAuthorizationService;

/**
 * Custom authorization service implementation - used in Proposal Development Dashboard
 * @author nataliac
 */
public class CustomAuthorizationServiceImpl extends KraAuthorizationServiceImpl implements CustomAuthorizationService {

    protected DocumentService documentService;
    protected PermissionService permissionService;

    private static final Log LOG = LogFactory.getLog(CustomAuthorizationServiceImpl.class);

    @Override
    public boolean hasPermissionOnPropDevDocument(String userId, String docNumber, String permissionName){
        LOG.debug("hasPermissionOnPropDevDocument(): uid="+userId+" docNbr="+docNumber+" permissionName="+permissionName);
        ProposalDevelopmentDocument propDevDocument = null;
        try {
            propDevDocument = (ProposalDevelopmentDocument) getDocumentService().getByDocumentHeaderId(docNumber);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        LOG.debug("hasPermissionOnPropDevDocument(): getDocumentService().getByDocumentHeaderId("+docNumber+"Finished.");
        return hasPermission(userId, propDevDocument, permissionName);
    }

    @Override
    public boolean hasSPSPermission(String userId, String permissionName){
        LOG.debug("hasSPSPermission(): uid="+userId+" permissionName="+permissionName);
        //Note: For now, SPS permissions don't have additional qualifications
        return getPermissionService().isAuthorized( userId, RoleConstants.KC_ADMIN_NAMESPACE, permissionName, Collections.<String, String>emptyMap() );
    }


    @Override
    public boolean hasSPSRole(String userId, String roleName){      
        LOG.debug("hasSPSRole(): uid="+userId+" roleName="+roleName);
        //Note: For now, SPS roles don't have additional qualifications
        Role role = getRoleService().getRoleByNamespaceCodeAndName(RoleConstants.KC_ADMIN_NAMESPACE, roleName);
        if(role != null) {
            return getRoleService().principalHasRole(userId, Collections.singletonList(role.getId()),Collections.<String, String>emptyMap());
        }
        return false;
    }

    @Override
    public Collection<String> getSPSReviewerRoleMembers(){
        LOG.debug("getSPSReviewerRoleMembers()");
        //Note: For now, SPS roles don't have additional qualifications
        return getRoleService().getRoleMemberPrincipalIds(RoleConstants.KC_ADMIN_NAMESPACE, PropDevRoutingStateConstants.SPS_REVIEWER_ROLE_NAME, Collections.<String, String>emptyMap());
    }






    protected DocumentService getDocumentService() {
        if ( documentService == null ){
            documentService = KraServiceLocator.getService(DocumentService.class);
        }
        return documentService;
    }


    protected PermissionService getPermissionService() {
        if ( permissionService == null ){
            permissionService = KraServiceLocator.getService(PermissionService.class);
        }
        return permissionService;
    }

    //    protected void populateContextQualifiers(Map<String, String> qualifiers) {
    //        qualifiers.put("namespaceCode", Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT);
    //        qualifiers.put("name", KcKrmsConstants.ProposalDevelopment.PROPOSAL_DEVELOPMENT_CONTEXT);
    //    }

}
