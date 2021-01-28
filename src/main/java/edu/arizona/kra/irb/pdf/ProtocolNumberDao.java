package edu.arizona.kra.irb.pdf;

import java.util.List;

public interface ProtocolNumberDao {
    List<String> getActiveProtocolNumbers(String startFromDate, String endToDate);
}
