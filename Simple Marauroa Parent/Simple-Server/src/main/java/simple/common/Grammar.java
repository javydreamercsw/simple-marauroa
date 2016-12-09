package simple.common;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper functions for producing and parsing grammatically-correct sentences.
 */
public class Grammar {

    private static final Logger LOG
            = Logger.getLogger(Grammar.class.getSimpleName());

    /**
     * "it" or "them", depending on the quantity.
     *
     * @param quantity The quantity to examine
     * @return Either "it" or "them" as appropriate
     */
    public static String itthem(int quantity) {
        return (quantity == 1 ? "it" : "them");
    }

    /**
     * Modify a word to upper case notation.
     *
     * @param word
     * @return word with first letter in upper case
     */
    public static String makeUpperCaseWord(String word) {
        StringBuilder res = new StringBuilder();
        if (word.length() > 0) {
            res.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                res.append(word.substring(1));
            }
        }
        return res.toString();
    }

    /**
     * "It" or "Them", depending on the quantity.
     *
     * @param quantity The quantity to examine
     * @return Either "It" or "Them" as appropriate
     */
    public static String itThem(int quantity) {
        return makeUpperCaseWord(itthem(quantity));
    }

    /**
     * "it" or "they", depending on the quantity.
     *
     * @param quantity The quantity to examine
     * @return Either "it" or "they" as appropriate
     */
    public static String itthey(int quantity) {
        return (quantity == 1 ? "it" : "they");
    }

    /**
     * "It" or "They", depending on the quantity.
     *
     * @param quantity The quantity to examine
     * @return Either "It" or "They" as appropriate
     */
    public static String itThey(int quantity) {
        return makeUpperCaseWord(itthey(quantity));
    }

    /**
     * "is" or "are", depending on the quantity.
     *
     * @param quantity The quantity to examine
     * @return Either "is" or "are" as appropriate
     */
    public static String isare(int quantity) {
        return (quantity == 1 ? "is" : "are");
    }

    /**
     * "Is" or "Are", depending on the quantity.
     *
     * @param quantity The quantity to examine
     * @return Either "Is" or "Are" as appropriate
     */
    public static String isAre(int quantity) {
        return makeUpperCaseWord(isare(quantity));
    }

    /**
     * Prefixes a noun with an article.
     *
     * @param noun noun
     * @param definite true for "the", false for a/an
     * @return noun with article
     */
    public static String article_noun(String noun, boolean definite) {
        if (definite) {
            return "the " + noun;
        } else {
            return a_noun(noun);
        }
    }

    /**
     * "a [noun]" or "an [noun]", depending on the first syllable.
     *
     * @param noun The noun to examine
     * @return Either "a [noun]" or "an [noun]" as appropriate
     */
    public static String a_noun(String noun) {
        if (noun == null) {
            return null;
        }
        String enoun = fullForm(noun);
        char initial = noun.length() > 0 ? Character.toLowerCase(enoun.charAt(0))
                : ' ';
        char second = noun.length() > 1 ? Character.toLowerCase(enoun.charAt(1))
                : ' ';
        if ((initial == 'e') && (second == 'u')) {
            return "a " + enoun;
        }
        if (vowel_p(initial)) {
            return "an " + enoun;
        }
        if ((initial == 'y') && consonant_p(second)) {
            return "an " + enoun;
        }
        return "a " + enoun;
    }

    /**
     * Adds a prefix unless it was already added.
     *
     * @param noun the noun (which may already start with the specified prefix
     * @param prefixSingular prefix to add
     * @param prefixPlural prefix, that may be present in plural form
     * @return noun starting with prefix
     */
    private static String addPrefixIfNotAlreadyThere(String noun,
            String prefixSingular, String prefixPlural) {
        if (noun.startsWith(prefixSingular)) {
            return noun;
        } else if (noun.startsWith(prefixPlural)) {
            return noun;
        } else {
            return prefixSingular + noun;
        }
    }

    /**
     * prefix a noun with an expression like "piece of".
     *
     * @param noun
     * @return
     */
    public static String fullForm(String noun) {
        String result;
        String lowString = noun.toLowerCase();
        result = lowString.replace("#", "");
        if (result.equals("meat") || result.equals("ham") || result.equals("cheese") || result.equals("wood") || result.equals("paper") || result.equals("iron") || result.equals("chicken")) {
            result = addPrefixIfNotAlreadyThere(lowString, "piece of ",
                    "pieces of ");
        } else if (result.endsWith(" ore")) {
            result = addPrefixIfNotAlreadyThere(lowString, "nugget of ",
                    "nuggets of ");
        } else if (result.equals("flour")) {
            result = addPrefixIfNotAlreadyThere(lowString, "sack of ",
                    "sacks of ");
        } else if (result.equals("grain")) {
            result = addPrefixIfNotAlreadyThere(lowString, "sheaf of ",
                    "sheaves of ");
        } else if (result.equals("bread")) {
            result = addPrefixIfNotAlreadyThere(lowString, "loaf of ",
                    "loaves of ");
        } else if (result.equals("beer") || result.equals("wine") || result.endsWith("poison") || result.equals("antidote")) {
            result = addPrefixIfNotAlreadyThere(lowString, "bottle of ",
                    "bottles of ");
        } else if (result.startsWith("book ")) {
            result = result.substring(5) + " book";
        } else if (result.equals("arandula")) {
            result = addPrefixIfNotAlreadyThere(lowString, "sprig of ",
                    "sprigs of ");
        } else if (result.contains(" armor")) {
            result = addPrefixIfNotAlreadyThere(lowString, "suit of ",
                    "suits of ");
        } else if (result.endsWith(" legs") || result.endsWith(" boots")) {
            result = addPrefixIfNotAlreadyThere(lowString, "pair of ",
                    "pairs of ");
        } else {
            result = lowString;
        }

        return result;
    }

    /**
     * Removes a prefix, if present.
     *
     * @param prefix
     * @param noun
     * @return object name without prefix, or same object as given if the prefix
     * was not found
     */
    private static String removePrefix(String noun, String prefix) {
        if (noun.startsWith(prefix)) {
            return noun.substring(prefix.length());
        } else {
            return noun;
        }
    }

    /**
     * Extracts noun from a string, that may be prefixed with a singular
     * expression like "piece of", ...
     *
     * @param expr
     * @return extracted noun, or same object as given if no matching prefix was
     * found
     */
    private static String extractNounSingular(String expr) {
        String result;

        result = removePrefix(expr, "piece of ");
        result = removePrefix(result, "nugget of ");
        result = removePrefix(result, "sack of ");
        result = removePrefix(result, "sheaf of ");
        result = removePrefix(result, "loaf of ");
        result = removePrefix(result, "bottle of ");
        result = removePrefix(result, "sprig of ");
        result = removePrefix(result, "suit of ");
        result = removePrefix(result, "pair of ");

        return result;
    }

    /**
     * Extracts noun from a string, that may be prefixed with a plural
     * expression like "piece of", ...
     *
     * @param expr
     * @return extracted noun, or same object as given if no matching prefix was
     * found
     */
    private static String extractNounPlural(String expr) {
        String result;

        result = removePrefix(expr, "pieces of ");
        result = removePrefix(result, "nuggets of ");
        result = removePrefix(result, "sacks of ");
        result = removePrefix(result, "sheaves of ");
        result = removePrefix(result, "loaves of ");
        result = removePrefix(result, "bottles of ");
        result = removePrefix(result, "sprigs of ");
        result = removePrefix(result, "suits of ");
        result = removePrefix(result, "pairs of ");

        return result;
    }

    /**
     * Extracts noun from a string, that may be prefixed with a plural
     * expression like "piece of", ... So this function is just the recursive
     * counter part to fullForm().
     *
     * @param expr
     * @return
     */
    public static String extractNoun(String expr) {
        if (expr == null) {
            return expr;
        }

        String lastExpr;
        String result = expr;

        // loop until all prefix strings are removed
        do {
            // remember original expression
            lastExpr = result;

            result = extractNounSingular(result);
            result = extractNounPlural(result);
        } // As the extract...() functions return the original object, if no change occurred,
        // we can just use an comparison without equals() here.
        while (!result.equals(lastExpr));
        return result;
    }

    /**
     * "A [noun]" or "An [noun]", depending on the first syllable.
     *
     * @param noun The noun to examine
     * @return Either "A [noun]" or "An [noun]" as appropriate
     */
    public static String A_noun(String noun) {
        return makeUpperCaseWord(a_noun(noun));
    }

    /**
     * "[noun]'s" or "[noun]'", depending on the last character.
     *
     * @param noun The noun to examine
     * @return Either "[noun]'s" or "[noun]'" as appropriate
     */
    public static String suffix_s(String noun) {
        char last = Character.toLowerCase(noun.charAt(noun.length() - 1));
        if (last == 's') {
            return noun + "'";
        }
        return noun + "'s";
    }

    /**
     * @return the plural form of the given noun if not already given in plural
     * form.
     *
     * @param noun The noun to examine
     */
    public static String plural(String noun) {
        if (noun == null) {
            return null;
        }

        String enoun = fullForm(noun);
        String postfix = "";

        int position = enoun.indexOf('+');
        if (position != -1) {
            if (enoun.charAt(position - 1) == ' ') {
                postfix = enoun.substring(position - 1);
                enoun = enoun.substring(0, position - 1);
            } else {
                postfix = enoun.substring(position);
                enoun = enoun.substring(0, position);
            }
        }

        // in "of"-phrases pluralize only the first part
        if (enoun.contains(" of ")) {
            return plural(enoun.substring(0, enoun.indexOf(" of ")))
                    + enoun.substring(enoun.indexOf(" of ")) + postfix;

            // first of all handle words which do not change
        } else if (enoun.endsWith("money") || enoun.endsWith("dice")
                || enoun.endsWith("sheep") || enoun.equals("deer")
                || enoun.equals("moose")) {
            return enoun + postfix;

            // ok and now all the special cases
        } else if (enoun.endsWith("staff") || enoun.endsWith("chief")) {
            return enoun + "s" + postfix;
        } else if (enoun.length() > 2 && enoun.endsWith("f")
                && ("aeiourl".indexOf(enoun.charAt(enoun.length() - 2)) > -1)) {
            return enoun.substring(0, enoun.length() - 1) + "ves" + postfix;
        } else if (enoun.endsWith("fe")) {
            return enoun.substring(0, enoun.length() - 2) + "ves" + postfix;
        } else if (enoun.length() >= 4 && enoun.endsWith("ouse")
                && ("mMlL".indexOf(enoun.charAt(enoun.length() - 5)) > -1)) {
            return enoun.substring(0, enoun.length() - 4) + "ice" + postfix;
        } else if (enoun.endsWith("oose") && !enoun.endsWith("caboose")
                && !enoun.endsWith("noose")) {
            return enoun.substring(0, enoun.length() - 4) + "eese" + postfix;
        } else if (enoun.endsWith("ooth")) {
            return enoun.substring(0, enoun.length() - 4) + "eeth" + postfix;
        } else if (enoun.endsWith("foot")) {
            return enoun.substring(0, enoun.length() - 4) + "feet" + postfix;
        } else if (enoun.endsWith("child")) {
            return enoun + "ren" + postfix;
        } else if (enoun.endsWith("eau")) {
            return enoun + "x" + postfix;
        } else if (enoun.endsWith("ato")) {
            return enoun + "es" + postfix;
        } else if (enoun.endsWith("ium")) {
            return enoun.substring(0, enoun.length() - 2) + "a" + postfix;
        } else if (enoun.endsWith("alga") || enoun.endsWith("hypha")
                || enoun.endsWith("larva")) {
            return enoun + "e" + postfix;
        } else if ((enoun.length() > 3) && enoun.endsWith("us")
                && !(enoun.endsWith("lotus") || enoun.endsWith("wumpus"))) {
            return enoun.substring(0, enoun.length() - 2) + "i" + postfix;
        } else if (enoun.endsWith("man") && !(enoun.endsWith("shaman")
                || enoun.endsWith("human"))) {
            return enoun.substring(0, enoun.length() - 3) + "men" + postfix;
        } else if (enoun.endsWith("rtex") || enoun.endsWith("index")) {
            return enoun.substring(0, enoun.length() - 2) + "ices" + postfix;
        } else if (enoun.endsWith("trix")) {
            return enoun.substring(0, enoun.length() - 1) + "ces" + postfix;
        } else if (enoun.endsWith("sis")) {
            return enoun.substring(0, enoun.length() - 2) + "es" + postfix;
        } else if (enoun.endsWith("erinys")) { // || enoun.endsWith("cyclops")

            return enoun.substring(0, enoun.length() - 1) + "es" + postfix;
        } else if (enoun.endsWith("mumak")) {
            return enoun + "il" + postfix;
        } else if (enoun.endsWith("djinni") || enoun.endsWith("efreeti")) {
            return enoun.substring(0, enoun.length() - 1) + postfix;
        } else if (enoun.endsWith("porcini") || (enoun.endsWith("porcino"))) {
            return enoun.substring(0, enoun.length() - 1) + "i" + postfix;
        } else if (enoun.length() > 2 && enoun.endsWith("y")
                && consonant_p(enoun.charAt(enoun.length() - 2))) {
            return enoun.substring(0, enoun.length() - 1) + "ies" + postfix;

            // If the word is already in plural form, return it unchanged.
        } else if (!singular(enoun).equals(enoun)) {
            return enoun + postfix;

            // last special case: Does the word end with "ch", "sh", "s", "x"
            // oder "z"?
        } else if (enoun.endsWith("ch") || enoun.endsWith("sh")
                || (enoun.length() > 1
                && "sxz".indexOf(enoun.charAt(enoun.length() - 1)) > -1)) {
            return enoun + "es" + postfix;
        } else {
            // no special case matched, so use the boring default plural rule
            return enoun + "s" + postfix;
        }
    }

    /**
     * @return the singular form of the given noun if not already given in
     * singular form.
     *
     * @param enoun The noun to examine
     */
    public static String singular(final String enoun) {
        if (enoun == null) {
            return null;
        }
        String result = enoun;
        String postfix = "";

        int position = result.indexOf('+');
        if (position != -1) {
            postfix = result.substring(position - 1);
            result = result.substring(0, position - 1);
        }

        // in "of"-phrases build only the singular of the first part
        if (result.contains(" of ")) {
            return singular(result.substring(0, result.indexOf(" of ")))
                    + result.substring(result.indexOf(" of ")) + postfix;

            // first of all handle words which do not change
        } else if (result.endsWith("money") || result.endsWith("dice")
                || result.endsWith("porcini") || result.endsWith("sheep")
                || result.equals("deer") || result.equals("moose")) {
            return result + postfix;

            // now all the special cases
        } else if (result.endsWith("staffs") || result.endsWith("chiefs")) {
            return result.substring(0, result.length() - 1) + postfix;
        } else if (result.length() > 4 && result.endsWith("ves")
                && ("aeiourl".indexOf(result.charAt(result.length() - 4)) > -1)
                && !result.endsWith("knives")) {
            return result.substring(0, result.length() - 3) + "f" + postfix;
        } else if (result.endsWith("ves")) {
            return result.substring(0, result.length() - 3) + "fe" + postfix;
        } else if (result.endsWith("houses")) {
            return result.substring(0, result.length() - 1) + postfix;
        } else if (result.length() > 3 && result.endsWith("ice")
                && ("mMlL".indexOf(result.charAt(result.length() - 4)) > -1)) {
            return result.substring(0, result.length() - 3) + "ouse" + postfix;
        } else if (result.endsWith("eese") && !result.endsWith("cabeese")
                && !result.endsWith("cheese")) {
            return result.substring(0, result.length() - 4) + "oose" + postfix;
        } else if (result.endsWith("eeth")) {
            return result.substring(0, result.length() - 4) + "ooth" + postfix;
        } else if (result.endsWith("feet")) {
            return result.substring(0, result.length() - 4) + "foot" + postfix;
        } else if (result.endsWith("children")) {
            return result.substring(0, result.length() - 3) + postfix;
        } else if (result.endsWith("eaux")) {
            return result.substring(0, result.length() - 1) + postfix;
        } else if (result.endsWith("atoes")) {
            return result.substring(0, result.length() - 2) + postfix;
            // don't transform "wikipedia" to "wikipedium" -> endswith("ia") is not enough
        } else if (result.endsWith("helia") || result.endsWith("sodia")) {
            return result.substring(0, result.length() - 1) + "um" + postfix;
        } else if (result.endsWith("algae") || result.endsWith("hyphae")
                || result.endsWith("larvae")) {
            return result.substring(0, result.length() - 1) + postfix;
        } else if ((result.length() > 2) && result.endsWith("ei")) {
            return result.substring(0, result.length() - 1) + "us" + postfix;
        } else if (result.endsWith("men")) {
            return result.substring(0, result.length() - 3) + "man" + postfix;
        } else if (result.endsWith("matrices")) {
            return result.substring(0, result.length() - 4) + "ix" + postfix;
        } else if (result.endsWith("ices")) { // indices, vertices, ...

            return result.substring(0, result.length() - 4) + "ex" + postfix;
        } else if (result.endsWith("erinyes")) { // || result.endsWith("cyclopes")

            return result.substring(0, result.length() - 2) + "s" + postfix;
        } else if (result.endsWith("erinys") || result.endsWith("cyclops")) {
            return result + postfix; // singular detected

        } else if (result.endsWith("mumakil")) {
            return result.substring(0, result.length() - 2) + postfix;
        } else if (result.endsWith("djin")) {
            return result + "ni" + postfix;
        } else if (result.endsWith("djinn") || result.endsWith("efreet")) {
            return result + "i" + postfix;
        } else if (result.endsWith("lotus") || result.endsWith("wumpus")
                || result.endsWith("deus")) {
            return result + postfix;
        } else if (result.endsWith("cabooses")) {
            return result.substring(0, result.length() - 1) + postfix;
        } else if (result.endsWith("yses") || result.endsWith("ysis")) {
            return result.substring(0, result.length() - 2) + "is" + postfix;
        } else if (result.length() > 3 && result.endsWith("es")
                && (("zxs".indexOf(result.charAt(result.length() - 3)) > -1)
                || (result.endsWith("ches") || result.endsWith("shes")))
                && !result.endsWith("axes") && !result.endsWith("bardiches")
                && !result.endsWith("nooses")) {
            return result.substring(0, result.length() - 2) + postfix;
        } else if (result.length() > 4 && result.endsWith("ies")
                && consonant_p(result.charAt(result.length() - 4))
                && !result.endsWith("zombies")) {
            return result.substring(0, result.length() - 3) + "y" + postfix;
            // no special case matched, so look for the standard "s" plural
        } else if (result.endsWith("s") && !result.endsWith("ss")) {
            return result.substring(0, result.length() - 1) + postfix;
        } else {
            return result + postfix;
        }
    }

    /**
     * @return either the plural or singular form of the given noun, depending
     * on the quantity.
     *
     * @param quantity The quantity to examine
     * @param noun The noun to examine
     */
    public static String plnoun(int quantity, String noun) {
        String enoun = fullForm(noun);
        return (quantity == 1 ? enoun : plural(noun));
    }

    /**
     * @return either the plural or singular form of the given noun, depending
     * on the quantity; also prefixes the quantity.
     *
     * @param quantity The quantity to examine
     * @param noun The noun to examine
     */
    public static String quantityplnoun(int quantity, String noun) {
        return "" + quantity + " " + plnoun(quantity, noun);
    }

    /**
     * @return either the plural or singular form of the given noun, depending
     * on the quantity; also prefixes the quantity and prints the noun with a
     * hash prefix.
     *
     * @param quantity The quantity to examine
     * @param noun The noun to examine
     */
    public static String quantityplnounWithHash(int quantity, String noun) {
        StringBuilder sb = new StringBuilder(Integer.toString(quantity));

        String result = noun;
        result = plnoun(quantity, result);

        if (noun.indexOf(' ') != -1) {
            sb.append(" #'").append(result).append("'");
        } else {
            sb.append(" #");
            sb.append(result);
        }

        return sb.toString();
    }

    /**
     * Is the character a vowel?
     *
     * @param c The character to examine
     * @return true if c is a vowel, false otherwise
     */
    protected static boolean vowel_p(char c) {
        char l = Character.toLowerCase(c);
        return ((l == 'a') || (l == 'e') || (l == 'i') || (l == 'o') || (l == 'u'));
    }

    /**
     * Is the character a consonant?
     *
     * @param c The character to examine
     * @return true if c is a consonant, false otherwise
     */
    protected static boolean consonant_p(char c) {
        return !vowel_p(c);
    }

    /**
     * first, second, third, ...
     *
     * @param n a number
     * @return first, second, third, ...
     */
    public static String ordered(int n) {
        switch (n) {
            case 1:
                return "first";
            case 2:
                return "second";
            case 3:
                return "third";
            default:
                LOG.log(Level.SEVERE,
                        "Grammar.ordered not implemented for: {0}", n);
                return Integer.toString(n);
        }
    }

    /**
     * Helper function to nicely formulate an enumeration of a collection.
     * <p>
     * For example, for a collection containing the 3 elements x, y, z, @return
     * the string "x, y, and z".
     *
     * @param collection The collection whose elements should be enumerated
     * @return A nice String representation of the collection
     */
    public static String enumerateCollection(Collection<String> collection) {
        if (collection == null) {
            return "";
        }

        String[] elements = collection.toArray(new String[collection.size()]);
        switch (elements.length) {
            case 0:
                return "";
            case 1:
                return quoteHash(elements[0]);
            case 2:
                return quoteHash(elements[0]) + " and " + quoteHash(elements[1]);
            default:
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < elements.length - 1; i++) {
                    sb.append(quoteHash(elements[i])).append(", ");
                }
                sb.append("and ").append(quoteHash(elements[elements.length - 1]));

                return sb.toString();
        }
    }

    /**
     * To let the client display compound words like "#battle axe" in blue, we
     * put the whole item name in quotes.
     *
     * @param str
     * @return
     */
    public static String quoteHash(String str) {
        if (str != null) {
            int idx = str.indexOf('#');

            if (idx != -1 && str.indexOf(' ', idx) != -1 && str.charAt(idx + 1) != '\'') {
                return str.substring(0, idx) + "#'" + str.substring(idx + 1) + '\'';
            }
        }

        return str;
    }

    /**
     * Converts numbers into their textual representation.
     *
     * @param n a number
     * @return one, two, three, ...
     */
    public static String numberString(int n) {
        switch (n) {
            case 0:
                return "no";
            case 1:
                return "one";
            case 2:
                return "two";
            case 3:
                return "three";
            case 4:
                return "four";
            case 5:
                return "five";
            case 6:
                return "six";
            case 7:
                return "seven";
            case 8:
                return "eight";
            case 9:
                return "nine";
            case 10:
                return "ten";
            case 11:
                return "eleven";
            case 12:
                return "twelve";
            default:
                LOG.log(Level.SEVERE,
                        "Grammar.ordered not implemented for: {0}", n);
                return Integer.toString(n);
        }
    }

    /**
     * Interprets number texts.
     *
     * @param text a number
     * @return one, two, three, ...
     */
    public static Integer number(String text) {
        if (text.equals("no") || text.equals("zero")) {
            return 0;
        }
        if (text.equals("a") || text.equals("an")) {
            return 1;
        }
        if (text.equals("one")) {
            return 1;
        }
        if (text.equals("two")) {
            return 2;
        }
        if (text.equals("three")) {
            return 3;
        }
        if (text.equals("four")) {
            return 4;
        }
        if (text.equals("five")) {
            return 5;
        }
        if (text.equals("six")) {
            return 6;
        }
        if (text.equals("seven")) {
            return 7;
        }
        if (text.equals("eight")) {
            return 8;
        }
        if (text.equals("nine")) {
            return 9;
        }
        if (text.equals("ten")) {
            return 10;
        }
        if (text.equals("eleven")) {
            return 11;
        }
        if (text.equals("twelve")) {
            return 12;
        } else {
            // also handle "a dozen", ...
            return null;
        }
    }

    /**
     * Normalize the given regular verb, or return null if not applicable. Note:
     * Some words like "close" are returned without the trailing "e" character.
     * This is handled in WordList.normalizeVerb().
     *
     * @param word
     * @return normalized string
     */
    public static String normalizeRegularVerb(String word) {
        if (word.length() > 4 && (word.endsWith("ed") || word.endsWith("es"))) {
            if (word.charAt(word.length() - 4) == word.charAt(word.length() - 3)) {
                return word.substring(0, word.length() - 3);
            } else {
                return word.substring(0, word.length() - 2);
            }
        } else if (word.length() > 3 && word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        } else if (isGerund(word)) {
            return word.substring(0, word.length() - 3);
        } else {
            return null;
        }
    }

    /**
     * Check the given verb for gerund form, e.g. "doing".
     *
     * @param word
     * @return
     */
    public static boolean isGerund(String word) {
        if (word.length() > 4 && word.endsWith("ing")) {
            // Is there a vowel in the preceding characters?
            for (int i = word.length() - 3; --i >= 0;) {
                if (vowel_p(word.charAt(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check the given word for derived adjectives like "magical" or "nomadic".
     *
     * @param word
     * @return
     */
    public static boolean isDerivedAdjective(String word) {
        return word.length() > 4 && word.endsWith("al") || word.endsWith("ic");
    }

    /**
     * Normalize the given derived adjective, or return null if not applicable.
     *
     * @param word
     * @return normalized string
     */
    public static String normalizeDerivedAdjective(String word) {
        if (word.length() > 4 && (word.endsWith("al") || word.endsWith("ic"))) {
            return word.substring(0, word.length() - 2);
        } else {
            return null;
        }
    }

    private Grammar() {
    }
}
