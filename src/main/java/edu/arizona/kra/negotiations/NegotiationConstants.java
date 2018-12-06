package edu.arizona.kra.negotiations;

import java.util.HashMap;
import java.util.Map;

/**
 * Negotiation Constants
 * @author nataliac
 */
public final class NegotiationConstants {


    public static final String AWARD_ID = "awardId";
    public static final String AWARD_NUMBER = "awardNumber";
    public static final String MAX_AWARD_ID = "max(awardId)";
    public static final String MAX_SUBAWARD_CODE = "max(subAwardCode)";
    public static final String PROPOSAL_NUMBER = "proposalNumber";

    public static final String MAPPING_NEGOTIATION_MEDUSA_PAGE = "medusa";


    public static final String AWARD_SEQUENCE_STATUS = "awardSequenceStatus";
    public static final String PROPOSAL_SEQUENCE_STATUS = "proposalSequenceStatus";
    public static final String SUBAWARD_SEQUENCE_STATUS = "subawardSequenceStatus";
    public static final String NEGOTIATION_ID = "negotiationId";
    public static final String SUBAWARD_ID = "subAwardId";
    public static final String SUBAWARD_CODE = "subAwardCode";
    public static final String UNASSOCIATED_ID = "negotiationUnassociatedDetailId";
    public static final String NEGOTIATION_AGE = "negotiationAge";
    public static final String ASSOC_PREFIX = "associatedNegotiable";
    public static final String NEGOTIATION_TYPE_ATTR = "negotiationAssociationTypeId";
    public static final String ASSOCIATED_DOC_ID_ATTR = "associatedDocumentId";
    public static final String INVALID_COLUMN_NAME = "NaN";
    public static final String PRINCIPAL_INVESTIGATOR = "projectPersons.fullName";
    public static final String VERSION_HISTORY_STATUS = "versionHistory.statusForOjb";

    public static final String NEGOTIATION_AGE_QUERY="select negotiation_id from (" +
            " select negotiation_id, NEGOTIATION_START_DATE, (case when NEGOTIATION_END_DATE IS NULL then SYSDATE else NEGOTIATION_END_DATE end) AS NEGOTIATION_END_DATE from negotiation )" +
            " where ";

    public static final String NEGOTIATION_AGE_CONDITION = " TRUNC(negotiation_end_date - negotiation_start_date) ";

    //hash maps that contain mapping search params names from negotiation to the corresponding field names in the associated objects
    //if a field does not exist for that associatable, it will contain INVALID_COLUMN_NAME
    public static final Map<String, String> awardTransform;
    public static final Map<String, String> institutionalProposalTransform;
    public static final Map<String, String> proposalLogTransform;
    public static final Map<String, String> unassociatedTransform;
    public static final Map<String, String> subAwardTransform;

    static {
        awardTransform = new HashMap<String, String>();
        awardTransform.put("sponsorName", "sponsor.sponsorName");
        awardTransform.put("piName", "projectPersons.fullName");
        awardTransform.put("negotiableProposalTypeCode", INVALID_COLUMN_NAME);
        awardTransform.put("leadUnitNumber", "unitNumber");
        awardTransform.put("leadUnitName", "leadUnit.unitName");
        awardTransform.put("subAwardRequisitionerId", INVALID_COLUMN_NAME);

        institutionalProposalTransform = new HashMap<String, String>();
        institutionalProposalTransform.put("sponsorName", "sponsor.sponsorName");
        institutionalProposalTransform.put("piName", "projectPersons.fullName");
        institutionalProposalTransform.put("leadUnitNumber", "leadUnit.unitNumber");
        institutionalProposalTransform.put("leadUnitName", "leadUnit.unitName");
        institutionalProposalTransform.put("negotiableProposalTypeCode", "proposalTypeCode");
        institutionalProposalTransform.put("subAwardRequisitionerId", INVALID_COLUMN_NAME);

        proposalLogTransform = new HashMap<String, String>();
        proposalLogTransform.put("sponsorName", "sponsor.sponsorName");
        proposalLogTransform.put("leadUnitNumber", "leadUnit");
        proposalLogTransform.put("leadUnitName", "unit.unitName");
        proposalLogTransform.put("negotiableProposalTypeCode", "proposalTypeCode");
        proposalLogTransform.put("subAwardRequisitionerId", INVALID_COLUMN_NAME);

        subAwardTransform = new HashMap<String, String>();
        subAwardTransform.put("sponsorName", INVALID_COLUMN_NAME);
        subAwardTransform.put("sponsorCode", INVALID_COLUMN_NAME);
        subAwardTransform.put("piName", INVALID_COLUMN_NAME);
        subAwardTransform.put("negotiableProposalTypeCode", INVALID_COLUMN_NAME);
        subAwardTransform.put("leadUnitNumber", "requisitionerUnit");
        subAwardTransform.put("leadUnitName", "unit.unitName");
        subAwardTransform.put("subAwardRequisitionerId", "requisitionerId");

        unassociatedTransform = new HashMap<String, String>();
        unassociatedTransform.put("sponsorName", "sponsor.sponsorName");
        unassociatedTransform.put("piName", "piName");
        unassociatedTransform.put("negotiableProposalTypeCode", INVALID_COLUMN_NAME);
        unassociatedTransform.put("leadUnitName", "leadUnit.unitName");
        unassociatedTransform.put("subAwardRequisitionerId", INVALID_COLUMN_NAME);

    }

}