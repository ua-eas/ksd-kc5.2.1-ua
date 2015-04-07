/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.award.paymentreports.awardreports.reporting.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.award.AwardFixtureFactory;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardService;
import org.kuali.kra.award.home.AwardServiceImpl;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.test.fixtures.SponsorFixture;
import org.kuali.kra.test.fixtures.UnitFixture;
import org.kuali.kra.test.helpers.SponsorTestHelper;
import org.kuali.kra.test.helpers.UnitTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

/*
 * FIXME: The UofA configuration is such that nearly all notifications
 *        are  purposely turned off -- this makes these test cases moot.
 *        Keeping this around in case the config changes.
 */
public class ReportTrackingNotificationServiceTest extends KcUnitTestBase {

    private static final String ROLE_ID = "roleId";
    private ReportTrackingNotificationServiceImpl service;
    private BusinessObjectService boService;
    private DocumentService documentService;
    
    private Award award;

     @Before
    public void setUp() throws Exception {
        UnitTestHelper unitTestHelper = new UnitTestHelper();
        unitTestHelper.createUnit( UnitFixture.TEST_1 );
        SponsorTestHelper sponsorTestHelper = new SponsorTestHelper();
        sponsorTestHelper.createSponsor( SponsorFixture.AZ_STATE );
        super.setUp();
        service = (ReportTrackingNotificationServiceImpl) KraServiceLocator.getService(ReportTrackingNotificationService.class);
        boService = KraServiceLocator.getService(BusinessObjectService.class);
        documentService = KraServiceLocator.getService(DocumentService.class);
        AwardDocument awardDoc = (AwardDocument) documentService.getNewDocument(AwardDocument.class);
        award = AwardFixtureFactory.createAwardFixture();
        awardDoc.setAward(award);
        awardDoc.getDocumentHeader().setDocumentDescription("Testing");
        documentService.saveDocument(awardDoc);
        AwardService mockAwardService = new AwardServiceImpl() {

            @Override
            public Award getActiveOrNewestAward(String awardNumber) {
                return award;
            }
        };
        service.setAwardService(mockAwardService);

    }

     @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void tstRunReportTrackingNotifications() {
    	/*
        int numberOspAdmin = getOSPAdminCount();
        ReportTrackingNotificationServiceTestHelper rtnsTestHelper = new ReportTrackingNotificationServiceTestHelper();
        rtnsTestHelper.createTestDataSet2( award, service );

        List<ReportTrackingNotificationDetails> details = service.runReportTrackingNotifications();
        assertEquals(1, details.size());
        assertTrue(details.get(0).isNotificationActive());
        assertEquals(1, details.get(0).getNotificationRecipients());
        assertEquals(2, details.get(0).getTrackingRecordsFound());
        assertEquals(1, details.get(0).getTrackingRecordsMatched());
        assertEquals(numberOspAdmin, details.get(0).getNotificationsSent());
        List<SentReportNotification> notificationsSent = (List<SentReportNotification>) boService.findAll(SentReportNotification.class);
        assertEquals(1, notificationsSent.size());
        */
    }
    
    @Test
    public void tstRunReportTrackingNotificationsPreviouslySent() {
    	/*
        ReportTrackingNotificationServiceTestHelper rtnsTestHelper = new ReportTrackingNotificationServiceTestHelper();
        rtnsTestHelper.createTestDataSet3( award, service );
        
        List<ReportTrackingNotificationDetails> details = service.runReportTrackingNotifications();
        List<SentReportNotification> notificationsSent = (List<SentReportNotification>) boService.findAll(SentReportNotification.class);
        assertEquals(1, notificationsSent.size());
        
        details = service.runReportTrackingNotifications();
        assertEquals(1, details.size());
        assertTrue(details.get(0).isNotificationActive());
        assertEquals(1, details.get(0).getNotificationRecipients());
        assertEquals(2, details.get(0).getTrackingRecordsFound());
        assertEquals(0, details.get(0).getTrackingRecordsMatched());
        assertEquals(0, details.get(0).getNotificationsSent());
        */
    }
    
    @Test
    public void tstDateBarriers() {
    	/*
        int ospAdminCount = getOSPAdminCount();
        ReportTrackingNotificationServiceTestHelper rtnsTestHelper = new ReportTrackingNotificationServiceTestHelper();
        rtnsTestHelper.createTestDataSet1( award, service );
        
        List<ReportTrackingNotificationDetails> details = service.runReportTrackingNotifications();
        List<SentReportNotification> notificationsSent = (List<SentReportNotification>) boService.findAll(SentReportNotification.class);
        assertEquals(6, notificationsSent.size());
        assertEquals(4, details.size());
        assertEquals("401", details.get(0).getActionCode());
        assertEquals(9, details.get(0).getTrackingRecordsFound());
        assertEquals(2, details.get(0).getTrackingRecordsMatched());
        //the below is based on current demo data and the number of OSP Admins for 000001
        assertEquals(ospAdminCount, details.get(0).getNotificationsSent());
        
        assertEquals("402", details.get(1).getActionCode());
        assertEquals(9, details.get(1).getTrackingRecordsFound());
        assertEquals(1, details.get(1).getTrackingRecordsMatched());
        //the below is based on current demo data and the number of OSP Admins for 000001        
        assertEquals(ospAdminCount, details.get(1).getNotificationsSent());  
        
        assertEquals("403", details.get(2).getActionCode());
        assertEquals(9, details.get(2).getTrackingRecordsFound());
        assertEquals(2, details.get(2).getTrackingRecordsMatched());
        //the below is based on current demo data and the number of OSP Admins for 000001        
        assertEquals(ospAdminCount, details.get(2).getNotificationsSent());
        
        assertEquals("404", details.get(3).getActionCode());
        assertEquals(9, details.get(3).getTrackingRecordsFound());
        assertEquals(1, details.get(3).getTrackingRecordsMatched());
        //the below is based on current demo data and the number of OSP Admins for 000001        
        assertEquals(ospAdminCount, details.get(3).getNotificationsSent());
        */            
    }   
    
    //FIXME: Remove suppress once class is fixed.
    @SuppressWarnings("unused")
	private int getOSPAdminCount(){
        Map<String,String> fieldValues = new HashMap<String,String>();
        fieldValues.put( ROLE_ID, "1114" );
        int count = boService.countMatching( RoleMemberBo.class, fieldValues );
        return count;
    }

}
