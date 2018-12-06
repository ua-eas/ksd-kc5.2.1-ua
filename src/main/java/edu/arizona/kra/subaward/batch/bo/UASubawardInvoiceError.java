package edu.arizona.kra.subaward.batch.bo;

import edu.arizona.kra.subaward.batch.InvoiceFeedConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

import java.sql.Date;

/**
 * Created by nataliac on 11/26/18.
 */
public class UASubawardInvoiceError extends KraPersistableBusinessObjectBase {
    static final long serialVersionUID = -24983129882357439L;

    private Long id;
    private Long executionId;
    private String errorMessage;

    private Long glEntryId;
    private String glEntryData;

    private String invoiceId;
    private Long subawardId;
    private Integer subAwardAmtReleasedId;
    private String invoiceDocumentNumber;

    private Date errorDate;
    private Date noticeSentDate;

    public Long getId() {
        return id;
    }

    public UASubawardInvoiceError setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public UASubawardInvoiceError setExecutionId(Long executionId) {
        this.executionId = executionId;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {

        if (errorMessage.length()> InvoiceFeedConstants.MAX_ERROR_MSG_LENGTH){
            this.errorMessage = errorMessage.substring(0, InvoiceFeedConstants.MAX_ERROR_MSG_LENGTH-1);
        } else
            this.errorMessage = errorMessage;
    }

    public Long getGlEntryId() {
        return glEntryId;
    }

    public UASubawardInvoiceError setGlEntryId(Long glEntryId) {
        this.glEntryId = glEntryId;
        return this;
    }

    public String getGlEntryData() {
        return glEntryData;
    }

    public UASubawardInvoiceError setGlEntryData(String glEntryData) {
        this.glEntryData = glEntryData;
        return this;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public UASubawardInvoiceError setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    public Long getSubawardId() {
        return subawardId;
    }

    public UASubawardInvoiceError setSubawardId(Long subawardId) {
        this.subawardId = subawardId;
        return this;
    }

    public Integer getSubAwardAmtReleasedId() {
        return subAwardAmtReleasedId;
    }

    public UASubawardInvoiceError setSubAwardAmtReleasedId(Integer subAwardAmtReleasedId) {
        this.subAwardAmtReleasedId = subAwardAmtReleasedId;
        return this;
    }

    public String getInvoiceDocumentNumber() {
        return invoiceDocumentNumber;
    }

    public UASubawardInvoiceError setInvoiceDocumentNumber(String invoiceDocumentNumber) {
        this.invoiceDocumentNumber = invoiceDocumentNumber;
        return this;
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public UASubawardInvoiceError setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
        return this;
    }

    public Date getNoticeSentDate() {
        return noticeSentDate;
    }

    public UASubawardInvoiceError setNoticeSentDate(Date noticeSentDate) {
        this.noticeSentDate = noticeSentDate;
        return this;
    }

    public String getEmailStringErrorDesc(){
        StringBuffer sb = new StringBuffer();
        sb.append("Error: "); sb.append(errorMessage);
        if (glEntryId!=null){
            sb.append(" GL Entry Id: "); sb.append(glEntryId);
        }
        if (StringUtils.isNotEmpty(glEntryData)){
            sb.append(" GL Entry : "); sb.append(glEntryData);
        }
        if (StringUtils.isNotEmpty(invoiceId)){
            sb.append(" Invoice Id: "); sb.append(invoiceId);
        }
        if (subawardId != null){
            sb.append(" Subaward Id: "); sb.append(subawardId);
        }
        if (subAwardAmtReleasedId != null){
            sb.append(" Subaward Amount Released Id: "); sb.append(subAwardAmtReleasedId);
        }
        if (StringUtils.isNotEmpty(invoiceDocumentNumber)){
            sb.append(" Invoice Document Number: "); sb.append(invoiceDocumentNumber);
        }
        sb.append("\n");
        return sb.toString();
    }
}
