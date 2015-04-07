/*
 * Copyright 2005-2014 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.kra.s2s.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.bo.Organization;
import org.kuali.kra.bo.Rolodex;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.printing.PrintingException;
import org.kuali.kra.proposaldevelopment.bo.Narrative;
import org.kuali.kra.proposaldevelopment.bo.NarrativeAttachment;
import org.kuali.kra.proposaldevelopment.bo.ProposalSite;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.S2SException;
import org.kuali.kra.s2s.bo.S2sOppForms;
import org.kuali.kra.s2s.generator.util.S2STestConstants;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to test the PDF Printing of Forms
 */
public class PrintFormTest extends KcUnitTestBase {
    private static final Log LOG = LogFactory.getLog(PrintFormTest.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testPrint() throws IOException {
        ProposalDevelopmentDocument document = new ProposalDevelopmentDocument();
        Organization performOrganization = new Organization();
        performOrganization.setOrganizationName("Espace");
        Rolodex rolodex = new Rolodex();
        rolodex.setAddressLine1("1290 Avenue of the Americas");
        rolodex.setAddressLine2("Suite 550");
        rolodex.setCity("New York");
        rolodex.setCounty("County");
        rolodex.setPostalCode("10104");
        rolodex.setState("NY");
        rolodex.setCountryCode("USA");

        performOrganization.setRolodex(rolodex);
        
        ProposalSite performSite = new ProposalSite();
        performSite.setOrganization(performOrganization);
        document.getDevelopmentProposal().setPerformingOrganization(performSite);

        List<ProposalSite> proList = new ArrayList<ProposalSite>();
        ProposalSite proposalSite = new ProposalSite();
        proposalSite.setRolodex(rolodex);
        proList.add(proposalSite);
        document.getDevelopmentProposal().setOtherOrganizations(proList);

        Narrative narrative = new Narrative();
        narrative.setModuleNumber(123);
        List<Narrative> naList = new ArrayList<Narrative>();

        NarrativeAttachment narrativeAttachment = new NarrativeAttachment();
        //InputStream inStream = getClass().getResourceAsStream("pdftestDoc4.pdf");
        File file = new File(S2STestConstants.ATT_DIR_PATH + "exercise5.pdf");
        InputStream inStream = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(inStream);
        byte[] narrativePdf = new byte[bis.available()];
        bis.read(narrativePdf);
        narrativeAttachment.setNarrativeData(narrativePdf);


        List<NarrativeAttachment> narrativeList = new ArrayList<NarrativeAttachment>();
        narrativeList.add(0, narrativeAttachment);
        narrative.setNarrativeTypeCode("40");
        narrative.setNarrativeAttachmentList(narrativeList);
        narrative.setFileName("OpportunityForm");
        naList.add(narrative);
        document.getDevelopmentProposal().setNarratives(naList);
        S2sOppForms forms = new S2sOppForms();
        forms.setOppNameSpace("http://apply.grants.gov/forms/RR_PerformanceSite-V1.0");
        List<S2sOppForms> oppForms = new ArrayList<S2sOppForms>();
        oppForms.add(forms);
        document.getDevelopmentProposal().setS2sOppForms(oppForms);
        PrintService printService = ((PrintService) KraServiceLocator.getService(PrintService.class));
        try {
            printService.printForm(document);
        }
        catch (S2SException e) {
            LOG.error(e.getMessage(), e);
        }catch(PrintingException pe){
        	LOG.error(pe.getMessage());
        }


    }


}
