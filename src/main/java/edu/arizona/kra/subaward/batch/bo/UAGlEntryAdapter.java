package edu.arizona.kra.subaward.batch.bo;

/**
 * Created by nataliac on 8/15/18.
 */
public class UAGlEntryAdapter {

    public UAGlEntryAdapter() {
    }

    public UAGlEntry translate(BiGlEntry biGlEntry) {
        UAGlEntry result = new UAGlEntry();


        //result.setEntryId(biGlEntry.getEntryId());
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
        result.setReferenceFinancialDocumentNumber(biGlEntry.getReferenceFinancialDocumentNumber());
        result.setFinancialDocumentReversalDate(biGlEntry.getFinancialDocumentReversalDate());
        result.setFinancialDocumentReversalDate(biGlEntry.getFinancialDocumentReversalDate());
        result.setTransactionEncumbranceUpdateCode(biGlEntry.getTransactionEncumbranceUpdateCode());
        result.setTransactionPostingDate(biGlEntry.getTransactionPostingDate());
        result.setTransactionDateTimeStamp(biGlEntry.getTransactionDateTimeStamp());
        //result.setGecDocumentNumber(biGlEntry.getGecDocumentNumber());

        return result;

    }


}
