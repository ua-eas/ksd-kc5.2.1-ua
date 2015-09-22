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
package org.kuali.kra.subaward.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.bo.versioning.VersionStatus;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.protocol.ProtocolDaoOjbBase;
import org.kuali.kra.service.VersionException;
import org.kuali.kra.service.VersionHistoryService;
import org.kuali.kra.service.VersioningService;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardAmountInfo;
import org.kuali.kra.subaward.bo.SubAwardAmountReleased;
import org.kuali.kra.subaward.bo.SubAwardFundingSource;
import org.kuali.kra.subaward.dao.SubAwardFundingSourceDao;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.kra.subaward.service.SubAwardService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.SequenceAccessorService;

/**
 * This class is service impl for subAward...
 */
@SuppressWarnings( "deprecation" )
public class SubAwardServiceImpl implements SubAwardService {


    private BusinessObjectService businessObjectService;
    private VersioningService versioningService;
    private VersionHistoryService versionHistoryService;
    private DocumentService documentService;
    private SequenceAccessorService sequenceAccessorService;
    private ParameterService parameterService;
    private SubAwardFundingSourceDao subAwardFundingSourceDao;

    private static final Log LOG = LogFactory.getLog(SubAwardService.class);
  

    /**.
     * This method is for creating new subAward version
     * @param subAwardDocument
     * @return newSubAwardDocument the newSubAwardDocument
     */
    public SubAwardDocument createNewSubAwardVersion(
    SubAwardDocument subAwardDocument) throws
    VersionException, WorkflowException {

        SubAward newVersion = getVersioningService().
        createNewVersion(subAwardDocument.getSubAward());

        SubAwardDocument newSubAwardDocument =
        (SubAwardDocument) getDocumentService().
        getNewDocument(SubAwardDocument.class);
        newSubAwardDocument.getDocumentHeader().
        setDocumentDescription(subAwardDocument.
        getDocumentHeader().getDocumentDescription());
        newSubAwardDocument.setSubAward(newVersion);
        newVersion.setSubAwardDocument(newSubAwardDocument);
        return newSubAwardDocument;
    }

    /**.
     * This method is for  getVersioningService
     * @return versioningService...
     *
     */
    protected VersioningService getVersioningService() {
        return versioningService;
    }

    /**
     * This method is for setting versioningService...
     * @param versioningService the versioningService
     */
    public void setVersioningService(VersioningService versioningService) {
        this.versioningService = versioningService;
    }


    /**.
    * This is the Getter Method for documentService
	 * @return Returns the documentService.
	 */
	public DocumentService getDocumentService() {
		return documentService;
	}

	/**.
	 * This is the Setter Method for documentService
	 * @param documentService The documentService to set.
	 */
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	/**.
	 * This is the Getter Method for parameterService
	 * @return Returns the parameterService.
	 */
	public ParameterService getParameterService() {
		return parameterService;
	}

	/**.
	 * This is the Setter Method for parameterService
	 * @param parameterService The parameterService to set.
	 */
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	@Override
    public void updateSubAwardSequenceStatus(
    SubAward subAward, VersionStatus status) {
        if (status.equals(VersionStatus.ACTIVE)) {
            archiveCurrentActiveSubAward(subAward.getSubAwardId());
        }
        subAward.setSubAwardSequenceStatus(status.toString());
       getBusinessObjectService().save(subAward);
    }
    /**
     * This method is for archiveCurrentActiveSubAward...
     * @param subAwardId
     */
    protected void archiveCurrentActiveSubAward(Long subAwardId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("subAwardId", Long.toString(subAwardId));
        values.put("subAwardSequenceStatus", VersionStatus.ACTIVE.name());
        Collection<SubAward> subAwards = getBusinessObjectService().
        findMatching(SubAward.class, values);
        for (SubAward subAward : subAwards) {
            subAward.setSubAwardSequenceStatus(VersionStatus.ARCHIVED.name());
            getBusinessObjectService().save(subAward);
        }
    }


    /**.
	 * This is the Getter Method for businessObjectService
	 * @return Returns the businessObjectService.
	 */
	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	/**.
	 * This is the Setter Method for businessObjectService
	 * @param businessObjectService The businessObjectService to set.
	 */
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	/** {@inheritDoc} */
    public String getNextSubAwardCode() {
        Long nextAwardNumber = sequenceAccessorService.getNextAvailableSequenceNumber(Constants.SUBAWARD_SEQUENCE_SUBAWARD_CODE);
        return nextAwardNumber.toString();
    }
    /**
     * Set the Sequence Accessor Service.
     * @param sequenceAccessorService the Sequence Accessor Service
     */
    public void setSequenceAccessorService(
    SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    /**.
     * This method is using for getAmountInfo
     * @param subAward
     * @return subAward
     */
    public SubAward getAmountInfo(SubAward subAward) {

        List<SubAwardAmountInfo> subAwardAmountInfoList = subAward.getSubAwardAmountInfoList();
        List<SubAwardAmountReleased> subAwardAmountReleasedList = subAward.getSubAwardAmountReleasedList();
        KualiDecimal totalObligatedAmount = new KualiDecimal(0.00);
        KualiDecimal totalAnticipatedAmount = new KualiDecimal(0.00);
        KualiDecimal totalAmountReleased = new KualiDecimal(0.00);
        if (subAwardAmountInfoList != null && subAwardAmountInfoList.size() > 0) {
            for (SubAwardAmountInfo subAwardAmountInfo: subAwardAmountInfoList) {
                if (subAwardAmountInfo.getObligatedChange() != null) {
                    subAward.setTotalObligatedAmount(totalObligatedAmount.add(subAwardAmountInfo.getObligatedChange()));
                    totalObligatedAmount = subAward.getTotalObligatedAmount();
                }
                if (subAwardAmountInfo.getAnticipatedChange() != null) {
                    subAward.setTotalAnticipatedAmount(totalAnticipatedAmount.add(subAwardAmountInfo.getAnticipatedChange()));
                    totalAnticipatedAmount = subAward.getTotalAnticipatedAmount();
                }
                if (subAwardAmountInfo.getModificationEffectiveDate() != null) {
                    subAward.setModificationEffectiveDate(subAwardAmountInfo.getModificationEffectiveDate());
                }
                if (subAwardAmountInfo.getModificationID() != null) {
                    subAward.setModificationId(subAwardAmountInfo.getModificationID());
                }
                if (subAwardAmountInfo.getPeriodofPerformanceStartDate() != null) {
                    subAward.setPerformanceStartDate(subAwardAmountInfo.getPeriodofPerformanceStartDate());
                }
                if (subAwardAmountInfo.getPeriodofPerformanceEndDate() != null) {
                    subAward.setPerformanceEnddate(subAwardAmountInfo.getPeriodofPerformanceEndDate());
                }
            }
            for (SubAwardAmountReleased subAwardAmountReleased: subAwardAmountReleasedList) {

                if (subAwardAmountReleased.getAmountReleased() != null
                        && !(StringUtils.equals(subAwardAmountReleased.getInvoiceStatus(), DocumentStatus.DISAPPROVED.getCode())
                        || StringUtils.equals(subAwardAmountReleased.getInvoiceStatus(), DocumentStatus.CANCELED.getCode())
                        || StringUtils.equals(subAwardAmountReleased.getInvoiceStatus(), DocumentStatus.RECALLED.getCode()))) {
                    subAward.setTotalAmountReleased(totalAmountReleased.add(subAwardAmountReleased.getAmountReleased()));
                    totalAmountReleased = subAward.getTotalAmountReleased();
                }
            }
            SubAwardAmountInfo amountInfo = subAward.getSubAwardAmountInfoList().get(subAward.getSubAwardAmountInfoList().size()-1);
            amountInfo.setAnticipatedAmount(totalAnticipatedAmount);
            amountInfo.setObligatedAmount(totalObligatedAmount);
        }
        subAward.setTotalObligatedAmount(totalObligatedAmount);
        subAward.setTotalAnticipatedAmount(totalAnticipatedAmount);
        subAward.setTotalAmountReleased(totalAmountReleased);
        subAward.setTotalAvailableAmount(totalObligatedAmount.subtract(totalAmountReleased));

        return subAward;
    }
    /**.
     * this method is for getFollowupDateDefaultLength
     *
     * @return followupDateRange
     */
    public String getFollowupDateDefaultLength() {
        String namespaceCode = "KC-SUBAWARD";
        String componentCode = "Document";
        String parameterName = "Subaward Follow Up";
        String followupDateRange = this.getParameterService().
        getParameterValueAsString(namespaceCode, componentCode, parameterName);
        return followupDateRange;
    }
    /**.
     * this method is for getCalculatedFollowupDate
     * @param baseDate
     * @return retDate
     */
    public Date getCalculatedFollowupDate(Date baseDate) {
        Date retDate =
        new Date(DateUtils.addDays(baseDate, getFollowupDateDefaultLengthInDays()).getTime());
        return retDate;
    }
    
    @Override
    public String getCalculatedFollowupDateForAjaxCall(String baseDate) {
        final String empty = "";
        String[] elements = baseDate.split("/");
        if (elements.length == 3) {
            try {
                int month = Integer.parseInt(elements[0]);
                int day = Integer.parseInt(elements[1]);
                int year = Integer.parseInt(elements[2]);
                if (year < 100) {
                    year = year + 2000;
                }
                Date requestedDate = new Date(year, month-1, day-1);
                Date followUpDate = getCalculatedFollowupDate(requestedDate);
                return (followUpDate.getMonth()+1) + "/" + (followUpDate.getDate()+1) + "/" + followUpDate.getYear();
                
            } catch (Exception e) {
                e.printStackTrace();
                // something wasn't a number or a valid date element;
                LOG.error(e);
            }
        }
        return empty;
    }

    /**.
     * this method is for gettingFollowUpDates
     * @return returnAmount
     */
    public int getFollowupDateDefaultLengthInDays() {
        String followupDateRange = getFollowupDateDefaultLength();
        String rangeUnit = followupDateRange.substring(
        followupDateRange.length() - 1, followupDateRange.length());
        int rangeAmount =
        Integer.parseInt(followupDateRange.substring(
        0, followupDateRange.length() - 1));
        int returnAmount = 0;
        if (StringUtils.equalsIgnoreCase(rangeUnit, "D")) {
            returnAmount = rangeAmount;
        } else if (StringUtils.equalsIgnoreCase(rangeUnit, "W")) {
            returnAmount = rangeAmount * 7;
        } else {
            throw new IllegalArgumentException(
            "An invalid range unit was set in the "
            + "'Subaward Follow Up' parameter: " + rangeUnit);
        }
        return returnAmount;
    }
    
    public SubAward getActiveSubAward(Long subAwardId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("subAwardId", subAwardId);
        List<SubAward> subAwards = (List<SubAward>) getBusinessObjectService().findMatching(SubAward.class, values);
        SubAward subAward = subAwards.get(0);
        getAmountInfo(subAward);
        return subAward;
    }

    @Override
    public Collection<SubAward> getLinkedSubAwards(Award award){
        try {
            return getSubAwardFundingSourceDao().getLinkedSubAwards(award);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
        }
        return new ArrayList<SubAward>();
    }


    @Override
    public Collection<Award> getLinkedAwards(SubAward subAward) {
        try {
            return getSubAwardFundingSourceDao().getLinkedAwards(subAward);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
        }
        return new ArrayList<Award>();
    }
    
    @Override
    public void deleteSubAwardFundingSource(String awardNumber, String subAwardCode) {
        LOG.debug("Deleting SubAwardFundingSource: award="+awardNumber+" subaward="+subAwardCode);
        try {
            getSubAwardFundingSourceDao().deleteSubAwardFundingSource(awardNumber, subAwardCode);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
        }
    }

    @Override
    public void deleteSubAwardFundingSource(SubAwardFundingSource sfs) {
        LOG.debug("Deleting SubAwardFundingSource:"+sfs.getSubAwardFundingSourceId()+" award="+sfs.getAwardNumber()+" subaward="+sfs.getSubAwardCode());
        try {
            getSubAwardFundingSourceDao().deleteSubAwardFundingSource(sfs);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.kuali.kra.subaward.service.SubAwardService#getActiveSubAwardFundingSources(org.kuali.kra.award.home.Award)
     */
    @Override
    public Collection<SubAwardFundingSource> getActiveSubAwardFundingSources(Award award){
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("awardNumber", award.getAwardNumber());
        values.put("active", Boolean.TRUE );
        return businessObjectService.findMatching(SubAwardFundingSource.class, values); 
    }

    
    /* (non-Javadoc)
     * @see org.kuali.kra.subaward.service.SubAwardService#getActiveSubAwardFundingSources(org.kuali.kra.award.home.Award)
     */
    @Override
    public Collection<SubAwardFundingSource> getActiveSubAwardFundingSources(SubAward subAward){
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("subAwardCode", subAward.getSubAwardCode());
        values.put("active", Boolean.TRUE );
        return businessObjectService.findMatching(SubAwardFundingSource.class, values);
    }
    
    public VersionHistoryService getVersionHistoryService() {
        return versionHistoryService;
    }

    public void setVersionHistoryService(VersionHistoryService versionHistoryService) {
        this.versionHistoryService = versionHistoryService;
    }
    
    public SubAwardFundingSourceDao getSubAwardFundingSourceDao() {
        return subAwardFundingSourceDao;
    }

    public void setSubAwardFundingSourceDao(SubAwardFundingSourceDao subAwardFundingSourceDao) {
        this.subAwardFundingSourceDao = subAwardFundingSourceDao;
    }

}
