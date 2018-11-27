package edu.arizona.kra.subaward.batch.bo;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.util.DateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a 'simplified' GL Entry for the purpose of Subaward Invoice Feed
 *
 * Created by nataliac on 8/6/18.
 */
public class UAGlEntry extends KraPersistableBusinessObjectBase {
    static final long serialVersionUID = -24983129882357448L;

    private Long entryId;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String universityFiscalPeriodCode;
    private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private String transactionLedgerEntryDescription;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionDebitCreditCode;
    private Date transactionDate;
    private String organizationDocumentNumber;
    private String projectCode;
    private String organizationReferenceId;
    private String referenceFinancialDocumentTypeCode;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private Date financialDocumentReversalDate;
    private String transactionEncumbranceUpdateCode;
    private Date transactionPostingDate;
    private Timestamp transactionDateTimeStamp;
    private Date importedDate;


//    private String gecDocumentNumber; //TODO Is this needed? should correspond to GEC_FDOC_NBR but we don't have that column from BI

    public UAGlEntry() {
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    public String getReferenceFinancialDocumentTypeCode() {
        return referenceFinancialDocumentTypeCode;
    }

    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
    }

    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    public Date getImportedDate() {
        return importedDate;
    }

    public UAGlEntry setImportedDate(Date importedDate) {
        this.importedDate = importedDate;
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("EntryId: "); sb.append(entryId);
        sb.append(" UniversityFiscalYear: "); sb.append(universityFiscalYear);
        sb.append(" ChartOfAccountsCode: "); sb.append(chartOfAccountsCode);
        sb.append(" AccountNumber: "); sb.append(chartOfAccountsCode);
        sb.append(" SubAccountNumber: "); sb.append(subAccountNumber);
        sb.append(" FinancialObjectCode: "); sb.append(financialObjectCode);
        sb.append(" FinancialSubObjectCode: "); sb.append(financialSubObjectCode);
        sb.append(" FinancialBalanceTypeCode: "); sb.append(financialBalanceTypeCode);
        sb.append(" FinancialObjectTypeCode: "); sb.append(financialObjectTypeCode);
        sb.append(" UniversityFiscalPeriodCode: "); sb.append(universityFiscalPeriodCode);
        sb.append(" FinancialDocumentTypeCode: "); sb.append(financialDocumentTypeCode);
        sb.append(" FinancialSystemOriginationCode: "); sb.append(financialSystemOriginationCode);
        sb.append(" DocumentNumber: "); sb.append(documentNumber);
        sb.append(" TransactionLedgerEntrySequenceNumber: "); sb.append(transactionLedgerEntrySequenceNumber);
        sb.append(" TransactionLedgerEntryDescription: "); sb.append(transactionLedgerEntryDescription);
        sb.append(" TransactionLedgerEntryAmount: "); sb.append(transactionLedgerEntryAmount.bigDecimalValue().toPlainString());
        sb.append(" TransactionDebitCreditCode: "); sb.append(transactionDebitCreditCode);
        sb.append(" TransactionDate: "); sb.append(transactionDate);
        sb.append(" OrganizationDocumentNumber: "); sb.append(organizationDocumentNumber);
        sb.append(" ProjectCode: "); sb.append(projectCode);
        sb.append(" OrganizationReferenceId: "); sb.append(organizationReferenceId);
        sb.append(" ReferenceFinancialDocumentTypeCode: "); sb.append(referenceFinancialDocumentTypeCode);
        sb.append(" ReferenceFinancialSystemOriginationCode: "); sb.append(referenceFinancialSystemOriginationCode);
        sb.append(" ReferenceFinancialDocumentNumber: "); sb.append(referenceFinancialDocumentNumber);
        sb.append(" FinancialDocumentReversalDate: "); sb.append(financialDocumentReversalDate);
        sb.append(" TransactionEncumbranceUpdateCode: "); sb.append(transactionEncumbranceUpdateCode);
        sb.append(" TransactionPostingDate: "); sb.append(transactionPostingDate);
        sb.append(" TransactionDateTimeStamp: "); sb.append(transactionDateTimeStamp);
        sb.append(" ImportedDate: "); sb.append(importedDate);

        return sb.toString();
    }




}
