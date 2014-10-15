package edu.arizona.kra.irb.associateworkflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.kns.web.struts.action.KualiAction;

@SuppressWarnings("deprecation")
public class ProtocolAssociateWorkflowAction extends KualiAction {
	private static final String ASSOCIATE_WORKFLOW_LOOKUP_URL = "kr/lookup.do?businessObjectClassName=edu.arizona.kra.irb.associateworkflow.ProtocolAssociateWorkflowSearch&docFormKey=88888888&hideReturnLink=true&showMaintenanceLinks=Yes";
	private static final String METHOD_TO_CALL_SEARCH = "&methodToCall=search";
	private static final String METHOD_TO_CALL_START = "&methodToCall=start";
	
	private DateTimeService dateTimeService;
	private ConfigurationService kualiConfigurationService;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		((ProtocolAssociateWorkflowForm) form).setLinksList(getLinksList());
		
		return super.execute(mapping, form, request, response);
	}
	
	/**
	 * Builds a list of links for the Associate Workflow portal page.
	 * When the links are needed to be updated, this method should be
	 * changed. In the future, these links could be persisted and
	 * loaded from the database.
	 * 
	 * @return	The list of associate workflow links
	 */
	private List<ProtocolAssociateWorkflowLinkInfo> getLinksList() {
		List<ProtocolAssociateWorkflowLinkInfo> linksList = new ArrayList<ProtocolAssociateWorkflowLinkInfo>();
		
		ProtocolAssociateWorkflowLinkInfo link;
		StringBuilder urlBuilder;
		String baseUrl = ASSOCIATE_WORKFLOW_LOOKUP_URL + getReturnLocationParam();

		// Pending Submissions 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Pending Submissions ");
		addQueryStringParam("associateUser.principalName", getSessionUser(), urlBuilder);
		addQueryStringParam("committeeId", "Staff", urlBuilder);
		addQueryStringParam("submissionStatusCode", "102", urlBuilder);
		addQueryStringParam("submissionStatusCode", "213", urlBuilder);
		addQueryStringParam("submissionStatusCode", "100", urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Associate = User, Committee = Staff, Submission Status = Pending, Returned to PI, Submitted to Committee");
		linksList.add(link);
		
		// Employee Assignment (Last 30 days) 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Employee Assignment (Last 30 days)");
		addQueryStringParam("associateUser.principalName", getSessionUser(), urlBuilder);
		addQueryStringParam("rangeLowerBoundKeyPrefix_submissionDate", getRelativeDateFromToday(-30), urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Associate = User, Submission Date From = sysdate-30");
		linksList.add(link);
		
		// Waiting for Signatures 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Waiting for Signatures");
		addQueryStringParam("associateUser.principalName", getSessionUser(), urlBuilder);
		addQueryStringParam("committeeId", "Expedite/Exempt", urlBuilder);
		addQueryStringParam("committeeId", "Full Committee", urlBuilder);
		addQueryStringParam("committeeId", "Admin Review", urlBuilder);
		addQueryStringParam("submissionStatusCode", "102", urlBuilder);
		addQueryStringParam("submissionStatusCode", "213", urlBuilder);
		addQueryStringParam("submissionStatusCode", "100", urlBuilder);
		addQueryStringParam("submissionStatusCode", "101", urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Associate = User, Submission status = Submitted to Committee, Returned to PI, Pending, In Agenda, Committee = Expedite/Exempt, Full Committee, Admin Review");
		linksList.add(link);
		
		// Ready for Out-Processing 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Ready for Out-Processing");
		addQueryStringParam("associateUser.principalName", getSessionUser(), urlBuilder);
		addQueryStringParam("chairReviewComplete", KRADConstants.YES_INDICATOR_VALUE, urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Associate = User, Review Complete, Ready for Out-Processing = Yes");
		linksList.add(link);
		
		// Weekly Admin Closures (0-30 days) 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Weekly Admin Closures (0-30 days)");
		addQueryStringParam("rangeLowerBoundKeyPrefix_expirationDate", getRelativeDateFromToday(0), urlBuilder);
		addQueryStringParam("expirationDate", getRelativeDateFromToday(30), urlBuilder);
		addQueryStringParam("submissionStatusCode", "100", urlBuilder);
		addQueryStringParam("committeeId", "Expedite/Exempt", urlBuilder);
		addQueryStringParam("committeeId", "Full Committee", urlBuilder);
		addQueryStringParam("committeeId", "Admin Review", urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Expiration Date From = sysdate, Expiration Date To = sysdate+30, Submission Status = Submitted to Committee, Committee = Expedite/Exempt, Full Committee, Admin Review");
		linksList.add(link);
		
		// Monthly Reminders for CR (30-90 days) 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Monthly Reminders for CR (30-90 days)");
		addQueryStringParam("rangeLowerBoundKeyPrefix_expirationDate", getRelativeDateFromToday(30), urlBuilder);
		addQueryStringParam("expirationDate", getRelativeDateFromToday(90), urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Expiration Date From = sysdate+30, Expiration Date To = sysdate+90");
		linksList.add(link);
		
		// Expedite/Exempt/Admin Review Committee (Last 30 days) 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Expedite/Exempt/Admin Review Committee (Last 30 days)");
		addQueryStringParam("associateUser.principalName", getSessionUser(), urlBuilder);
		addQueryStringParam("rangeLowerBoundKeyPrefix_submissionDate", getRelativeDateFromToday(-30), urlBuilder);
		addQueryStringParam("committeeId", "Expedite/Exempt", urlBuilder);
		addQueryStringParam("committeeId", "Admin Review", urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Associate = User, Submission Date From = sysdate-30, Committee = Expedite/Exempt, Admin Review");
		linksList.add(link);
		
		// Full Committee (Last 30 Days) 
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_SEARCH);
		link.setAnchorText("Full Committee (Last 30 Days)");
		addQueryStringParam("associateUser.principalName", getSessionUser(), urlBuilder);
		addQueryStringParam("rangeLowerBoundKeyPrefix_submissionDate", getRelativeDateFromToday(-30), urlBuilder);
		addQueryStringParam("committeeId", "Full Committee", urlBuilder);
		link.setUrl(urlBuilder.toString());
		link.setLinkDescription("Associate = User, Submission Date From = sysdate-30, Committee = Full Committee");
		linksList.add(link);
		
		// Blank Search
		link = new ProtocolAssociateWorkflowLinkInfo();
		urlBuilder = new StringBuilder(baseUrl + METHOD_TO_CALL_START);
		link.setAnchorText("Blank Search");
		link.setUrl(urlBuilder.toString());
		linksList.add(link);
		
		return linksList;
	}

	/**
	 * Calculates a relative date from today.
	 * 
	 * @param days	Number of days to increment by. Can be negative.
	 * @return		The relative date, calculated as today + days.
	 */
	private String getRelativeDateFromToday(int days) {
		Calendar c = getDateTimeService().getCurrentCalendar();
		c.add(Calendar.DATE, days);
		return getDateTimeService().toDateString(c.getTime());
	}
	
	/**
	 * Helper method that adds a query string parameter and its value to a 
	 * string using the provided StringBuilder.
	 * 
	 * @param field			Name of the query string parameter.
	 * @param value			Value of the query string parameter.
	 * @param urlBuilder	The StringBuilder to append the parameter 
	 * 						and its value to.
	 */
	private void addQueryStringParam(String field, String value, StringBuilder urlBuilder) {
		urlBuilder.append("&");
		urlBuilder.append(field);
		urlBuilder.append("=");
		urlBuilder.append(value);
	}

	private String getSessionUser() {
		return GlobalVariables.getUserSession().getPrincipalName();
	}
	
	private String getReturnLocationParam() {
		String appUrl = getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
		String returnLocation = "&returnLocation=" + appUrl + "/associateWorkflow.do";
		
		return returnLocation;
	}
	
	private ConfigurationService getKualiConfigurationService() {
		if(kualiConfigurationService == null) {
			kualiConfigurationService = CoreApiServiceLocator.getKualiConfigurationService();
		}
		return kualiConfigurationService;
	}

	private DateTimeService getDateTimeService() {
		if(dateTimeService == null) {
			dateTimeService = KraServiceLocator.getService(DateTimeService.class);
		}
		return dateTimeService;
	}
}
