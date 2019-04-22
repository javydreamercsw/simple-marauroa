package com.reflexit.magiccards.core.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public final class Editions implements ISearchableProperty {

    private static Editions instance = new Editions();
    private HashMap<String, Editions.Edition> name2ed;

    public static class Edition {

        private String name;
        private String abbrs[];
        private Date release;
        private String type = "?";
        private Set<String> format;

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
                release = new Date();
            } else {
                SimpleDateFormat formatter
                        = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
                release = formatter.parse(date);
            }
        }

        public Date getReleaseDate() {
            return release == null ? new Date() : (Date) release.clone();
        }

        public boolean abbreviationOf(String abbr) {
            if (abbr == null || abbrs.length == 0) {
                return false;
            }
            for (String a : abbrs) {
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
                for (String abbr1 : abbrs) {
                    if (abbr1.equals(abbr)) {
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
            return format == null ? false : format.contains(leg);
        }

        private boolean isAbbreviationFake() {
            return getMainAbbreviation().startsWith("_");
        }

        public String getMainAbbreviation() {
            return abbrs.length >= 1 ? abbrs[0] : "";
        }

        public String getExtraAbbreviation() {
            if (abbrs.length > 1) {
                String line = abbrs[1];
                for (int i = 2; i < abbrs.length; i++) {
                    line += "," + abbrs[i];
                }
                return line;
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
            if (release == null)
            {
              release = new Date();
            }
            release.setTime(time.getTime());
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
            for (String string : legs) {
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
        name2ed = new HashMap<String, Editions.Edition>();
    }

    public static Editions getInstance() {
        return instance;
    }

    public Collection<Editions.Edition> getEditions() {
        return this.name2ed.values();
    }

    public String getNameByAbbr(String abbr) {
        for (String name : name2ed.keySet()) {
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

    @Override
    public String getIdPrefix() {
        return "edition";
    }

    @Override
    public Collection<String> getIds() {
        ArrayList<String> list = new ArrayList<String>();
        for (Editions.Edition ed : this.name2ed.values()) {
            String abbr = ed.getMainAbbreviation();
            list.add(getPrefConstant(abbr));
        }
        return list;
    }

    public String getPrefConstant(String abbr) {
        return getPrefConstant(getIdPrefix(), abbr);
    }

    public String getPrefConstantByName(String name) {
        return getPrefConstant(getIdPrefix(), getAbbrByName(name));
    }

    @Override
    public String getNameById(String id) {
        HashMap<String, String> idToName = new HashMap<String, String>();
        for (String name : this.name2ed.keySet()) {
            String id1 = getPrefConstantByName(name);
            idToName.put(id1, name);
        }
        return idToName.get(id);
    }

    @Override
    public Collection<String> getNames() {
        return new ArrayList<String>(this.name2ed.keySet());
    }
    private static final Logger LOG = Logger.getLogger(Editions.class.getName());

    public static String escapeProperty(String string) {
        String res = string.toLowerCase();
        res = res.replaceAll("[^\\w-./]", "_");
        return res;
    }

    public static String getPrefConstant(String sub, String name) {
        return "com.reflexit.magiccards.core" + ".filter." + sub + "." + escapeProperty(name);
    }
}
