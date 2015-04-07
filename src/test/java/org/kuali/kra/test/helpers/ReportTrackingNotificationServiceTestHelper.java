package org.kuali.kra.test.helpers;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.Distribution;
import org.kuali.kra.award.paymentreports.Frequency;
import org.kuali.kra.award.paymentreports.FrequencyBase;
import org.kuali.kra.award.paymentreports.Report;
import org.kuali.kra.award.paymentreports.ReportClass;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTracking;
import org.kuali.kra.award.paymentreports.awardreports.reporting.service.ReportTrackingNotification;
import org.kuali.kra.award.paymentreports.awardreports.reporting.service.ReportTrackingNotificationServiceImpl;
import org.kuali.kra.bo.Sponsor;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.common.notification.bo.NotificationType;
import org.kuali.kra.test.fixtures.NotificationTypeFixture;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.ReportTrackingFixture;
import org.kuali.kra.test.fixtures.ReportTrackingNotificationFixture;
import org.kuali.kra.test.fixtures.SponsorFixture;
import org.kuali.kra.test.fixtures.UnitFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class ReportTrackingNotificationServiceTestHelper extends TestHelper {
	private static class ReportTrackingTestHelper extends TestHelper {
		private static class ReportTrackingDefault {
			private static final String PI_PERSON_ID = PersonFixture.UAR_TEST_001.getPrincipalId();
			private static final Integer PI_ROLODEX_ID = 1;
			private static final String PI_NAME = "Quickstart Name";
			private static final String LEAD_UNIT_NUMBER = UnitFixture.TEST_1.getUnitNumber();
			private static final String REPORT_CODE = "4";
			private static final String FREQUENCY_CODE = "1";
			private static final String OSP_DISTRIBUTION_CODE = "1";
			private static final String STATUS_CODE = "1";
			private static final Date BASE_DATE = new Date( 0L );
			private static final Date ACTIVITY_DATE = new Date( 0L );
			private static final Integer OVERDUE = 0;
			private static final String COMMENTS = "COMMENTS";
			private static final String PREPARER_ID = "PREPARER_ID";
			private static final String PREPARER_NAME = "PREPARER_NAME";
			private static final String SPONSOR_CODE = SponsorFixture.AZ_STATE.getSponsorCode();
			private static final String SPONSOR_AWARD_NUMBER = "SPONSOR_AWARD_NUMBER";
			private static final String TITLE = "TITLE";
			private static final String LAST_UPDATE_USER = PersonFixture.UAR_TEST_001.getPrincipalName();
			private static final Timestamp LAST_UPDATE_DATE = new Timestamp( 0 );
			private static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			private static final String UPDATE_USER = PersonFixture.UAR_TEST_001.getPrincipalName();
			private static final Long VER_NBR = 1L;
		}

		public void createReportTracking( ReportTrackingFixture fixture, Award award )
		{
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			ReportTracking rt = buildReportTracking( fixture, award );
			businessObjectService.save( rt );
		}

		private ReportTracking buildReportTracking( ReportTrackingFixture fixture, Award award ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			ReportTracking rt = new ReportTracking();
			rt.setAwardNumber( award.getAwardNumber() );
			rt.setAwardReportTermId( fixture.getAwardReportTermId() );
			rt.setReportClassCode( fixture.getReportClassCode() );
			rt.setFrequencyBaseCode( fixture.getFrequencyBaseCode() );
			rt.setDueDate( fixture.getDueDate() );
			rt.setObjectId( fixture.getObjectId() );
			rt.setPiPersonId( ReportTrackingDefault.PI_PERSON_ID );
			rt.setPiRolodexId( ReportTrackingDefault.PI_ROLODEX_ID );
			rt.setPiName( ReportTrackingDefault.PI_NAME );
			rt.setLeadUnitNumber( ReportTrackingDefault.LEAD_UNIT_NUMBER );
			rt.setReportCode( ReportTrackingDefault.REPORT_CODE );
			rt.setFrequencyCode( ReportTrackingDefault.FREQUENCY_CODE );
			rt.setOspDistributionCode( ReportTrackingDefault.OSP_DISTRIBUTION_CODE );
			rt.setStatusCode( ReportTrackingDefault.STATUS_CODE );
			rt.setBaseDate( ReportTrackingDefault.BASE_DATE );
			rt.setOverdue( ReportTrackingDefault.OVERDUE );
			rt.setActivityDate( ReportTrackingDefault.ACTIVITY_DATE );
			rt.setComments( ReportTrackingDefault.COMMENTS );
			rt.setPreparerId( ReportTrackingDefault.PREPARER_ID );
			rt.setPreparerName( ReportTrackingDefault.PREPARER_NAME );
			rt.setSponsorCode( ReportTrackingDefault.SPONSOR_CODE );
			rt.setSponsorAwardNumber( ReportTrackingDefault.SPONSOR_AWARD_NUMBER );
			rt.setTitle( ReportTrackingDefault.TITLE );
			rt.setLastUpdateUser( ReportTrackingDefault.LAST_UPDATE_USER );
			rt.setLastUpdateDate( ReportTrackingDefault.LAST_UPDATE_DATE );
			rt.setUpdateTimestamp( ReportTrackingDefault.UPDATE_TIMESTAMP );
			rt.setUpdateUser( ReportTrackingDefault.UPDATE_USER );
			rt.setVersionNumber( ReportTrackingDefault.VER_NBR );
			// Set object members
			Distribution distribution = businessObjectService.findBySinglePrimaryKey( Distribution.class, rt.getOspDistributionCode() );
			Frequency frequency = businessObjectService.findBySinglePrimaryKey( Frequency.class, rt.getFrequencyCode() );
			FrequencyBase frequencyBase = businessObjectService.findBySinglePrimaryKey( FrequencyBase.class, rt.getFrequencyBaseCode() );
			Report report = businessObjectService.findBySinglePrimaryKey( Report.class, rt.getReportCode() );
			ReportClass reportClass = businessObjectService.findBySinglePrimaryKey( ReportClass.class, rt.getReportClassCode() );
			Unit leadUnit = businessObjectService.findBySinglePrimaryKey( Unit.class, rt.getLeadUnitNumber() );
			Sponsor sponsor = businessObjectService.findBySinglePrimaryKey( Sponsor.class, rt.getSponsorCode() );
			rt.setDistribution( distribution );
			rt.setFrequency( frequency );
			rt.setFrequencyBase( frequencyBase );
			rt.setReport( report );
			rt.setReportClass( reportClass );
			rt.setLeadUnit( leadUnit );
			rt.setSponsor( sponsor );
			return rt;
		}
	}

	private static class NotificationTypeTestHelper extends TestHelper {
		private static class NotificationTypeDefault {
			private static final String DESCRIPTION = "NotificationTypeTestHelper";
			private static final String SUBJECT = "NotificationTypeTestHelper";
			private static final String MESSAGE = "NotificationTypeTestHelper";
			private static final boolean PROMPT_USER = false;
			private static final String UPDATE_USER = "NotificationTypeTestHelper";
			private static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			private static final Long VER_NBR = 1L;
			private static final String OBJ_ID = "NotificationTypeTestHelper";
		}

		public void createNotificationType( NotificationTypeFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			NotificationType nt = businessObjectService.findBySinglePrimaryKey( NotificationType.class, fixture.getNotificationTypeId() );
			if ( nt == null ) {
				nt = buildNotificationType( fixture );
			}
			else {
				nt = updateNotificationType( nt, fixture );
			}
			businessObjectService.save( nt );
		}

		private NotificationType updateNotificationType( NotificationType nt, NotificationTypeFixture fixture ) {
			nt.setModuleCode( fixture.getModuleCode() );
			nt.setActionCode( fixture.getActionCode() );
			nt.setActive( fixture.getActive() );
			return nt;
		}

		private NotificationType buildNotificationType( NotificationTypeFixture fixture ) {
			NotificationType nt = new NotificationType();
			nt.setNotificationTypeId( fixture.getNotificationTypeId() );
			nt.setModuleCode( fixture.getModuleCode() );
			nt.setActionCode( fixture.getActionCode() );
			nt.setActive( fixture.getActive() );
			nt.setDescription( NotificationTypeDefault.DESCRIPTION );
			nt.setSubject( NotificationTypeDefault.SUBJECT );
			nt.setMessage( NotificationTypeDefault.MESSAGE );
			nt.setPromptUser( NotificationTypeDefault.PROMPT_USER );
			nt.setUpdateUser( NotificationTypeDefault.UPDATE_USER );
			nt.setUpdateTimestamp( NotificationTypeDefault.UPDATE_TIMESTAMP );
			nt.setVersionNumber( NotificationTypeDefault.VER_NBR );
			nt.setObjectId( NotificationTypeDefault.OBJ_ID );
			return nt;
		}
	}

	public ReportTrackingNotification generateReportTrackingNotification( ReportTrackingNotificationFixture fixture ) {
		ReportTrackingNotification rtn = new ReportTrackingNotification( fixture.getName(), fixture.getActionCode(), fixture.isOverride(), fixture.getDays(), fixture.getScope(), fixture.getReportClassCode() );
		return rtn;
	}

	public void createTestDataSet1( Award award, ReportTrackingNotificationServiceImpl service ) {
		ReportTrackingTestHelper rttHelper = new ReportTrackingTestHelper();
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999001, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999002, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999003, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999004, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999005, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999006, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999007, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999008, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999009, award );
		NotificationTypeTestHelper nttHelper = new NotificationTypeTestHelper();
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014001 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014002 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014003 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014004 );
		service.getNotifications().clear();
		ReportTrackingNotification notification1 = generateReportTrackingNotification( ReportTrackingNotificationFixture.REPORT_TRACKING_NOTIFICATION_999001 );
		ReportTrackingNotification notification2 = generateReportTrackingNotification( ReportTrackingNotificationFixture.REPORT_TRACKING_NOTIFICATION_999002 );
		ReportTrackingNotification notification3 = generateReportTrackingNotification( ReportTrackingNotificationFixture.REPORT_TRACKING_NOTIFICATION_999003 );
		ReportTrackingNotification notification4 = generateReportTrackingNotification( ReportTrackingNotificationFixture.REPORT_TRACKING_NOTIFICATION_999004 );
		service.getNotifications().add( notification1 );
		service.getNotifications().add( notification2 );
		service.getNotifications().add( notification3 );
		service.getNotifications().add( notification4 );
	}

	public void createTestDataSet2( Award award, ReportTrackingNotificationServiceImpl service ) {
		ReportTrackingTestHelper rttHelper = new ReportTrackingTestHelper();
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999001, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999010, award );
		NotificationTypeTestHelper nttHelper = new NotificationTypeTestHelper();
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014001 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014002 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014003 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014004 );
		ReportTrackingNotification notification1 = generateReportTrackingNotification( ReportTrackingNotificationFixture.REPORT_TRACKING_NOTIFICATION_999001 );
		service.getNotifications().clear();
		service.getNotifications().add( notification1 );
	}

	public void createTestDataSet3( Award award, ReportTrackingNotificationServiceImpl service ) {
		ReportTrackingTestHelper rttHelper = new ReportTrackingTestHelper();
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999001, award );
		rttHelper.createReportTracking( ReportTrackingFixture.REPORT_TRACKING_9999010, award );
		NotificationTypeTestHelper nttHelper = new NotificationTypeTestHelper();
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014001 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014002 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014003 );
		nttHelper.createNotificationType( NotificationTypeFixture.NOTIFICATION_TYPE_014004 );
		ReportTrackingNotification notification1 = generateReportTrackingNotification( ReportTrackingNotificationFixture.REPORT_TRACKING_NOTIFICATION_999001 );
		service.getNotifications().clear();
		service.getNotifications().add( notification1 );
	}
}
