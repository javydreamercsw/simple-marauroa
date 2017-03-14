package simple.common;

import java.util.*;
import java.util.regex.Pattern;

public class BadWordsStringFilter {

    private static final String QUESTION_MARK = "?";

    private static final String CLOSE_BRACKET = "]";

    private static final String OPEN_BRACKET = "[";

    private static final String WORD_BOUNDARY = "\\b";

    private final List<String> badWords;

    private final Map<String, List<String>> possibleLetterReplacements
            = buildReplacements();
    private final Set<String> possibleInterLetterFillings
            = buildInterLetterFillings();

    private String fillingString;

    public BadWordsStringFilter(final List<String> badWords) {
        this.badWords = new LinkedList<>();
        badWords.forEach((word) -> {
            this.badWords.add(this.buildRegEx(word));
        });
    }

    private String buildRegEx(final String word) {
        StringBuilder sb = new StringBuilder();
        String lowerCaseWord = word.toLowerCase();
        sb.append(WORD_BOUNDARY);
        sb.append(this.getPossibleInterLetterFilling());
        for (int i = 0; i < word.length(); i++) {
            sb.append(OPEN_BRACKET);
            char currentChar = lowerCaseWord.charAt(i);
            sb.append(currentChar);
            if (this.possibleLetterReplacements.containsKey(Character
                    .toString(currentChar))) {
                this.possibleLetterReplacements.get(Character
                        .toString(currentChar)).forEach((replacer) -> {
                    sb.append(replacer);
                });
            }
            sb.append(CLOSE_BRACKET);
            if (i < word.length() - 1) {
                sb.append(this.getPossibleInterLetterFilling());
            }
        }
        sb.append(WORD_BOUNDARY);
        return sb.toString();
    }

    private String getPossibleInterLetterFilling() {
        if (this.fillingString == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(OPEN_BRACKET);
            this.possibleInterLetterFillings.forEach((filler) -> {
                sb.append(filler);
            });
            sb.append(CLOSE_BRACKET);
            sb.append(QUESTION_MARK);
            this.fillingString = sb.toString();
        }
        return this.fillingString;
    }

    private Set<String> buildInterLetterFillings() {
        Set<String> fillings = new HashSet<>();
        fillings.add(".");
        fillings.add("_");
        fillings.add("-");
        fillings.add("#");
        return fillings;
    }

    private Map<String, List<String>> buildReplacements() {
        Map<String, List<String>> replacement = new HashMap<>();
        String[] aArray = {"4", "@"};
        replacement.put("a", Arrays.asList(aArray));
        String[] iArray = {"1"};
        replacement.put("i", Arrays.asList(iArray));
        String[] sArray = {"5"};
        replacement.put("s", Arrays.asList(sArray));
        String[] eArray = {"3"};
        replacement.put("e", Arrays.asList(eArray));
        String[] lArray = {"7"};
        replacement.put("l", Arrays.asList(lArray));
        String[] bArray = {"8"};
        replacement.put("b", Arrays.asList(bArray));
        return replacement;
    }

    public boolean containsBadWord(final String text) {
        StringTokenizer st = new StringTokenizer(text);
        while (st.hasMoreTokens()) {
            if (this.isBadWord(st.nextToken())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBadWord(final String word) {
        String lowerCaseWord = word.toLowerCase();
        return this.badWords.stream().map((badWord)
                -> Pattern.compile(badWord)).map((p)
                -> p.matcher(lowerCaseWord)).anyMatch((m)
                -> (m.matches()));
    }

    public String censorBadWords(final String text) {
        String returnString = text;
        for (String replacer : this.badWords) {
            returnString = returnString.replaceAll(replacer, "*CENSORED*");
        }
        return returnString;
    }

    public List<String> listBadWordsInText(final String text) {
        List<String> returnList = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(text);
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            if (this.isBadWord(word)) {
                returnList.add(word);
            }
        }
        return returnList;
    }

}
