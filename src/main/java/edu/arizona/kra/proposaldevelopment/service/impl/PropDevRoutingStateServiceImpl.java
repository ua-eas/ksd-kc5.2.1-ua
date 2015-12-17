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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.service.KcPersonService;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants;
import edu.arizona.kra.proposaldevelopment.bo.ProposalDevelopmentRoutingState;
import edu.arizona.kra.proposaldevelopment.bo.SPSReviewer;
import edu.arizona.kra.proposaldevelopment.dao.PropDevRoutingStateDao;
import edu.arizona.kra.proposaldevelopment.service.CustomAuthorizationService;
import edu.arizona.kra.proposaldevelopment.service.PropDevRoutingStateService;


/**
 * Service implementation for the PropDevRoutingStateService used in PropDevRoutingState Dashboard
 * @author nataliac
 */
public class PropDevRoutingStateServiceImpl implements PropDevRoutingStateService {


    private CustomAuthorizationService authorizationService;
    private KcPersonService kcPersonService;
    protected PropDevRoutingStateDao dataAccessObject;


    private static final Log LOG = LogFactory.getLog(PropDevRoutingStateServiceImpl.class);

    /**
     * Method that generates the search results for the lookup framework.
     * Called by performLookup()
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<ProposalDevelopmentRoutingState> findPropDevRoutingState(Map<String, String> searchCriteria) {
        LOG.debug("getSearchResults():"+searchCriteria.toString());
        List <ProposalDevelopmentRoutingState> results = new ArrayList<ProposalDevelopmentRoutingState>();
        try {
            results = dataAccessObject.getPropDevRoutingState(searchCriteria);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
        }
        LOG.debug("getSearchResults(): size="+results.size());
        return results;
    }



    public void setDataAccessObject(PropDevRoutingStateDao dataAccessObject) {
        this.dataAccessObject = dataAccessObject;
    }

    public void setAuthorizationService(CustomAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    protected CustomAuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    protected KcPersonService getKcPersonService(){
        return kcPersonService;
    }

    public void setKcPersonService(KcPersonService kcPersonService) {
        this.kcPersonService = kcPersonService;
    }


    @Override
    public Boolean getORDExpedited(String proposalNumber) throws LookupException {
        LOG.debug("getORDExpedited():"+proposalNumber);
        try {
            return dataAccessObject.getORDExpedited(proposalNumber);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
            throw new LookupException(e);
        }
    }


    @Override
    public void setORDExpedited(String proposalNumber, Boolean ordExp) throws AuthorizationException, IllegalArgumentException {    
        LOG.debug("setORDExpedited() propNumber:"+proposalNumber+ " ordExp="+ordExp+" currentUser:"+GlobalVariables.getUserSession().getPrincipalName() );
        checkUserAuthorization( PropDevRoutingStateConstants.EDIT_ORD_EXPEDITED_PERMISSION);

        try {
            dataAccessObject.setORDExpedited(proposalNumber, ordExp);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
            throw new RuntimeException(e);
        }
        LOG.debug("setORDExpedited(): Finished.");
    }


    @Override
    public KcPerson getSPSReviewer(String proposalNumber) throws LookupException {
        LOG.debug("getSPSReviewer():"+proposalNumber);
        try {
            String spsReviewerId = dataAccessObject.getSPSReviewer(proposalNumber);
            if ( StringUtils.isNotEmpty(spsReviewerId)){
                return getKcPersonService().getKcPersonByPersonId(spsReviewerId);
            }
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
            throw new LookupException(e);
        }
        LOG.debug("getSPSReviewer():" +proposalNumber+"returning null...");
        return null;
    }


    @Override
    public void setSPSReviewer(String proposalNumber, String kcPersonId) throws AuthorizationException, IllegalArgumentException {
        LOG.debug("setSPSReviewer() propNumber:"+proposalNumber+ " kcPersonId="+kcPersonId +" crtUser="+GlobalVariables.getUserSession().getPrincipalName());
        checkUserAuthorization( PropDevRoutingStateConstants.EDIT_SPS_REVIEWER_PERMISSION); 
        KcPerson kcPerson = null;
        try {
            kcPerson = getKcPersonService().getKcPersonByPersonId(kcPersonId);
        } catch (Exception e){
            throw new IllegalArgumentException("setSPSReviewer: Invalid user:"+kcPersonId);
        }
        try {
            dataAccessObject.setSPSReviewer(proposalNumber, kcPerson.getPersonId(), kcPerson.getFullName());
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
            throw new RuntimeException(e);
        }
        LOG.debug("setSPSReviewer(): Finished.");
    }



    @Override
    public List<SPSReviewer> findSPSReviewers() throws LookupException{
        LOG.debug("findSPSReviewers():Starting. crtUser="+GlobalVariables.getUserSession().getPrincipalName());
        List<SPSReviewer> result = new ArrayList<SPSReviewer>();
        try {
            Collection<String> membersIds= getAuthorizationService().getSPSReviewerRoleMembers();
            result = dataAccessObject.findSPSReviewers(membersIds);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
            throw new LookupException(e);
        }
        LOG.debug("findSPSReviewers():FINISHED");
        return result;
    }





    private void checkUserAuthorization(String permissionName){
        LOG.debug("checkUserAuthorization():"+ permissionName);
        String currentUserId = GlobalVariables.getUserSession().getPrincipalId();
        if ( StringUtils.isEmpty(currentUserId) ){
            throw new AuthorizationException(currentUserId, permissionName, "DevelopmentProposalDashboard");
        }
        if (!getAuthorizationService().hasSPSPermission(currentUserId, permissionName)){
            throw new AuthorizationException(currentUserId, permissionName, "DevelopmentProposalDashboard");
        }
        LOG.debug("checkUserAuthorization():FINISHED");
    }


}
