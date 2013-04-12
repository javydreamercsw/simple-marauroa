/*
 * PreSize.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 18858 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2012-12-29 09:26:36 -0600 (Sat, 29 Dec 2012) $
 *
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 *
 */
public class PreSizeTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
	{
		final int targetSize = SizeUtilities.sizeInt(prereq.getOperand());

		final int runningTotal =
				prereq.getOperator().compare(display.sizeInt(), targetSize);

		return countedTotal(prereq, runningTotal);
	}

	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		CharacterDisplay display) throws PrerequisiteException
	{
		final int targetSize = SizeUtilities.sizeInt(prereq.getOperand());

		final int runningTotal =
				prereq.getOperator().compare(equipment.sizeInt(), targetSize);

		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
    @Override
	public String kindHandled()
	{
		return "SIZE"; //$NON-NLS-1$
	}

}
