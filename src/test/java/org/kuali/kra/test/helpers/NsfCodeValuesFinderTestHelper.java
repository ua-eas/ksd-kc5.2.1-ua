package org.kuali.kra.test.helpers;

import java.sql.Timestamp;

import org.kuali.kra.bo.NsfCode;
import org.kuali.kra.test.fixtures.NsfCodeFixture;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class NsfCodeValuesFinderTestHelper extends TestHelper {
	public void createNsfCode( NsfCodeFixture fixture ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		NsfCode nsfCode = buildNsfCode( fixture );
		businessObjectService.save( nsfCode );

	}

	private NsfCode buildNsfCode( NsfCodeFixture fixture ) {
		NsfCode nsfCode = new NsfCode();
		nsfCode.setNsfSequenceNumber( fixture.getKey() );
		nsfCode.setNsfCode( fixture.getCode() );
		nsfCode.setDescription( fixture.getValue() );
		nsfCode.setUpdateTimestamp( new Timestamp( 0 ) );
		nsfCode.setUpdateUser( GlobalVariables.getUserSession().getPrincipalName() );
		nsfCode.setVersionNumber( (long) 1 );
		nsfCode.setObjectId( "NsfCodeValuesFinderTestHelper" );
		return nsfCode;
	}

}
