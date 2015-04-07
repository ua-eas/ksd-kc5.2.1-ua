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
package org.kuali.kra.test.infrastructure;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.service.ProposalDevelopmentService;
import org.kuali.kra.questionnaire.answer.QuestionnaireAnswerService;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.helpers.PersonTestHelper;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.kra.test.infrastructure.lifecycle.KcUnitTestMainLifecycle;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.identity.EntityDefaultInfoCacheBo;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class serves as a base test class for all KC unit tests. It handles ensuring all of the necessary lifecycles are properly
 * launched, started and stopped.
 */
@SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
@RunWith(KcUnitTestRunner.class)
public class KcUnitTestBase extends Assert implements KcUnitTestMethodAware {
    // non static Log to allow it to be named after the runtime class
    protected final Log LOG = LogFactory.getLog(this.getClass());

    private static KcUnitTestMainLifecycle LIFECYCLE = new KcUnitTestMainLifecycle();
    private static RunListener RUN_LISTENER = new KcUnitTestRunListener(LIFECYCLE);

    private long startTime;
    private long totalMem;
    private long freeMem;
    
    private final String memStatFormat = "[%1$-7s] total: %2$10d, free: %3$10d";

    private Method method;
    
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private QuestionnaireAnswerService questionnaireAnswerService;
    private ProposalDevelopmentService proposalDevelopmentService;
    
    protected boolean transactional = true;
    
    /**
     * This method executes before each unit test and ensures the necessary lifecycles have been started
     */
    @Before
    public final void baseBeforeTest() {
        logBeforeRun();
        LIFECYCLE.startPerTest(transactional);
        GlobalVariables.setMessageMap(new MessageMap());
        KNSGlobalVariables.setAuditErrorMap(new HashMap());
        createTestUsers();
    }
    
    /*
     * Create all test users at once, and subclasses can use PersonFixture and PersonService
     * to pull the user if needed.
     */
    private void createTestUsers() {
    	
    	// Need to clear KIM_ENTITY_CACHE_T. Some tests trigger flushing
        // of Entities to this table, and the transaction isn't always
        // rolling back this table between tests. This prevents errors
        // created by attempting to insert existing Entities.
        final Map<String, Object> emptyMap = Collections.emptyMap();
        KRADServiceLocator.getBusinessObjectService().deleteMatching(EntityDefaultInfoCacheBo.class, emptyMap);

    	// Any child test class that creates documents relies on this user and session,
        // we need to ensure the user is present -- we should not count on this being
        // in the entity cache, and infact should expect the cache to be empty.
        PersonTestHelper personHelper = new PersonTestHelper();
        Person quickstart = personHelper.createPerson(PersonFixture.UAR_TEST_001);
        RoleTestHelper roleHelper = new RoleTestHelper();
        roleHelper.addPersonToRole(quickstart, RoleFixture.SUPER_USER);
        GlobalVariables.setUserSession(new UserSession(quickstart.getPrincipalName()));
        
        // Other test users
        personHelper.createPerson(PersonFixture.UAR_TEST_002);
        personHelper.createPerson(PersonFixture.UAR_TEST_003);
        personHelper.createPerson(PersonFixture.UAR_TEST_004);
        personHelper.createPerson(PersonFixture.UAR_TEST_005);
        personHelper.createPerson(PersonFixture.UAR_TEST_006);
        personHelper.createPerson(PersonFixture.UAR_TEST_007);
        personHelper.createPerson(PersonFixture.UAR_TEST_008);
        personHelper.createPerson(PersonFixture.UAR_TEST_009);
        personHelper.createPerson(PersonFixture.UAR_TEST_010);
    }

    /**
     * This method executes after each unit test and makes sure the necessary lifecycles have been stopped
     */
    @After
    public final void baseAfterTest() {
        GlobalVariables.setMessageMap(new MessageMap());
        KNSGlobalVariables.setAuditErrorMap(new HashMap());
        GlobalVariables.setUserSession(null);
        LIFECYCLE.stopPerTest();
        logAfterRun();
    }
    
    @BeforeClass
    public static final void baseBeforeClass() {
        if (!LIFECYCLE.isPerSuiteStarted()) {
            LIFECYCLE.startPerSuite();
        }
        LIFECYCLE.startPerClass();
    }
    
    @AfterClass
    public static final void baseAfterClass() {
        LIFECYCLE.stopPerClass();
    }

    /**
     * This method is the canonical <code>@Before</code> method, included here to maintain compatibility with existing subclasses
     * calling <code>super.setUp()</code>.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // no-op
    }

    /**
     * This method is the canonical <code>@After</code> method, included here to maintain compatibility with existing subclasses
     * calling <code>super.tearDown()</code>.
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        // no-op
    }

    /**
     * This method is called by the <code>KCUnitTestRunner</code> and passes the method being called so the required lifecycles can
     * be determined.
     * 
     * @param method the <code>Method</code> being called by the current test
     * 
     * @see org.kuali.kra.test.infrastructure.KcUnitTestMethodAware#setTestMethod(java.lang.reflect.Method)
     */
    public void setTestMethod(Method method) {
        this.method = method;
    }
        
    /**
     * This method returns the <code>RunListener</code> needed to ensure the KC persistent lifecycles shut down properly
     * @return the RunListener responsible for shutting down all KC persistent lifecycles
     */
    public static RunListener getRunListener() {
        return RUN_LISTENER;
    }
    
    protected void logBeforeRun() {
        if (LOG.isInfoEnabled()) {
            statsBegin();
            LOG.info("##############################################################");
            LOG.info("# Starting test " + getFullTestName() + "...");
            LOG.info("##############################################################");
        }
    }

    protected void logAfterRun() {
        if (LOG.isInfoEnabled()) {
            LOG.info("##############################################################");
            LOG.info("# ...finished test " + getFullTestName());
            for (String stat : statsEnd()) {
                LOG.info("# " + stat);
            }
            LOG.info("##############################################################");
        }
    }
    
    private void statsBegin() {
        startTime = System.currentTimeMillis();
        totalMem = Runtime.getRuntime().totalMemory();
        freeMem = Runtime.getRuntime().freeMemory();
    }

    protected String[] statsEnd() {
        long currentTime = System.currentTimeMillis();
        long currentTotalMem = Runtime.getRuntime().totalMemory();
        long currentFreeMem = Runtime.getRuntime().freeMemory();
        return new String[]{
                String.format(memStatFormat, "MemPre", totalMem, freeMem),
                String.format(memStatFormat, "MemPost", currentTotalMem, currentFreeMem),
                String.format(memStatFormat, "MemDiff", totalMem-currentTotalMem, freeMem-currentFreeMem),
                String.format("[ElapsedTime] %1$d ms", currentTime-startTime)
        };
    }
    
    protected String getFullTestName() {
        return getClass().getSimpleName() + "." + method.getName();
    }

    protected QuestionnaireAnswerService getQuestionnaireAnswerService(){
        if (questionnaireAnswerService == null){
            questionnaireAnswerService = KraServiceLocator.getService(QuestionnaireAnswerService.class);
        }
		return questionnaireAnswerService;
    }
    
    protected ProposalDevelopmentService getProposalDevelopmentService(){
        if (proposalDevelopmentService == null){
            proposalDevelopmentService = KraServiceLocator.getService(ProposalDevelopmentService.class);
        }
		return proposalDevelopmentService;
    }
    
    protected BusinessObjectService getBusinessObjectService() {
        if(businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }
    
    protected void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected DocumentService getDocumentService() {
        if(documentService == null) {
            documentService = KRADServiceLocatorWeb.getDocumentService();
        }
        return documentService;
    }

    protected void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    protected ParameterService getParameterService() {
        if(parameterService == null) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

    protected void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    protected Document getDocument(String documentNumber) throws Exception {
        
        // Unfortunately, I can only clear the cache for OJB.  I have been
        // unable to force a refresh on a document when it is in the cache.
        // This is a pain if I need to recheck a document after the database
        // has been changed.  Therefore, for OJB, I clear the cache which
        // will force a new instance of the document to be retrieved from the database
        // instead of the cache.
        if (!OrmUtils.isJpaEnabled()) {
            KRADServiceLocatorWeb.getPersistenceServiceOjb().clearCache(); 
        }
        Document doc=getDocumentService().getByDocumentHeaderId(documentNumber);
        return doc;

    }
    
    /**
     *  Delegate to <code>{@link KraServiceLocator#getService(Class)}</code>
     * @param <T>
     * @param serviceClass class of service to get instance for
     * @return Service instance
     */
    protected final <T> T getService(Class<T> serviceClass) {
        return KraServiceLocator.getService(serviceClass);
    }

    protected void updateParameterForTesting(Class componentClass, String parameterName, String newValue) {
        Parameter parameter = getParameterService().getParameter(componentClass, parameterName);
        Parameter.Builder parameterForUpdate = Parameter.Builder.create(parameter);
        parameterForUpdate.setValue(newValue);
        getParameterService().updateParameter(parameterForUpdate.build());
    }
}
