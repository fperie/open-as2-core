package org.openas2.processor;

import org.openas2.OpenAS2Exception;

public class UnsupportedException extends OpenAS2Exception {
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedException() {
		this(null);
	}

	public UnsupportedException(String msg) {
		super(msg);
	}
}
