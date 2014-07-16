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
package edu.arizona.kra.kim.api.identity;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;

/**
 * This service provides operations to query for principal and identity data.
 * 
 * <p>A principal represents an identity that can authenticate.  In essence, a principal can be
 * thought of as an "account" or as an identity's authentication credentials.  A principal has
 * an id which is used to uniquely identify it.  It also has a name which represents the
 * principal's username and is typically what is entered when authenticating.  All principals
 * are associated with one and only one identity.
 * 
 * <p>An identity represents a person or system.  Additionally, other "types" of entities can
 * be defined in KIM.  Information like name, phone number, etc. is associated with an identity.
 * It is the representation of a concrete person or system.  While an identity will typically
 * have a single principal associated with it, it is possible for an identity to have more than
 * one principal or even no principals at all (in the case where the identity does not actually
 * authenticate).
 * 
 * <p>This service also provides operations for querying various pieces of reference data, such as 
 * address types, affiliation types, phone types, etc.
 *
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@SuppressWarnings("restriction")
@WebService(name = "identityService", targetNamespace = KimConstants.Namespaces.KIM_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface IdentityService extends org.kuali.rice.kim.api.identity.IdentityService {

    @WebMethod(operationName = "lookupEntityDefault")
    @WebResult(name = "results")
    List<EntityDefault> lookupEntityDefault(Map<String,String> searchCriteria, boolean unbounded);
}
