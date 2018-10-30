
package edu.arizona.kra.web.struts.action;

import edu.arizona.kra.sys.batch.BatchConstants;
import edu.arizona.kra.sys.batch.BatchJobStatus;
import edu.arizona.kra.sys.batch.service.SchedulerService;
import edu.arizona.kra.web.struts.form.KualiBatchJobModifyForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KualiBatchJobModifyAction extends KualiAction {


    private static SchedulerService schedulerService;
    private static ParameterService parameterService;
    private static PermissionService permissionService;
    private static DateTimeService dateTimeService;


    /**
     * Performs the actual authorization check for a given job and action against the current user. This method can be overridden by
     * sub-classes if more granular controls are desired.
     *
     * @param methodToCall
     * @throws AuthorizationException
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (form instanceof KualiBatchJobModifyForm) {
            if (!userHasBatchModifyPermission()) {
                throw new AuthorizationException(GlobalVariables.getUserSession().getPrincipalName(), methodToCall, (((KualiBatchJobModifyForm) form).getJob().getName()));
            }
        } else {
            super.checkAuthorization(form, methodToCall);
        }
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // load the given job and map into the form
        String jobName = request.getParameter(BatchConstants.JOB_NAME_PARAMETER);
        String jobGroup = request.getParameter(BatchConstants.JOB_GROUP_PARAMETER);
        if (form instanceof KualiBatchJobModifyForm) {
            ((KualiBatchJobModifyForm) form).setJob(getSchedulerService().getJob(jobGroup, jobName));
        }
        ActionForward forward = super.execute(mapping, form, request, response);
        return forward;
    }



    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm) form;

        request.setAttribute("job", batchModifyForm.getJob());
        request.setAttribute("canRunJob", canModifyJob(batchModifyForm, "runJob"));
        request.setAttribute("canSchedule", canModifyJob(batchModifyForm, "schedule"));
        request.setAttribute("canUnschedule", canModifyJob(batchModifyForm, "unschedule"));
        request.setAttribute("canStopJob", canModifyJob(batchModifyForm, "stopJob"));
        request.setAttribute("userEmailAddress", GlobalVariables.getUserSession().getPerson().getEmailAddressUnmasked());

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward runJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm) form;

        checkJobAuthorization(batchModifyForm, "runJob");

        String startStepStr = request.getParameter(BatchConstants.START_STEP_PARAMETER);
        String endStepStr = request.getParameter(BatchConstants.END_STEP_PARAMETER);
        String startTimeStr = request.getParameter(BatchConstants.START_TIME_PARAMETER);
        String emailAddress = request.getParameter(BatchConstants.EMAIL_PARAMETER);

        int startStep = Integer.parseInt(startStepStr);
        int endStep = Integer.parseInt(endStepStr);
        Date startTime;
        if (!StringUtils.isBlank(startTimeStr)) {
            startTime = getDateTimeService().convertToDateTime(startTimeStr);
        } else {
            startTime = getDateTimeService().getCurrentDate();
        }

        batchModifyForm.getJob().runJob(startStep, endStep, startTime, emailAddress);

        // redirect to display form to prevent re-execution of the job by mistake
        return getForward(batchModifyForm.getJob());
    }

    public ActionForward stopJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm) form;

        checkJobAuthorization(batchModifyForm, "stopJob");

        batchModifyForm.getJob().interrupt();

        return getForward(batchModifyForm.getJob());
    }


    public ActionForward schedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm) form;

        checkJobAuthorization(batchModifyForm, "schedule");

        batchModifyForm.getJob().schedule();

        return getForward(batchModifyForm.getJob());
    }

    public ActionForward unschedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm) form;

        checkJobAuthorization(batchModifyForm, "unschedule");

        batchModifyForm.getJob().unschedule();

        // move to the unscheduled job object since the scheduled one has been removed
        batchModifyForm.setJob(getSchedulerService().getJob(BatchConstants.UNSCHEDULED_GROUP, batchModifyForm.getJob().getName()));

        return getForward(batchModifyForm.getJob());
    }

    protected boolean canModifyJob(KualiBatchJobModifyForm form, String actionType) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, form.getJob().getNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.BEAN_NAME, form.getJob().getName());

        return getPermissionService().isAuthorizedByTemplate(
                GlobalVariables.getUserSession().getPrincipalId(),
                BatchConstants.PERMISSION_TEMPLATE_MODIFY_BATCH_JOB_NAMESPACE,
                BatchConstants.PERMISSION_NAME_MODIFY_BATCH_JOB,
                permissionDetails,
                new HashMap<String, String>(getRoleQualification(form, actionType)));
    }


    protected boolean userHasBatchModifyPermission() {
        return getPermissionService().hasPermission(
                GlobalVariables.getUserSession().getPerson().getPrincipalId(),
                BatchConstants.PERMISSION_MODIFY_BATCH_JOB_NAMESPACE,
                BatchConstants.PERMISSION_NAME_MODIFY_BATCH_JOB);
    }


    protected void checkJobAuthorization(KualiBatchJobModifyForm form, String actionType) throws AuthorizationException {
        if (!canModifyJob(form, actionType)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPrincipalName(), "actionType", form.getJob().getName());
        }
    }

    private ActionForward getForward(BatchJobStatus job) {
        return new ActionForward( KraServiceLocator.getService(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/batchModify.do?methodToCall=start&name=" + UrlFactory.encode(job.getName()) + "&group=" + UrlFactory.encode(job.getGroup()), true);
    }

    public static DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = KraServiceLocator.getService(DateTimeService.class);
        }
        return dateTimeService;
    }

    public PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService =KraServiceLocator.getService(PermissionService.class);
        }
        return permissionService;
    }

    private SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService =  KraServiceLocator.getService(SchedulerService.class);
        }
        return schedulerService;
    }

    public static ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService =  KraServiceLocator.getService(ParameterService.class);
        }
        return parameterService;
    }
}

