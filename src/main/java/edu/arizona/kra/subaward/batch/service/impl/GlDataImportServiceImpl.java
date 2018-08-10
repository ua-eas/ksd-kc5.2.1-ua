package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.dao.BiGLFeedDao;
import edu.arizona.kra.subaward.batch.dao.SubawardGLFeedDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Date;

/**
 * Created by nataliac on 8/8/18.
 */
public class GlDataImportServiceImpl  {
    private static final Log LOG = LogFactory.getLog(GlDataImportServiceImpl.class);

    private BiGLFeedDao biGLFeedDao;
    private SubawardGLFeedDao subawardGLFeedDao;

    public int importGLData(Date beginDate, Date endDate) {
        LOG.info("GlDataImportService: Import GL data from: "+beginDate.toString()+ " to: "+endDate.toString());
        int importedLinesCount = 0;



        LOG.info("GlDataImportService: Number of imported lines:"+importedLinesCount);
        return  importedLinesCount;

    }



    public void setBiGLFeedDao(BiGLFeedDao biGLFeedDao) {
        this.biGLFeedDao = biGLFeedDao;
    }

    public void setSubawardGLFeedDao(SubawardGLFeedDao subawardGLFeedDao) {
        this.subawardGLFeedDao = subawardGLFeedDao;
    }
}
