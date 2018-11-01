package edu.arizona.kra.subaward.batch.service;

import java.sql.Date;

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
    int importGLData(Date beginDate, Date endDate);

}
