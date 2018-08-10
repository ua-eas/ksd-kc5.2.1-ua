package edu.arizona.kra.subaward.batch.service;

import java.sql.Date;

/**
 * Created by nataliac on 8/8/18.
 */
public interface GlDataImportService {
    int importGLData(Date beginDate, Date endDate);
}
