package edu.arizona.kra.subaward.batch.bo;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;

/**
 * Created by nataliac on 11/26/18.
 */
public class UASubawardInvoiceData extends KraPersistableBusinessObjectBase {
    static final long serialVersionUID = -24983129882357449L;

    private Long entryId;
    private String financialDocNumber;
    private String purchaseOrderNumber;
    private KualiDecimal amountReleased;
    private String comments;
    private Date financialDocumentReversalDate;
    private Date effectiveDate;
    private String financialObjectCode;
    private Long subAwardId;
    private Integer subAwardAmtReleasedId;
    private String invoiceDocumentNumber;
    private Date importedDate;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getEntryId() {
        return entryId;
    }

    public UASubawardInvoiceData setEntryId(Long entryId) {
        this.entryId = entryId;
        return this;
    }

    public String getFinancialDocNumber() {
        return financialDocNumber;
    }

    public UASubawardInvoiceData setFinancialDocNumber(String fDocNumber) {
        this.financialDocNumber = fDocNumber;
        return this;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public UASubawardInvoiceData setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
        return this;
    }

    public KualiDecimal getAmountReleased() {
        return amountReleased;
    }

    public UASubawardInvoiceData setAmountReleased(KualiDecimal amountReleased) {
        this.amountReleased = amountReleased;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public UASubawardInvoiceData setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    public UASubawardInvoiceData setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
        return this;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public UASubawardInvoiceData setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public UASubawardInvoiceData setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
        return this;
    }

    public Long getSubAwardId() {
        return subAwardId;
    }

    public UASubawardInvoiceData setSubAwardId(Long subAwardId) {
        this.subAwardId = subAwardId;
        return this;
    }

    public Integer getSubAwardAmtReleasedId() {
        return subAwardAmtReleasedId;
    }

    public UASubawardInvoiceData setSubAwardAmtReleasedId(Integer subAwardAmtReleasedId) {
        this.subAwardAmtReleasedId = subAwardAmtReleasedId;
        return this;
    }

    public String getInvoiceDocumentNumber() {
        return invoiceDocumentNumber;
    }

    public UASubawardInvoiceData setInvoiceDocumentNumber(String invoiceDocumentNumber) {
        this.invoiceDocumentNumber = invoiceDocumentNumber;
        return this;
    }

    public Date getImportedDate() {
        return importedDate;
    }

    public UASubawardInvoiceData setImportedDate(Date importedDate) {
        this.importedDate = importedDate;
        return this;
    }
}
