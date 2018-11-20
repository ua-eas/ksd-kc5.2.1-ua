package edu.arizona.kra.sys.batch.lookup;

import edu.arizona.kra.sys.batch.BatchConstants;
import edu.arizona.kra.sys.batch.BatchJobStatus;
import edu.arizona.kra.sys.batch.service.SchedulerService;
import edu.arizona.kra.sys.batch.service.impl.KcModuleServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by nataliac on 10/18/18: Batch framework imported and adapted from KFS
 */
public class BatchJobStatusLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchJobStatusLookupableHelperServiceImpl.class);

    private SchedulerService schedulerService;
    private ParameterService parameterService;
    private KualiModuleService kualiModuleService;
    private PermissionService permissionService;


    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        super.setBackLocation((String) fieldValues.get(KRADConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KRADConstants.DOC_FORM_KEY));
        List<BatchJobStatus> allJobs = schedulerService.getJobs();
        List<BatchJobStatus> jobs = new ArrayList<BatchJobStatus>();

        String namespaceCode = fieldValues.get("namespaceCode");
        String nameValue = fieldValues.get("name");
        Pattern namePattern = null;
        if (!StringUtils.isEmpty(nameValue)) {
            namePattern = Pattern.compile(nameValue.replace("*", ".*"), Pattern.CASE_INSENSITIVE);
        }
        String schedulerGroup = fieldValues.get("group");
        String jobStatus = fieldValues.get("status");
        for (BatchJobStatus job : allJobs) {
            if (!StringUtils.isEmpty(namespaceCode) &&
                    (!namespaceCode.equalsIgnoreCase(job.getNamespaceCode()) && job.getNamespaceCode() != null)) {
                continue;
            }
            if (namePattern != null && !namePattern.matcher(job.getName()).matches()) {
                continue; // match failed, skip this entry
            }
            if (!StringUtils.isEmpty(schedulerGroup) && !schedulerGroup.equalsIgnoreCase(job.getGroup())) {
                continue;
            }
            if (!StringUtils.isEmpty(jobStatus) && !jobStatus.equalsIgnoreCase(job.getStatus())) {
                continue;
            }
            jobs.add(job);
        }

        return jobs;
    }

    public boolean doesModuleServiceHaveJobStatus(BatchJobStatus job) {
        if (job != null) {
            KcModuleServiceImpl moduleService = (KcModuleServiceImpl) getKualiModuleService().getResponsibleModuleServiceForJob(job.getName());
            //This means this job is externalized and we do not want to show any action urls for it.
            return (moduleService != null && moduleService.isExternalJob(job.getName()));
        }
        return false;
    }

    /***
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if (businessObject instanceof BatchJobStatus) {
            BatchJobStatus job = (BatchJobStatus) businessObject;
            if (doesModuleServiceHaveJobStatus(job) && !userHasBatchModifyPermission()) {
                return getEmptyActionUrls();
            }
            String linkText = BatchConstants.MODIFY_ACTION_NAME;
            Map<String, String> permissionDetails = new HashMap<String, String>(1);
            permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, job.getNamespaceCode());

            String href = configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/batchModify.do?methodToCall=start&name=" + (UrlFactory.encode(job.getName())) + ("&group=") + (UrlFactory.encode(job.getGroup()));
            List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
            HtmlData.AnchorHtmlData anchorHtmlData = new HtmlData.AnchorHtmlData(href, KRADConstants.START_METHOD, linkText);
            anchorHtmlDataList.add(anchorHtmlData);
            return anchorHtmlDataList;
        }
        return getEmptyActionUrls();
    }


    @Override
    protected String getActionUrlTitleText(BusinessObject businessObject, String displayText, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        BatchJobStatus job = (BatchJobStatus) businessObject;
        String titleText = displayText + " "
                + getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(getBusinessObjectClass().getName()).getObjectLabel()
                + " "
                + configurationService.getPropertyValueAsString(TITLE_ACTION_URL_PREPENDTEXT_PROPERTY);
        titleText += "Name=" + job.getName() + " Group=" + job.getGroup();
        return titleText;
    }

    @Override
    public boolean allowsMaintenanceNewOrCopyAction(){
        return true;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    public KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KraServiceLocator.getService(KualiModuleService.class);
        }
        return kualiModuleService;
    }


    public PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService =KraServiceLocator.getService(PermissionService.class);
        }
        return permissionService;
    }

    protected boolean userHasBatchModifyPermission() {
        return getPermissionService().hasPermission(
                GlobalVariables.getUserSession().getPerson().getPrincipalId(),
                BatchConstants.PERMISSION_MODIFY_BATCH_JOB_NAMESPACE,
                BatchConstants.PERMISSION_NAME_MODIFY_BATCH_JOB);
    }

}
