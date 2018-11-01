package edu.arizona.kra.subaward.batch;

import java.sql.Date;
import java.util.Calendar;


/**
 * Class holding utility methods for the Subaward Invoice Feed Batch jobs
 * Created by nataliac on 8/14/18.
 */
public final class InvoiceFeedUtils {

    public static final java.sql.Date substractDaysFromDate(java.sql.Date date, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -days);
        Date result = new Date(c.getTimeInMillis());
        return result;
    }

}
