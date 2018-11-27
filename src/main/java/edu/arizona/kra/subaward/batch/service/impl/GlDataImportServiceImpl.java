package edu.arizona.kra.subaward.batch.service.impl;


import edu.arizona.kra.subaward.batch.bo.BiGlEntry;
import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceData;
import edu.arizona.kra.subaward.batch.dao.BiGLFeedDao;
import edu.arizona.kra.subaward.batch.dao.SubawardGlFeedDao;
import edu.arizona.kra.subaward.batch.service.GlDataImportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;

import org.kuali.rice.krad.service.BusinessObjectService;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by nataliac on 8/8/18.
 */
public class GlDataImportServiceImpl implements GlDataImportService {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GlDataImportServiceImpl.class);

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
            //UAR-2691: forced cast to UAGlEntry list instead of adapter as per Code Review
            List<UAGlEntry> importedGlEntries =  (List<UAGlEntry>)(List<?>)biGlEntryList;
            List<?> result = getBusinessObjectService().save(importedGlEntries);
            importedLinesCount = result.size();
        } catch (Exception e){
            LOG.error("Exception", e);
            subawardInvoiceErrorReportService.recordError("GlDataImportServiceImpl: Exception at importing GL Entries from BI!", e);
            throw new RuntimeException(e);
        }

        return importedLinesCount;

    }

    @Transactional
    public UASubawardInvoiceData createInvoiceData(UAGlEntry uaGlEntry){
        UASubawardInvoiceData subawardInvoiceData = new UASubawardInvoiceData();

        subawardInvoiceData.setEntryId( uaGlEntry.getEntryId());
        subawardInvoiceData.setFinancialDocNumber( uaGlEntry.getDocumentNumber() );
        subawardInvoiceData.setPurchaseOrderNumber( uaGlEntry.getReferenceFinancialDocumentNumber());
        subawardInvoiceData.setAmountReleased( uaGlEntry.getTransactionLedgerEntryAmount());
        subawardInvoiceData.setFinancialDocumentReversalDate( uaGlEntry.getFinancialDocumentReversalDate());
        subawardInvoiceData.setEffectiveDate( uaGlEntry.getTransactionPostingDate());
        subawardInvoiceData.setComments( uaGlEntry.getTransactionLedgerEntryDescription());
        subawardInvoiceData.setFinancialObjectCode( uaGlEntry.getFinancialObjectCode());

        LOG.debug("GlDataImportService: createInvoiceData for GL Entry ID="+uaGlEntry.getEntryId());
        return subawardInvoiceData;

    }


    public Collection<UAGlEntry> allImportedGLEntries(){
        return getBusinessObjectService().findAll(UAGlEntry.class);
    }


    public boolean isDuplicateRow(UAGlEntry uaGlEntry){
        //returns true if there is at least one UASubaward Invoice Data with the same entry ID
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put("entryId", uaGlEntry.getEntryId());
        if ( getBusinessObjectService().countMatching(UASubawardInvoiceData.class, keyMap) > 0 ){
            return true;
        }
        return false;
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
