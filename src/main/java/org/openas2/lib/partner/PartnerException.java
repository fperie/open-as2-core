package org.openas2.lib.partner;

import org.openas2.lib.OpenAS2Exception;


public class PartnerException extends OpenAS2Exception {

    /**
	 * Version de sérialization.
	 */
	private static final long serialVersionUID = 1L;

	public PartnerException() {
        this((Throwable)null);
    }

    public PartnerException(String msg) {
        this(msg, null);
    }

    public PartnerException(Throwable cause) {
        super(cause);
    }
    
    public PartnerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    

}
