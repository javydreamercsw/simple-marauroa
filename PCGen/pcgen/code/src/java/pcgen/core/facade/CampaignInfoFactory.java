/*
 * CampaignInfoFactory.java
 * Copyright James Dempsey, 2011
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
 * Created on 08/03/2011 7:18:51 PM
 *
 * $Id: CampaignInfoFactory.java 18938 2013-01-01 04:39:47Z jdempsey $
 */
package pcgen.core.facade;

import java.util.List;

/**
 * The Class <code>CampaignInfoFactory</code> is ...
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2012-12-31 22:39:47 -0600 (Mon, 31 Dec 2012) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 18938 $
 */
public interface CampaignInfoFactory
{
	/**
	 * Produce a html formatted information string for the supplied campaign.
	 * @param campaign The campaign to be described
	 * @return The information on the campaign.
	 */
	public String getHTMLInfo(CampaignFacade campaign);

	/**
	 * Produce a html formatted information string for the supplied campaign using 
	 * a specified set of campaigns for prereq determination.
	 * @param campaign The campaign to be described
	 * @param selectedCampaigns The previously selected campaigns to be checked against.
	 * @return The information on the campaign.
	 */
	public String getHTMLInfo(CampaignFacade campaign, List<CampaignFacade> selectedCampaigns);

	/**
	 * Produce a html formatted information string for the supplied source selection.
	 * @param selection The source selection, referring to one or more campaigns.
	 * @return The information on the campaign(s).
	 */
	public String getHTMLInfo(SourceSelectionFacade selection);

	/**
	 * Produce a html formatted string detailing the requirements for the supplied campaign using 
	 * a specified set of campaigns for prereq determination.
	 * @param campaign The campaign to be described
	 * @param selectedCampaigns The previously selected campaigns to be checked against.
	 * @return The description of the prerequisites for the campaign.
	 */
	public String getRequirementsHTMLString(CampaignFacade campaign,
		List<CampaignFacade> selectedCampaigns);
}
