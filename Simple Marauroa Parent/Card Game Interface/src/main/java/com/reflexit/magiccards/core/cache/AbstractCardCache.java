package com.reflexit.magiccards.core.cache;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.openide.util.Lookup;

import com.reflexit.magiccards.core.CachedImageNotFoundException;
import com.reflexit.magiccards.core.CannotDetermineSetAbbriviation;
import com.reflexit.magiccards.core.model.CardFileUtils;
import com.reflexit.magiccards.core.model.Editions;
import com.reflexit.magiccards.core.model.Editions.Edition;
import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardGame;
import com.reflexit.magiccards.core.model.ICardSet;
import com.reflexit.magiccards.core.model.storage.db.DBException;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractCardCache implements ICardCache
{

  /**
   * Is caching in progress
   */
  private static boolean caching;
  /**
   * Is loading in progress
   */
  private static boolean loading;
  /**
   * Cache directory
   */
  private static File cacheDir;
  /**
   * Logger
   */
  private static final Logger LOG
          = Logger.getLogger(AbstractCardCache.class.getName());
  /**
   * The game for this cache
   */
  private final ICardGame game;

  /**
   * Enable/disable loading
   *
   * @param enabled
   */
  public static void setLoadingEnabled(final boolean enabled)
  {
    loading = enabled;
  }

  /**
   * Is loading enabled?
   *
   * @return
   */
  public static boolean isLoadingEnabled()
  {
    return loading;
  }

  /**
   * Set caching enabled
   *
   * @param enabled
   */
  public static void setCachingEnabled(final boolean enabled)
  {
    caching = enabled;
  }

  /**
   * @return the caching
   */
  public static boolean isCachingEnabled()
  {
    return caching;
  }

  /**
   * Constructor
   *
   * @param game Cache name
   * @throws DBException Error initializing Cache database
   */
  public AbstractCardCache(final ICardGame game) throws DBException
  {
    setLoadingEnabled(true);
    setCachingEnabled(true);
    Lookup.getDefault().lookup(IDataBaseCardStorage.class).initialize();
    this.game = game;
  }

  @Override
  public URL createSetImageURL(final ICard card, final String editionAbbr,
          final boolean upload) throws IOException
  {
    String rarity = Lookup.getDefault().lookup(IDataBaseCardStorage.class)
            .getCardAttribute(card, "rarity");
    if (editionAbbr == null)
    {
      return null;
    }
    String path = createLocalSetImageFilePath(editionAbbr, rarity);
    File file = new File(path);
    URL localUrl = file.toURI().toURL();
    if (upload == false)
    {
      return localUrl;
    }
    if (file.exists())
    {
      return localUrl;
    }
    try
    {
      URL url = createSetImageRemoteURL(editionAbbr, rarity);
      if (url == null)
      {
        return null;
      }
      InputStream st = url.openStream();
      CardFileUtils.saveStream(st, file);
      st.close();
    }
    catch (IOException e1)
    {
      throw e1;
    }
    return localUrl;
  }

  @Override
  public String createLocalImageFilePath(final ICard card,
          final ICardSet<ICard> set)
          throws CannotDetermineSetAbbriviation
  {
    String editionName = set.getName();
    Editions editions = Editions.getInstance();
    Edition cset = editions.getEditionByName(editionName);
    if (cset == null)
    {
      throw new CannotDetermineSetAbbriviation(editionName);
    }
    String editionAbbr = cset.getBaseFileName();
    String id = Lookup.getDefault().lookup(IDataBaseCardStorage.class)
            .getCardAttribute(card, "CardId");
    if (id != null)
    {
      Integer cardId = Integer.valueOf(id);
      File loc = getCacheLocationFile();
      String locale = "EN";
      String part = set.getCardGame().getName()
              + System.getProperty("file.separator") + "Cards"
              + System.getProperty("file.separator") + editionAbbr
              + System.getProperty("file.separator")
              + locale + System.getProperty("file.separator") + "Card"
              + cardId + ".jpg";
      String file = new File(loc, part).getPath();
      return file;
    }
    else
    {
      return null;
    }
  }

  /**
   * Create image path
   *
   * @param editionAbbr Edition abbreviation
   * @param rarity Rarity
   * @return Path
   * @throws MalformedURLException
   */
  public String createLocalSetImageFilePath(final String editionAbbr,
          final String rarity) throws MalformedURLException
  {
    File loc = getCacheLocationFile();
    String part = "Sets/" + editionAbbr + "-" + rarity + ".jpg";
    String file = new File(loc, part).getPath();
    return file;
  }

  /**
   * Check if image already exists
   *
   * @param card Card to check image for
   * @param set Set the card is from
   * @return true if exists
   */
  public boolean cardImageExists(final ICard card,
          final ICardSet<ICard> set)
  {
    try
    {
      String path = createLocalImageFilePath(card, set);
      return path == null ? false : new File(path).exists();
    }
    catch (CannotDetermineSetAbbriviation ex)
    {
      LOG.log(Level.FINE, null, ex);
      return false;
    }
  }

  /**
   * Download and save card image, if not already saved
   *
   * @param card Card to get image for
   * @param set Set the card is from
   * @param url URL to get the image from
   * @param remote do remote
   * @param forceRemote force remote update
   * @return File of the card image.
   * @throws IOException
   * @throws CannotDetermineSetAbbriviation
   */
  @Override
  public File getCardImage(final ICard card, final ICardSet<ICard> set,
          final URL url, final boolean remote, final boolean forceRemote)
          throws IOException, CannotDetermineSetAbbriviation
  {
    String path = createLocalImageFilePath(card, set);
    if (path == null || url == null)
    {
      //Game might not have a image for the card.
      return null;
    }
    else
    {
      File file = new File(path);
      if (forceRemote == false && file.exists())
      {
        return file;
      }
      if (!remote)
      {
        throw new CachedImageNotFoundException(
                "Cannot find cached image for " + card.getName());
      }
      InputStream st = null;
      try
      {
        st = url.openStream();
      }
      catch (IOException e)
      {
        throw new IOException("Cannot connect: " + e.getMessage());
      }
      File file2 = new File(path + ".part");
      CardFileUtils.saveStream(st, file2);
      st.close();
      file.delete();
      if (file2.exists())
      {
        FileUtils.moveFile(file2, file);
        return file;
      }
      throw new FileNotFoundException(file.getName());
    }
  }

  /**
   * Get card image or schedule a loading job if image not found. This image is
   * not managed - to be disposed by called. To get notified when job is done
   * loading, can wait on card object
   *
   * @param card
   * @param set
   * @param forceUpdate
   * @return true if card image exists, schedule update otherwise. If loading is
   * disabled and there is no cached image through an exception
   * @throws IOException
   * @throws CannotDetermineSetAbbriviation
   */
  @Override
  public boolean loadCardImageOffline(final ICard card,
          final ICardSet<ICard> set,
          final boolean forceUpdate) throws IOException,
          CannotDetermineSetAbbriviation
  {
    String path = createLocalImageFilePath(card, set);
    if (path != null)
    {
      File file = new File(path);
      if (file.exists() && forceUpdate == false)
      {
        return true;
      }
      if (!isLoadingEnabled())
      {
        throw new CachedImageNotFoundException(
                "Cannot find cached image for " + card.getName());
      }
      Lookup.getDefault().lookup(ICacheData.class).add(card);
    }
    return false;
  }

  /**
   * Check if is already cached
   *
   * @param card card to check
   * @param set
   * @return true or false
   * @throws CannotDetermineSetAbbriviation
   */
  public boolean isImageCached(final ICard card, final ICardSet<ICard> set)
          throws CannotDetermineSetAbbriviation
  {
    String path = createLocalImageFilePath(card, set);
    File file = new File(path);
    return file.exists();
  }

  @Override
  public File getCacheLocationFile()
  {
    return cacheDir;
  }

  /**
   * Set the cache's directory
   *
   * @param aCacheDir the cacheDir to set
   */
  public static void setCacheDir(final File aCacheDir)
  {
    if (!aCacheDir.exists() && !aCacheDir.mkdirs())
    {
      throw new RuntimeException("Unable to create cache dir at: "
              + aCacheDir.getAbsolutePath());
    }
    else
    {
      cacheDir = aCacheDir;
    }
  }

  /**
   * Get game name
   *
   * @return the name
   */
  @Override
  public final String getGameName()
  {
    return getGame().getName();
  }

  /**
   * Download image from URL
   *
   * @param url URL to download file from
   * @param dest File to store the image to
   * @param overwrite overwrite file if found
   * @return Downloaded image
   * @throws IOException
   */
  protected synchronized Image downloadImageFromURL(final URL url,
          final File dest,
          final boolean overwrite) throws IOException
  {
    InputStream st = null;
    if (!dest.exists() || overwrite)
    {
      LOG.log(Level.FINE, "Downloading file from: {0}", url);
      try
      {
        st = url.openStream();
      }
      catch (IOException e)
      {
        throw new IOException("Cannot connect: " + e.getMessage());
      }
      File file2 = new File(dest.getAbsolutePath() + ".part");
      CardFileUtils.saveStream(st, file2);
      st.close();
      if (dest.exists())
      {
        dest.delete();
      }
      FileUtils.copyFile(file2, dest);
      file2.deleteOnExit();
    }
    if (dest.exists())
    {
      return (new ImageIcon(dest.toURI().toURL(), "icon")).getImage();
    }
    throw new FileNotFoundException(dest.toString());
  }

  @Override
  public String getSetIconPath(final ICardSet<ICard> set)
  {
    File loc = getCacheLocationFile();
    Edition edition = Editions.getInstance()
            .getEditionByName(set.getName());
    String part;
    if (edition != null)
    {
      part = set.getCardGame().getName()
              + System.getProperty("file.separator")
              + "Sets"
              + System.getProperty("file.separator")
              + edition.getMainAbbreviation()
              + System.getProperty("file.separator")
              + System.getProperty("file.separator")
              + edition.getMainAbbreviation()
              + ".jpg";
      return new File(loc, part).getPath();
    }
    else
    {
      return null;
    }
  }

  @Override
  public String getGameIconPath()
  {
    File loc = getCacheLocationFile();
    String part = getGame().getName()
            + System.getProperty("file.separator")
            + "game.jpg";
    return new File(loc, part).getPath();
  }

  private ICardGame getGame()
  {
    return game;
  }
}
