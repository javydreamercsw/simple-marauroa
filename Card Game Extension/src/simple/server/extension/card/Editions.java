package simple.server.extension.card;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Editions implements ISearchableProperty {

    private static final String EDITIONS_FILE = "editions.txt";
    private static Editions instance = new Editions();
    private HashMap<String, Editions.Edition> name2ed;

    public static class Edition {

        private String name;
        private String abbrs[];
        private Date release;
        private String type = "?";
        private Set<String> format;
        private static final SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

        public Edition(String name, String abbr) {
            this.name = name;
            this.abbrs = new String[]{abbr == null ? fakeAbbr(name) : abbr};
        }

        private String fakeAbbr(String xname) {
            return "_" + xname.replaceAll("\\W", "_");
        }

        @Override
        public String toString() {
            return name;
        }

        public void setReleaseDate(String date) throws ParseException {
            if (date == null || date.length() == 0 || date.equals("?")) {
                release = null;
            } else {
                release = formatter.parse(date);
            }
        }

        public Date getReleaseDate() {
            return release;
        }

        public boolean abbreviationOf(String abbr) {
            if (abbr == null || abbrs.length == 0) {
                return false;
            }
            for (int i = 0; i < abbrs.length; i++) {
                String a = abbrs[i];
                if (abbr.equals(a)) {
                    return true;
                }
            }
            return false;
        }

        public void addAbbreviation(String abbr) {
            if (abbr == null) {
                return;
            }
            if (isAbbreviationFake()) {
                abbrs[0] = abbr;
            } else {
                for (int i = 0; i < abbrs.length; i++) {
                    if (abbrs[i].equals(abbr)) {
                        return;
                    }
                }
                String[] arr = new String[abbrs.length + 1];
                System.arraycopy(abbrs, 0, arr, 0, abbrs.length);
                arr[abbrs.length] = abbr;
                abbrs = arr;
            }
        }

        public boolean isLegal(String leg) {
            if (format == null) {
                return false;
            }
            return format.contains(leg);
        }

        private boolean isAbbreviationFake() {
            return getMainAbbreviation().startsWith("_");
        }

        public String getMainAbbreviation() {
            return abbrs[0];
        }

        public String getExtraAbbreviation() {
            if (abbrs.length > 1) {
                return abbrs[1];
            }
            return "";
        }

        public void setType(String type) {
            if (type == null || type.length() == 0) {
                this.type = "?";
            }
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setReleaseDate(Date time) {
            release = time;
        }

        public String getType() {
            return type;
        }

        public String getBaseFileName() {
            String a = getMainAbbreviation();
            if (a.equals("CON")) {
                // special hack for windows, which cannot create CON
                // directory
                a = "CONFL";
            }
            return a;
        }

        public Set<String> getLegalities() {
            return format;
        }

        public String getFormatString() {
            if (format == null) {
                return "";
            }
            String string = format.toString();
            return string.substring(1, string.length() - 1);
        }

        public void addFormat(String leg) {
            if (format == null) {
                format = new LinkedHashSet<String>();
            }
            format.add(leg);
        }

        public void clearLegality() {
            if (format == null) {
                return;
            }
            format.clear();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Editions.Edition other = (Editions.Edition) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        public void setFormats(String legality) {
            String[] legs = legality.split(",");
            clearLegality();
            for (int i = 0; i < legs.length; i++) {
                String string = legs[i];
                addFormat(string.trim());
            }
        }
    }

    private Editions() {
        init();
    }

    /**
     * This is not public API, only called by tests
     */
    public void init() {
        try {
            this.name2ed = new HashMap<String, Editions.Edition>();
            load();
        } catch (IOException ex) {
            Logger.getLogger(Editions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Editions getInstance() {
        return instance;
    }

    public Collection<Editions.Edition> getEditions() {
        return this.name2ed.values();
    }

    public String getNameByAbbr(String abbr) {
        for (Iterator<String> iterator = name2ed.keySet().iterator(); iterator.hasNext();) {
            String name = iterator.next();
            Editions.Edition value = name2ed.get(name);
            if (value != null && value.abbreviationOf(abbr)) {
                return name;
            }
        }
        return null;
    }

    public synchronized Editions.Edition addEdition(String name, String abbr) {
        if (name.length() == 0) {
            throw new IllegalArgumentException();
        }
        Editions.Edition edition = name2ed.get(name);
        if (edition == null) {
            edition = new Editions.Edition(name, abbr);
            this.name2ed.put(name, edition);
        } else {
            if (abbr != null) {
                edition.addAbbreviation(abbr);
            }
        }
        return edition;
    }

    public synchronized boolean containsName(String name) {
        return getEditionByName(name) != null;
    }

    public String getAbbrByName(String name) {
        Editions.Edition edition = getEditionByName(name);
        if (edition == null) {
            return null;
        }
        return edition.getMainAbbreviation();
    }

    public Editions.Edition getEditionByName(String name) {
        return this.name2ed.get(name);
    }

    private synchronized void load() throws IOException {
//        File file = new File(DataManager.getStateLocationFile(), EDITIONS_FILE);
//        if (DbActivator.getDefault() != null) {
//            InputStream ist = DbActivator.loadResource(EDITIONS_FILE);
//            loadEditions(ist);
//        }
//        if (!file.exists()) {
//            save();
//        }
//        InputStream st = new FileInputStream(file);
//        loadEditions(st);
    }

    private synchronized void loadEditions(InputStream st) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(st));
        try {
            String line;
            while ((line = r.readLine()) != null) {
                try {
                    String[] attrs = line.split("\\|");
                    String name = attrs[0].trim();
                    String abbr1 = attrs[1].trim();
                    Editions.Edition set = addEdition(name, abbr1);
                    if (attrs.length < 3) {
                        continue; // old style
                    }
                    String abbrOther = attrs[2].trim();
                    if (abbrOther.equals("en-us") || abbrOther.equals("EN")) {
                        continue; // old style
                    }
                    if (abbrOther.length() > 0) {
                        set.addAbbreviation(abbrOther);
                    }
                    String releaseDate = attrs[3].trim();
                    if (releaseDate != null && releaseDate.length() > 0) {
                        set.setReleaseDate(releaseDate);
                    } else {
                        System.err.println("Missing release date " + line);
                    }
                    String type = attrs[4].trim();
                    if (type != null && type.length() > 0) {
                        set.setType(type);
                    } else {
                        System.err.println("Missing type " + line);
                    }
                    // Block
                    // skipping
                    if (attrs.length <= 6) {
                        continue;
                    }
                    // Legality
                    String legality = attrs[6].trim();
                    String[] legs = legality.split(",");
                    set.clearLegality();
                    for (int i = 0; i < legs.length; i++) {
                        String string = legs[i];
                        set.addFormat(string.trim());
                    }
                } catch (Exception e) {
                    System.err.println("bad editions record: " + line);
                    e.printStackTrace();
                }
            }
        } finally {
            r.close();
        }
    }

    public synchronized void save() throws FileNotFoundException {
//        File file = new File(DataManager.getStateLocationFile(), EDITIONS_FILE);
//        PrintStream st = new PrintStream(file);
//        try {
//            for (Iterator<String> iterator = this.name2ed.keySet().iterator(); iterator.hasNext();) {
//                String name = iterator.next();
//                Editions.Edition ed = getEditionByName(name);
//                String rel = "";
//                if (ed.getReleaseDate() != null) {
//                    rel = Editions.Edition.formatter.format(ed.getReleaseDate());
//                }
//                String type = "";
//                if (ed.getType() != null) {
//                    type = ed.getType();
//                }
//                st.println(name + "|" + ed.getMainAbbreviation() + "|" + ed.getExtraAbbreviation() + "|" + rel + "|" + type + "||"
//                        + ed.getFormatString());
//            }
//        } finally {
//            st.close();
//        }
    }

    @Override
    public String getIdPrefix() {
        return "edition";
    }

    @Override
    public Collection<String> getIds() {
        ArrayList<String> list = new ArrayList<String>();
        for (Iterator<Editions.Edition> iterator = this.name2ed.values().iterator(); iterator.hasNext();) {
            Editions.Edition ed = iterator.next();
            String abbr = ed.getMainAbbreviation();
            list.add(getPrefConstant(abbr));
        }
        return list;
    }

    public String getPrefConstant(String abbr) {
        return "";//FilterHelper.getPrefConstant(getIdPrefix(), abbr);
    }

    public String getPrefConstantByName(String name) {
        String abbr = getAbbrByName(name);
        return "";//FilterHelper.getPrefConstant(getIdPrefix(), abbr);
    }

    @Override
    public String getNameById(String id) {
        HashMap<String, String> idToName = new HashMap<String, String>();
        for (Iterator<String> iterator = this.name2ed.keySet().iterator(); iterator.hasNext();) {
            String name = iterator.next();
            String id1 = getPrefConstantByName(name);
            idToName.put(id1, name);
        }
        return idToName.get(id);
    }

    @Override
    public Collection<String> getNames() {
        return new ArrayList<String>(this.name2ed.keySet());
    }
}
