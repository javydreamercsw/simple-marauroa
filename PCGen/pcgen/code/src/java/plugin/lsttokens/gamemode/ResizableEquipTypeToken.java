/*
 * ResizableEquipTypeToken.java
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 3/05/2008
 *
 * $Id: ResizableEquipTypeToken.java 18181 2012-11-06 19:50:30Z javydreamercsw $
 */
package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * <code>ResizableEquipTypeToken</code> parses the list of equipment
 * types designated as able to be automatically resized. 
 *
 * Last Editor: $Author: javydreamercsw $
 * Last Edited: $Date: 2012-11-06 13:50:30 -0600 (Tue, 06 Nov 2012) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 18181 $
 */
public class ResizableEquipTypeToken implements GameModeLstToken
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
    @Override
	public String getTokenName()
	{
		return "RESIZABLEEQUIPTYPE";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.GameModeLstToken#parse(pcgen.core.GameMode, java.lang.String, java.net.URI)
	 */
    @Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		List<String> typelist = new ArrayList<String>();
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			typelist.add(aString);
		}
		gameMode.setResizableTypeList(typelist);
		return true;
	}
}
