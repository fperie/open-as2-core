package org.openas2.lib;

public class MDNException extends OpenAS2Exception {
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public MDNException() {
		this(null, (Throwable)null);		
	}

	public MDNException(String msg) {
		this(msg, null);
	}

	public MDNException(Throwable cause) {
		this(null, cause);
	}
	
	public MDNException(String msg, Throwable cause) {
		super(msg, cause);
	}

	
}
