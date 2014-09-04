package edu.arizona.kra.irb.associateworkflow;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kra.committee.bo.Committee;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.correspondence.ProtocolCorrespondenceTemplate;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.kra.lookup.keyvalue.KeyValueComparator;


/**
 * 
 * This class is to create key/values pair of active committees.
 * It is based on the org.kuali.kra.committee.lookup.keyvalue.CommitteeIdValuesFinder
 * except that it does not add a "select" element at the top of the list
 * and the getKeyValues is slightly optimized for performance.
 */
@SuppressWarnings("serial")
public class CommitteeIdValuesFinder extends KeyValuesBase {
    
    private List<ProtocolCorrespondenceTemplate> correspondenceTemplates;

    /**
     * @return the list of &lt;key, value&gt; pairs of committees. The first entry is always &lt;"", "select:"&gt;.
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
    	Collection<Committee> committees = KraServiceLocator.getService(BusinessObjectService.class).findAll(Committee.class);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        if (CollectionUtils.isNotEmpty(committees)) {
        	Set<String> excludedCommitteeIds = getExcludedCommitteeIds();

            // only the active ones
            Collections.sort((List<Committee>) committees, Collections.reverseOrder());
            for (Committee committee : committees) {
                if (!excludedCommitteeIds.contains(committee.getCommitteeId())) {
                    keyValues.add(new ConcreteKeyValue(committee.getCommitteeId(), committee.getCommitteeName()));
                    excludedCommitteeIds.add(committee.getCommitteeId());
                }
            }

            sort(keyValues, new KeyValueComparator());
        }
        
        return keyValues;
    }
    
    private Set<String> getExcludedCommitteeIds() {
        Set<String> committeeIds = new HashSet<String>();

        if (CollectionUtils.isNotEmpty(correspondenceTemplates)) {
            for (ProtocolCorrespondenceTemplate correspondenceTemplate : correspondenceTemplates) {
                committeeIds.add(correspondenceTemplate.getCommitteeId());
            }
        }

        return committeeIds;
    }

    public List<ProtocolCorrespondenceTemplate> getCorrespondenceTemplates() {
        return correspondenceTemplates;
    }

    public void setCorrespondenceTemplates(List<ProtocolCorrespondenceTemplate> correspondenceTemplates) {
        this.correspondenceTemplates = correspondenceTemplates;
    }

}
