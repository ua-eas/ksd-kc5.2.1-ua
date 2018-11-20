package edu.arizona.kra.sys.batch;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.List;

import static edu.arizona.kra.sys.batch.BatchConstants.SCHEDULED_GROUP;
import static edu.arizona.kra.sys.batch.BatchConstants.UNSCHEDULED_GROUP;

/**
 * Batch Job Status Edit supporting class
 */
public class SchedulerGroupValuesFinder extends KeyValuesBase {

    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue("", ""));
        labels.add(new ConcreteKeyValue(SCHEDULED_GROUP, "Scheduled"));
        labels.add(new ConcreteKeyValue(UNSCHEDULED_GROUP, "Unscheduled"));

        return labels;
    }


}