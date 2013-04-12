/*
 * GameModeLstToken.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 2, 2002, 8:16 AM
 *
 * Current Ver: $Revision: 2077 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-01-27 10:45:58 -0600 (Sat, 27 Jan 2007) $
 *
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.GameMode;

/**
 * <code>GameModeLstToken</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public interface GameModeLstToken extends LstToken
{
	/**
	 * Parse the token
	 * @param gameMode
	 * @param value
	 * @return true if successful
	 */
	public abstract boolean parse(GameMode gameMode, String value, URI source);
}
