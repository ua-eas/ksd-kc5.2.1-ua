/*
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kra.proposaldevelopment.service;

import java.util.Collection;

import org.kuali.kra.service.KraAuthorizationService;

/**
 * Custom authorization servicefor the Proposal Development Routing Status Dashboard
 * @author nataliac
 */
public interface CustomAuthorizationService extends KraAuthorizationService {

    /**
     * Does the user have the given permission for the DevelpmentProposalDocument specified by the docNumber?
     * @param userId 
     * @param String docNumber
     * @param String permissionName the name of the Permission
     * @return true if the user has permission; otherwise false
     */
    public boolean hasPermissionOnPropDevDocument(String userId, String docNumber, String permissionName);
    
    
    /**
     * Does the user have the given SPS permission for all Proposal Development Routing State Dashboard search results?
     * @param userId
     * @param String permissionName the name of the Permission
     * @return true if the user has permission; otherwise false
     */
    public boolean hasSPSPermission(String userId, String permissionName);


    /**
     * Does the user have the given SPS role?
     * @param userId
     * @param String roleName
     * @return true if the user has the role; otherwise false
     */
    public boolean hasSPSRole(String userId, String roleName);


    /**
     * Finds the userIds for the SPSReviewers Role Members
     * @return Collection<String>
     */
    public Collection<String> getSPSReviewerRoleMembers();


    

}
