package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.InvoiceFeedUtils;
import edu.arizona.kra.subaward.batch.bo.BiGlEntry;
import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.dao.BiGLFeedDao;
import edu.arizona.kra.subaward.batch.dao.SubawardGlFeedDao;
import edu.arizona.kra.subaward.batch.service.GlDataImportService;
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



    @Transactional
    public int importGLData(Date beginDate, Date endDate) {
        LOG.info("GlDataImportService: Import GL data from: "+beginDate.toString()+ " to: "+endDate.toString());
        int importedLinesCount = 0;
        // cleanup GL temporary table in UAR first
        subawardGLFeedDao.emptyGLTempTable();
        try {

            List<BiGlEntry> biGlEntryList = biGLFeedDao.importGLData(beginDate, endDate);
            if (biGlEntryList.isEmpty() ){
                //TODO handle this: write notice report!
                return 0;
            }
            List<UAGlEntry> importedGlEntries = InvoiceFeedUtils.translateBiGlEntries(biGlEntryList);
            getBusinessObjectService().save(importedGlEntries);
        } catch (Exception e){
            LOG.error(e);
            //TODO How to handle the exceptions!???!!! -> create a error for the error reporting step + abort rest of jobs -> in the higher layer
        }

        LOG.info("GlDataImportService: Number of imported lines:"+importedLinesCount);
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
}
