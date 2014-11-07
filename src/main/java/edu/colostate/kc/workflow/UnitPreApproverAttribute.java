package edu.colostate.kc.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.service.UnitService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.xml.XmlHelper;
import org.kuali.rice.kew.api.identity.Id;
import org.kuali.rice.kew.api.identity.PrincipalId;
import org.kuali.rice.kew.api.rule.RoleName;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.GenericRoleAttribute;
import org.kuali.rice.kew.rule.QualifiedRoleName;
import org.kuali.rice.kew.rule.ResolvedQualifiedRole;

import edu.colostate.kc.infrastructure.CSUKeyConstants;

public class UnitPreApproverAttribute extends GenericRoleAttribute {
	private static final long serialVersionUID = 8284679400892921738L;
	private static final String OWNED_BY_UNIT = "ownedByUnitNumber";
	private static final String ROLE_NAME = "Unit Administrator";
    private static final List<RoleName> UNIT_ADMIN_ROLE_LIST;
    static{
    	UNIT_ADMIN_ROLE_LIST = new LinkedList<RoleName>();
    	RoleName.Builder roleNameBuilder = RoleName.Builder.create(UnitApproverRoleAttribute.class.getName(), ROLE_NAME, ROLE_NAME);
    	UNIT_ADMIN_ROLE_LIST.add(roleNameBuilder.build());
    }
	
	
	private ConfigurationService kualiConfigurationService;
	private List<String> preApproverCodeList;

	public List<String> getQualifiedRoleNames(String roleName,
			DocumentContent documentContent) {
		List<String> qualifiedRoleNames = new ArrayList<String>();
		qualifiedRoleNames.add(ROLE_NAME);

		return qualifiedRoleNames;
	}

	public List<RoleName> getRoleNames() {
		return UNIT_ADMIN_ROLE_LIST;
	}

	@Override
	public Map<String, String> getProperties() {
		// intentionally unimplemented...not intending on using this attribute
		// client-side
		return null;
	}

	private UnitService getUnitService() {
		return KraServiceLocator.getService(UnitService.class);
	}
	
    @Override
    protected ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, QualifiedRoleName qualifiedRoleName) {
        List<Id> recipients = resolveRecipients(routeContext, qualifiedRoleName);
        ResolvedQualifiedRole rqr = new ResolvedQualifiedRole(getLabelForQualifiedRoleName(qualifiedRoleName),
                                                              recipients
                                                              ); // default to no annotation...
        String unitNumber = retrieveDocumentUnitNumber(routeContext);
          if (StringUtils.isNotBlank(unitNumber)) {
        	  rqr.setAnnotation(unitNumber + " Lead Unit Pre-Approval");
          }
        return rqr;
    }

	@Override
	protected List<Id> resolveRecipients(RouteContext routeContext,
			QualifiedRoleName qualifiedRoleName) {
		
		String unitNumber = retrieveDocumentUnitNumber(routeContext);

		List<Id> members = new ArrayList<Id>();
		if (StringUtils.isNotBlank(unitNumber)) {
			List<UnitAdministrator> unitAdministrators = getUnitService().retrieveUnitAdministratorsByUnitNumber(unitNumber);
			for (UnitAdministrator unitAdministrator : unitAdministrators) {
				if (StringUtils.isNotBlank(unitAdministrator.getPersonId()) && 
					unitAdministrator.getUnitAdministratorType() != null 	&&
					getUnitPreApproverCodes().contains(unitAdministrator.getUnitAdministratorType().getUnitAdministratorTypeCode())) {

					members.add(new PrincipalId(unitAdministrator.getPersonId()));
				}
			}
		}

		return members;
	}

	protected List<String> getUnitPreApproverCodes() {
		if (preApproverCodeList == null) {
			// TODO will change below to parameter instead of property accessed
			// via keyConsts.
			String listString = getKualiConfigurationService().getPropertyValueAsString(CSUKeyConstants.UNIT_PREAPPROVER_TYPE_CODE);
			String[] arrayString = StringUtils.split(listString, ',');
			preApproverCodeList = Arrays.asList(arrayString);
		}
		return preApproverCodeList;
	}

	protected ConfigurationService getKualiConfigurationService() {
		if (kualiConfigurationService == null) {
			kualiConfigurationService = KraServiceLocator.getService(ConfigurationService.class);
		}
		return this.kualiConfigurationService;
	}

    
    private String retrieveDocumentUnitNumber(RouteContext context) {
        Document document = XmlHelper.buildJDocument(context.getDocumentContent().getDocument());
        String ownedByUnitNumber = null;
        Collection<Element> documentElements = XmlHelper.findElements(document.getRootElement(), DevelopmentProposal.class.getName());
        if (documentElements != null) {
        	Iterator<Element> iter = documentElements.iterator();
        	if(iter.hasNext()){
        		Element element = iter.next();
        		if(element != null){
        			ownedByUnitNumber = element.getChildText(OWNED_BY_UNIT);
        		}
        	}
        }
        return ownedByUnitNumber;
    }
}
