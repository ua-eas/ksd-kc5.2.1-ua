/*
 * Copyright 2005-2015 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.proposaldevelopment.dao.ojb;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.util.OjbCollectionAware;

import edu.arizona.kra.proposaldevelopment.bo.SPSRestrictedNote;
import edu.arizona.kra.proposaldevelopment.dao.SPSRestrictedNoteDao;

import static edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants.*;


/**
 * SPS Restricted Notes DAO Ojb implementation.
 * @author nataliac
 */
public class SPSRestrictedNoteDaoOjb extends PlatformAwareDaoBaseOjb implements OjbCollectionAware, SPSRestrictedNoteDao {
    private static final Log LOG = LogFactory.getLog(SPSRestrictedNoteDaoOjb.class);
    
    private BusinessObjectService businessObjectService;

    @SuppressWarnings("unchecked")
    @Override
    public List<SPSRestrictedNote> getSPSRestrictedNotes(String proposalNumber) throws SQLException, LookupException {
        LOG.debug("getSPSRestrictedNotes: prop="+proposalNumber);
        
        List<SPSRestrictedNote> result = new ArrayList<SPSRestrictedNote>();
        if ( StringUtils.isNotEmpty( proposalNumber ) ){
            Criteria criteria = new Criteria();
            criteria.addEqualTo(PROP_NUMBER, proposalNumber);
            criteria.addEqualTo(ACTIVE, Boolean.TRUE);
            QueryByCriteria query = new QueryByCriteria(SPSRestrictedNote.class,criteria);
            query.addOrderBy(CREATED_DATE, true);
            result = (List<SPSRestrictedNote>)getPersistenceBrokerTemplate().getCollectionByQuery(query);
        }
        return result;
    }

    @Override
    public SPSRestrictedNote addSPSRestrictedNote(SPSRestrictedNote spsRestrictedNote) throws SQLException, LookupException {
        return getBusinessObjectService().save(spsRestrictedNote);
    }

    @Override
    public boolean deleteSPSRestrictedNote(SPSRestrictedNote spsRestrictedNote) throws SQLException, LookupException {
        spsRestrictedNote.setActive(false);
        getBusinessObjectService().save(spsRestrictedNote);
        return true;
    }
    
    
    protected BusinessObjectService getBusinessObjectService(){
        if ( businessObjectService == null ){
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

}
