/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.kim.impl.identity;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kra.kim.api.identity.IdentityService;



/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 * 
 * The methods overridden here are dictated by methods that use the UA custom
 * IdentityService, and the private methods that the former use.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@SuppressWarnings("deprecation")
public class PersonServiceImpl extends org.kuali.rice.kim.impl.identity.PersonServiceImpl {

	private static Logger LOG = Logger.getLogger( PersonServiceImpl.class );
	private String personEntityTypeLookupCriteria = null;
	
	// UA Custom changes
	protected static final String EDS_ACTIVE_STATUS_KEY = "principals.active";
	protected IdentityService identityService;
    
	
	/**
	 * @see org.kuali.rice.kim.api.identity.PersonService#getPerson(java.lang.String)
	 */
	@Override
	public Person getPerson(String principalId) {
		if ( StringUtils.isBlank(principalId) ) {
			return null;
		}

		// get the corresponding principal
		final Principal principal = getIdentityService().getPrincipal( principalId );
		// get the identity
		if ( principal != null ) {
			final EntityDefault entity = getIdentityService().getEntityDefault(principal.getEntityId());
         	// convert the principal and identity to a Person
            // skip if the person was created from the DB cache
            if (entity != null ) {
                return convertEntityToPerson( entity, principal );
            }
		}
		return null;
	}
	
	
	/**
	 * @see org.kuali.rice.kim.api.identity.PersonService#getPersonByPrincipalName(java.lang.String)
	 */
	@Override
	public Person getPersonByPrincipalName(String principalName) {
		if ( StringUtils.isBlank(principalName) ) {
			return null;
		}

		// get the corresponding principal
		final Principal principal = getIdentityService().getPrincipalByPrincipalName( principalName );
		// get the identity
		if ( principal != null ) {
            final EntityDefault entity = getIdentityService().getEntityDefault(principal.getEntityId());

            // convert the principal and identity to a Person
            if ( entity != null ) {
                return convertEntityToPerson( entity, principal );
            }
		}
		return null;
	}


	@Override
	public Person getPersonByEmployeeId(String employeeId) {
		if ( StringUtils.isBlank( employeeId  ) ) {
			return null;
		}

		final List<Person> people = findPeople( Collections.singletonMap(KIMPropertyConstants.Person.EMPLOYEE_ID, employeeId) );
		if ( !people.isEmpty() ) {
			return people.get(0);

		}
		
	    // If no person was found above, check for inactive records
        EntityDefault entity = getIdentityService().getEntityDefaultByEmployeeId(employeeId);
        if (entity != null) {
            if ( !entity.getPrincipals().isEmpty() ) {
                Principal principal = getIdentityService().getPrincipal(entity.getPrincipals().get(0).getPrincipalId());
                if (principal != null) {
                    return convertEntityToPerson( entity, principal );
                }  
            }
        }

		return null;
	}
	

	@Override
	protected List<Person> findPeopleInternal(Map<String,String> criteria, boolean unbounded ) {

		if(criteria.containsKey(KIMPropertyConstants.Person.ACTIVE)){
			// Convert "active" KIM key to "active" EDS key
			String value = criteria.get(KIMPropertyConstants.Person.ACTIVE);
			criteria.remove(KIMPropertyConstants.Person.ACTIVE);
			criteria.put(EDS_ACTIVE_STATUS_KEY, value);
		}		

		List<EntityDefault> entities = ((edu.arizona.kra.kim.api.identity.IdentityService)getIdentityService()).lookupEntityDefault(criteria, unbounded);
		List<Person> people = new ArrayList<Person>();
        if (entities.size() > 0) {
            for ( EntityDefault e : entities ) {
			    // get to get all principals for the identity as well
			    for ( Principal p : e.getPrincipals() ) {
			    	people.add( convertEntityToPerson( e, p ) );
			    }
		    }
        }

        return people;
	}


	@Override
	public Map<String,String> convertPersonPropertiesToEntityProperties( Map<String,String> criteria ) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "convertPersonPropertiesToEntityProperties: " + criteria );
		}
		boolean nameCriteria = false;
		boolean addressCriteria = false;
		boolean affiliationCriteria = false;
		boolean affiliationDefaultOnlyCriteria = false;
		boolean phoneCriteria = false;
		boolean emailCriteria = false;
		boolean employeeIdCriteria = false;
		// add base lookups for all person lookups
		HashMap<String,String> newCriteria = new HashMap<String,String>();
		newCriteria.putAll( baseLookupCriteria );

		newCriteria.put( "entityTypeContactInfos.entityTypeCode", personEntityTypeLookupCriteria );

        if ( criteria != null ) {
			for ( String key : criteria.keySet() ) {
			    //check active radio button
	            if(key.equals(KIMPropertyConstants.Person.ACTIVE)) {
	                newCriteria.put(criteriaConversion.get(KIMPropertyConstants.Person.ACTIVE), criteria.get(KIMPropertyConstants.Person.ACTIVE));
	            } else {
	                // The following if statement enables the "both" button to work correctly.
	                if (!(criteria.containsKey(KIMPropertyConstants.Person.ACTIVE))) {
	                    newCriteria.remove( KIMPropertyConstants.Person.ACTIVE );
	                }
	            }
	            
				// if no value was passed, skip the entry in the Map
				if ( StringUtils.isEmpty( criteria.get(key) ) ) {
					continue;
				}
				// check if the value needs to be encrypted
				// handle encrypted external identifiers
				if ( key.equals( KIMPropertyConstants.Person.EXTERNAL_ID ) && StringUtils.isNotBlank(criteria.get(key)) ) {
					// look for a ext ID type property
					if ( criteria.containsKey( KIMPropertyConstants.Person.EXTERNAL_IDENTIFIER_TYPE_CODE ) ) {
						String extIdTypeCode = criteria.get(KIMPropertyConstants.Person.EXTERNAL_IDENTIFIER_TYPE_CODE);
						if ( StringUtils.isNotBlank(extIdTypeCode) ) {
							// if found, load that external ID Type via service
							EntityExternalIdentifierType extIdType = getIdentityService().getExternalIdentifierType(extIdTypeCode);
							// if that type needs to be encrypted, encrypt the value in the criteria map
							if ( extIdType != null && extIdType.isEncryptionRequired() ) {
								try {
                                    if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
									    criteria.put(key,
										    	CoreApiServiceLocator.getEncryptionService().encrypt(criteria.get(key))
										    	);
                                    }
								} catch (GeneralSecurityException ex) {
									LOG.error("Unable to encrypt value for external ID search of type " + extIdTypeCode, ex );
								}								
							}
						}
					}
				}
				
				// convert the property to the Entity data model
				String entityProperty = criteriaConversion.get( key );
				if ( entityProperty != null ) {
					newCriteria.put( entityProperty, criteria.get( key ) );
				} else {
					entityProperty = key;
					// just pass it through if no translation present
					newCriteria.put( key, criteria.get( key ) );
				}
				// check if additional criteria are needed based on the types of properties specified
				if ( isNameEntityCriteria( entityProperty ) ) {
					nameCriteria = true;
				}
				if ( isAffiliationEntityCriteria( entityProperty ) ) {
					affiliationCriteria = true;
				}
				if ( isAddressEntityCriteria( entityProperty ) ) {
					addressCriteria = true;
				}
				if ( isPhoneEntityCriteria( entityProperty ) ) {
					phoneCriteria = true;
				}
				if ( isEmailEntityCriteria( entityProperty ) ) {
					emailCriteria = true;
				}
				if ( isEmployeeIdEntityCriteria( entityProperty ) ) {
					employeeIdCriteria = true;
				}				
				// special handling for the campus code, since that forces the query to look
				// at the default affiliation record only
				if ( key.equals( "campusCode" ) ) {
					affiliationDefaultOnlyCriteria = true;
				}
			} 
			
			if ( nameCriteria ) {
				newCriteria.put( ENTITY_NAME_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_NAME_PROPERTY_PREFIX + "defaultValue", "Y" );
				//newCriteria.put(ENTITY_NAME_PROPERTY_PREFIX + "nameCode", "PRFR");//so we only display 1 result
			}
			if ( addressCriteria ) {
				newCriteria.put( ENTITY_ADDRESS_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_ADDRESS_PROPERTY_PREFIX + "defaultValue", "Y" );
			}
			if ( phoneCriteria ) {
				newCriteria.put( ENTITY_PHONE_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_PHONE_PROPERTY_PREFIX + "defaultValue", "Y" );
			}
			if ( emailCriteria ) {
				newCriteria.put( ENTITY_EMAIL_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_EMAIL_PROPERTY_PREFIX + "defaultValue", "Y" );
			}
			if ( employeeIdCriteria ) {
				newCriteria.put( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX + "primary", "Y" );
			}
			if ( affiliationCriteria ) {
				newCriteria.put( ENTITY_AFFILIATION_PROPERTY_PREFIX + "active", "Y" );
			}
			if ( affiliationDefaultOnlyCriteria ) {
				newCriteria.put( ENTITY_AFFILIATION_PROPERTY_PREFIX + "defaultValue", "Y" );
			} 
        }   
		
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Converted: " + newCriteria );
		}
		return newCriteria;		
	}


    /**
     * Builds a map containing entries from the passed in Map that do NOT represent properties on an embedded
     * Person object.
     */
    private Map<String,String> getNonPersonSearchCriteria( BusinessObject bo, Map<String,String> fieldValues) {
        Map<String,String> nonUniversalUserSearchCriteria = new HashMap<String,String>();
        for ( String propertyName : fieldValues.keySet() ) {
            if (!isPersonProperty(bo, propertyName)) {
                nonUniversalUserSearchCriteria.put(propertyName, fieldValues.get(propertyName));
            }
        }
        return nonUniversalUserSearchCriteria;
    }


    private boolean isPersonProperty(BusinessObject bo, String propertyName) {
        try {
        	if ( ObjectUtils.isNestedAttribute( propertyName ) // is a nested property
            		&& !StringUtils.contains(propertyName, "add.") ) {// exclude add line properties (due to path parsing problems in PropertyUtils.getPropertyType)
        		Class<?> type = PropertyUtils.getPropertyType(bo, ObjectUtils.getNestedAttributePrefix( propertyName ));
        		// property type indicates a Person object
        		if ( type != null ) {
        			return Person.class.isAssignableFrom(type);
        		}
        		LOG.warn( "Unable to determine type of nested property: " + bo.getClass().getName() + " / " + propertyName );
        	}
        } catch (Exception ex) {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug("Unable to determine if property on " + bo.getClass().getName() + " to a person object: " + propertyName, ex );
        	}
        }
        return false;
    }


    /**
     * @see org.kuali.rice.kim.api.identity.PersonService#resolvePrincipalNamesToPrincipalIds(org.kuali.rice.krad.bo.BusinessObject, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
	public Map<String,String> resolvePrincipalNamesToPrincipalIds(BusinessObject businessObject, Map<String,String> fieldValues) {
    	if ( fieldValues == null ) {
    		return null;
    	}
    	if ( businessObject == null ) {
    		return fieldValues;
    	}
    	StringBuffer resolvedPrincipalIdPropertyName = new StringBuffer();
    	// save off all criteria which are not references to Person properties
    	// leave person properties out so they can be resolved and replaced by this method
        Map<String,String> processedFieldValues = getNonPersonSearchCriteria(businessObject, fieldValues);
        for ( String propertyName : fieldValues.keySet() ) {        	
            if (	!StringUtils.isBlank(fieldValues.get(propertyName))  // property has a value
            		&& isPersonProperty(businessObject, propertyName) // is a property on a Person object
            		) {
            	// strip off the prefix on the property
                String personPropertyName = ObjectUtils.getNestedAttributePrimitive( propertyName );
                // special case - the user ID 
                if ( StringUtils.equals( KIMPropertyConstants.Person.PRINCIPAL_NAME, personPropertyName) ) {
                    @SuppressWarnings("rawtypes")
					Class targetBusinessObjectClass = null;
                    BusinessObject targetBusinessObject = null;
                    resolvedPrincipalIdPropertyName.setLength( 0 ); // clear the buffer without requiring a new object allocation on each iteration
                	// get the property name up until the ".principalName"
                	// this should be a reference to the Person object attached to the BusinessObject                	
                	String personReferenceObjectPropertyName = ObjectUtils.getNestedAttributePrefix( propertyName );
                	// check if the person was nested within another BO under the master BO.  If so, go up one more level
                	// otherwise, use the passed in BO class as the target class
                    if ( ObjectUtils.isNestedAttribute( personReferenceObjectPropertyName ) ) {
                        String targetBusinessObjectPropertyName = ObjectUtils.getNestedAttributePrefix( personReferenceObjectPropertyName );
                        targetBusinessObject = (BusinessObject)ObjectUtils.getPropertyValue( businessObject, targetBusinessObjectPropertyName );
                        if (targetBusinessObject != null) {
                            targetBusinessObjectClass = targetBusinessObject.getClass();
                            resolvedPrincipalIdPropertyName.append(targetBusinessObjectPropertyName).append(".");
                        } else {
                            LOG.error("Could not find target property '"+propertyName+"' in class "+businessObject.getClass().getName()+". Property value was null.");
                        }
                    } else { // not a nested Person property
                        targetBusinessObjectClass = businessObject.getClass();
                        targetBusinessObject = businessObject;
                    }
                    
                    if (targetBusinessObjectClass != null) {
                    	// use the relationship metadata in the KNS to determine the property on the
                    	// host business object to put back into the map now that the principal ID
                    	// (the value stored in application tables) has been resolved
                        String propName = ObjectUtils.getNestedAttributePrimitive( personReferenceObjectPropertyName );
                        DataObjectRelationship rel = getBusinessObjectMetaDataService().getBusinessObjectRelationship( targetBusinessObject, propName );
                        if ( rel != null ) {
                            String sourcePrimitivePropertyName = rel.getParentAttributeForChildAttribute(KIMPropertyConstants.Person.PRINCIPAL_ID);
                            resolvedPrincipalIdPropertyName.append(sourcePrimitivePropertyName);
                        	// get the principal - for translation of the principalName to principalId
                            String principalName = fieldValues.get( propertyName );
                        	Principal principal = getIdentityService().getPrincipalByPrincipalName( principalName );
                            if (principal != null ) {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), principal.getPrincipalId());
                            } else {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                                try {
                                    // if the principalName is bad, then we need to clear out the Person object
                                    // and base principalId property
                                    // so that their values are no longer accidentally used or re-populate
                                    // the object
                                    ObjectUtils.setObjectProperty(targetBusinessObject, resolvedPrincipalIdPropertyName.toString(), null );
                                    ObjectUtils.setObjectProperty(targetBusinessObject, propName, null );
                                    ObjectUtils.setObjectProperty(targetBusinessObject, propName + ".principalName", principalName );
                                } catch ( Exception ex ) {
                                    LOG.error( "Unable to blank out the person object after finding that the person with the given principalName does not exist.", ex );
                                }
                            }
                        } else {
                        	LOG.error( "Missing relationship for " + propName + " on " + targetBusinessObjectClass.getName() );
                        }
                    } else { // no target BO class - the code below probably will not work
                        processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                    }
                }
            // if the property does not seem to match the definition of a Person property but it
            // does end in principalName then...
            // this is to handle the case where the user ID is on an ADD line - a case excluded from isPersonProperty()
            } else if (propertyName.endsWith("." + KIMPropertyConstants.Person.PRINCIPAL_NAME)){
                // if we're adding to a collection and we've got the principalName; let's populate universalUser
                String principalName = fieldValues.get(propertyName);
                if ( StringUtils.isNotEmpty( principalName ) ) {
                    String containerPropertyName = propertyName;
                    if (containerPropertyName.startsWith(KRADConstants.MAINTENANCE_ADD_PREFIX)) {
                        containerPropertyName = StringUtils.substringAfter( propertyName, KRADConstants.MAINTENANCE_ADD_PREFIX );
                    }
                    // get the class of the object that is referenced by the property name
                    // if this is not true then there's a principalName collection or primitive attribute 
                    // directly on the BO on the add line, so we just ignore that since something is wrong here
                    if ( ObjectUtils.isNestedAttribute( containerPropertyName ) ) {
                    	// the first part of the property is the collection name
                        String collectionName = StringUtils.substringBefore( containerPropertyName, "." );
                        // what is the class held by that collection?
                        // JHK: I don't like this.  This assumes that this method is only used by the maintenance
                        // document service.  If that will always be the case, this method should be moved over there.
                        Class<? extends BusinessObject> collectionBusinessObjectClass = getMaintenanceDocumentDictionaryService()
                        		.getCollectionBusinessObjectClass(
                        				getMaintenanceDocumentDictionaryService()
                        						.getDocumentTypeName(businessObject.getClass()), collectionName);
                        if (collectionBusinessObjectClass != null) {
                            // we are adding to a collection; get the relationships for that object; 
                        	// is there one for personUniversalIdentifier?
                            List<DataObjectRelationship> relationships =
                            		getBusinessObjectMetaDataService().getBusinessObjectRelationships( collectionBusinessObjectClass );
                            // JHK: this seems like a hack - looking at all relationships for a BO does not guarantee that we get the right one
                            // JHK: why not inspect the objects like above?  Is it the property path problems because of the .add. portion?
                            for ( DataObjectRelationship rel : relationships ) {
                            	String parentAttribute = rel.getParentAttributeForChildAttribute( KIMPropertyConstants.Person.PRINCIPAL_ID );
                            	if ( parentAttribute == null ) {
                            		continue;
                            	}
                                // there is a relationship for personUserIdentifier; use that to find the universal user
                            	processedFieldValues.remove( propertyName );
                        		String fieldPrefix = StringUtils.substringBeforeLast( StringUtils.substringBeforeLast( propertyName, "." + KIMPropertyConstants.Person.PRINCIPAL_NAME ), "." );
                                String relatedPrincipalIdPropertyName = fieldPrefix + "." + parentAttribute;
                                // KR-683 Special handling for extension objects
                         	 	if(EXTENSION.equals(StringUtils.substringAfterLast(fieldPrefix, ".")) && EXTENSION.equals(StringUtils.substringBefore(parentAttribute, ".")))
                         	 	{
                         	 		relatedPrincipalIdPropertyName = fieldPrefix + "." + StringUtils.substringAfter(parentAttribute, ".");
                         	 	}
                                String currRelatedPersonPrincipalId = processedFieldValues.get(relatedPrincipalIdPropertyName);
                                if ( StringUtils.isBlank( currRelatedPersonPrincipalId ) ) {
                                	Principal principal = getIdentityService().getPrincipalByPrincipalName( principalName );
                                	if ( principal != null ) {
                                		processedFieldValues.put(relatedPrincipalIdPropertyName, principal.getPrincipalId());
                                	} else {
                                		processedFieldValues.put(relatedPrincipalIdPropertyName, null);
                                	}
                                }
                            } // relationship loop
                        } else {
                        	if ( LOG.isDebugEnabled() ) {
                        		LOG.debug( "Unable to determine class for collection referenced as part of property: " + containerPropertyName + " on " + businessObject.getClass().getName() );
                        	}
                        }
                    } else {
                    	if ( LOG.isDebugEnabled() ) {
                    		LOG.debug( "Non-nested property ending with 'principalName': " + containerPropertyName + " on " + businessObject.getClass().getName() );
                    	}
                    }
                }
            }
        }
        return processedFieldValues;
    }
	
	// OTHER METHODS

    /*
     * Injecting this in order to get UA implementation, couldn't cast to UA impl
     * using KimApiServiceLocator.getIdentityService(), since a ClassCastException
     * is thrown.
     * 
     * (non-Javadoc)
     * @see org.kuali.rice.kim.impl.identity.PersonServiceImpl#getIdentityService()
     */
    @Override
	public IdentityService getIdentityService() {
		return identityService;
	}


    public void setIdentityService(IdentityService identityService){
		this.identityService = identityService;
	}

}