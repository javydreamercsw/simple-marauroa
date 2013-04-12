/*
 * ChangeListener.java
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
 * Created on 16/01/2012 8:48:48 AM
 *
 * $Id: ChangeListener.java 15994 2012-02-04 11:53:38Z jdempsey $
 */
package pcgen.core.facade.event;

/**
 * The interface <code>ChangeListener</code> should be implemented by classes interested in 
 * object changes.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2012-02-04 05:53:38 -0600 (Sat, 04 Feb 2012) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 15994 $
 */

public interface ChangeListener
{
	/**
	 * Notification that an item changed.
	 * @param event The change event.
	 */
	public void ItemChanged(ChangeEvent event);
}
