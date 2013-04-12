package plugin.exporttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * <code>VFeatToken</code> deals with VFEAT output token.
 *
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2012-12-08 17:07:45 -0600 (Sat, 08 Dec 2012) $
 *
 * @author karianna
 * @version $Revision: 18567 $
 */
public class VFeatToken extends AbilityToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "VFEAT";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
						   ExportHandler eh)
	{
		setVisibility(ABILITY_ALL);
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();

		return getTokenForCategory(tokenSource, pc, eh, aTok, fString,
								   AbilityCategory.FEAT);
	}

	/**
	 * @see pcgen.io.exporttoken.AbilityToken#getAbilityList(pcgen.core.PlayerCharacter, pcgen.core.AbilityCategory)
	 */
	@Override
	protected List<Ability> getAbilityList(PlayerCharacter pc,
										   final AbilityCategory aCategory)
	{
		final List<Ability> abilityList = new ArrayList<Ability>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (aCat.getParentCategory().equals(aCategory))
			{
				abilityList.addAll(pc.getAbilityList(aCat, Nature.VIRTUAL));
			}
		}
		return abilityList;
	}

}
