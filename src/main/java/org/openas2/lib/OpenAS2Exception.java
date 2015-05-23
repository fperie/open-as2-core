package org.openas2.lib;

public class OpenAS2Exception extends Exception {
    /**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public OpenAS2Exception() 
	{
        this(null, (Throwable)null);
    }

    public OpenAS2Exception(String msg) {
        this(msg, null);
    }

    public OpenAS2Exception(Throwable cause) {
    	this(null, cause);
    }

    public OpenAS2Exception(String msg, Throwable cause) {
        super(msg, cause);
    }

}
