package edu.arizona.kra.irb.actions.reviewcomments;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.arizona.kra.util.MaxAgeSoftReference;

public class CustomReviewCommentsServiceImpl extends org.kuali.kra.irb.actions.reviewcomments.ReviewCommentsServiceImpl {
	
	private static final int IS_ADMIN_CACHE_TIME = 300;
	private static ConcurrentMap<String, MaxAgeSoftReference<Boolean>> isAdministratorCache 
		= new ConcurrentHashMap<String, MaxAgeSoftReference<Boolean>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kuali.kra.protocol.actions.reviewcomments.ReviewCommentsServiceImplBase
	 * #isAdministrator(java.lang.String) Overriding isAdministrator in
	 * ReviewCommentsServiceImplBase to cache the value for 300 seconds for each
	 * principalId. We found that this method was called several thousand times
	 * while opening and viewing a protocol. It is probably safe to assume that
	 * if a principal is an IRB Administrator right now, that they probably will
	 * still be an IRB Administrator in 5 minutes.
	 */
	@Override
	protected boolean isAdministrator(String principalId) {
		Boolean isAdministratorVal = null;
		isAdministratorVal = getCachedIsAdministrator(principalId);
		
		if ( isAdministratorVal == null ) {
			
			isAdministratorVal = (Boolean)super.isAdministrator(principalId);
			
			if ( isAdministratorVal != null ) {
				cacheIsAdministratorValue(principalId, isAdministratorVal);
			}
		}
		
		return isAdministratorVal.booleanValue();

	}
	
	protected void cacheIsAdministratorValue(String cacheKey, Boolean isAdministratorVal) {
		isAdministratorCache.put(cacheKey, new MaxAgeSoftReference<Boolean>(IS_ADMIN_CACHE_TIME, isAdministratorVal));
	}

	protected Boolean getCachedIsAdministrator( String cacheKey ) {
		Boolean returnValue = null;
		MaxAgeSoftReference<Boolean> reference = isAdministratorCache.get(cacheKey);
		
		if ( reference != null && reference.isValid() ) {
			returnValue = reference.get();
		}
		
		return returnValue;
	}

}
