package mtg.card.seller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mtg.card.IMagicCard;
import mtg.card.MagicCard;
import mtg.card.MagicCardFilter;
import mtg.card.MagicFilteredCardStore;
import simple.server.extension.card.Editions;
import simple.server.extension.card.ICard;
import simple.server.extension.card.ISellableCard;
import simple.server.extension.card.price.IPriceProvider;
import simple.server.extension.card.price.IStoreUpdator;
import simple.server.extension.card.storage.*;

public class FindMagicCardsPrices implements IStoreUpdator, IPriceProvider {

    String baseURL;
    String cardURL;
    HashMap<String, String> setIdMap = new HashMap<String, String>();
    private String setURL;

    public FindMagicCardsPrices() {
        // http://www.mtgfanatic.com/store/viewproducts.aspx?CatID=217&AffiliateID=44349&PageSize=500
        baseURL = "http://findmagiccards.com/Find/BySet/${SetAbbr}/Matches.html";
        cardURL = "http://findmagiccards.com/Cards/${SetAbbr}/${CardName}.html";
        setURL = "http://findmagiccards.com/CardSets.html";
        // hardcoded setIdMap
        setIdMap.put("Limited Edition Beta", "B");
        setIdMap.put("Limited Edition Alpha", "A");
        setIdMap.put("Revised Edition", "RV");
        setIdMap.put("Unlimited Edition", "UN");
        setIdMap.put("Time Spiral \"Timeshifted\"", "TSS");
    }

    @Override
    public String getName() {
        return "Find Magic Cards";
    }

    @Override
    public URL getURL() {
        try {
            return new URL("http://findmagiccards.com");
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public void updateStore(ICardStore<ISellableCard> store, Iterable<ISellableCard> iterable, int size)
            throws IOException {
//	TODO: use NetBeans RCP	monitor.beginTask("Loading prices from http://findmagiccards.com ...", size + 10);
        if (iterable == null) {
            iterable = store;
            size = store.size();
        }
        HashSet<String> sets = new HashSet();
        for (Iterator<ISellableCard> it = iterable.iterator(); it.hasNext();) {
            ISellableCard card = it.next();
            String set = card.getSet();
            sets.add(set);
        }

        IStorage storage = store.getStorage();
        storage.setAutoCommit(false);
        processSetList(sets);
        try {
            for (String set : sets) {
                String id = findSetId(set);
                if (id != null) {
                    // System.err.println("found " + set + " " + id);
                    HashMap<String, Float> prices = null;
                    try {
                        prices = parse(id);
                    } catch (IOException e) {
                        //
                    }
                    for (ISellableCard card : iterable) {
                        String set2 = card.getSet();
                        if (set2.equals(set)) {
                            float price = -1;
                            if (prices != null && prices.containsKey(card.getName())) {
                                Float price1 = prices.get(card.getName());
                                price = (price1.floatValue());
                            } else {
                                // load individual card
                                try {
                                    price = parseSingleCard(id, card);
                                } catch (IOException e) {
                                    System.err.println("Cannot load card " + e.getMessage() + " " + set);
                                }
                            }
                            if (price > 0) {
                                if (!setIdMap.containsKey(set)) {
                                    setIdMap.put(set, id);
                                }
                                card.setDbPrice(price);
                                store.update(card);
                            }
//							monitor.worked(1);
                        }
                    }
                }
            }
        } finally {
            storage.save();
            storage.setAutoCommit(true);
//			monitor.done();
        }
    }

    private String findSetId(String set) {
        if (setIdMap.containsKey(set)) {
            return setIdMap.get(set);
        }
        set = set.replaceAll(" vs. ", " vs ");
        if (setIdMap.containsKey(set)) {
            return setIdMap.get(set);
        }
        String abbrByName = Editions.getInstance().getAbbrByName(set);
        if (abbrByName != null && testSetUrl(abbrByName)) {
            return abbrByName;
        }
        if (abbrByName != null && abbrByName.length() > 2) {
            String t12 = abbrByName.substring(0, 2);
            if (testSetUrl(t12)) {
                return t12;
            }
        }
        System.err.println("Cannot find prices for " + set);
        return null;
    }

    private boolean testSetUrl(String abbr) {
        String testurl = "http://findmagiccards.com/Cards/" + abbr;
        try {
            URL url = new URL(testurl);
            InputStream openStream = url.openStream();
            BufferedReader st = new BufferedReader(new InputStreamReader(openStream));
            st.readLine();
            st.readLine();
            String title = st.readLine();
            if (title.contains("404")) {
                return false;
            }
            openStream.close();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public HashMap<String, Float> parse(String setId) throws IOException {
        HashMap<String, Float> res = new HashMap<String, Float>();
        URL url = new URL(baseURL.toString().replace("${SetAbbr}", setId));
        InputStream openStream = url.openStream();
        BufferedReader st = new BufferedReader(new InputStreamReader(openStream));
        processFile(st, res);
        st.close();
        return res;
    }

    private float parseSingleCard(String setAbbr, ICard card) throws IOException {
        String name = card.getName().replaceAll("\\W", "_");
        URL url = new URL(cardURL.toString().replace("${SetAbbr}", setAbbr).replace("${CardName}", name));
        InputStream openStream = url.openStream();
        BufferedReader st = new BufferedReader(new InputStreamReader(openStream));
        try {
            return processCard(st);
        } finally {
            st.close();
        }
    }
    private static final Pattern rowPattern = Pattern.compile("<TR(.*?)</TR>");
    private static final Pattern rowPattern2 = Pattern.compile( //
            "<TD><a href='[^']*'>(.*)</a>.nbsp;</TD>" // name
            + ".*" //
            + "<TD>([0-9.]+).nbsp;</TD>$" // price
            );

    /*-
     * no NL
     <TABLE><TR class=defTitleRow><TH><a href='../../../Find/BySet/TSN/Matches/Name.html'>Name</a></TH><TH><a href='../../../Find/BySet/TSN/Matches/Edition.html'>Edition</a></TH><TH><a href='../../../Find/BySet/TSN/Matches/Colour.html'>Colour</a></TH><TH><a href='../../../Find/BySet/TSN/Matches/Type.html'>Type</a></TH><TH><a href='../../../Find/BySet/TSN/Matches/Cost.html'>Cost</a></TH><TH><a href='../../../Find/BySet/TSN/Matches/Power.html'>Power</a></TH><TH><a href='../../../Find/BySet/TSN/Matches/Rarity.html'>Rarity</a></TH></TR>
     <TR class=defRowEven>
     <TD><a href='../../../Cards/TSN/Academy_Ruins.html'>Academy Ruins</a>&nbsp;</TD>
     <TD>TSN&nbsp;</TD>
     <TD><font color='#669999'>Land</font>&nbsp;</TD>
     <TD>Legendary Land&nbsp;</TD>
     <TD>&nbsp;</TD>
     <TD>&nbsp;</TD>
     <TD><font color='#999966'>Rare</font>&nbsp;</TD>
     <TD>1.94&nbsp;</TD></TR>
     */
    private void processFile(BufferedReader st, HashMap<String, Float> res) throws IOException {
        String line;
        while ((line = st.readLine()) != null) {
            Matcher matcher = rowPattern.matcher(line);
            while (matcher.find()) {
                String row = matcher.group(1);
                Matcher matcher2 = rowPattern2.matcher(row);
                if (matcher2.find()) {
                    String name = matcher2.group(1);
                    String price = matcher2.group(2);
                    try {
                        float f = Float.parseFloat(price);
                        res.put(name, f);
                    } catch (NumberFormatException e) {
                        return;
                    }
                }
            }
        }
    }

    /*-
     * <TD align=right>Price :</TD><TD>$ 1.23&nbsp;&nbsp;</TD>
     */
    private static final Pattern cardPattern = Pattern.compile("<TD align=right>Price :</TD><TD>\\$ ([0-9.]+)");

    private float processCard(BufferedReader st) throws IOException {
        String line;
        while ((line = st.readLine()) != null) {
            Matcher matcher = cardPattern.matcher(line);
            if (matcher.find()) {
                String price = matcher.group(1);
                try {
                    float f = Float.parseFloat(price);
                    return f;
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }
    private static final Pattern set1Pattern = Pattern.compile("<TR(.*?)</TR>");
    private static final Pattern set2Pattern = Pattern.compile( //
            "<TD>([A-Z]+).nbsp;</TD>" + // abbr
            "<TD><a href='[^']*'>(.*)</a>.nbsp;</TD>" // name
            );

    /*- no NL
     * <TR class=defRowEven>
     * <TD>RE&nbsp;</TD>
     * <TD><a href='CardSets/RE.html'>Rise of the Eldrazi</a>&nbsp;</TD>
     * </TR>
     * <TR class=defRowOdd><TD>WW&nbsp;</TD><TD><a href='CardSets/WW.html'>Worldwake</a>&nbsp;</TD></TR>
     */
    private void processSetList(HashSet sets) throws IOException {
        URL url = new URL(setURL);
        InputStream openStream = url.openStream();
        BufferedReader st = new BufferedReader(new InputStreamReader(openStream));
        try {
            String line;
            while ((line = st.readLine()) != null) {
                Matcher matcher = set1Pattern.matcher(line);
                while (matcher.find()) {
                    String row = matcher.group(1);
                    Matcher matcher2 = set2Pattern.matcher(row);
                    if (matcher2.find()) {
                        String name = matcher2.group(2);
                        String abbr = matcher2.group(1);
                        if (!setIdMap.containsKey(name)) {
                            setIdMap.put(name, abbr);
                        }
                    }
                }
            }
        } finally {
            st.close();
        }
    }

    public void updateStore(IFilteredCardStore<ISellableCard> fstore) throws IOException {
        updateStore(fstore.getCardStore(), fstore, fstore.getSize());
    }

    public static void main(String[] args) throws IOException {
        try {
            FindMagicCardsPrices prices = new FindMagicCardsPrices();
            MagicFilteredCardStore<IMagicCard> fstore = new MagicFilteredCardStore<IMagicCard>() {

                MemoryCardStore<MemoryCardStorage<IMagicCard>> store = new MemoryCardStore<MemoryCardStorage<IMagicCard>>();

                @Override
                public ICardStore getCardStore() {
                    return store;
                }
            };
            MagicCard card = new MagicCard();
            card.setSet("Time Spiral");
            card.setName("Amrou Scout");
            fstore.getCardStore().add(card);
            fstore.update(new MagicCardFilter());
            prices.updateStore(fstore);
            Iterator iterator = fstore.iterator();
            while (iterator.hasNext()) {
                MagicCard temp = (MagicCard) iterator.next();
                System.out.println(temp.toString());
                System.out.println("Prize: $" + temp.getDbPrice());
            }
        } catch (Exception ex) {
            Logger.getLogger(FindMagicCardsPrices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
