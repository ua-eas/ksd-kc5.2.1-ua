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
package org.kuali.kra.common.customattributes;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.bo.DocumentCustomData;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.CustomAttributeService;
import org.kuali.kra.service.TaskAuthorizationService;
import org.kuali.rice.krad.util.GlobalVariables;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * The CustomDataHelperBase is the base class for all Custom Data Helper classes.
 */
public abstract class CustomDataHelperBase<T extends DocumentCustomData> implements Serializable {
        
    private SortedMap<String, List> customAttributeGroups = new TreeMap<String, List>();
   
    /*
     * Is the end-user allowed to modify the custom data values?
     */
    private boolean modifyCustomData = false;
    
        
    public abstract boolean documentNotRouted(); 
       
    /**
     * This method builds the custom data collections used on the form and populates the values from the collection of AwardCustomData on the Award.
     * @param customAttributeGroups
     */
    @SuppressWarnings("unchecked")
    public void buildCustomDataCollectionsOnExistingDocument(SortedMap<String, List> customAttributeGroups) {
        boolean documentNotRouted = false;
        documentNotRouted = documentNotRouted();
        
        /*
         * Going through all customDataDocs and adding the custom ones already in the document
         */
        List<T> customDataInDocument = getCustomDataList();
        Set<Entry<String, CustomAttributeDocument>> allCustomAttributeDocuments = getCustomAttributeDocuments().entrySet();
        for(Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry:allCustomAttributeDocuments) {
            for (T  documentCustomData : customDataInDocument) {
                if (isMatch(documentCustomData, customAttributeDocumentEntry)) {
                    String groupName = getCustomAttributeDocuments().get(documentCustomData.getCustomAttributeId().toString()).getCustomAttribute().getGroupName();
                    addToGroup(groupName, customAttributeGroups, customAttributeDocumentEntry);
                    break;
                }
            }
        }
        
        /*
         * Go through all the custom data documents and if the document HAS NOT ROUTED and there are new custom data documents available, 
         * add those to the document as well.
         */
        if (documentNotRouted) {
            for(Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry:allCustomAttributeDocuments) {
                List<CustomAttributeDocument> entriesInCurrentGroup = customAttributeGroups.get(customAttributeDocumentEntry.getValue().getCustomAttribute().getGroupName());
                if (entriesInCurrentGroup == null || !alreadyExists(entriesInCurrentGroup, customAttributeDocumentEntry)) {
                    String groupName = customAttributeDocumentEntry.getValue().getCustomAttribute().getGroupName();
                    // create customAttributeDocValues for the new custom data fields.
                    addToCustomDataList(customAttributeDocumentEntry);
                    addToGroup(groupName, customAttributeGroups, customAttributeDocumentEntry);
                }
            }
        }
    }

    protected void addToCustomDataList(Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry) {
        T newCustomData = getNewCustomData();
        newCustomData.setCustomAttribute(customAttributeDocumentEntry.getValue().getCustomAttribute());
        newCustomData.setCustomAttributeId(customAttributeDocumentEntry.getValue().getCustomAttributeId().longValue());
        newCustomData.setValue(customAttributeDocumentEntry.getValue().getCustomAttribute().getDefaultValue());
        getCustomDataList().add(newCustomData);    
    }
    
    protected boolean alreadyExists(List<CustomAttributeDocument> entriesInCurrentGroup, Entry<String, CustomAttributeDocument> customAttributeDocumentEntry) {
        for ( CustomAttributeDocument entry : entriesInCurrentGroup) {
            if (entry.getCustomAttributeId() == Integer.parseInt(customAttributeDocumentEntry.getKey())) {
                return true;
            }
        }
        return false;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addToGroup(String groupName, SortedMap<String, List> customAttributeGroups, Entry<String, CustomAttributeDocument> customAttributeDocument) {
        List<CustomAttributeDocument> customAttributeDocumentList = customAttributeGroups.get(groupName);   
        if (customAttributeDocumentList == null) {
            customAttributeDocumentList = new ArrayList<CustomAttributeDocument>();
            customAttributeGroups.put(groupName, customAttributeDocumentList);
        }
        customAttributeDocumentList.add(customAttributeDocument.getValue());
        Collections.sort(customAttributeDocumentList, new NameComparator() ); 
    }

    protected boolean isMatch(T documentCustomData, Entry<String, CustomAttributeDocument> customAttributeDocumentEntry) {
        return documentCustomData.getCustomAttributeId() == ((long)customAttributeDocumentEntry.getValue().getCustomAttributeId());
    }
    
    /**
     * This method builds the custom data collections used on the form
     * @param customAttributeGroups
     */
    @SuppressWarnings("unchecked")
    public void buildCustomDataCollectionsOnNewDocument(SortedMap<String, List> customAttributeGroups) {
        for(Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry:getCustomAttributeDocuments().entrySet()) {
            String groupName = customAttributeDocumentEntry.getValue().getCustomAttribute().getGroupName();
            
            addToCustomDataList(customAttributeDocumentEntry);
            
            if (StringUtils.isEmpty(groupName)) {
                groupName = "No Group";
            }
            
            List<CustomAttributeDocument> customAttributeDocumentList = customAttributeGroups.get(groupName);
            if (customAttributeDocumentList == null) {
                customAttributeDocumentList = new ArrayList<CustomAttributeDocument>();
                customAttributeGroups.put(groupName, customAttributeDocumentList);
                
            }
            customAttributeDocumentList.add(getCustomAttributeDocuments().get(customAttributeDocumentEntry.getValue().getCustomAttributeId().toString()));
            Collections.sort(customAttributeDocumentList, new NameComparator());
        }
    }
    
    public void prepareCustomData() {
        initializePermissions();
        SortedMap<String, List> customAttributeGroups = new TreeMap<String, List>();
        if(getCustomDataList().size() > 0) {
            buildCustomDataCollectionsOnExistingDocument(customAttributeGroups);
        }else {
            buildCustomDataCollectionsOnNewDocument(customAttributeGroups);
        }
        setCustomAttributeGroups(customAttributeGroups);
    }
    
    protected abstract T getNewCustomData();
    
    public abstract List<T> getCustomDataList();
    
    public abstract Map<String, CustomAttributeDocument> getCustomAttributeDocuments();
    
    
    /**
     * 
     * This method takes in a groupName from the data entry and return a valid string to use in the Map functions later.
     * Note, data entry may create a null group name, which is invalid with the Map funcitons.
     * @param groupName
     * @return
     */
    public String getValidCustomAttributeGroupName(String groupName) {
        return groupName != null ? groupName : "Custom Data Group";
    }
    
    /**
     * Initialize the permissions for viewing/editing the Custom Data web page.
     */
    public void initializePermissions() {
        modifyCustomData = canModifyCustomData();
    }
    
    /**
     * Can the current user modify the custom data values?
     * @return true if can modify the custom data; otherwise false
     */
    public boolean canModifyCustomData() {
        return modifyCustomData;
    }
    
    /**
     * Get the ModifyCustomData value.
     * @return the ModifyCustomData value
     */
    public boolean getModifyCustomData() {
        return modifyCustomData;
    }
    
    /**
     * Sets the customAttributeGroups attribute value.
     * @param customAttributeGroups The customAttributeGroups to set.
     */
    public void setCustomAttributeGroups(SortedMap<String, List> customAttributeGroups) {
        this.customAttributeGroups = customAttributeGroups;
    }

    /**
     * Gets the customAttributeGroups attribute.
     * @return Returns the customAttributeGroups.
     */
    public Map<String, List> getCustomAttributeGroups() {
        return customAttributeGroups;
    }
    
    protected TaskAuthorizationService getTaskAuthorizationService() {
        return KraServiceLocator.getService(TaskAuthorizationService.class);
    }

    /**
     * Get the userName of the user for the current session.
     * @return the current session's userName
     */
    protected String getUserIdentifier() {
         return GlobalVariables.getUserSession().getPrincipalId();
    }

    /**
     * Set the custom attribute content in workflow.
     */
   public void setCustomAttributeContent(String documentNumber, String attributeName) {
       getCustomAttributeService().setCustomAttributeKeyValue(documentNumber, getCustomAttributeDocuments(), attributeName, getUserIdentifier());
   } 
   
   protected CustomAttributeService getCustomAttributeService() {
       return KraServiceLocator.getService(CustomAttributeService.class); 
   }
    
    /**
     * Sorts custom data attributes by label for alphabetical order on custom data panels.
     */
    public class NameComparator implements Comparator
    {    
        public NameComparator(){}
        
        public int compare(Object cad1, Object cad2 )
        {    
            try
            {
                String name1 = ((CustomAttributeDocument)cad1).getCustomAttribute().getName();
                String name2 = ((CustomAttributeDocument)cad2).getCustomAttribute().getName();
                if (name1 == null)
                {
                	name1 = "";
                }
                if (name2 == null)
                {
                	name2 = "";
                }
                return name1.compareTo(name2);  
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }
}
