package org.openas2.lib;

public class CryptoException extends OpenAS2Exception {
    /**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public CryptoException() {
        this(null, (Throwable)null);
    }

    public CryptoException(String msg) {
        this(msg, null);
    }

    public CryptoException(Throwable cause) {
        this(null, cause);
    }
    
    public CryptoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    
}
