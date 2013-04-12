/*
 * NoteFacade.java
 * Copyright James Dempsey, 2012
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
 * Created on 12/03/2012 1:54:05 PM
 *
 * $Id: NoteFacade.java 16472 2012-03-12 06:44:08Z jdempsey $
 */
package pcgen.core.facade;

/**
 * NoteFacade defines the interface used by the user interface to interact 
 * with a character note.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2012-03-12 01:44:08 -0500 (Mon, 12 Mar 2012) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 16472 $
 */

public interface NoteFacade
{

	/**
	 * @return The name of this note.
	 */
	public String getName();

	/**
	 * @return The ciontents of the note.
	 */
	public String getValue();

	/**
	 * @param newValue The new contents of the note.
	 */
	public void setValue(String newValue);

	/**
	 * @return Can this note be renamed or removed?
	 */
	public boolean isRequired();
}
