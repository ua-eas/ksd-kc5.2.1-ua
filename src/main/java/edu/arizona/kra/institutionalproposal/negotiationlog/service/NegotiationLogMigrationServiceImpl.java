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

package edu.arizona.kra.institutionalproposal.negotiationlog.service;

import java.util.List;

import org.kuali.kra.negotiations.bo.Negotiation;

import edu.arizona.kra.institutionalproposal.negotiationlog.NegotiationLog;

/**
 * Custom UofA service with the only purpose to perform the Negotiation Log migration to the new 5.2.1 Negotiation object
 * @author nataliac
 */
public class NegotiationLogMigrationServiceImpl implements NegotiationLogMigrationService{
    
    /**
     * Migrates one individual negotiation log and return the resulting Negotiation object
     * @param nlog 
     * @return
     */
    public Negotiation migrateNegotiationLog(NegotiationLog nlog) throws NegotiationMigrationException{
        return null;
        
    }
	
	/**
	 * Migrates all NegotiationLogs that have the specified status - true for opened or false - closed
	 * Returns a list with the Id of the NegotiationLogs that could not be migrated
	 */
	public List<Integer> migrateNegotiationLogs(boolean completeStatus) throws NegotiationMigrationException{
        return null;
	    
	}
	
	
}
