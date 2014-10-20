package edu.arizona.kra.irb.lookup.valuefinder;

import java.util.Calendar;
import java.util.Date;

import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class CustomCurrentDateFinder implements ValueFinder {
	@Override
	public String getValue() {
		Calendar tempCal= Calendar.getInstance();
		Date currentDate = tempCal.getTime();
		String dateString = CoreApiServiceLocator.getDateTimeService().toDateString(currentDate);
		return dateString;
	}
}