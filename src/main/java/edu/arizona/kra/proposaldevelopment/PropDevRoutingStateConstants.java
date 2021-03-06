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
package edu.arizona.kra.proposaldevelopment;

import org.kuali.rice.krad.util.KRADConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * PropDev Routing State Dashboard Constants
 * @author nataliac
 */
public final class PropDevRoutingStateConstants {
    public static final String DOC_NAME_PROPDEV = "ProposalDevelopmentDocument";
    /* Permissions */   
    public static final String EDIT_ORD_EXPEDITED_PERMISSION = "Edit ORD Expedited";
    public static final String EDIT_SPS_REVIEWER_PERMISSION = "Edit SPS Reviewer";
    public static final String EDIT_SPS_RESTRICTED_NOTES_PERMISSION = "Edit SPS Restricted Notes";
    /* Roles */ 
    public static final String SPS_REVIEWER_ROLE_NAME = "SPS Proposal Reviewer";

    
    public static final String SQL_LOOKUP =  "select actions.nodeStopDate AS stop_date, rn.NM AS node_name,  prop.PROPOSAL_NUMBER AS proposal_number, prop.DEADLINE_TYPE as deadline_type, doc.DOC_HDR_ID AS document_number, persons.prncpl_nm as document_initiator, prop.TITLE AS proposal_title,"
            +" s.SPONSOR_NAME AS sponsor_name, s.SPONSOR_CODE AS sponsor_code, prop.DEADLINE_DATE AS sponsor_deadline_date, prop.DEADLINE_TIME AS sponsor_deadline_time, pers.FULL_NAME AS principal_investigator, prop.OWNED_BY_UNIT AS lead_unit, "
            +" u.UNIT_NAME AS lead_unit_name, actions.annot AS annotation, CASE WHEN tmp.ord_expedited IS NULL THEN 'N' ELSE tmp.ord_expedited END AS ord_expedited, tmp.full_name as sps_reviewer, tmp.person_id as sps_id, tmp.received_time as final_prop_received"
            +" FROM krew_doc_hdr_t doc, krew_rte_node_t rn, krew_rte_node_instn_t rni,  krim_entity_cache_t persons,"
            + " eps_proposal prop LEFT OUTER JOIN (select COALESCE(ordexp.proposal_number, sps.proposal_number, notes.proposal_number) AS proposal_nbr, ord_expedited, full_name, person_id, received_time FROM"
            + " (SELECT * FROM eps_prop_ord_expedited WHERE eps_prop_ord_expedited.cur_ind=1) ordexp FULL OUTER JOIN (SELECT * FROM eps_prop_sps_reviewer WHERE eps_prop_sps_reviewer.cur_ind=1) sps ON ordexp.proposal_number = sps.proposal_number"
            + " FULL OUTER JOIN (SELECT proposal_number, received_time FROM ( SELECT proposal_number, TO_TIMESTAMP(PROPOSAL_RECEIVED_DATE || ' ' ||PROPOSAL_RECEIVED_TIME, 'DD-MON-YY HH:MI AM') as received_time,"
            + "    RANK() OVER (PARTITION BY proposal_number ORDER BY TO_TIMESTAMP(PROPOSAL_RECEIVED_DATE || ' ' ||PROPOSAL_RECEIVED_TIME, 'DD-MON-YY HH:MI AM') DESC) latest_received"
            + "    FROM eps_prop_sps_notes where eps_prop_sps_notes.ACTV_IND='Y'"
            + "  ) where latest_received = 1) notes"
            + " ON notes.proposal_number = coalesce(ordexp.PROPOSAL_NUMBER, sps.proposal_number)) tmp"
            + " ON prop.proposal_number = tmp.proposal_nbr, sponsor s, unit u, eps_prop_person pers,"
            +" ( SELECT actnRteNodeInstanceId, annot, nodeStopDate, adHocIndicator, Row_Number() OVER (PARTITION BY actnRteNodeInstanceId ORDER BY adHocIndicator, nodeStopDate) AS ord"
            +"   FROM ( SELECT actn.RTE_NODE_INSTN_ID actnRteNodeInstanceId, actn.ACTN_RQST_ANNOTN_TXT annot, actn.CRTE_DT nodeStopDate," 
            +"            (CASE WHEN actn.ACTN_RQST_ANNOTN_TXT LIKE 'Ad Hoc%' THEN 10 ELSE 1 END) AS adHocIndicator"
            +"            FROM krew_actn_rqst_t actn"
            +"            WHERE actn.CUR_IND=1 AND actn.STAT_CD in ('A', 'I') AND actn.actn_rqst_cd='A'"
            +"          ) a"
            +"   ) actions"
            +" WHERE doc.DOC_HDR_ID=rni.DOC_HDR_ID AND doc.DOC_HDR_STAT_CD='R'"
            +" AND doc.doc_typ_id IN (SELECT doc_typ_id FROM krew_doc_typ_t WHERE doc_typ_nm='"+DOC_NAME_PROPDEV+"' AND actv_ind=1)"
            +" AND prop.DOCUMENT_NUMBER = doc.DOC_HDR_ID AND rni.doc_hdr_id=doc.doc_hdr_id AND actions.ord = 1"
            +" AND rni.RTE_NODE_INSTN_ID=actions.actnRteNodeInstanceId AND rn.rte_node_id = rni.RTE_NODE_ID"
            +" AND s.SPONSOR_CODE = prop.SPONSOR_CODE"
            +" AND u.UNIT_NUMBER = prop.OWNED_BY_UNIT"
            +" AND doc.INITR_PRNCPL_ID = persons.PRNCPL_ID"
            +" AND pers.PROPOSAL_NUMBER = prop.PROPOSAL_NUMBER AND pers.PROP_PERSON_ROLE_ID='PI'";

    public static final String ORD_EXPEDITED_QUERY = "select ord_expedited FROM eps_prop_ord_expedited WHERE cur_ind=1 AND proposal_number=?";
    public static final String SPS_REVIEWER_QUERY = "select person_id AS sps_id FROM eps_prop_sps_reviewer WHERE cur_ind=1 AND proposal_number=?";
    public static final String SPS_REVIEWERS_QUERY = "select prncpl_id AS sps_id, (COALESCE(first_nm, '')||' '||COALESCE(middle_nm, '')||' '||COALESCE(last_nm, '')) AS sps_reviewer FROM krim_entity_cache_t WHERE prncpl_id IN (";

    public static final String WORKFLOW_UNITS_PROPOSALS_QUERY = "SELECT DISTINCT proposal_number FROM ("+
            " SELECT DISTINCT e.PROPOSAL_NUMBER FROM EPS_PROP_PERSON_UNITS e WHERE e.UNIT_NUMBER IN ";
    public static final String WORKFLOW_UNITS_PROPOSALS_QUERY_CONT = "UNION SELECT distinct p.PROPOSAL_NUMBER FROM EPS_PROP_COST_SHARING ecs"+
            " JOIN BUDGET b ON b.BUDGET_ID = ecs.BUDGET_ID "+
            " JOIN BUDGET_DOCUMENT bd ON bd.DOCUMENT_NUMBER=b.DOCUMENT_NUMBER "+
            " JOIN EPS_PROPOSAL_DOCUMENT epd on bd.PARENT_DOCUMENT_KEY= epd.DOCUMENT_NUMBER "+
            " JOIN EPS_PROPOSAL p on p.DOCUMENT_NUMBER = epd.DOCUMENT_NUMBER "+
            " WHERE bd.PARENT_DOCUMENT_TYPE_CODE='PRDV' AND b.FINAL_VERSION_FLAG='Y' AND p.STATUS_CODE in (1,2,5,12) " +
            " AND ecs.SOURCE_UNIT IS NOT NULL and ecs.SOURCE_UNIT IN ";
    public static final String WORKFLOW_UNITS_PROPOSALS_QUERY_FIN = ")";

    public static final String WORKFLOW_UNIT_HIERARCHY_QUERY="SELECT DISTINCT unit_number FROM (" +
            " SELECT unit_number FROM unit u WHERE u.ACTIVE_FLAG='Y' START WITH u.UNIT_NUMBER=? CONNECT BY PRIOR u.UNIT_NUMBER = u.PARENT_UNIT_NUMBER " +
            " UNION " +
            " SELECT unit_number FROM unit u WHERE ACTIVE_FLAG='Y' START WITH u.UNIT_NUMBER=? CONNECT BY u.UNIT_NUMBER = PRIOR u.PARENT_UNIT_NUMBER)";


    public static final String CUR_IND_UPDATE_STMT = "UPDATE $tablename SET cur_ind=0 where proposal_number=? and cur_ind=1";
    public static final String ORD_EXP_TABLE_NAME = "eps_prop_ord_expedited";
    public static final String SPS_REV_TABLE_NAME = "eps_prop_sps_reviewer"; 

    public static final String ADD_ORD_EXPEDITED_QUERY = "INSERT INTO eps_prop_ord_expedited (ID, PROPOSAL_NUMBER, ORD_EXPEDITED, CUR_IND, OBJ_ID, UPDATE_TIMESTAMP, UPDATE_USER)"
            +" VALUES(eps_prop_ord_expedited_seq.NEXTVAL, ?, ?, 1, SYS_GUID(), CURRENT_TIMESTAMP, ?)";
    public static final String ADD_SPS_REVIEWER_QUERY = "INSERT INTO eps_prop_sps_reviewer (ID, PROPOSAL_NUMBER, PERSON_ID, FULL_NAME, CUR_IND, OBJ_ID, UPDATE_TIMESTAMP, UPDATE_USER)"
            +" VALUES(eps_prop_sps_reviewer_seq.NEXTVAL, ?, ?, ?, 1, SYS_GUID(), CURRENT_TIMESTAMP, ?)";


    public static final String COL_STOP_DATE = "stop_date";
    public static final String COL_NODE_NAME = "node_name";
    public static final String COL_ANNOTATION= "annotation";
    public static final String COL_PROPOSAL_NUMBER = "proposal_number";
    public static final String COL_DOCUMENT_NUMBER = "document_number";
    public static final String COL_DOC_INITIATOR = "document_initiator";
    public static final String COL_PROPOSAL_TITLE = "proposal_title";
    public static final String COL_SPONSOR_NAME = "sponsor_name";
    public static final String COL_SPONSOR_CODE = "sponsor_code";
    public static final String COL_SPONSOR_DEADLINE_DATE = "sponsor_deadline_date";
    public static final String COL_SPONSOR_DEADLINE_TIME = "sponsor_deadline_time";
    public static final String COL_DEADLINE_TYPE = "deadline_type";
    public static final String COL_PRINCIPAL_INVESTIGATOR = "principal_investigator";
    public static final String COL_LEAD_UNIT = "lead_unit";
    public static final String COL_LEAD_UNIT_NAME = "lead_unit_name";
    public static final String COL_ORD_EXP = "ord_expedited";
    public static final String COL_FPR = "final_prop_received";
    public static final String COL_SPS_REVIEWER_NAME = "sps_reviewer";
    public static final String COL_SPS_REVIEWER_ID = "sps_id";

    public static final String ROUTE_STOP_NAME = "routeStopName";
    public static final String ROUTE_UNIT_NBR = "nodeStopLeadUnit.unitNumber";
    public static final String ROUTE_STOP_DATE = "routeStopDate";
    public static final String ROUTE_STOP_DATE_AFTER = KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ROUTE_STOP_DATE;
    public static final String PROPOSAL_NUMBER = "developmentProposal.proposalNumber";
    public static final String DOCUMENT_NUMBER = "proposalDocument.documentNumber";
    public static final String PROPOSAL_TITLE = "proposalTitle";
    public static final String SPONSOR_NAME = "sponsor.sponsorName";
    public static final String SPONSOR_CODE = "sponsor.sponsorCode";
    public static final String SPONSOR_DATE = "sponsorDeadlineDate";
    public static final String SPONSOR_DATE_AFTER = KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + SPONSOR_DATE;
    public static final String PROPOSAL_PERSON_NAME = "proposalPersonName";
    public static final String LEAD_UNIT = "leadUnit.unitNumber";
    public static final String LEAD_COLLEGE = "leadUnit.parentUnitNumber";
    public static final String WORKFLOW_UNIT = "workflowUnit.unitNumber";
    public static final String DEADLINE_TYPE = "deadlineType";
    public static final String INITIATOR = "initiatorPrincipalName";

    public static final String NODE_NAME_CRITERIA = " AND rn.NM = '";
    public static final String SPONSOR_NAME_CRITERIA = " AND lower(s.SPONSOR_NAME) LIKE '%";
    public static final String SPONSOR_CODE_CRITERIA = " AND s.SPONSOR_CODE = '";
    public static final String UNIT_CRITERIA = " AND u.UNIT_NUMBER = '";
    public static final String PROP_NUMBER_CRITERIA = " AND prop.PROPOSAL_NUMBER = '";
    public static final String DOC_NUMBER_CRITERIA = " AND doc.DOC_HDR_ID = '";
    public static final String PROP_TITLE_CRITERIA = " AND lower(prop.TITLE) LIKE '%";
    public static final String SPONSOR_DATE_CRITERIA  = " AND prop.DEADLINE_DATE ";
    public static final String ROUTE_STOP_DATE_CRITERIA = " AND actions.nodeStopDate ";
    public static final String LEAD_UNIT_ANNOT_CRITERIA = " AND actn.ACTN_RQST_ANNOTN_TXT LIKE '";
    public static final String PROPOSAL_PERSON_NAME_CRITERIA = " AND prop.proposal_number in (select prop.proposal_number from eps_proposal prop,eps_prop_person pers WHERE prop.proposal_number=pers.proposal_number AND lower(pers.FULL_NAME) LIKE '%";  
    public static final String LEAD_COLLEGE_CRITERIA = " AND prop.owned_by_unit IN (SELECT unit_number FROM unit u START WITH u.UNIT_NUMBER='";
    public static final String LEAD_COLLEGE_CRITERIA_CONT = "' CONNECT BY PRIOR u.UNIT_NUMBER = u.PARENT_UNIT_NUMBER)";
    public static final String ORDER_CRITERIA = " ORDER BY stop_date";
    public static final String WORKFLOW_UNITS_CRITERIA = " AND prop.owned_by_unit IN (";
    public static final String WORKFLOW_UNITS_CRITERIA_CONT = ") ";
    public static final String DEADLINE_TYPE_CRITERIA = " AND prop.DEADLINE_TYPE='";
    public static final String INITIATOR_CRITERIA = " AND persons.prncpl_nm='";

    public static final String DATE_QUERY_PREFIX = "to_date('";
    public static final String DATE_FORMAT_STR = "','MM/DD/RRRR')";
    public static final String END_DATE_FORMAT_STR = "','HH24:MI MM/DD/RRRR')";
    public static final String END_DATE_TIME ="23:59 ";

    public static final String NODE_NAME_ADHOC = "Ad Hoc";
    public static final String NODE_NAME_ALL = "All";
    public static final String NODE_NAME_SPS_APPROVE = "SPS Approve";
    public static final String NODE_NAME_PI = "PI/Co-PI";
    public static final String NODE_NAME_LEAD_UNIT = "Lead Unit";
    public static final String NODE_NAME_COLLEGE = "Department/College";
    public static final String NODE_NAME_INITIATED = "Returned to Initiator";

    public static final String NODE_KEY_SPS_APPROVE = "OSPOfficeRouting";
    public static final String NODE_KEY_PI = "ProposalPersons";
    public static final String NODE_KEY_LEAD_UNIT = "LeadUnitPreApproval";
    public static final String NODE_KEY_HIERARCHY = "Hierarchy Request";
    public static final String NODE_KEY_INITIATED = "Initiated";

    public static final String DEADLINE_TYPE_NONE_LABEL = "None";
    public static final String DEADLINE_TYPE_POSTMARK_LABEL = "Postmark";
    public static final String DEADLINE_TYPE_RECEIPT_LABEL = "Receipt";
    public static final String DEADLINE_TYPE_TARGET_LABEL = "Target";
    public static final String DEADLINE_TYPE_POSTMARK = "P";
    public static final String DEADLINE_TYPE_RECEIPT = "R";
    public static final String DEADLINE_TYPE_TARGET = "T";

    public static final String YES = "Y";
    public static final String NO = "N";

    public static final Integer annotationCriteriaOffset;
    public static final Map<String, String> SEARCH_QUERIES;
    public static final Map<String, String> SEARCH_QUERIES_LIKE;
    
    public static final String PROP_NUMBER = "proposalNumber";
    public static final String CREATED_DATE = "createdDate";

    public static final String ACTIVE = "active";
    
    static {
        //this maps the user search criteria to the appropriate sql criteria to add to the main query
        Map<String, String> aMap = new HashMap<String, String>( );
        aMap.put(ROUTE_STOP_NAME, NODE_NAME_CRITERIA);
        aMap.put(PROPOSAL_NUMBER, PROP_NUMBER_CRITERIA);
        aMap.put(DOCUMENT_NUMBER, DOC_NUMBER_CRITERIA);
        aMap.put(SPONSOR_CODE, SPONSOR_CODE_CRITERIA);
        aMap.put(LEAD_UNIT, UNIT_CRITERIA);
        aMap.put(DEADLINE_TYPE, DEADLINE_TYPE_CRITERIA);
        aMap.put(INITIATOR, INITIATOR_CRITERIA);
        SEARCH_QUERIES = Collections.unmodifiableMap(aMap);

        //this maps the user search criteria that translate to a LIKE % in the main query
        Map<String, String> bMap = new HashMap<String, String>( );
        bMap.put(PROPOSAL_TITLE, PROP_TITLE_CRITERIA);
        bMap.put(SPONSOR_NAME, SPONSOR_NAME_CRITERIA);
        SEARCH_QUERIES_LIKE =  Collections.unmodifiableMap(bMap);

        annotationCriteriaOffset = SQL_LOOKUP.lastIndexOf("actn.actn_rqst_cd='A'")+21 ;
    }
    
}