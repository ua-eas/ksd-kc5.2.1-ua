package edu.arizona.kra.irb;

/**
 * Created by nataliac on 1/3/18.
 */
public final class ProtocolConstants {
    public static final String PROTOCOL_MASK_STRING = "**********";
    public static final String IRB_ADMINISTRATOR_ROLE_NAME = "IRB Administrator";
    public static final String UA_IRB_PROTOCOL_VIEWER_ROLE_NAME = "UA IRB Protocol Viewer";
    public static final String IRB_ROLE_NAMESPACE = "KC-UNT";

    public static final String ROLE_PRINCIPAL_INVESTIGATOR = "PI";
    public static final String ROLE_CO_INVESTIGATOR = "COI";
    public static final String ROLE_STUDY_PERSONNEL = "SP";
    public static final String ROLE_CORRESPONDENT_ADMINISTRATOR = "CA";
    public static final String ROLE_CORRESPONDENT_CRC = "CRC";

    public static final String ROLE_QUALIFIER_PROTOCOL = "protocol";
    public static final String ROLE_QUALIFIER_UNIT_NUMBER = "unitNumber";
    public static final String ROLE_QUALIFIER_SUBUNITS = "subunits";
    public static final String ROLE_QUALIFIER_DESCENDS_HIERARCHY = "Y";

    public static final String PROTOCOL_NUMBER_COL_NAME = "protocolNumber";
    public static final String PROTOCOL_TITLE_COL_NAME = "title";
    public static final String PROTOCOL_DESCR_COL_NAME = "description";

    public static final String PROTOCOL_NUMBER_COL_TITLE = "Protocol Number";

    public static final String PROTOCOL_STATUS_ACTIVE_OPEN_TO_ENROLLMENT="200";
    public static final String PROTOCOL_STATUS_ACTIVE_CLOSED_TO_ENROLLMENT="201";
    public static final String PROTOCOL_STATUS_ACTIVE_DATA_ANALYSIS_ONLY="202";
    public static final String PROTOCOL_STATUS_EXEMPT="203";
    public static final String PROTOCOL_STATUS_PENDING_IN_PROGRESS="100";
    public static final String PROTOCOL_STATUS_SUBMITTED="101";
    public static final String PROTOCOL_STATUS_MINOR_REVISIONS_REQUIRED="102";
    public static final String PROTOCOL_STATUS_DEFERRED="103";
    public static final String PROTOCOL_STATUS_REVISIONS_REQUIRED="104";
    public static final String PROTOCOL_STATUS_AMENDMENT_PROGRESS="105";
    public static final String PROTOCOL_STATUS_RENEWAL_PROGRESS="106";

    public static final String PROTOCOL_ACTION_REVISIONS_REQUIRED = "202";
    public static final String PROTOCOL_ACTION_MINOR_REVISIONS_REQUIRED = "203";

    public static final String PROTOCOL_LEAD_UNIT_FLAG = "leadUnitFlag";
    public static final String PROTOCOL_UNIT_NUMBER = "unitNumber";
    public static final String PROTOCOL_NUMBER = "protocolNumber";
    public static final String PROTOCOL_DOC_NUMBER = "documentNumber";

    public static final String PROTOCOL_NUMBERS_FOR_PERSONNEL_QUERY = "select unique protocol_number from protocol_persons where PERSON_ID=?";
    public static final String ROLE_WITHOUT_QUALIFIERS_FOR_MEMBER_QUERY_PART1 =
            "select r.role_id from KRIM_ROLE_MBR_T r left join KRIM_ROLE_MBR_ATTR_DATA_T a on r.ROLE_MBR_ID = a.ROLE_MBR_ID "+
            " where r.mbr_id=? and ROLE_ID in (";
    public static final String ROLE_WITHOUT_QUALIFIERS_FOR_MEMBER_QUERY_PART2 = ") and (r.actv_to_dt IS NULL OR sysdate <= r.actv_to_dt) and a.ROLE_MBR_ID IS NULL";

    public static final String ROLE_QUALIFIERS_FOR_MEMBER_AND_ATTRIBUTE_QUERY_PART1 = " select r.role_mbr_id, a.attr_val from KRIM_ROLE_MBR_T r left join KRIM_ROLE_MBR_ATTR_DATA_T a on "+
            "r.ROLE_MBR_ID = a.ROLE_MBR_ID where r.ROLE_ID in (";

    public static final String ROLE_QUALIFIERS_FOR_MEMBER_AND_ATTRIBUTE_QUERY_PART2 = ") and (r.actv_to_dt IS NULL OR sysdate <= r.actv_to_dt) "+
            "and r.MBR_ID=? and a.KIM_ATTR_DEFN_ID in ("+
            "select kim_attr_defn_id from krim_attr_defn_t where ACTV_IND='Y' and NM=?)" ;

    public static final String ROLE_QUALIFIERS_FOR_ROOT_UNIT_QUERY_PART1= "select r.role_mbr_id, r.MBR_ID, r.ROLE_ID, a.attr_val, b.attr_val from KRIM_ROLE_MBR_T r inner join KRIM_ROLE_MBR_ATTR_DATA_T a on r.ROLE_MBR_ID = a.ROLE_MBR_ID inner join KRIM_ROLE_MBR_ATTR_DATA_T b on a.role_mbr_id = b.role_mbr_id "
    +" where r.ROLE_ID in (";
    public static final String ROLE_QUALIFIERS_FOR_ROOT_UNIT_QUERY_PART2 = ") and (r.actv_to_dt IS NULL OR sysdate <= r.actv_to_dt) and r.MBR_ID=? "
    +" and a.KIM_ATTR_DEFN_ID in (select KIM_ATTR_DEFN_ID from krim_attr_defn_t where ACTV_IND='Y' and NM='unitNumber') and a.ATTR_VAL='000001' "
    +" and b.KIM_ATTR_DEFN_ID in (select KIM_ATTR_DEFN_ID from krim_attr_defn_t where ACTV_IND='Y' and NM='subunits') and b.ATTR_VAL='Y'";


    public static final String PROTOCOL_NUMBER_WITH_LEAD_UNIT_QUERY = "select unique protocol_number from protocol_units where LEAD_UNIT_FLAG='Y' and UNIT_NUMBER in (";


}
