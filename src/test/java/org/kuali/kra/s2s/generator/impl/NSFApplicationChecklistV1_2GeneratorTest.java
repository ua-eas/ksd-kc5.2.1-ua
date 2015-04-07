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
package org.kuali.kra.s2s.generator.impl;

import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.proposaldevelopment.bo.ProposalAbstract;
import org.kuali.kra.proposaldevelopment.bo.ProposalYnq;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.generator.S2STestBase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests the NSFApplicationChecklistV1_2 Generator
 */
public class NSFApplicationChecklistV1_2GeneratorTest extends
		S2STestBase<NSFApplicationChecklistV1_2Generator> {

	@Override
	protected Class<NSFApplicationChecklistV1_2Generator> getFormGeneratorClass() {
		return NSFApplicationChecklistV1_2Generator.class;
	}

	@Override
	protected void prepareData(ProposalDevelopmentDocument document)
			throws Exception {
		DevelopmentProposal developmentProposal = document
				.getDevelopmentProposal();
		developmentProposal.setProposalTypeCode("8");
		developmentProposal.setProposalTypeCode("2");
		developmentProposal.setProposalTypeCode("5");
		ProposalYnq proposalYnq = new ProposalYnq();
		proposalYnq.setAnswer("Y");
		proposalYnq.setQuestionId("21");
		ProposalYnq proposalYnq1 = new ProposalYnq();
		proposalYnq1.setAnswer("Y");
		proposalYnq1.setQuestionId("FG");
		List<ProposalYnq> ynqList = new ArrayList<ProposalYnq>();
		ynqList.add(proposalYnq);
		ynqList.add(proposalYnq1);
		developmentProposal.setProposalYnqs(ynqList);

		ProposalAbstract propsAbstract = new ProposalAbstract();
		propsAbstract.setAbstractTypeCode("15");
		ProposalAbstract propsAbstract1 = new ProposalAbstract();
		propsAbstract1.setAbstractTypeCode("12");
		List<ProposalAbstract> proList = new ArrayList<ProposalAbstract>();
		proList.add(propsAbstract);
		proList.add(propsAbstract1);
		developmentProposal.setProposalAbstracts(proList);
		document.setDevelopmentProposal(developmentProposal);
	}
}
