package edu.arizona.kra.committee.print;

import edu.mit.irb.irbnamespace.MinutesDocument.Minutes;
import org.kuali.kra.committee.bo.CommitteeSchedule;
import org.kuali.kra.committee.print.IrbPrintXmlUtilService;
import org.kuali.kra.committee.print.IrbPrintXmlUtilServiceImpl;
import org.kuali.kra.meeting.CommitteeScheduleMinute;

import java.math.BigInteger;

/**
 * Overriding foundation service to add more info to the meeting minutes
 * Created by nataliac on 11/2/17.
 */
public class CustomIrbPrintXmlUtilServiceImpl extends IrbPrintXmlUtilServiceImpl implements IrbPrintXmlUtilService {

    protected void addMinute(CommitteeSchedule committeeSchedule, CommitteeScheduleMinute committeeScheduleMinute, Minutes minutesType) {
        committeeScheduleMinute.refreshNonUpdateableReferences();
        minutesType.setScheduleId(committeeScheduleMinute.getScheduleIdFk().toString());
        minutesType.setEntryNumber(new BigInteger(String.valueOf(committeeScheduleMinute.getEntryNumber())));
        minutesType.setEntryTypeCode(new BigInteger(String.valueOf(committeeScheduleMinute.getMinuteEntryTypeCode())));
        minutesType.setEntryTypeDesc(committeeScheduleMinute.getMinuteEntryType().getDescription());
        minutesType.setProtocolContingencyCode(committeeScheduleMinute.getProtocolContingencyCode() != null ? committeeScheduleMinute.getProtocolContingencyCode() : null);
        minutesType.setMinuteEntry(committeeScheduleMinute.getMinuteEntry());
        minutesType.setPrivateCommentFlag(committeeScheduleMinute.getPrivateCommentFlag());
        minutesType.setFinalFlag(committeeScheduleMinute.isFinalFlag());
        committeeScheduleMinute.refreshReferenceObject("protocol");
        if (committeeScheduleMinute.getProtocol() != null && committeeScheduleMinute.getProtocol().getProtocolNumber() != null) {
            String otherItemDescription = getOtherItemDescription(committeeSchedule, committeeScheduleMinute);
            if (otherItemDescription != null) {
                minutesType.setProtocolNumber(otherItemDescription);
            }
            else {
                minutesType.setProtocolNumber(committeeScheduleMinute.getProtocol().getProtocolNumber());
            }
        }
        minutesType.setEntrySortCode(BigInteger.valueOf(committeeScheduleMinute.getMinuteEntryType().getSortId()));

    }

}
