package org.openas2.util;

import org.openas2.WrappedException;

/**  
 * Emulates StringTokenizer.
 * 
 * @author joseph mcverry
 */
public class CommandTokenizer
{
	String workString;

	int pos = 0;
	int len = -1;
	
	/**
	 * Constructor
	 * @param inString work string.
	 */
	public CommandTokenizer(String inString)
	{
		workString = inString;
		len = workString.length();
	}

	/**
	 * any more tokens in String
	 * @return true if there are any more tokens 
	 * @throws WrappedException exception occured during the execution method.
	 */
	public boolean hasMoreTokens() throws WrappedException
	{
		try
		{
			while (pos < len - 1 && workString.charAt(pos) == ' ')
			{
				pos++;
			}

			if (pos < len)
			{
				return true;
			}

			return false;
		}
		catch (Exception e)
		{
			throw new WrappedException(e);
		}
	}

	/**
	 * returns the next token, this handles spaces and quotes
	 * @return a string
	 * @throws WrappedException exception occured during the execution method.
	 */
	public String nextToken() throws WrappedException
	{
		try
		{
			while (pos < len - 1 && workString.charAt(pos) == ' ')
			{
				pos++;
			}
			StringBuffer sb = new StringBuffer();

			while (pos < len && workString.charAt(pos) != ' ')
			{
				if (workString.charAt(pos) == '"')
				{
					pos++;
					while (pos < len && workString.charAt(pos) != '"')
					{
						sb.append(workString.charAt(pos));
						pos++;
					}
					pos++;
					return sb.toString();
				}
				sb.append(workString.charAt(pos));
				pos++;
			}

			return sb.toString();
		}
		catch (Exception e)
		{
			throw new WrappedException(e);
		}
	}
}
