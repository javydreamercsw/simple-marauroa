/*
 * PCCastTimesAtWillTermEvaluator.java
 * Copyright 2009 (C) James Dempsey
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
 * Created on 10/07/2009 6:41:36 PM
 *
 * $Id: PCCastTimesAtWillTermEvaluator.java 15744 2012-01-07 17:57:16Z thpr $
 */
package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;

/**
 * The Class <code>PCCastTimesAtWillTermEvaluator</code> supplies the 
 * times per day value of the ATWILL constant. 
 * 
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2012-01-07 11:57:16 -0600 (Sat, 07 Jan 2012) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 15744 $
 */
public class PCCastTimesAtWillTermEvaluator
		extends BasePCTermEvaluator implements TermEvaluator
{

	public PCCastTimesAtWillTermEvaluator(
			String originalText)
	{
		this.originalText = originalText;
	}

	@Override
	public Float resolve(PlayerCharacter pc)
	{
		return -1f;
	}

	@Override
	public Float resolve(PlayerCharacter pc, final CharacterSpell aSpell) {
		return -1f;
	}

	@Override
	public boolean isSourceDependant()
	{
		return false;
	}

	public boolean isStatic()
	{
		return true;
	}
}
