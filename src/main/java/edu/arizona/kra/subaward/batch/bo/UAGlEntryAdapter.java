package edu.arizona.kra.subaward.batch.bo;

/**
 * Created by nataliac on 8/15/18.
 */
public class UAGlEntryAdapter {

    public UAGlEntryAdapter() {
    }

    public UAGlEntry translate(BiGlEntry biGlEntry) {
        UAGlEntry result = new UAGlEntry();


    result.setEntryId(biGlEntry.getEntryId());
    result.setUniversityFiscalYear(biGlEntry.getUniversityFiscalYear());
    result.setChartOfAccountsCode(biGlEntry.getChartOfAccountsCode());
    result.setAccountNumber(biGlEntry.getAccountNumber());
    result.setSubAccountNumber(biGlEntry.getSubAccountNumber());
    result.setFinancialObjectCode(biGlEntry.getFinancialObjectCode());
    result.setFinancialSubObjectCode(biGlEntry.getFinancialSubObjectCode());
    result.setFinancialBalanceTypeCode(biGlEntry.getFinancialBalanceTypeCode());
    result.setFinancialObjectTypeCode(biGlEntry.getFinancialObjectTypeCode());
    result.setUniversityFiscalPeriodCode(biGlEntry.getUniversityFiscalPeriodCode());
    result.setFinancialDocumentTypeCode(biGlEntry.getFinancialDocumentTypeCode());
    result.setFinancialSystemOriginationCode(biGlEntry.getFinancialSystemOriginationCode());
    result.setDocumentNumber(biGlEntry.getDocumentNumber());
    result.setTransactionLedgerEntrySequenceNumber(biGlEntry.getTransactionLedgerEntrySequenceNumber());
    result.setTransactionLedgerEntryDescription(biGlEntry.getTransactionLedgerEntryDescription());
    result.setTransactionLedgerEntryAmount(biGlEntry.getTransactionLedgerEntryAmount());
    result.setTransactionDebitCreditCode(biGlEntry.getTransactionDebitCreditCode());
    result.setTransactionDate(biGlEntry.getTransactionDate());
    result.setOrganizationDocumentNumber(biGlEntry.getOrganizationDocumentNumber());
    result.setProjectCode(biGlEntry.getProjectCode());
    result.setOrganizationReferenceId(biGlEntry.getOrganizationReferenceId());
    result.setReferenceFinancialDocumentTypeCode(biGlEntry.getReferenceFinancialDocumentTypeCode());
    result.setReferenceFinancialSystemOriginationCode(biGlEntry.getReferenceFinancialSystemOriginationCode());

    return result;

// TODO: Continue this:
//
//
//
//    public String getReferenceFinancialDocumentNumber() {
//        return referenceFinancialDocumentNumber;
//    }
//
//    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
//        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
//    }
//
//    public Date getFinancialDocumentReversalDate() {
//        return financialDocumentReversalDate;
//    }
//
//    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
//        this.financialDocumentReversalDate = financialDocumentReversalDate;
//    }
//
//    public String getTransactionEncumbranceUpdateCode() {
//        return transactionEncumbranceUpdateCode;
//    }
//
//    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
//        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
//    }
//
//    public Date getTransactionPostingDate() {
//        return transactionPostingDate;
//    }
//
//    public void setTransactionPostingDate(Date transactionPostingDate) {
//        this.transactionPostingDate = transactionPostingDate;
//    }
//
//    public Timestamp getTransactionDateTimeStamp() {
//        return transactionDateTimeStamp;
//    }
//
//    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
//        this.transactionDateTimeStamp = transactionDateTimeStamp;
//    }
//
//    public String getGecDocumentNumber() {
//        return gecDocumentNumber;
//    }
//
//    public void setGecDocumentNumber(String gecDocumentNumber) {
//        this.gecDocumentNumber = gecDocumentNumber;
//    }
    }


}
