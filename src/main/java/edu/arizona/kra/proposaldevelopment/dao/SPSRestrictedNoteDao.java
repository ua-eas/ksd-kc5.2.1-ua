/*
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kra.proposaldevelopment.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ojb.broker.accesslayer.LookupException;

import edu.arizona.kra.proposaldevelopment.bo.SPSRestrictedNote;

/**
 * SPS Restricted Notes DAO interface.
 * @author nataliac
 */
public interface SPSRestrictedNoteDao {

    /**
     * Searches the SPS Restricted Notes associated with a Proposal Number
     * 
     * @param String proposalNumber
     * @return  A list of SPSRestrictedNote
     * @throws LookupException 
     * @throws SQLException 
     */
    public List<SPSRestrictedNote> getSPSRestrictedNotes(String proposalNumber) throws SQLException, LookupException;


    /**
     * Adds the given SPS Restricted note in the DB
     * 
     * @param SPSRestrictedNote spsRestrictedNote
     * @return SPSRestrictedNote the newly added SPS Restricted Note
     * @throws LookupException 
     * @throws SQLException 
     */
    public SPSRestrictedNote addSPSRestrictedNote(SPSRestrictedNote spsRestrictedNote) throws SQLException, LookupException;

    
    /**
     * Deletes a SPS Restricted note from the DB
     * 
     * @param SPSRestrictedNote spsRestrictedNote to be deleted
     * @return true if note was successfully deleted
     * @throws LookupException 
     * @throws SQLException 
     */
    public boolean deleteSPSRestrictedNote(SPSRestrictedNote spsRestrictedNote) throws SQLException, LookupException;




}