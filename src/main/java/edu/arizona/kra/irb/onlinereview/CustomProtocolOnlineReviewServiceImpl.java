package edu.arizona.kra.irb.onlinereview;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewService;
import org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewServiceImpl;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolOnlineReviewDocumentBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionBase;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nataliac on 9/15/17.
 */
public class CustomProtocolOnlineReviewServiceImpl extends ProtocolOnlineReviewServiceImpl implements ProtocolOnlineReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomProtocolOnlineReviewServiceImpl.class);

    public boolean isProtocolInStateToBeReviewed(ProtocolBase protocol) {
        boolean isReviewable = false;
        ProtocolSubmission submission = ((Protocol)protocol).getProtocolSubmission();
        if(submission != null) {
            try {
                isReviewable = StringUtils.isNotEmpty(submission.getScheduleId()) || "2".equals(submission.getProtocolReviewTypeCode()) || "7".equalsIgnoreCase(submission.getProtocolReviewTypeCode()) && "112".equalsIgnoreCase(submission.getProtocolSubmissionType().getSubmissionTypeCode());
                isReviewable &= StringUtils.equals(submission.getSubmissionStatusCode(), "100") || StringUtils.equals(submission.getSubmissionStatusCode(), "101");
                if ( isReviewable ) {
                    LOG.debug("isProtocolInStateToBeReviewed: checking with Workflow Service if protocol isReviewable");
                    ProtocolDocument protocolDocument = (ProtocolDocument) protocol.getProtocolDocument();
                     //protocolDocument = (ProtocolDocument) this.documentService.getByDocumentHeaderId(protocol.getProtocolDocument().getDocumentNumber());

                    isReviewable &= this.getKraWorkflowService().isCurrentNode(protocolDocument, "IRBReview");
                }
            } catch (Exception var6) {
                String errorString = String.format("WorkflowException checking route node for creating new ProtocolOnlineReviewDocument for protocol %s", new Object[]{submission.getProtocolNumber()});
                LOG.error(errorString, var6);
                throw new RuntimeException(errorString, var6);
            }
        }

        return isReviewable;
    }

    /**
     * @see org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewService#getProtocolReviewDocumentsForCurrentSubmission(org.kuali.kra.protocol.ProtocolBase)
     */
    public List<ProtocolOnlineReviewDocumentBase> getProtocolReviewDocumentsForCurrentSubmission(ProtocolBase protocol) {
        LOG.debug("getProtocolReviewDocumentsForCurrentSubmission protocol number="+protocol.getProtocolNumber()+" protocolId="+protocol.getProtocolId()+" submission="+protocol.getProtocolSubmission().getSubmissionId());
        List<ProtocolOnlineReviewDocumentBase> onlineReviewDocuments = new ArrayList<ProtocolOnlineReviewDocumentBase>();
        ProtocolSubmissionBase submission = protocol.getProtocolSubmission();
        List<ProtocolOnlineReviewBase> reviews = findProtocolOnlineReviews(protocol.getProtocolId(), submission.getSubmissionId());
        LOG.debug("AFTER findProtocolOnlineReviews reviews="+reviews.toString());
        for (ProtocolOnlineReviewBase review : reviews) {
            if (review.isActive()) {
                review.refresh();
                try {
                    onlineReviewDocuments.add((ProtocolOnlineReviewDocumentBase)(documentService.getByDocumentHeaderId( review.getProtocolOnlineReviewDocument().getDocumentNumber() )));
                }
                catch (WorkflowException e) {
                    throw new RuntimeException( String.format( "Could not load ProtocolOnlineReviewBase docuemnt %s due to WorkflowException: %s", review.getProtocolOnlineReviewDocument().getDocumentNumber(), e.getMessage() ),e);
                }
            }
        }
        return onlineReviewDocuments;
    }



}
