package edu.arizona.kra.web.struts.form;


import edu.arizona.kra.sys.batch.BatchJobStatus;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class KualiBatchJobModifyForm extends KualiForm {

    private BatchJobStatus job;

    public BatchJobStatus getJob() {
        return job;
    }

    public void setJob(BatchJobStatus job) {
        this.job = job;
    }
}
