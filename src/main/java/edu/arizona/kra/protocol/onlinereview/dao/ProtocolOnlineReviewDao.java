package edu.arizona.kra.protocol.onlinereview.dao;

import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;

import java.util.List;
import java.util.Map;
import java.util.Collection;

public interface ProtocolOnlineReviewDao{

    List<ProtocolOnlineReviewBase> getProtocolOnlineReviewsByQuery(QueryByCriteria query);

    Collection<ProtocolOnlineReviewBase> getCustomSearchResults(Map<String, String> fieldValues);
}
