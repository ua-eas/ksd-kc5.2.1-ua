package org.kuali.kra.test.helpers;

import java.sql.Timestamp;

import org.kuali.kra.irb.protocol.participant.ParticipantType;
import org.kuali.kra.test.fixtures.ParticipantTypeFixture;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class ParticipantTypeValueFinderTestHelper extends TestHelper {
	public void createParticipantType( ParticipantTypeFixture participantTypeFixture ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		ParticipantType participantType = buildParticipantType( participantTypeFixture );
		businessObjectService.save( participantType );
	}

	private ParticipantType buildParticipantType( ParticipantTypeFixture partType ) {
		ParticipantType participantType = new ParticipantType();
		participantType.setParticipantTypeCode( partType.getKey() );
		participantType.setDescription( partType.getValue() );
		participantType.setUpdateTimestamp( new Timestamp( 0 ) );
		participantType.setUpdateUser( GlobalVariables.getUserSession().getPrincipalName() );
		participantType.setVersionNumber( (long) 1 );
		participantType.setObjectId( "ParticipantTypeTestHelper" );
		return participantType;
	}

}
