package edu.arizona.kra.sys.batch;

import edu.arizona.kra.sys.batch.service.SchedulerService;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nataliac for Batch Job Status Edit.
 */
public class ScheduledJobStatusValuesFinder extends KeyValuesBase {
    public List getKeyValues() {
        List labels = new ArrayList();

        for (String status : KraServiceLocator.getService(SchedulerService.class).getJobStatuses()) {
            labels.add(new ConcreteKeyValue(status, status));
        }
        return labels;
    }
}
