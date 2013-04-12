/*
 * PPCost.java
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
 * Created on January 27, 2005, 11:26 PM
 *
 * Current Ver: $Revision: 15068 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2011-05-30 06:56:35 -0500 (Mon, 30 May 2011) $
 *
 */
package plugin.bonustokens;

import pcgen.cdom.base.Constants;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;

/**
 * Handles the BONUS:PPCOST token.
 */
public final class PPCost extends BonusObj
{
	/**
	 * Parse the bonus token.
	 * @see pcgen.core.bonus.BonusObj#parseToken(LoadContext, java.lang.String)
	 * @return True if successfully parsed.
	 */
	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		if (token.startsWith(Constants.LST_TYPE_EQUAL))
		{
			addBonusInfo(token.replace('=', '.'));
		}
		else
		{
			addBonusInfo(token);
		}

		return true;
	}

	/**
	 * Unparse the bonus token.
	 * @see pcgen.core.bonus.BonusObj#unparseToken(java.lang.Object)
	 * @param obj The object to unparse
	 * @return The unparsed string.
	 */
	@Override
	protected String unparseToken(final Object obj)
	{
		return (String) obj;
	}

	/**
	 * Return the bonus tag handled by this class.
	 * @return The bonus handled by this class.
	 */
	@Override
	public String getBonusHandled()
	{
		return "PPCOST";
	}
}
