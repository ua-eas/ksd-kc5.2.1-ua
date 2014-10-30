package edu.arizona.kra.committee.print;

import java.util.List;

import org.kuali.kra.committee.bo.CommitteeSchedule;
import org.kuali.kra.committee.print.ScheduleXmlStream;
import org.kuali.kra.common.committee.bo.CommitteeMembershipBase;
import org.kuali.kra.common.committee.meeting.CommitteeScheduleAttendanceBase;

import edu.mit.irb.irbnamespace.ScheduleDocument.Schedule;
import edu.mit.irb.irbnamespace.ScheduleDocument.Schedule.Attendents;

public class CustomScheduleXmlStream extends ScheduleXmlStream {

	/* We are overriding this method so we can customize what data we are setting for attendance information.
	 * Initially, we are adding the attendant comments to the xml so it will be available when generating the
	 * letter.
	 * 
	 * (non-Javadoc)
	 * @see org.kuali.kra.committee.print.ScheduleXmlStream#setAttendance(org.kuali.kra.committee.bo.CommitteeSchedule, edu.mit.irb.irbnamespace.ScheduleDocument.Schedule)
	 */
	@Override
	protected void setAttendance(CommitteeSchedule committeeSchedule, Schedule schedule) {
		List<CommitteeScheduleAttendanceBase> attendenceList = committeeSchedule.getCommitteeScheduleAttendances();
        for (CommitteeScheduleAttendanceBase attendanceInfoBean : attendenceList) {
            Attendents attendents = schedule.addNewAttendents();
            attendents.setAttendentName(attendanceInfoBean.getPersonName());
            attendents.setAlternateFlag(attendanceInfoBean.getAlternateFlag());
            attendents.setGuestFlag(attendanceInfoBean.getGuestFlag());
            attendents.setAlternateFor(attendanceInfoBean.getAlternateFor());
            attendents.setPresentFlag(true);
            
            String comments = attendanceInfoBean.getComments();
            if ( comments != null ) {
            	attendents.setAttendantComments(comments);
            }
        }

        List<CommitteeMembershipBase> committeeMemberships = committeeSchedule.getParentCommittee().getCommitteeMemberships();
        for (CommitteeMembershipBase committeeMembership : committeeMemberships) {
            if (!getCommitteeMembershipService().isMemberAttendedMeeting(committeeMembership, committeeSchedule.getParentCommittee().getCommitteeId())) {
                Attendents attendents = schedule.addNewAttendents();
                attendents.setAttendentName(committeeMembership.getPersonName());
                attendents.setAlternateFlag(false);
                attendents.setGuestFlag(false);
                attendents.setPresentFlag(false);
            }
        }
	}
	
}
