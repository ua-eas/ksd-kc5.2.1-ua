package edu.arizona.kra.proposaldevelopment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.krad.util.KRADConstants;

public final class PropDevRoutingStateConstants {
    
    public static final String DOC_NAME_PROPDEV = "ProposalDevelopmentDocument";
    public static final String SQL_LOOKUP =  "select actions.nodeStopDate AS stop_date, rn.NM AS node_name, prop.PROPOSAL_NUMBER AS proposal_number, doc.DOC_HDR_ID AS document_number, prop.TITLE AS proposal_title,"
       +" s.SPONSOR_NAME AS sponsor_name, s.SPONSOR_CODE AS sponsor_code, prop.DEADLINE_DATE AS sponsor_deadline_date, prop.DEADLINE_TIME AS sponsor_deadline_time, pers.FULL_NAME AS principal_investigator, prop.OWNED_BY_UNIT AS lead_unit, u.UNIT_NAME AS lead_unit_name, actions.annot AS annotation"
       +" FROM krew_doc_hdr_t doc, krew_rte_node_t rn, krew_rte_node_instn_t rni, eps_proposal prop, sponsor s, unit u, eps_prop_person pers,"
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
       +" AND pers.PROPOSAL_NUMBER = prop.PROPOSAL_NUMBER AND pers.PROP_PERSON_ROLE_ID='PI'";
    
    
    public static final String COL_STOP_DATE = "stop_date";
    public static final String COL_NODE_NAME = "node_name";
    public static final String COL_ANNOTATION= "annotation";
    public static final String COL_PROPOSAL_NUMBER = "proposal_number";
    public static final String COL_DOCUMENT_NUMBER = "document_number";
    public static final String COL_PROPOSAL_TITLE = "proposal_title";
    public static final String COL_SPONSOR_NAME = "sponsor_name";
    public static final String COL_SPONSOR_CODE = "sponsor_code";
    public static final String COL_SPONSOR_DEADLINE_DATE = "sponsor_deadline_date";
    public static final String COL_SPONSOR_DEADLINE_TIME = "sponsor_deadline_time";
    public static final String COL_PRINCIPAL_INVESTIGATOR = "principal_investigator";
    public static final String COL_LEAD_UNIT = "lead_unit";
    public static final String COL_LEAD_UNIT_NAME = "lead_unit_name";
    
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
        
    
    public static final String DATE_QUERY_PREFIX = "to_date('";
    public static final String DATE_FORMAT_STR = "','MM/DD/YYYY')";
    public static final String END_DATE_FORMAT_STR = "','MM/DD/YYYY HH24:MI')";
    public static final String END_DATE_TIME =" 23:59";
    
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
    
    public static final Integer annotationCriteriaOffset;
    public static final Map<String, String> SEARCH_QUERIES;
    public static final Map<String, String> SEARCH_QUERIES_LIKE;
    static {
        Map<String, String> aMap = new HashMap<String, String>( );
        aMap.put(ROUTE_STOP_NAME, NODE_NAME_CRITERIA);
        aMap.put(PROPOSAL_NUMBER, PROP_NUMBER_CRITERIA);
        aMap.put(DOCUMENT_NUMBER, DOC_NUMBER_CRITERIA);
        aMap.put(SPONSOR_CODE, SPONSOR_CODE_CRITERIA);
        aMap.put(LEAD_UNIT, UNIT_CRITERIA);
        SEARCH_QUERIES = Collections.unmodifiableMap(aMap);
        
        Map<String, String> bMap = new HashMap<String, String>( );
        bMap.put(PROPOSAL_TITLE, PROP_TITLE_CRITERIA);
        bMap.put(SPONSOR_NAME, SPONSOR_NAME_CRITERIA);
        SEARCH_QUERIES_LIKE =  Collections.unmodifiableMap(bMap);
        
        annotationCriteriaOffset = SQL_LOOKUP.lastIndexOf("actn.actn_rqst_cd='A'")+21 ;
    }
}