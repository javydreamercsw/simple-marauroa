/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Each parameter name in the control string is replaced by its value.
 *
 * @author matthias
 */
public class StringFormatter {

    /** start of each parameter. no escaping. */
    private static final String PARAMETER_START = "${";
    /** end of each parameter. no escaping. */
    private static final String PARAMETER_END = "}";
    /** shows that the cache should be refreshed */
    private boolean refreshCache;
    /** cached formatted String */
    private String cachedString;
    /** the static parts of the control-string */
    private List<String> staticParts;
    /** names of the parameter in the correct order */
    private List<String> parameterPositions;
    /** names/values of the parameter */
    private Map<String, String> parameter;

    /** Creates a new instance of StringFormatter
     * @param formatString
     */
    public StringFormatter(String formatString) {
        staticParts = new ArrayList<String>();
        parameterPositions = new ArrayList<String>();
        parameter = new HashMap<String, String>();
        String current = formatString;
        int index;
        boolean hasStart = false;

        do {
            index = current.indexOf(hasStart ? PARAMETER_END : PARAMETER_START);
            if (index >= 0) {
                // we found something
                if (hasStart) {
                    // found the end of the parameter definition
                    String param = current.substring(PARAMETER_START.length(), index);
                    current = current.substring(index + PARAMETER_END.length());
                    parameter.put(param, "");
                    parameterPositions.add(param);
                } else {
                    // found start
                    String s = current.substring(0, index);
                    current = current.substring(index);
                    staticParts.add(s);
                }
                hasStart = !hasStart;
            }
        } while (index >= 0);

        staticParts.add(current);
    }

    /** sets the value of a parameter
     * @param param
     * @param value
     */
    public void set(String param, String value) {
        if (parameter.containsKey(param)) {
            parameter.put(param, value);
            refreshCache = true;
        }
    }

    /** sets the value of a parameter
     * @param param
     * @param value
     */
    public void set(String param, int value) {
        if (parameter.containsKey(param)) {
            parameter.put(param, Integer.toString(value));
            refreshCache = true;
        }
    }

    /** toString formats the string */
    @Override
    public String toString() {
        if ((cachedString == null) || refreshCache) {
            // recalculate the string
            StringBuilder buf = new StringBuilder();
            Iterator<String> staticIt = staticParts.iterator();
            Iterator<String> paramIt = parameterPositions.iterator();
            while (staticIt.hasNext()) {
                buf.append(staticIt.next());
                if (paramIt.hasNext()) {
                    buf.append(parameter.get(paramIt.next()));
                }
            }
            cachedString = buf.toString();
        }

        return cachedString;
    }

    /** main method
     * @param args
     */
    public static void main(String[] args) {
        StringFormatter formatter = new StringFormatter("test");
        System.out.println(formatter.toString());
        formatter = new StringFormatter("<test>${test}</test>");
        System.out.println(formatter.toString());
        formatter.set("test", "hello");
        System.out.println(formatter.toString());
        formatter = new StringFormatter("<first>${first}</first><2nd>${2nd}</2nd><3rd>${3rd}</3rd>");
        System.out.println(formatter.toString());
        formatter.set("3rd", "last");
        formatter.set("first", "winner");
        formatter.set("2nd", "another one");
        System.out.println(formatter.toString());

    }
}
