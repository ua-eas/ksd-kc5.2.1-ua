/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.irb.service.impl;

import edu.arizona.kra.irb.service.ProtocolSecurityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolLookupableHelperServiceImpl;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.personnel.ProtocolPersonBase;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.*;

import static edu.arizona.kra.irb.ProtocolConstants.*;

/**
 * This class handles searching for protocols.
 */
public class ProtocolCustomLookupableHelperServiceImpl<GenericProtocol extends ProtocolBase> extends ProtocolLookupableHelperServiceImpl {

    private static final long serialVersionUID = 6180836146164439176L;

    private static final Log LOG = LogFactory.getLog(ProtocolCustomLookupableHelperServiceImpl.class);



    protected ProtocolSecurityService protocolSecurityService;


    //Mask title and description to unauthorized users
    public static final String[] COLUMNS_TO_HIDE = new String[] { PROTOCOL_TITLE_COL_NAME, PROTOCOL_DESCR_COL_NAME };



    protected boolean userHasPrivilegedProtocolRoles = false;
    protected Set<String> visibleProtocolNumbers =  new HashSet<String>();
    protected Set<String> visibleLeadUnitNumbers =  new HashSet<String>();


    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        initializeUserPerms(lookupForm);
        Collection lookupResults = super.performLookup(lookupForm, resultTable, bounded);

        if ( !userHasPrivilegedProtocolRoles ) {
            for (Object result : resultTable) {
                ResultRow row = (ResultRow) result;
                for (Column col : row.getColumns()) {
                    if (PROTOCOL_NUMBER_COL_NAME.equals(col.getPropertyName())) {
                        String currentRowProtocolNumber = col.getPropertyValue().substring(0, 10);
                        if (isProtocolNumberVisible(currentRowProtocolNumber)) {
                            LOG.debug("Visible=true: currentRowProtocolNumber=" + currentRowProtocolNumber);
                            break;
                        }
                    } else {
                        for (String columnToHide : COLUMNS_TO_HIDE) {
                            if (col.getPropertyName().equals(columnToHide)) {
                                col.setPropertyValue(PROTOCOL_MASK_STRING);
                            }
                        }
                    }
                }
            }
        }
        return lookupResults;
    }


    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        validateSearchParameters(fieldValues);

        // need to set backlocation & docformkey here. Otherwise, they are empty
        super.setBackLocationDocFormKey(fieldValues);

        List<? extends BusinessObject> returnedProtocols = getSearchResultsFilteredByTask(fieldValues);

        if(CollectionUtils.isNotEmpty(visibleProtocolNumbers) || CollectionUtils.isNotEmpty(visibleLeadUnitNumbers)) {
            Set<String> returnedProtocolNumbers = new HashSet<String>();
            for(BusinessObject result : returnedProtocols) {
                returnedProtocolNumbers.add(((Protocol) result).getProtocolNumber().substring(0, 10));
                //if user has unit qualified view permissions, add the protocols with the corresponding lead units to visible
                if ( CollectionUtils.isNotEmpty(visibleLeadUnitNumbers) ){
                    Protocol protocol = (Protocol) result;
                    if (visibleLeadUnitNumbers.contains(protocol.getLeadUnitNumber())){
                        visibleProtocolNumbers.add(protocol.getProtocolNumber());
                    }
                }
            }
            visibleProtocolNumbers = new HashSet<String>(CollectionUtils.intersection(returnedProtocolNumbers, visibleProtocolNumbers));
        }

        return returnedProtocols;
    }


    private void initializeUserPerms(LookupForm lookupForm) {
        String userId = GlobalVariables.getUserSession().getPrincipalId();

        boolean canView = protocolSecurityService.hasViewPermission(userId);

        // Check for any protocols the user is listed on as Personnel (all should have view access)
        Collection<String> personnelProtocolNumbers = protocolSecurityService.findProtocolNumbersForPersonnelRole(userId);
        canView |= personnelProtocolNumbers.size() > 0;

        if(canView) {
            lookupForm.setSuppressActions(false);

            // If the user doesn't have a privileged Protocol role, then we have to derive the Protocol's they have permission to view
            if(protocolSecurityService.userHasPrivilegedProtocolRoles(userId)) {
                userHasPrivilegedProtocolRoles = true;

            } else {

                // Add in Protocols where user is listed as Personnel
                addVisibleProtocolNumbers(personnelProtocolNumbers);

                // Add all protocol numbers if the user has any protocol-qualified role memberships
                addVisibleProtocolNumbers( protocolSecurityService.findProtocolNumbersForProtocolQualifiedRoles(userId) );

                // Find all the visible protocol lead unit numbers from the unit-number qualified role memberships and descending hierarchy that the user should be able to view
                visibleLeadUnitNumbers =  protocolSecurityService.findUnitsForUnitQualifiedRoles(userId);
            }
        }
        else {
            // Don't display actions column
            lookupForm.setSuppressActions(true);
        }
    }

    private boolean isProtocolNumberVisible(String protocolNumber) {
        if ( userHasPrivilegedProtocolRoles)
            return true;

        if ( CollectionUtils.isEmpty(visibleProtocolNumbers)){
            return false;
        }

        return visibleProtocolNumbers.contains(protocolNumber.substring(0, 10));
    }

    private void addVisibleProtocolNumbers(Collection<String> protocolNumbers) {
        for(String protocolNumber : protocolNumbers) {
            visibleProtocolNumbers.add(protocolNumber.substring(0, 10));
        }
    }



    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        HtmlData inqUrl = super.getInquiryUrl(bo, propertyName);
        if(propertyName.equals(PROTOCOL_NUMBER_COL_NAME)) {
            if( !isProtocolNumberVisible(((GenericProtocol) bo).getProtocolNumber())) {
                inqUrl = new HtmlData.AnchorHtmlData(null, PROTOCOL_NUMBER_COL_TITLE);
            }
        }
        return inqUrl;
    }

    protected List<HtmlData> getEditCopyViewLinks(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();

        if(!isProtocolNumberVisible(((ProtocolBase) businessObject).getProtocolNumber())) {
                return htmlDataList;
        }

        // Change "edit" to edit same document, NOT initializing a new Doc
        HtmlData.AnchorHtmlData editHtmlData = getViewLink(((ProtocolBase) businessObject).getProtocolDocument());
        String href = editHtmlData.getHref();
        href = href.replace("viewDocument=true", "viewDocument=false");
        editHtmlData.setHref(href);
        editHtmlData.setDisplayText("edit");
        htmlDataList.add(editHtmlData);
        // DocCopyHandler doesn't actually load the document properly... which is fine for copying, but bad if you want to do anything else
        HtmlData.AnchorHtmlData htmlData = getViewLink(((ProtocolBase) businessObject).getProtocolDocument());
        htmlData.setHref(href.replace("protocolProtocol.do?", "protocolProtocolActions.do?"));
        htmlData.setDisplayText("copy");
        htmlDataList.add(htmlData);

        //add the protocol id to request params
        htmlDataList.add(getViewLink(((ProtocolBase) businessObject).getProtocolDocument()));

        return htmlDataList;
    }

    /**
     * Separates the list of protocols into pageable results.
     * @param protocols the list of protocols
     * @return a collection of protocol pageable results.
     */
    private CollectionIncomplete<GenericProtocol> getPagedResults(List<GenericProtocol> protocols) {
        Long matchingResultsCount = new Long(protocols.size());
        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(getProtocolClassHook());
        if ((matchingResultsCount == null) || (matchingResultsCount.intValue() <= searchResultsLimit.intValue())) {
            return new CollectionIncomplete<GenericProtocol>(protocols, new Long(0));
        } else {
            return new CollectionIncomplete<GenericProtocol>(protocols.subList(0, searchResultsLimit), matchingResultsCount);
        }
    }



    /**
     * Filters out extra lookup parameters from the fieldValues.
     * @param fieldValues the field values that form a normal protocol search
     * @return the field values with extra lookup parameters removed
     */
    private Map<String, String> filterFieldValues(Map<String, String> fieldValues) {
        return removeExtraFilterParameters(fieldValues);
    }

    protected boolean sequenceContainsUser(Protocol protocol, String principalKey) {
        for (ProtocolPersonBase protocolPerson : protocol.getProtocolPersons()) {
            if (protocolPerson.getPersonId() != null && protocolPerson.getPersonId().equals(principalKey)) {
                return true;
            }
        }
        return false;
    }


    public void setProtocolSecurityService(ProtocolSecurityService protocolSecurityService) {
        this.protocolSecurityService = protocolSecurityService;
    }

    public ProtocolSecurityService getProtocolSecurityService() {
        return protocolSecurityService;
    }


}
