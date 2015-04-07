package org.kuali.kra.test.helpers;

import java.sql.Timestamp;

import org.kuali.kra.proposaldevelopment.bo.MailType;
import org.kuali.kra.test.fixtures.MailTypeFixture;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class MailTypeTestHelper extends TestHelper {

	public MailType createMailType( MailTypeFixture fixture ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		MailType mailType = buildMailType( fixture );
		businessObjectService.save( mailType );
		return mailType;
	}

	private MailType buildMailType( MailTypeFixture fixture ) {
		MailType mailType = new MailType();
		mailType.setMailType( fixture.getMailType() );
		mailType.setDescription( fixture.getDescription() );
		mailType.setUpdateTimestamp( new Timestamp( 0 ) );
		mailType.setUpdateUser( GlobalVariables.getUserSession().getPrincipalId() );
		mailType.setVersionNumber( 1L );
		mailType.setObjectId( "MailTypeTestHelper" );
		return mailType;
	}

}
