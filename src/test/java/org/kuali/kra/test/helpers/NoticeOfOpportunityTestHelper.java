package org.kuali.kra.test.helpers;


import java.sql.Timestamp;

import org.kuali.kra.bo.NoticeOfOpportunity;
import org.kuali.kra.test.fixtures.NoticeOfOpportunityFixture;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;


public class NoticeOfOpportunityTestHelper extends TestHelper {

	public void createNoticeOfOpportunity( NoticeOfOpportunityFixture notice ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		NoticeOfOpportunity noticeOfOpportunity = buildNoticeOfOpportunity( notice );
		businessObjectService.save( noticeOfOpportunity );
	}


	public NoticeOfOpportunity buildNoticeOfOpportunity( NoticeOfOpportunityFixture notice ) {
		NoticeOfOpportunity noticeOfOpportunity = new NoticeOfOpportunity();
		noticeOfOpportunity.setNoticeOfOpportunityCode( notice.getKey() );
		noticeOfOpportunity.setDescription( notice.getValue() );
		noticeOfOpportunity.setUpdateTimestamp( new Timestamp( 0 ) );
		noticeOfOpportunity.setUpdateUser( GlobalVariables.getUserSession().getPrincipalName() );
		noticeOfOpportunity.setVersionNumber( (long) 1 );
		noticeOfOpportunity.setObjectId( "NoticeOfOpportunityTestHelper" );
		return noticeOfOpportunity;
	}

}
