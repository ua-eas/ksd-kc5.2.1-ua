package edu.arizona.kra.negotiations.dao.ojb;

import edu.arizona.kra.negotiations.dao.NegotiationLookupDao;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.*;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.bo.versioning.VersionStatus;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
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

import static edu.arizona.kra.negotiations.NegotiationConstants.*;

import java.util.*;

/**
 * Created by nataliac.
 */
public class NegotiationLookupDaoOjb extends LookupDaoOjb implements NegotiationLookupDao {
    private static final Logger LOG = LoggerFactory.getLogger(NegotiationLookupDaoOjb.class);

    private NegotiationService negotiationService;


    @Override
    public Collection<Negotiation> getNegotiationResults(Map<String, String> fieldValues) throws LookupException {
        LOG.debug("getNegotiationResults fieldValues={}",fieldValues);
        Collection<Negotiation> searchResults = new ArrayList();

        String negotiationAge = fieldValues.get(NEGOTIATION_AGE);
        fieldValues.remove(NEGOTIATION_AGE);

        Map<String, String> associationDetails = getAssociationFilters( fieldValues );

        //generate negotiation query criteria from user filters
        Criteria searchCriteria = getCollectionCriteriaFromMap(new Negotiation(), fieldValues);

        //add additional criteria related to associated objects - if any
        if (!associationDetails.isEmpty()) {
            Criteria associatedCriteria = new Criteria();

            associatedCriteria.addOrCriteria( getCriteria(new Award(), NegotiationAssociationType.AWARD_ASSOCIATION, "awardNumber", associationDetails, awardTransform, Boolean.TRUE) );
            associatedCriteria.addOrCriteria( getCriteria(new SubAward(), NegotiationAssociationType.SUB_AWARD_ASSOCIATION, "subAwardCode", associationDetails, subAwardTransform, Boolean.TRUE) );
            associatedCriteria.addOrCriteria( getCriteria(new InstitutionalProposal(), NegotiationAssociationType.INSTITUATIONAL_PROPOSAL_ASSOCIATION, "proposalNumber", associationDetails, institutionalProposalTransform, Boolean.TRUE) );
            associatedCriteria.addOrCriteria( getCriteria(new NegotiationUnassociatedDetail(), NegotiationAssociationType.NONE_ASSOCIATION, "negotiationUnassociatedDetailId", associationDetails, unassociatedTransform, Boolean.FALSE) );

            //UofA doesn't use ProposalLogs... commenting out in case we decide to use them later on
            //associatedCriteria.addOrCriteria( getCriteria(new ProposalLog(), NegotiationAssociationType.PROPOSAL_LOG_ASSOCIATION, "proposalNumber", associationDetails, proposalLogTransform, Boolean.FALSE) );


            if ( !associatedCriteria.isEmpty() ) {
                searchCriteria.addAndCriteria(associatedCriteria);
            }

        }
        if ( StringUtils.isNotEmpty(negotiationAge) ) {
            //add additional criteria related to negotiation age - if any
            searchCriteria.addAndCriteria( getNegotiationAgeCriteria(negotiationAge) );
        }

        LOG.debug("Final search criteria: {}", searchCriteria);

        try {
            //add orderBy NegotiationId to show the newest on top to query and DISTINCT = true to avoid duplicate results.
            QueryByCriteria query = QueryFactory.newQuery(Negotiation.class, searchCriteria, Boolean.TRUE);
            query.addOrderByDescending(NEGOTIATION_ID);

            Long matchingResultsCount = null;
            Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(Negotiation.class);
            if (searchResultsLimit != null) {
                LOG.debug("getNegotiationResults searchResultsLimit={}",searchResultsLimit);

                matchingResultsCount = new Long(getPersistenceBrokerTemplate().getCount(query));
                LOG.debug("getNegotiationResults matchingResultsCount={}",matchingResultsCount);

                LookupUtils.applySearchResultsLimit(Negotiation.class, searchCriteria, getDbPlatform());
            } else {
                LOG.warn("Negotiation Lookup: SearchResultsLimit for Business Object and Application is undefined!");
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

    /**
     * getCriteria constructs the specific subquery criteria referring to the negotiation's assoociated BO which will be used to compose the
     * final lookup criteria
     *
     * @param bo -> the BO of the associated object: Award, Subaward, InstitutionalProposal, unassociatedNegotiation, (proposalLog - not used currently)
     * @param associationType -> NegotiationAssociationType specivic to the BO
     * @param associatedId -> the name of the id field in the associated BO used as a foreign key in the negotiation association document id
     * @param associatedValues -> lookup filters specified by user for this lookup
     * @param transformKey -> map specifying how to translate lookup filters keys to associated BO specific fields
     * @param addActivePendingVersion -> Boolean indicating if we need to look only for ACTIVE+PENDING versions of the associated BOs
     *
     * @return Criteria -> criteria for this lookup specific to the associated BO
     */
    private Criteria getCriteria(KraPersistableBusinessObjectBase bo, String associationType, String associatedId, Map<String,String> associatedValues, Map<String,String> transformKey, boolean addActivePendingVersion){
        Map<String, String> filters = transformMap(associatedValues, transformKey);
        if ( filters == null ){
            LOG.debug("getCriteria: No additional filters for {}..", bo.getClass());
            return new Criteria();
        }
        //creating subQuery for this particular's BO related filtering of negotiations
        if ( addActivePendingVersion && bo instanceof InstitutionalProposal ) {
            filters.put("proposalSequenceStatus", VersionStatus.ACTIVE.name());
        }

        Criteria associatedCriteria = getCollectionCriteriaFromMap(bo, filters);

        //add active or pending VersionHistory criteria to the subquery
        if ( addActivePendingVersion && !(bo instanceof InstitutionalProposal) ){
                associatedCriteria.addAndCriteria(getActiveOrPendingVersionCriteria());
        }
        //create subquery for the associated object with the above criteria
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(bo.getClass(), associatedCriteria);
        subQuery.setAttributes(new String[] {associatedId});

        //create and return the negotiation search criteria including the above subquery
        Criteria associatedBosCriteria = new Criteria();
        associatedBosCriteria.addEqualTo(NEGOTIATION_TYPE_ATTR,
                getNegotiationService().getNegotiationAssociationType(associationType).getId());
        associatedBosCriteria.addIn(ASSOCIATED_DOC_ID_ATTR, subQuery);

        LOG.debug("Exit getCriteria for class {} = {}", bo.getClass(), associatedBosCriteria );
        return associatedBosCriteria;
    }

    /**
     * Parses the user input for negotiation Age and returns the appropriate Criteria for it.
     * If the input is empty or cannot be parsed, will return an empty Criteria.
     *
     * @param ageFilter Valid examples: integer, >minvalue, <maxvalue, minvalue..maxvalue
     * @return
     */
    private Criteria getNegotiationAgeCriteria( String ageFilter ) {
        if ( StringUtils.isEmpty(ageFilter) ) {
            LOG.debug("getCriteria: No additional filters for ageFilter.");
            return new Criteria();
        }
        Criteria negotiationAgeCriteria = new Criteria();
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
        StringBuffer negotiationAgeSqlQuery = new StringBuffer( NEGOTIATION_AGE_QUERY );
        if ( equalValue != 0){
            negotiationAgeSqlQuery.append( NEGOTIATION_AGE_CONDITION + "=" + equalValue );
        } else if ( greaterThan ){
            negotiationAgeSqlQuery.append( NEGOTIATION_AGE_CONDITION + ">=" + lowValue );
        } else if ( lessThan ){
            negotiationAgeSqlQuery.append( NEGOTIATION_AGE_CONDITION + "<=" + highValue );
        } else if ( between ){
            negotiationAgeSqlQuery.append( NEGOTIATION_AGE_CONDITION + ">=" + lowValue );
            negotiationAgeSqlQuery.append( " AND ");
            negotiationAgeSqlQuery.append( NEGOTIATION_AGE_CONDITION + "<=" + highValue);
        } else {
            LOG.warn("Negotiation age criteria: Uncharted territory!!! {} Ignoring...", ageFilter);
            return negotiationAgeCriteria;
        }

        QueryBySQL sqlSubQuery = QueryFactory.newQuery(Negotiation.class, negotiationAgeSqlQuery.toString());

        negotiationAgeCriteria.addIn(NEGOTIATION_ID, sqlSubQuery);
        LOG.debug("Exit getNegotiationAgeCriteria={}", negotiationAgeCriteria);
        return negotiationAgeCriteria;
    }


    private Criteria getActiveOrPendingVersionCriteria(){
        List<String> statuses = new ArrayList<String>(2);
        statuses.add(VersionStatus.ACTIVE.name());
        statuses.add(VersionStatus.PENDING.name());
        Criteria activeOrPendingCriteria = new Criteria();
        activeOrPendingCriteria.addIn("versionHistory.statusForOjb", statuses);
        return activeOrPendingCriteria;
    }


    public NegotiationService getNegotiationService() {
        return negotiationService;
    }

    public void setNegotiationService(NegotiationService negotiationService) {
        this.negotiationService = negotiationService;
    }


}
