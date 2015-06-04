package org.openas2;

public class WrappedException extends OpenAS2Exception
{
    /**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public WrappedException()
	{
        this((Throwable)null);
    }

	public WrappedException(String msg)
	{
        this(msg, null);
    }

	public WrappedException(Throwable cause)
	{
    	this(null, cause);
    }
    
	public WrappedException(String msg, Throwable cause)
	{
        super(msg, cause);
    }


	public Exception getSource()
	{
        return (Exception) getCause();
    }

	public void rethrow() throws Exception
	{
        throw (Exception) getCause();
    }
}
