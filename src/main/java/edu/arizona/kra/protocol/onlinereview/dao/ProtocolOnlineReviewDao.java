package edu.arizona.kra.protocol.onlinereview.dao;

import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;

import java.util.List;
import java.util.Map;

public interface ProtocolOnlineReviewDao{

    List<ProtocolOnlineReviewBase> getCollectionByQuery(ReportQueryByCriteria query);

    List<ProtocolOnlineReviewBase> getProtocolOnlineReviewsByReportQuery(ReportQueryByCriteria query);

    List<ProtocolOnlineReviewBase> getProtocolOnlineReviewsByQuery(QueryByCriteria query);

    List<ProtocolOnlineReviewBase> getCustomSearchResults(Map<String, String> fieldValues);
}
