package edu.arizona.kra.subaward.batch.service;

import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceData;
import org.kuali.kra.subaward.bo.SubAward;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

/**
 * Created by nataliac on 8/8/18.
 */
public interface GlDataImportService {

    /**
     * Import the GL Data filtered specifically for Subawards from the destination - BI View of GL_ENTRY - into the SUBAWARD_GL_IMPORT table
     * that has the transaction posting date between beginDate and endDate.
     * If any errors occur, they will be reported through the SubawardInvoiceErrorReportService.
     *
     * @param beginDate -> begining of the transaction posting date for GL Entries that will be imported into Subaward
     * @param endDate ->  ending of the transaction posting date for GL Entries that will be imported into Subaward
     * @return int - the number of rows imported.
     */
    int importGLData(Long executionId, Date beginDate, Date endDate);


    /**
     * Returns all imported  UAGlEntry in the last run of the job
     *
     * @return  List<UAGlEntry>
     */
    Collection<UAGlEntry> allImportedGLEntries();

    /**
     * Returns all Subaward Invoice Data created from GL Entries in the current run of the job
     *
     * @return  List<UASubawardInvoiceData>
     */
    Collection<UASubawardInvoiceData> findNewInvoiceDataEntries(Long executionId);

    /**
     * Checks if an imported  GL Entry row has been already imported
     *
     * @param uaGlEntry -> GL Entry row to validate against the already imported rows
     *
     * @return boolean - true if the UAGlEntry has already been imported successfully previously
     */
    boolean isDuplicateRow(UAGlEntry uaGlEntry);

    /**
     * Finds the ACTIVE Subaward that has the corresponding PO number matching the one in the Invoice Data
     *
     * @param invoiceData -> GL Entry row to validate against the already imported rows
     */
    SubAward findMatchingActiveSubaward(UASubawardInvoiceData invoiceData);

    /**
     * Generates the Subaward Invoice Data from a UAGlEntry
     *
     * @param uaGlEntry -> GL Entry row to extract Subaward Invoice data from
     *
     * @return UASubawardInvoiceData - created invoice data.
     */
    UASubawardInvoiceData createInvoiceData(UAGlEntry uaGlEntry);

}
