/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.subaward.dao.impl;


import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kra.bo.Organization;
import org.kuali.kra.bo.versioning.VersionStatus;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.dao.SubAwardLookupDao;
import org.kuali.kra.subaward.service.SubAwardService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.kim.api.identity.PersonService;
import org.apache.commons.lang.StringUtils;

import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;


public class SubAwardLookupDaoOjb extends LookupDaoOjb implements SubAwardLookupDao{
    private static final Logger LOG = LoggerFactory.getLogger(SubAwardLookupDaoOjb.class);

    private static final String AWARD_NUMBER = "awardNumber";
    private static final String ORGANIZATION_NAME = "organizationName";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String REQUISITIONER_NAME = "requisitionerUserName";
    private static final String REQUISITIONER_ID = "requisitionerId";
    private static final String SUBAWARD_ID = "subAwardId";
    private static final String VERSION_CLASS = "versionHistory.sequenceOwnerClassName";
    private static final String VERSION_STATUS = "versionHistory.statusForOjb";

    private PersonService personService;
    private SubAwardService subAwardService;


    @Override
    public Collection<SubAward> findSubAwards(Map<String, String> filters) throws SQLException, LookupException {
        LOG.debug("findSubawards filters={}",filters);
        Collection searchResults = new ArrayList();

        //deal with requisitionerName and orgName from filters as the columns don't exist in the subawards table and criteria doesn't correctly generate
        String requisitionerName = filters.get(REQUISITIONER_NAME);
        if ( StringUtils.isNotEmpty(requisitionerName) ){
            filters.remove(REQUISITIONER_NAME);
        }

        String organizationName = filters.get(ORGANIZATION_NAME);
        if ( StringUtils.isNotEmpty(organizationName) ){
            filters.remove(ORGANIZATION_NAME);
        }

        //if Award is specified, reduce the scope of the search by finding first the subAwards associated with the given AwardNumber
        String awardNumber = filters.get(AWARD_NUMBER);
        if ( StringUtils.isNotEmpty(awardNumber) ) {
            filters.remove(AWARD_NUMBER);
        }

        //generate subaward query criteria from remaining filters
        Criteria criteria = this.getCollectionCriteriaFromMap(new SubAward(), filters);

        //add additional criteria related to the out of the ordinary fields: reqName, orgName and awardNumber as well versioning
        addRequisitionerCriteria(requisitionerName, criteria);
        addOrganizationCriteria(organizationName, criteria);
        addAwardCriteria(awardNumber,criteria);
        //add additional criteria that only active/pending subawards should be returned from the search
        addActiveOrPendingVersionCriteria(criteria);


        try {
            //add orderBy to query
            QueryByCriteria query = QueryFactory.newQuery(SubAward.class, criteria);
            query.addOrderByAscending(SUBAWARD_ID);

            Long matchingResultsCount = null;
            Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(SubAward.class);
            if (searchResultsLimit != null) {
                LOG.debug("findSubawards searchResultsLimit={}",searchResultsLimit);
                matchingResultsCount = new Long(getPersistenceBrokerTemplate().getCount(query));
                LOG.debug("findSubawards matchingResultsCount={}",matchingResultsCount);
                LookupUtils.applySearchResultsLimit(SubAward.class, criteria, getDbPlatform());
            }
            if ((matchingResultsCount == null) || (matchingResultsCount.intValue() <= searchResultsLimit.intValue())) {
                matchingResultsCount = new Long(0);
            }
            searchResults = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            return new CollectionIncomplete(searchResults, matchingResultsCount);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new LookupException(e);
        }

    }


    private void addRequisitionerCriteria(String requisitionerName, Criteria criteria) {
        if ( StringUtils.isNotEmpty(requisitionerName) ) {
            Person requisitioner = getPersonService().getPersonByPrincipalName(requisitionerName);
            if (requisitioner != null) {
                LOG.debug("Found requisitionerId={} requisitionerName={}", requisitioner.getPrincipalId(), requisitionerName);
                Criteria requisitionerCriteria = new Criteria();
                requisitionerCriteria.addEqualTo(REQUISITIONER_ID, requisitioner.getPrincipalId());
                criteria.addAndCriteria(requisitionerCriteria);
            }
            else {
                LOG.warn("Could not find person for requisitionerName={}", requisitionerName);
            }
        }
    }


    private void addOrganizationCriteria(String organizationName, Criteria criteria) {
        if ( StringUtils.isNotEmpty(organizationName) ) {
            Collection<String> organizationIds = findOrganizationIds( organizationName );
            if ( !organizationIds.isEmpty() ){
                Criteria organizationsCriteria = new Criteria();
                organizationsCriteria.addIn(ORGANIZATION_ID, organizationIds);
                criteria.addAndCriteria(organizationsCriteria);
            }
        }
    }


    private void addAwardCriteria(String awardNumber, Criteria criteria) {
        if ( StringUtils.isNotEmpty(awardNumber) ) {
            String number = awardNumber.replace("*", "%").replace("?","_");
            Collection<String> subAwardIds = getSubAwardService().getLinkedSubAwardsIds(number);
            if (!subAwardIds.isEmpty()) {
                Criteria awardCriteria = new Criteria();
                awardCriteria.addIn(SUBAWARD_ID, subAwardIds);
                criteria.addAndCriteria(awardCriteria);
            }
        }
    }


    private void addActiveOrPendingVersionCriteria(Criteria criteria) {
        LOG.debug("addActiveOrPendingVersionCriteria start");

        criteria.addEqualTo(VERSION_CLASS, SubAward.class.getName());
        List<String> statuses = Arrays.asList(VersionStatus.ACTIVE.name(), VersionStatus.PENDING.name());

        Criteria andCriteria = new Criteria();
        andCriteria.addIn(VERSION_STATUS, statuses);
        criteria.addAndCriteria(andCriteria);

        LOG.debug("addActiveOrPendingVersionCriteria end criteria={}",criteria);
    }

    private Collection<String> findOrganizationIds(String organizationName){
        Collection<String> organizationIds = new ArrayList<>();

        String lowerCaseOrgName = organizationName.replace("*", "%").replace("?","_").toLowerCase();
        LOG.debug("Searching for organization name={} ",lowerCaseOrgName);
        Criteria orgCriteria = new Criteria();
        orgCriteria.addLike("lower("+ORGANIZATION_NAME+")", lowerCaseOrgName);

        Collection<Organization> organizations = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Organization.class, orgCriteria));
        if ( organizations != null && !organizations.isEmpty() ) {
            for ( Organization org : organizations){
                organizationIds.add( org.getOrganizationId() );
            }
        }
        else {
            LOG.warn("Could not find any organizations for organizationName={}", organizationName);
        }
        return organizationIds;
    }


    public SubAwardService getSubAwardService(){
        if (this.subAwardService == null) {
            this.subAwardService = KraServiceLocator.getService(SubAwardService.class);
        }
        return this.subAwardService;
    }


    public PersonService getPersonService() {
        if (this.personService == null) {
            this.personService = KraServiceLocator.getService(PersonService.class);
        }
        return this.personService;
    }

}
