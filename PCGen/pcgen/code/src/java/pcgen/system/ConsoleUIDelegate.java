/**
 * ConsoleUIDelegate.java
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
 * Created on 20/01/2012 3:48:05 PM
 *
 * $Id: ConsoleUIDelegate.java 18181 2012-11-06 19:50:30Z javydreamercsw $
 */
package pcgen.system;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.ChooserFacade;
import pcgen.core.facade.UIDelegate;
import pcgen.system.PropertyContext;
import pcgen.util.Logging;

/**
 * The Class <code>ConsoleUIDelegate</code> displays messages to the console 
 * and returns default choices. It is used when PCGen is running in batch mode 
 * without any windows shown.
 *
 * <br/>
 * Last Editor: $Author: javydreamercsw $
 * Last Edited: $Date: 2012-11-06 13:50:30 -0600 (Tue, 06 Nov 2012) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 18181 $
 */
public class ConsoleUIDelegate implements UIDelegate
{

	/* (non-Javadoc)
	 * @see pcgen.core.facade.UIDelegate#maybeShowWarningConfirm(java.lang.String, java.lang.String, java.lang.String, pcgen.system.PropertyContext, java.lang.String)
	 */
    @Override
	public Boolean maybeShowWarningConfirm(String title, String message,
		String checkBoxText, PropertyContext context, String contextProp)
	{
		Logging.log(Logging.WARNING, title + " - " + message);
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.UIDelegate#showErrorMessage(java.lang.String, java.lang.String)
	 */
    @Override
	public void showErrorMessage(String title, String message)
	{
		Logging.log(Logging.ERROR, title + " - " + message);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.UIDelegate#showInfoMessage(java.lang.String, java.lang.String)
	 */
    @Override
	public void showInfoMessage(String title, String message)
	{
		Logging.log(Logging.INFO, title + " - " + message);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.UIDelegate#showLevelUpInfo(pcgen.core.facade.CharacterFacade, int)
	 */
    @Override
	public void showLevelUpInfo(CharacterFacade character, int oldLevel)
	{
		Logging.log(Logging.INFO, "Level up from " + oldLevel + " complete for character " + character);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.UIDelegate#showWarningConfirm(java.lang.String, java.lang.String)
	 */
    @Override
	public boolean showWarningConfirm(String title, String message)
	{
		Logging.log(Logging.WARNING, title + " - " + message);
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.UIDelegate#showWarningPrompt(java.lang.String, java.lang.String)
	 */
    @Override
	public boolean showWarningPrompt(String title, String message)
	{
		Logging.log(Logging.WARNING, title + " - " + message);
		return true;
	}

    @Override
	public void showWarningMessage(String title, String message)
	{
		Logging.log(Logging.WARNING, title + " - " + message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean showGeneralChooser(ChooserFacade chooserFacade)
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String showInputDialog(String title, String message, String initialValue)
	{
		return null;
	}

}
