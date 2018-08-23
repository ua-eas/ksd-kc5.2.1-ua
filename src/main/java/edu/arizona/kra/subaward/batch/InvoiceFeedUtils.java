package edu.arizona.kra.subaward.batch;

import edu.arizona.kra.subaward.batch.bo.BiGlEntry;
import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.bo.UAGlEntryAdapter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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


    public static final List<UAGlEntry> translateBiGlEntries( List<BiGlEntry> biGlEntryList){
        List<UAGlEntry> result = new ArrayList<>(biGlEntryList.size());
        UAGlEntryAdapter adapter = new UAGlEntryAdapter();
        for( BiGlEntry biGlEntry: biGlEntryList){
            UAGlEntry uaGlEntry =  adapter.translate(biGlEntry);
            result.add(uaGlEntry);
        }
        return result;
    }

}
