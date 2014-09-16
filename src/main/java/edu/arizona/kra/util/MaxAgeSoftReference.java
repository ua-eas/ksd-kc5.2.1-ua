package edu.arizona.kra.util;

import java.lang.ref.SoftReference;

/**
 * An extension to SoftReference that stores an expiration time for the
 * value stored in the SoftReference. If no expiration time is passed in
 * the value will never be cached.
 */
public class MaxAgeSoftReference<T> extends SoftReference<T> {

	private long expires;

	public MaxAgeSoftReference(long expires, T referent) {
		super(referent);
		this.expires = System.currentTimeMillis() + expires * 1000;
	}

	public boolean isValid() {
		return System.currentTimeMillis() < expires;
	}

	@Override
    public T get() {
		return isValid() ? super.get() : null;
	}

}