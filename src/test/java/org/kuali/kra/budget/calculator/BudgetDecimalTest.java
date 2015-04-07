/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.budget.calculator;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.budget.BudgetDecimal;

public class BudgetDecimalTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void divideTest() throws Exception{
        BudgetDecimal op1 = new BudgetDecimal(100);
        BudgetDecimal op2 = new BudgetDecimal(365);
        Assert.assertEquals(op1.divide(op2),new BudgetDecimal(100d/365d));
    }

    @Test
    public void percentageTest() throws Exception{
        BudgetDecimal op1 = new BudgetDecimal(39);
        BudgetDecimal op2 = new BudgetDecimal(9);
        Assert.assertEquals(op1.percentage(op2),new BudgetDecimal(3.51));
    }
    @Test
    public void divide1Test() throws Exception{
        BudgetDecimal op1 = new BudgetDecimal(100);
        BudgetDecimal op2 = new BudgetDecimal(3);
        Assert.assertEquals(op1.divide(op2),new BudgetDecimal(100d/3d));
    }
}
