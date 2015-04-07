package org.kuali.kra.test.helpers;

import java.sql.Timestamp;

import org.kuali.kra.budget.rates.ValidCeRateType;
import org.kuali.kra.test.fixtures.ValidCeRateTypeFixture;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class ValidCeRateTypeTestHelper extends TestHelper {

	public ValidCeRateType createValidCeRateType( ValidCeRateTypeFixture fixture ) {
		ValidCeRateType validCeRateType = buildValidCeRateType( fixture );
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		businessObjectService.save( validCeRateType );
		return validCeRateType;
	}

	private ValidCeRateType buildValidCeRateType( ValidCeRateTypeFixture fixture ) {
		ValidCeRateType validCeRateType = new ValidCeRateType();
		validCeRateType.setCostElement( fixture.getCostElement() );
		validCeRateType.setRateClassCode( fixture.getRateClassCode() );
		validCeRateType.setRateTypeCode( fixture.getRateTypeCode() );
		validCeRateType.setActive( true );
		validCeRateType.setUpdateTimestamp( new Timestamp( 0 ) );
		validCeRateType.setUpdateUser( GlobalVariables.getUserSession().getPrincipalId() );
		validCeRateType.setVersionNumber( 1L );
		validCeRateType.setObjectId( "ValidCeRateTypeTestHelper" );
		return validCeRateType;
	}

}
