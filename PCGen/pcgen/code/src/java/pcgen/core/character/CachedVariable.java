/*
 * CachedVariable.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 14-Mar-2004
 *
 * Current Ver: $Revision: 15050 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2011-05-29 08:02:34 -0500 (Sun, 29 May 2011) $
 *
 */
package pcgen.core.character;

/**
 * @param <T>
 * Todo This doesn't belong in this package.  It should probably be an inner
 * class in VariableProcessor.
 *
 */
public final class CachedVariable<T>
{
	private int serial;
	private T value;
	/**
	 * @return Returns the serial.
	 */
	public int getSerial()
	{
		return serial;
	}

	/**
	 * @param i The serial to set.
	 */
	public void setSerial(final int i)
	{
		serial = i;
	}

	/**
	 * @return Returns the value.
	 */
	public T getValue()
	{
		return value;
	}

	/**
	 * @param v The value to set.
	 */
	public void setValue(final T v)
	{
		value = v;
	}

}
