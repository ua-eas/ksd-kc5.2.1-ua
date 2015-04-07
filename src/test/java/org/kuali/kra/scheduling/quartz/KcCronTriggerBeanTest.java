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
package org.kuali.kra.scheduling.quartz;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.core.impl.datetime.DateTimeServiceImpl;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.quartz.JobDetail;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the KcCronTriggerBean class.
 */
@RunWith(JMock.class)
public class KcCronTriggerBeanTest {
    
    private static final String CRON_EXPRESSION = "1 3 22 * * ?";
    
    private Mockery context = new JUnit4Mockery();
    
    /**
     * The KC Cron Trigger needs to be tested to simply verify that it is getting the
     * correct cron expression.
     * @throws ParseException 
     */
    @Test
    public void testCronExpression() throws Exception {
        KcCronTriggerBean cronTrigger = new KcCronTriggerBean();
        
        /*
         * The configuration service will be invoked once to get the Cron Expression.
         */
        final ParameterService parameterService = context.mock(ParameterService.class);
        context.checking(new Expectations() {
            {
                one(parameterService).parameterExists("KC-PD", "Document",
                        KeyConstants.PESSIMISTIC_LOCKING_CRON_EXPRESSION);
                will(returnValue(true));
                one(parameterService).getParameterValueAsString("KC-PD", "Document",
                        KeyConstants.PESSIMISTIC_LOCKING_CRON_EXPRESSION);
                will(returnValue(CRON_EXPRESSION));
            }
        });
        cronTrigger.setParameterService(parameterService);

        JobDetail jobDetail = new JobDetail();
        jobDetail.setName("test");
        cronTrigger.setBeanName("test");
        cronTrigger.setJobDetail(jobDetail);
        cronTrigger.setParameterNamespace("KC-PD");
        cronTrigger.setParameterComponent("Document");
        cronTrigger.setCronExpressionParameterName(KeyConstants.PESSIMISTIC_LOCKING_CRON_EXPRESSION);
        cronTrigger.setDateTimeService(new DateTimeServiceImpl());
        cronTrigger.afterPropertiesSet();
        
        assertEquals(CRON_EXPRESSION, cronTrigger.getCronExpression());
    }
}
