/*
 * SpaceTokenTest.java
 * Copyright 2008 (C) PCGen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Dec 01, 2008
 *
 * $Id: SpaceTokenTest.java 16168 2012-02-12 03:22:53Z thpr $
 */
package plugin.exporttokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.display.CharacterDisplay;

/**
 * <code>SpaceTokenTest</code> - Unit test for the SPACE output token
 *
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2008-11-25 02:54:29 +0000 (Tue, 25 Nov 2008) $
 *
 * @author Martijn Verburg <karianna@users.sourceforge.net>
 * @version $Revision: 8488 $
 */
public class SpaceTokenTest extends AbstractCharacterTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(SpaceTokenTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		// Do Nothing
	}

	/*
	 * @see TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		// Do Nothing
	}

	/**
	 * Test the SPACE token.
	 */
	public void testSpaceToken()
	{
		SpaceToken token = new SpaceToken();
		assertEquals(" ", token.getToken("SPACE", (CharacterDisplay) null, null));
	}

}
