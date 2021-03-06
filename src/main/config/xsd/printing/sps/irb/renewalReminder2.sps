<?xml version="1.0" encoding="UTF-8"?>
<structure version="7" xsltversion="1" cssmode="strict" relativeto="*SPS" encodinghtml="UTF-8" encodingrtf="UTF-8" encodingpdf="UTF-8" embed-images="1">
	<parameters/>
	<schemasources>
		<namespaces>
			<nspair prefix="n1" uri="http://irb.mit.edu/irbnamespace"/>
		</namespaces>
		<schemasources>
			<xsdschemasource name="$XML" main="1" schemafile="irb.xsd" workingxmlfile="C:\correspondenceTemplates\xml files\renewalReminder.xml">
				<xmltablesupport/>
				<textstateicons/>
			</xsdschemasource>
		</schemasources>
	</schemasources>
	<modules/>
	<flags>
		<scripts/>
		<globalparts/>
		<designfragments/>
		<pagelayouts/>
	</flags>
	<scripts>
		<script language="javascript"/>
	</scripts>
	<globalstyles/>
	<mainparts>
		<children>
			<globaltemplate match="/" matchtype="named" parttype="main">
				<children>
					<template match="$XML" matchtype="schemasource">
						<children>
							<template match="n1:RenewalReminder" matchtype="schemagraphitem">
								<children>
									<table>
										<properties border="0"/>
										<children>
											<tablebody>
												<children>
													<tablerow>
														<children>
															<tablecell>
																<properties align="right" colspan="4" height="15" valign="top" width="43"/>
																<children>
																	<image>
																		<properties border="0"/>
																		<target>
																			<fixtext value="/export/home/www/https/tomcat5.0.25/webapps/coeus/images/couhes_byline2.gif"/>
																		</target>
																		<imagesource>
																			<fixtext value="/export/home/www/https/tomcat5.0.25/webapps/coeus/images/couhes_byline2.gif"/>
																		</imagesource>
																	</image>
																</children>
															</tablecell>
														</children>
													</tablerow>
													<tablerow>
														<children>
															<tablecell>
																<properties align="right" height="15" valign="top" width="43"/>
																<children>
																	<text fixtext="To:">
																		<styles font-weight="bold"/>
																	</text>
																</children>
															</tablecell>
															<tablecell>
																<properties height="15" valign="top" width="281"/>
																<children>
																	<template match="n1:Protocol" matchtype="schemagraphitem">
																		<children>
																			<template match="n1:Investigator" matchtype="schemagraphitem">
																				<children>
																					<template match="n1:Person" matchtype="schemagraphitem">
																						<children>
																							<template match="n1:Fullname" matchtype="schemagraphitem">
																								<children>
																									<condition>
																										<children>
																											<conditionbranch xpath="../../n1:PI_flag =&apos;true&apos;">
																												<children>
																													<content/>
																												</children>
																											</conditionbranch>
																										</children>
																									</condition>
																								</children>
																							</template>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</tablecell>
															<tablecell>
																<properties align="right" height="15" valign="top" width="100"/>
																<children>
																	<text fixtext="Date:">
																		<styles font-weight="bold"/>
																	</text>
																</children>
															</tablecell>
															<tablecell>
																<properties height="15" width="99"/>
																<children>
																	<template match="n1:CurrentDate" matchtype="schemagraphitem">
																		<children>
																			<content>
																				<format string="MM/DD/YYYY"/>
																			</content>
																		</children>
																	</template>
																</children>
															</tablecell>
														</children>
													</tablerow>
													<tablerow>
														<children>
															<tablecell>
																<properties align="right" height="15" valign="top" width="43"/>
															</tablecell>
															<tablecell>
																<properties height="15" valign="top" width="281"/>
																<children>
																	<template match="n1:Protocol" matchtype="schemagraphitem">
																		<children>
																			<template match="n1:Investigator" matchtype="schemagraphitem">
																				<children>
																					<template match="n1:Person" matchtype="schemagraphitem">
																						<children>
																							<template match="n1:OfficeLocation" matchtype="schemagraphitem">
																								<children>
																									<condition>
																										<children>
																											<conditionbranch xpath="../../n1:PI_flag =&apos;true&apos;">
																												<children>
																													<content/>
																												</children>
																											</conditionbranch>
																										</children>
																									</condition>
																								</children>
																							</template>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</tablecell>
															<tablecell>
																<properties align="right" height="15" valign="top" width="100"/>
															</tablecell>
															<tablecell>
																<properties height="15" valign="top" width="99"/>
															</tablecell>
														</children>
													</tablerow>
													<tablerow>
														<children>
															<tablecell>
																<properties align="right" height="15" valign="top" width="43"/>
																<children>
																	<text fixtext="From:">
																		<styles font-weight="bold"/>
																	</text>
																</children>
															</tablecell>
															<tablecell>
																<properties height="15" valign="top" width="281"/>
																<children>
																	<template match="n1:CommitteeMasterData" matchtype="schemagraphitem">
																		<children>
																			<template match="n1:CommitteeName" matchtype="schemagraphitem">
																				<children>
																					<content/>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</tablecell>
															<tablecell>
																<properties align="right" height="15" valign="top" width="100"/>
																<children>
																	<text fixtext="Expiration Date:">
																		<styles font-weight="bold"/>
																	</text>
																</children>
															</tablecell>
															<tablecell>
																<properties height="15" valign="top" width="99"/>
																<children>
																	<template match="n1:Protocol" matchtype="schemagraphitem">
																		<children>
																			<template match="n1:ProtocolMasterData" matchtype="schemagraphitem">
																				<children>
																					<template match="n1:ExpirationDate" matchtype="schemagraphitem">
																						<children>
																							<content>
																								<format string="MM/DD/YYYY"/>
																							</content>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</tablecell>
														</children>
													</tablerow>
												</children>
											</tablebody>
										</children>
									</table>
									<table>
										<properties border="0" width="100%"/>
										<children>
											<tablebody>
												<children>
													<tablerow>
														<children>
															<tablecell>
																<properties align="right" valign="top" width="9%"/>
																<children>
																	<text fixtext="Re:">
																		<styles font-weight="bold"/>
																	</text>
																</children>
															</tablecell>
															<tablecell>
																<properties valign="top" width="91%"/>
																<children>
																	<text fixtext="Protocol #: "/>
																	<template match="n1:Protocol" matchtype="schemagraphitem">
																		<children>
																			<template match="n1:ProtocolMasterData" matchtype="schemagraphitem">
																				<children>
																					<template match="n1:ProtocolNumber" matchtype="schemagraphitem">
																						<children>
																							<content/>
																						</children>
																					</template>
																					<text fixtext=" : "/>
																					<template match="n1:ProtocolTitle" matchtype="schemagraphitem">
																						<children>
																							<content/>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</tablecell>
														</children>
													</tablerow>
												</children>
											</tablebody>
										</children>
									</table>
									<paragraph>
										<children>
											<paragraph paragraphtag="p">
												<children>
													<paragraph paragraphtag="p">
														<children>
															<paragraph paragraphtag="p">
																<children>
																	<text fixtext="This is a reminder that the ">
																		<styles font-size="10pt"/>
																	</text>
																	<template match="n1:CommitteeMasterData" matchtype="schemagraphitem">
																		<children>
																			<template match="n1:CommitteeName" matchtype="schemagraphitem">
																				<children>
																					<content>
																						<styles font-size="10pt"/>
																					</content>
																				</children>
																			</template>
																		</children>
																	</template>
																	<text fixtext=" approval for the above-mentioned protocol will expire on ">
																		<styles font-size="10pt"/>
																	</text>
																	<template match="n1:Protocol" matchtype="schemagraphitem">
																		<children>
																			<template match="n1:ProtocolMasterData" matchtype="schemagraphitem">
																				<children>
																					<template match="n1:ExpirationDate" matchtype="schemagraphitem">
																						<children>
																							<content>
																								<styles font-size="10pt"/>
																								<format string="MM/DD/YYYY"/>
																							</content>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</template>
																	<text fixtext=".  Please return the Continuing Review Questionnaire (web.mit.edu/committees/couhes/forms.shtml) before the next scheduled COUHES meeting, or it will be necessary for you to submit a new application to the Committee if you want to continue this project.  Please submit the Continuing Review Questionnaire for this project whether or not you intend to continue this research.  Please note that no human use research may be continued under this protocol without COUHES approval.">
																		<styles font-size="10pt"/>
																	</text>
																	<paragraph paragraphtag="p">
																		<children>
																			<text fixtext="Please submit the following documents:">
																				<styles font-size="10pt"/>
																			</text>
																		</children>
																	</paragraph>
																	<paragraph paragraphtag="p">
																		<children>
																			<paragraph paragraphtag="p">
																				<children>
																					<paragraph paragraphtag="p">
																						<children>
																							<text fixtext="1) The original copy of the Continuing Review Questionnaire (CRQ).">
																								<styles font-size="10pt"/>
																							</text>
																						</children>
																					</paragraph>
																					<text fixtext="2) Two (2) copies of each consent form(s) used in the study (without the validation stamp to allow for revalidation).  COUHES requires that MIT consent forms follow the template on the web site. ">
																						<styles font-size="10pt"/>
																					</text>
																					<text fixtext=" ">
																						<styles font-size="10pt"/>
																					</text>
																					<text fixtext="Note: template updated in March, 2008.  The &quot;Emergency Care and Compensation for Injury&quot; required language has changed.">
																						<styles font-size="10pt" font-weight="bold"/>
																					</text>
																					<paragraph paragraphtag="p">
																						<children>
																							<text fixtext="3) A current protocol summary, inclusive of all amendments and revisions, which will serve as an IRB file copy.">
																								<styles font-size="10pt"/>
																							</text>
																							<newline/>
																							<newline/>
																							<text fixtext="Please note that you can obtain a copy of the Continuing Review Questionnaire through our web site : http://web.mit.edu/committees/couhes/forms.shtml.">
																								<styles font-size="10pt"/>
																							</text>
																							<newline/>
																							<text fixtext="As of July 1, 2003, all personnel involved in Human Subjects Research must complete the Human Subjects training course.  It is the responsibility of the PI to make sure that all personnel associated with this study have completed the human subjects training course (see the COUHES web site for a link to the training).">
																								<styles font-size="10pt"/>
																							</text>
																							<text fixtext=" ">
																								<styles font-size="10pt"/>
																							</text>
																							<text fixtext=" ">
																								<styles font-size="10pt"/>
																							</text>
																							<text fixtext="Human subjects training must be updated every 3 years.  Training must be current for all study personnel before renewal can be approved.">
																								<styles font-size="10pt" font-weight="bold"/>
																							</text>
																							<newline/>
																							<newline/>
																							<text fixtext="It is a violation of Massachusetts Institute of Technology policy and federal regulations to continue research activities after the approval period has expired.  If the IRB has not reviewed and re-approved this research by its current expiration date, all enrollment, research activities and intervention on previously enrolled subjects must stop.  If you believe that the health and welfare of the subjects will be jeopardized if the study treatment is discontinued, you may submit a written request to the IRB to continue treatment activities with currently enrolled subjects.">
																								<styles font-size="10pt"/>
																							</text>
																						</children>
																					</paragraph>
																					<newline/>
																					<newline/>
																					<newline/>
																				</children>
																			</paragraph>
																		</children>
																	</paragraph>
																</children>
															</paragraph>
														</children>
													</paragraph>
												</children>
											</paragraph>
										</children>
									</paragraph>
								</children>
							</template>
						</children>
					</template>
				</children>
			</globaltemplate>
		</children>
	</mainparts>
	<globalparts/>
	<pagelayout>
		<properties pagemultiplepages="0" pagenumberingformat="1" pagenumberingstartat="1" paperheight="11in" papermarginbottom="0.79in" papermarginleft="0.6in" papermarginright="0.6in" papermargintop="0.79in" paperwidth="8.5in"/>
	</pagelayout>
	<designfragments/>
</structure>
