/*
 * Kuali Coeus, a comprehensive research administration system for higher education.
 *
 * Copyright 2005-2016 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kra.negotiations.lookup;

import edu.arizona.kra.negotiations.dao.ojb.NegotiationLookupDaoOjb;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.*;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.bo.versioning.VersionStatus;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.proposallog.ProposalLog;
import org.kuali.kra.negotiations.bo.Negotiation;
import org.kuali.kra.negotiations.bo.NegotiationAssociationType;
import org.kuali.kra.negotiations.bo.NegotiationUnassociatedDetail;
import org.kuali.kra.negotiations.service.NegotiationService;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.lookup.CollectionIncomplete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static edu.arizona.kra.negotiations.NegotiationConstants.*;

import java.util.*;

/**
 * Negotiation Dao to assist with lookups. This implements looking up associated document information
 * as well as just negotiation info.
 */
public class NegotiationDaoOjb extends LookupDaoOjb implements NegotiationDao {
    private static final Logger LOG = LoggerFactory.getLogger(NegotiationLookupDaoOjb.class);

    private NegotiationService negotiationService;
    private String negotiationAge;
    private Criteria negotiationAgeCriteria;
    private Integer maxSearchResults;



    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public Collection<Negotiation> getNegotiationResults(Map<String, String> fieldValues) {
        LOG.debug("getNegotiationResults fieldValues={}",fieldValues);
        CollectionIncomplete<Negotiation> searchResults = new CollectionIncomplete<>(new ArrayList(0), 0L);

        //negotiation Age filtering done as a special case
        negotiationAge = fieldValues.get(NEGOTIATION_AGE);
        fieldValues.remove(NEGOTIATION_AGE);

        Map<String, String> associationDetails = getAssociationFilters( fieldValues );


        //add additional criteria related to associated objects - if any
        if (!associationDetails.isEmpty()) {
            addListToList(searchResults, getNegotiationsLinkedToAward(fieldValues, associationDetails));
            addListToList(searchResults, getNegotiationsLinkedToProposal(fieldValues, associationDetails, searchResults.size()>=getMaxSearchResultLimit()));
            addListToList(searchResults, getNegotiationsUnassociated(fieldValues, associationDetails, searchResults.size()>=getMaxSearchResultLimit()));
            addListToList(searchResults, getNegotiationsLinkedToSubAward(fieldValues, associationDetails, searchResults.size()>=getMaxSearchResultLimit()));

        } else {
            //generate negotiation query criteria from user filters
            Criteria negotiationSearchCriteria = getCollectionCriteriaFromMap(new Negotiation(), fieldValues);
            LOG.debug("Negotiation search criteria: {}", negotiationSearchCriteria);
            searchResults = executeSearch(negotiationSearchCriteria, false);
        }


        LOG.debug("getNegotiationResults exit getNegotiationResults");
        return searchResults;
    }

    


    /**
     * Search for awards linked to negotiation using both award and negotiation values.
     */
    protected CollectionIncomplete<Negotiation> getNegotiationsLinkedToAward(Map<String, String> negotiationFilters, Map<String, String> associatedValues) {
        Map<String, String> values = transformMap(associatedValues, awardTransform);

        if (values == null)  {
            return new CollectionIncomplete<>(new ArrayList(0),0L);
        }

        Long negotiationTypeId = getNegotiationService().getNegotiationAssociationType(NegotiationAssociationType.AWARD_ASSOCIATION).getId();
        String negotiationTypeIdFilter = negotiationFilters.get(NEGOTIATION_TYPE_ATTR);
        if ( StringUtils.isNotEmpty(negotiationTypeIdFilter) && !negotiationTypeId.equals( Long.parseLong(negotiationTypeIdFilter)) )  {
            LOG.debug("getNegotiationsLinkedToAward: Skipping search as found different type filter="+negotiationTypeIdFilter);
            return new CollectionIncomplete<>(new ArrayList(0),0L);
        }

        Criteria criteria = getCollectionCriteriaFromMap(new Award(), values);

        //filter only active/pending awards
        Criteria activeAwardSubCriteria = new Criteria();
        activeAwardSubCriteria.addIn(AWARD_SEQUENCE_STATUS, Arrays.asList(VersionStatus.ACTIVE.toString(), VersionStatus.PENDING.toString()));
        ReportQueryByCriteria activeAwardIdsQuery = QueryFactory.newReportQuery(Award.class, activeAwardSubCriteria);
        activeAwardIdsQuery.setAttributes(new String[]{MAX_AWARD_ID});
        activeAwardIdsQuery.addGroupBy(AWARD_NUMBER);

        criteria.addIn(AWARD_ID, activeAwardIdsQuery);

        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(Award.class, criteria);
        subQuery.setAttributes(new String[] {AWARD_NUMBER});

        Criteria negotiationsLinkedToAwardCriteria =  getCollectionCriteriaFromMap(new Negotiation(), negotiationFilters);
        negotiationsLinkedToAwardCriteria.addIn(ASSOCIATED_DOC_ID_ATTR, subQuery);
        negotiationsLinkedToAwardCriteria.addEqualTo(NEGOTIATION_TYPE_ATTR, negotiationTypeId);

        return executeSearch(negotiationsLinkedToAwardCriteria, false);
    }

    /**
     * Search for institutional proposals linked to negotiations using both criteria.
     */
    protected CollectionIncomplete<Negotiation> getNegotiationsLinkedToProposal(Map<String, String> negotiationFilters, Map<String, String> associatedValues, boolean rowCountOnly) {
        LOG.debug("ENTER getNegotiationsLinkedToProposal rowCountOnly="+rowCountOnly);
        Map<String, String> values = transformMap(associatedValues, institutionalProposalTransform);
        CollectionIncomplete<Negotiation> result =  new CollectionIncomplete<>(new ArrayList(0),0L);
        if (values == null) {
            return result;
        }

        Long negotiationTypeId = getNegotiationService().getNegotiationAssociationType(NegotiationAssociationType.INSTITUATIONAL_PROPOSAL_ASSOCIATION).getId();
        String negotiationTypeIdFilter = negotiationFilters.get(NEGOTIATION_TYPE_ATTR);
        if ( StringUtils.isNotEmpty(negotiationTypeIdFilter) && !negotiationTypeId.equals( Long.parseLong(negotiationTypeIdFilter)) )  {
            LOG.debug("getNegotiationsLinkedToProposal: Skipping search as found different type filter="+negotiationTypeIdFilter);
            return result;
        }


        values.put(PROPOSAL_SEQUENCE_STATUS, VersionStatus.ACTIVE.name());
        Criteria criteria = getCollectionCriteriaFromMap(new InstitutionalProposal(), values);

        Criteria negotiationsLinkedToIPCriteria = getCollectionCriteriaFromMap(new Negotiation(), negotiationFilters);

        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(InstitutionalProposal.class, criteria);
        subQuery.setAttributes(new String[] {PROPOSAL_NUMBER});
        negotiationsLinkedToIPCriteria.addIn(ASSOCIATED_DOC_ID_ATTR, subQuery);
        negotiationsLinkedToIPCriteria.addEqualTo(NEGOTIATION_TYPE_ATTR, negotiationTypeId);

        return executeSearch(negotiationsLinkedToIPCriteria, rowCountOnly);

    }


    /**
     *
     * This method returns Negotiations linked to subawards based on search.
     */
    protected CollectionIncomplete<Negotiation> getNegotiationsLinkedToSubAward(Map<String, String> negotiationFilters, Map<String, String> associatedValues, boolean rowCountOnly) {
        LOG.debug("ENTER getNegotiationsLinkedToSubAward rowCountOnly="+rowCountOnly);
        Map<String, String> values = transformMap(associatedValues, subAwardTransform);

        CollectionIncomplete<Negotiation> result =  new CollectionIncomplete<>(new ArrayList(0),0L);
        if (values == null) {
            return result;
        }

        Long negotiationTypeId = getNegotiationService().getNegotiationAssociationType(NegotiationAssociationType.SUB_AWARD_ASSOCIATION).getId();
        String negotiationTypeIdFilter = negotiationFilters.get(NEGOTIATION_TYPE_ATTR);
        if ( StringUtils.isNotEmpty(negotiationTypeIdFilter) && !negotiationTypeId.equals( Long.parseLong(negotiationTypeIdFilter)) )  {
            LOG.debug("getNegotiationsLinkedToProposal: Skipping search as found different type filter="+negotiationTypeIdFilter);
            return result;
        }

        Criteria subAwardCriteria = getCollectionCriteriaFromMap(new SubAward(), values);

        //filter only active/pending subAwards
        Criteria activeSubAwardCriteria = getActiveOrPendingVersionCriteria();
        ReportQueryByCriteria activeSubAwardCodesQuery = QueryFactory.newReportQuery(SubAward.class, activeSubAwardCriteria);
        activeSubAwardCodesQuery.setAttributes(new String[]{MAX_SUBAWARD_CODE});
        activeSubAwardCodesQuery.addGroupBy(SUBAWARD_CODE);

        subAwardCriteria.addIn(SUBAWARD_CODE, activeSubAwardCodesQuery);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(SubAward.class, subAwardCriteria);
        subQuery.setAttributes(new String[] {SUBAWARD_CODE});


        Criteria negotiationsLinedToSubAwardsCriteria =  getCollectionCriteriaFromMap(new Negotiation(), negotiationFilters);
        negotiationsLinedToSubAwardsCriteria.addIn(ASSOCIATED_DOC_ID_ATTR, subQuery);
        negotiationsLinedToSubAwardsCriteria.addEqualTo(NEGOTIATION_TYPE_ATTR, negotiationTypeId);

        LOG.debug("getNegotiationsLinkedToSubAward criteria="+negotiationsLinedToSubAwardsCriteria.toString());

        return executeSearch(negotiationsLinedToSubAwardsCriteria, rowCountOnly);

    }

    /**
     * Search for unassociated negotiations using criteria from the unassociated detail.
     */
    protected CollectionIncomplete<Negotiation> getNegotiationsUnassociated(Map<String, String> negotiationFilters, Map<String, String> associatedValues, boolean rowCountOnly) {
        LOG.debug("ENTER getNegotiationsUnassociated rowCountOnly="+rowCountOnly);
        Map<String, String> values = transformMap(associatedValues, unassociatedTransform);

        CollectionIncomplete<Negotiation> result =  new CollectionIncomplete<>(new ArrayList(0),0L);
        if (values == null) {
            return result;
        }

        Long negotiationTypeId = getNegotiationService().getNegotiationAssociationType(NegotiationAssociationType.NONE_ASSOCIATION).getId();
        String negotiationTypeIdFilter = negotiationFilters.get(NEGOTIATION_TYPE_ATTR);
        if ( StringUtils.isNotEmpty(negotiationTypeIdFilter) && !negotiationTypeId.equals( Long.parseLong(negotiationTypeIdFilter)) )  {
            LOG.debug("getNegotiationsLinkedToProposal: Skipping search as found different type filter="+negotiationTypeIdFilter);
            return result;
        }


        Criteria negotiationsUnassociatedCriteria = getCollectionCriteriaFromMap(new NegotiationUnassociatedDetail(), values);
        Criteria negotiationCrit = getCollectionCriteriaFromMap(new Negotiation(), negotiationFilters);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(NegotiationUnassociatedDetail.class, negotiationsUnassociatedCriteria);
        subQuery.setAttributes(new String[] {UNASSOCIATED_ID});

        negotiationCrit.addIn(ASSOCIATED_DOC_ID_ATTR, subQuery);
        negotiationCrit.addEqualTo(NEGOTIATION_TYPE_ATTR, negotiationTypeId);

        return executeSearch(negotiationCrit, rowCountOnly);
    }

    private Integer getMaxSearchResultLimit() {
        if (maxSearchResults == null){
            maxSearchResults = LookupUtils.getSearchResultsLimit(Negotiation.class);
            if (maxSearchResults == null) {
                LOG.warn("Negotiation Lookup: SearchResultsLimit for Business Object and Application is undefined!");
                //CAP Negotiation lookup at 300 forcefully:
                maxSearchResults = 300;
            }

        }
        return maxSearchResults;
    }

    private void addListToList(CollectionIncomplete<Negotiation> fullResultList, CollectionIncomplete<Negotiation> listToAdd) {
            if (listToAdd.size() > 0) {
                if (fullResultList.getActualSizeIfTruncated() < getMaxSearchResultLimit()) {
                    int fullResultListPlusListToAddSize = fullResultList.size() + listToAdd.size();
                    if (fullResultListPlusListToAddSize <= getMaxSearchResultLimit()) {
                        fullResultList.addAll(listToAdd);
                    } else {
                        int numberOfNewEntriesToAdd = getMaxSearchResultLimit() - fullResultList.size();

                        Collection<Negotiation> truncatedList = listToAdd.subList(0, numberOfNewEntriesToAdd);
                        fullResultList.addAll(truncatedList);
                    }
                }
            }

            //set the actual count of objects found by search
            fullResultList.setActualSizeIfTruncated(fullResultList.getActualSizeIfTruncated() + listToAdd.getActualSizeIfTruncated());
    }


    /**
     * Take the associated field values and convert them to document specific values using the provided
     * transform key.
     * @param values
     * @param transformKey
     * @return
     */
    protected Map<String, String> transformMap(Map<String, String> values, Map<String, String> transformKey) {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (transformKey.get(entry.getKey()) != null) {
                result.put(transformKey.get(entry.getKey()), entry.getValue());
            } else {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        if (result.containsKey(INVALID_COLUMN_NAME)) {
            return null;
        } else {
            return result;
        }
    }



    private CollectionIncomplete<Negotiation> executeSearch(Criteria searchCriteria, boolean justResultsCount ) {
        LOG.debug("ENTER executeSearch searchCriteria= "+searchCriteria +" justResultsCount="+justResultsCount);
        Collection <Negotiation> searchResults = new ArrayList();
        Long matchingResultsCount = null;
        try {
            if ( StringUtils.isNotEmpty(negotiationAge) ) {
                //add additional criteria related to negotiation age - if any
                searchCriteria.addAndCriteria( getNegotiationAgeCriteria(negotiationAge) );
            }
            LOG.debug("ExecuteSearch: searchCriteria= "+searchCriteria);

            //add orderBy NegotiationId to show the newest on top to query and DISTINCT = true to avoid duplicate results.
            QueryByCriteria query = QueryFactory.newQuery(Negotiation.class, searchCriteria, Boolean.TRUE);
            query.addOrderByDescending(NEGOTIATION_ID);

            LOG.debug("ExecuteSearch: final search Query= "+query.toString());

            matchingResultsCount = new Long(getPersistenceBrokerTemplate().getCount(query));
            LOG.debug("getNegotiationResults matchingResultsCount={}",matchingResultsCount);

            LookupUtils.applySearchResultsLimit(Negotiation.class, searchCriteria, getDbPlatform());

            //don't execute the query if we only want a count...
            if ( !justResultsCount ) {
                    searchResults = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            }

        }
        catch (Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException("NegotiationDaoOjb encountered exception during executeSearch", e);
        }

        return new CollectionIncomplete(searchResults, matchingResultsCount);
    }



    public NegotiationService getNegotiationService() {
        return negotiationService;
    }

    public void setNegotiationService(NegotiationService negotiationService) {
        this.negotiationService = negotiationService;
    }


    /**
     * Method that removes the associated objects filters from the negotiation filters and only if they have values
     * returns them with the appropriate name in a HashMap<String, String>
     * The key name will be modified to remove the ASSOC_PREFIX from it for consistency in compiling the associated object search criteria
     *
     * @param fieldValues
     * @return HashMap<String, String>
     */
    protected Map<String, String> getAssociationFilters(Map<String, String>fieldValues) {
        Map<String, String> associationDetails = new HashMap<String, String>();

        for (Iterator<Map.Entry<String, String>> fieldIter = fieldValues.entrySet().iterator(); fieldIter.hasNext(); ) {
            Map.Entry<String, String> field = fieldIter.next();
            if (StringUtils.startsWith(field.getKey(), ASSOC_PREFIX)) {
                fieldIter.remove();
                //add the associated filter to the result ONLY if it contains some value
                if (!StringUtils.isEmpty(field.getValue())) {
                    associationDetails.put(field.getKey().replaceFirst(ASSOC_PREFIX + ".", ""), field.getValue());
                }
            }
        }

        return associationDetails;
    }

    private Criteria getActiveOrPendingVersionCriteria(){
        List<String> statuses = new ArrayList<String>(2);
        statuses.add(VersionStatus.ACTIVE.name());
        statuses.add(VersionStatus.PENDING.name());
        Criteria activeOrPendingCriteria = new Criteria();
        activeOrPendingCriteria.addIn(VERSION_HISTORY_STATUS, statuses);
        return activeOrPendingCriteria;
    }



    /**
     * Parses the user input for negotiation Age and returns the appropriate Criteria for it.
     * If the input is empty or cannot be parsed, will return an empty Criteria.
     *
     * @param ageFilter Valid examples: integer, >minvalue, <maxvalue, minvalue..maxvalue
     * @return
     */
    private Criteria getNegotiationAgeCriteria( String ageFilter ) {
        //lazy instantiation of the Criteria
        if (negotiationAgeCriteria == null) {
            negotiationAgeCriteria = new Criteria();
            if (StringUtils.isEmpty(ageFilter)) {
                LOG.debug("getCriteria: No additional filters for ageFilter.");
                return negotiationAgeCriteria;
            }

            LOG.debug("Add negotiation age filter={}", ageFilter);

            //Parse the ageFilter to find out what operation we need and what are the limits
            //TODO refactor this old code...
            int lowValue = 0;
            int highValue = 0;
            int equalValue = 0;
            boolean greaterThan = false;
            boolean lessThan = false;
            boolean between = false;

            try {
                if (ageFilter.contains(">")) {
                    greaterThan = true;
                    lowValue = Integer.parseInt(ageFilter.replace(">", ""));
                } else if (ageFilter.contains("<")) {
                    lessThan = true;
                    highValue = Integer.parseInt(ageFilter.replace("<", ""));
                } else if (ageFilter.contains("..")) {
                    between = true;
                    String[] values = ageFilter.split("\\.\\.");
                    lowValue = Integer.parseInt(values[0]);
                    highValue = Integer.parseInt(values[1]);
                } else {
                    equalValue = Integer.parseInt(ageFilter);
                }
            } catch (NumberFormatException e) {
                LOG.warn("Negotiation age criteria unParsable={} ignoring...", ageFilter, e);
                return negotiationAgeCriteria;
            }

            //create subquery for the associated object with the above criteria
            StringBuffer negotiationAgeSqlQuery = new StringBuffer(NEGOTIATION_AGE_QUERY);
            if (equalValue != 0) {
                negotiationAgeSqlQuery.append(NEGOTIATION_AGE_CONDITION + "=" + equalValue);
            } else if (greaterThan) {
                negotiationAgeSqlQuery.append(NEGOTIATION_AGE_CONDITION + ">=" + lowValue);
            } else if (lessThan) {
                negotiationAgeSqlQuery.append(NEGOTIATION_AGE_CONDITION + "<=" + highValue);
            } else if (between) {
                negotiationAgeSqlQuery.append(NEGOTIATION_AGE_CONDITION + ">=" + lowValue);
                negotiationAgeSqlQuery.append(" AND ");
                negotiationAgeSqlQuery.append(NEGOTIATION_AGE_CONDITION + "<=" + highValue);
            } else {
                LOG.warn("Negotiation age criteria: Uncharted territory!!! {} Ignoring...", ageFilter);
                return negotiationAgeCriteria;
            }

            QueryBySQL sqlSubQuery = QueryFactory.newQuery(Negotiation.class, negotiationAgeSqlQuery.toString());

            negotiationAgeCriteria.addIn(NEGOTIATION_ID, sqlSubQuery);
            LOG.debug("Exit getNegotiationAgeCriteria={}", negotiationAgeCriteria);
        }
        return negotiationAgeCriteria;
    }

}