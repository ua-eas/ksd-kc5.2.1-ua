package edu.arizona.kra.irb.onlinereview.authorization;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kuali.kra.irb.onlinereview.authorization.ProtocolOnlineReviewDocumentAuthorizer;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kra.util.MaxAgeSoftReference;

public class CustomProtocolOnlineReviewDocumentAuthorizer extends
		ProtocolOnlineReviewDocumentAuthorizer {

	private static final String CAN_EDIT_METHOD = "canEdit";
	private static final String CAN_SAVE_METHOD = "canSave";
	private static final int AUTHORIZER_CACHE_TIME = 300;
	private static ConcurrentMap<String, MaxAgeSoftReference<Boolean>> authorizerCache 
		= new ConcurrentHashMap<String, MaxAgeSoftReference<Boolean>>();
	
	/* (non-Javadoc)
	 * @see org.kuali.kra.irb.onlinereview.authorization.ProtocolOnlineReviewDocumentAuthorizer#canEdit(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person)
	 */
	@Override
	public boolean canEdit(Document document, Person user) {
		Boolean userCanEdit = null;
		String cacheKey = getCacheKey( CAN_EDIT_METHOD, document, user );
		
		userCanEdit = getCachedValue(cacheKey);
		
		if ( userCanEdit == null ) {
			
			userCanEdit = (Boolean)super.canEdit( document, user );
			
			if ( userCanEdit != null ) {
				cacheValue(cacheKey, userCanEdit);
			}
		}
		
		return userCanEdit.booleanValue();
	}

	/* (non-Javadoc)
	 * @see org.kuali.kra.irb.onlinereview.authorization.ProtocolOnlineReviewDocumentAuthorizer#canSave(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person)
	 */
	@Override
	public boolean canSave(Document document, Person user) {
		Boolean userCanEdit = null;
		String cacheKey = getCacheKey( CAN_SAVE_METHOD, document, user );
		
		userCanEdit = getCachedValue(cacheKey);
		
		if ( userCanEdit == null ) {
			
			userCanEdit = (Boolean)super.canEdit( document, user );
			
			if ( userCanEdit != null ) {
				cacheValue(cacheKey, userCanEdit);
			}
		}
		
		return userCanEdit.booleanValue();
	}
	
	protected String getCacheKey( String methodName, Document document, Person user ) {
		StringBuilder cacheKey = new StringBuilder();
		
		cacheKey.append(methodName);
		cacheKey.append(".");
		cacheKey.append(document.getDocumentNumber());
		cacheKey.append(".");
		cacheKey.append(user.getPrincipalId());
		
		return cacheKey.toString();
	}
	
	protected void cacheValue(String cacheKey, Boolean authorization) {
		authorizerCache.put( cacheKey, new MaxAgeSoftReference<Boolean>( AUTHORIZER_CACHE_TIME, authorization ) );
	}

	protected Boolean getCachedValue( String cacheKey ) {
		Boolean returnValue = null;
		MaxAgeSoftReference<Boolean> reference = authorizerCache.get(cacheKey);
		
		if ( reference != null && reference.isValid() ) {
			returnValue = reference.get();
		}
		
		return returnValue;
	}


		
}
