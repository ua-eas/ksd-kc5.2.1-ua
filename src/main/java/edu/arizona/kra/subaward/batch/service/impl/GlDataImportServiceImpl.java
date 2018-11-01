package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.InvoiceFeedUtils;
import edu.arizona.kra.subaward.batch.bo.BiGlEntry;
import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.dao.BiGLFeedDao;
import edu.arizona.kra.subaward.batch.dao.SubawardGlFeedDao;
import edu.arizona.kra.subaward.batch.service.GlDataImportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 * Created by nataliac on 8/8/18.
 */
public class GlDataImportServiceImpl implements GlDataImportService {
    private static final Log LOG = LogFactory.getLog(GlDataImportServiceImpl.class);

    private BiGLFeedDao biGLFeedDao;
    private SubawardGlFeedDao subawardGLFeedDao;

    private BusinessObjectService businessObjectService;
    private SubawardInvoiceErrorReportService subawardInvoiceErrorReportService;



    @Transactional
    public int importGLData(Date beginDate, Date endDate) {
        LOG.info("GlDataImportService: Import GL data from: "+beginDate.toString()+ " to: "+endDate.toString());
        int importedLinesCount = 0;
        // cleanup GL temporary table in UAR first
        subawardGLFeedDao.emptyGLTempTable();
        try {

            List<BiGlEntry> biGlEntryList = biGLFeedDao.importGLData(beginDate, endDate);
            if (biGlEntryList.isEmpty() ){
                LOG.info("GLDataImportService: importGLData: 0 rows were returned from BI!");
                //TODO handle this: write notice report! -> entry in error table??!?
                return 0;
            }
            List<UAGlEntry> importedGlEntries = InvoiceFeedUtils.translateBiGlEntries(biGlEntryList);
            List<?> result = getBusinessObjectService().save(importedGlEntries);
            importedLinesCount = result.size();
        } catch (Exception e){
            LOG.error(e);
            subawardInvoiceErrorReportService.recordError("GlDataImportServiceImpl: Exception at importing GL Entries from BI!", e);
            throw new RuntimeException(e);
        }

        return  importedLinesCount;

    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setBiGLFeedDao(BiGLFeedDao biGLFeedDao) {
        this.biGLFeedDao = biGLFeedDao;
    }

    public void setSubawardGLFeedDao(SubawardGlFeedDao subawardGLFeedDao) {
        this.subawardGLFeedDao = subawardGLFeedDao;
    }

    public void setSubawardInvoiceErrorReportService(SubawardInvoiceErrorReportService subawardInvoiceErrorReportService) {
        this.subawardInvoiceErrorReportService = subawardInvoiceErrorReportService;
    }
}
