package edu.arizona.kra.negotiations.dao.ojb;

import edu.arizona.kra.negotiations.dao.NegotiationLookupDao;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kra.negotiations.bo.Negotiation;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static edu.arizona.kra.negotiations.NegotiationConstants.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by nataliac.
 */
public class NegotiationLookupDaoOjb extends LookupDaoOjb implements NegotiationLookupDao {
    private static final Logger LOG = LoggerFactory.getLogger(NegotiationLookupDaoOjb.class);



    @Override
    public Collection<Negotiation> getNegotiationResults(Map<String, String> fieldValues) throws LookupException {
        LOG.debug("getNegotiationResults fieldValues={}",fieldValues);
        Collection<Negotiation> searchResults = new ArrayList();

        String negotiationAge = fieldValues.get(NEGOTIATION_AGE);
        if ( StringUtils.isNotEmpty(negotiationAge) ){
            fieldValues.remove(NEGOTIATION_AGE);
        }

        Map<String, String> associationDetails = getAssociationFilters( fieldValues );

        //generate negotiation query criteria from user filters
        Criteria searchCriteria = getCollectionCriteriaFromMap(new Negotiation(), fieldValues);

        //add additional criteria related to associated objects - if any
        if (!associationDetails.isEmpty()) {
            addLinkedAwardsCriteria(associationDetails, searchCriteria);
            addLinkedSubAwardsCriteria(associationDetails, searchCriteria);
            addLinkedProposalsCriteria(associationDetails, searchCriteria);
            addLinkedProposalLogsCriteria(associationDetails, searchCriteria);
            addUnassociatedNegotiationsCriteria(associationDetails, searchCriteria);
        }

        //add additional criteria related to negotiation age - if any
        addNegotiationAgeCriteria(negotiationAge, searchCriteria);

        try {
            //add orderBy to query
            QueryByCriteria query = QueryFactory.newQuery(Negotiation.class, searchCriteria);
            query.addOrderByAscending(NEGOTIATION_ID);


            Long matchingResultsCount = null;
            Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(Negotiation.class);
            if (searchResultsLimit != null) {
                LOG.debug("getNegotiationResults searchResultsLimit={}",searchResultsLimit);

                matchingResultsCount = new Long(getPersistenceBrokerTemplate().getCount(query));
                LOG.debug("getNegotiationResults matchingResultsCount={}",matchingResultsCount);

                LookupUtils.applySearchResultsLimit(Negotiation.class, searchCriteria, getDbPlatform());
            }
            if ((matchingResultsCount == null) || (matchingResultsCount.intValue() <= searchResultsLimit.intValue())) {
                matchingResultsCount = new Long(0);
            }
            searchResults = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            return new CollectionIncomplete(searchResults, matchingResultsCount);


        }catch (Exception e) {
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


    private void addLinkedAwardsCriteria(Map<String, String> associatedValues, Criteria criteria) {
        Map<String, String> awardCriteria = transformMap(associatedValues, awardTransform);
        if (awardCriteria == null) {
            LOG.debug("addLinkedAwardsCriteria: No additional criteria for awards..")
            return;
        }
        //add active or pending version criteria to award search
        awardCriteria.put
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

    private void addLinkedAwardsCriteria(String negotiationAge, Criteria criteria) {
        if ( StringUtils.isNotEmpty(negotiationAge) ) {
            if(){

            }
            else {
                LOG.warn("Negotiation age criteria unParseable={}", negotiationAge);
            }
        }
    }



}
