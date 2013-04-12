/*
 * SignedInteger.java
 * Copyright James Dempsey, 2013
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 04/01/2013 11:26:33 AM
 *
 * $Id: SignedInteger.java 18993 2013-01-04 02:09:09Z jdempsey $
 */
package pcgen.util;

/**
 * The Class <code>SignedInteger</code> provides a number which always carries a 
 * leading sign in its string representation. It is currently used for displaying 
 * numerically sorted lists of modifiers in choosers.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2013-01-03 20:09:09 -0600 (Thu, 03 Jan 2013) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 18993 $
 */
public class SignedInteger extends Number implements Comparable<SignedInteger>
{

	/** Version for serialisation. */
	private static final long serialVersionUID = 3744855657358887537L;
	
	/** The integer value being represented. */
	private final int value;

	/**
	 * Create a new instance of SignedInteger
	 * @param value The integer value to be represented.
	 */
	public SignedInteger(int value)
	{
		this.value = value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double doubleValue()
	{
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float floatValue()
	{
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int intValue()
	{
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long longValue()
	{
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		if (value > 0)
		{
			return "+" + value;
		}
		return String.valueOf(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(SignedInteger arg0)
	{
		if (value == arg0.value)
		{
			return 0;
		}
		if (value > arg0.value)
		{
			return 1;
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof SignedInteger))
		{
			return false;
		}
		SignedInteger other = (SignedInteger) obj;
		if (value != other.value)
		{
			return false;
		}
		return true;
	}

}
