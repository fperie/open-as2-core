package org.openas2;

import java.util.HashMap;
import java.util.Map;

import org.openas2.app.OpenAS2Server;
import org.openas2.partner.XMLPartnershipFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 ;


public class OpenAS2Exception extends Exception 
{
    /**
	 * Version de sérialisation.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String SOURCE_MESSAGE = "message";
    public static final String SOURCE_FILE = "file";
    
    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLPartnershipFactory.class);
    
    private Map sources;
	

    public OpenAS2Exception() {
        log(false);
    }

    public OpenAS2Exception(String msg) {
        super(msg);
    }

    public OpenAS2Exception(String msg, Throwable cause) {
        super(msg, cause);
    }

    public OpenAS2Exception(Throwable cause) {
        super(cause);
    }

    public Object getSource(String id) {
        Map sources = getSources();

        return sources.get(id);
    }

    public void setSources(Map sources) {
        this.sources = sources;
    }

    public Map getSources() {
        if (sources == null) {
            sources = new HashMap();
        }

        return sources;
    }

    public void addSource(String id, Object source) {
        Map sources = getSources();
        sources.put(id, source);
    }

    public void terminate() {
        log(true);
    }

    protected void log(boolean terminated) {
    }
}
