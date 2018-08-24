/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.arizona.kra.sys.batch.service.impl;


import edu.arizona.kra.sys.batch.BatchConstants;
import org.kuali.rice.krad.service.impl.ModuleServiceBase;
import edu.arizona.kra.sys.batch.service.BatchModuleService;

/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 * This class is the KC implementation of a module service. It also implements the batch related methods
 */
public class KcModuleServiceImpl extends ModuleServiceBase implements BatchModuleService {


    public boolean isExternalJob(String jobName) {
        return false;
    }


    public String getExternalJobStatus(String jobName) {
        if (isExternalJob(jobName))
            return BatchConstants.SUCCEEDED_JOB_STATUS_CODE;
        return null;
    }

}
