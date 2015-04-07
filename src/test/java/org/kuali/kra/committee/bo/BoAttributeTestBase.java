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
package org.kuali.kra.committee.bo;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class is abstract template BO test class could use to test toStringMapper. 
 * a. It checks for number of attributes used in toStringMapper.
 * b. It also test for fields and values passed in toStringMapper.
 */
public abstract class BoAttributeTestBase<T extends KraPersistableBusinessObjectBase> {

    private int attributeCount;

    private T bo;

    /**
     * Constructs a BoAttributeTestBase.java.
     * @param attributeCount is total count of persistent fields in BO used by toStringMapper().
     * @param bo is instance of BO.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public BoAttributeTestBase(int attributeCount, T bo) throws IllegalArgumentException, IllegalAccessException,
                                                        InvocationTargetException {
        super();
        this.attributeCount = attributeCount;
        this.bo = bo;
        boPrerequisite();
        setUpBo();
        boPostrequisite();
    }

    /**
     * Concrete implementer should return Map<String, Object>, using bo's field and value. Map should include all the fields used in
     * definition of toStringMapper().
     * 
     * @return Map<String, Object> of bo's key & value.
     */
    protected abstract Map<String, Object> getFieldMap();

    /**
     * This method can be used to set BO's post-requisites.
     */
    protected void boPrerequisite() {
    }

    /**
     * This method can be used to set BO's prerequisites.
     */
    protected void boPostrequisite() {
    }

    /**
     * This method is accessory of generic T type object.
     * 
     * @return
     */
    public T getT() {
        return bo;
    }

    /**
     * This method uses reflection to set values of BO.
     * 
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    private void setUpBo() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method[] methods = bo.getClass().getDeclaredMethods();
        Map map = getFieldMap();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.contains("set")) {
                methodName = methodName.substring(3);
                methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
                Object value = map.get(methodName);
                if (null != value) {
                    Object[] values = new Object[1];
                    values[0] = value;
                    method.invoke(bo, values);
                }
            }
        }
    }
}
