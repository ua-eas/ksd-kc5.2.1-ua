package edu.arizona.kra.negotiations.dao.ojb;

import edu.arizona.kra.negotiations.dao.NegotiationLookupDao;
import org.kuali.kra.negotiations.bo.Negotiation;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;

import java.util.Collection;
import java.util.Map;

/**
 * Created by nataliac.
 */
public class NegotiationLookupDaoOjb extends LookupDaoOjb implements NegotiationLookupDao {

    @Override
    public Collection<Negotiation> getNegotiationResults(Map<String, String> fieldValues) {
        return null;
    }


}
