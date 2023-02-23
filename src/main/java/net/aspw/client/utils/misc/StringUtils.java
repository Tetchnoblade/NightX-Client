package net.aspw.client.utils.misc;


import java.util.Arrays;
import java.util.HashMap;

public final class StringUtils {

    private static final HashMap<String, String> stringCache = new HashMap<>();
    private static final HashMap<String, String> stringReplaceCache = new HashMap<>();
    private static final HashMap<String, String> stringRegexCache = new HashMap<>();
    private static final HashMap<String, String> airCache = new HashMap<>();

    public static String fixString(String str) {
        if (stringCache.containsKey(str)) return stringCache.get(str);

        str = str.replaceAll("\uF8FF", "");//remove air chars

        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if ((int) c > (33 + 65248) && (int) c < (128 + 65248)) {
                sb.append(Character.toChars((int) c - 65248));
            } else {
                sb.append(c);
            }
        }
        String result = sb.toString();
        stringCache.put(str, result);

        return result;
    }

    public static String injectAirString(String str) {
        if (airCache.containsKey(str)) return airCache.get(str);

        StringBuilder stringBuilder = new StringBuilder();

        boolean hasAdded = false;
        for (char c : str.toCharArray()) {
            stringBuilder.append(c);
            if (!hasAdded) stringBuilder.append('\uF8FF');
            hasAdded = true;
        }

        String result = stringBuilder.toString();
        airCache.put(str, result);

        return result;
    }

    public static String toCompleteString(final String[] args, final int start) {
        if (args.length <= start) return "";

        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    public static String replace(final String string, final String searchChars, String replaceChars) {
        return replace(string, searchChars, replaceChars, false);
    }

    public static String replace(final String string, final String searchChars, String replaceChars, boolean forceReload) {
        if (string.isEmpty() || searchChars.isEmpty() || searchChars.equals(replaceChars))
            return string;

        if (!forceReload && stringRegexCache.get(searchChars) != null && stringRegexCache.get(searchChars).equals(replaceChars) && stringReplaceCache.containsKey(string))
            return stringReplaceCache.getOrDefault(string, replace(string, searchChars, replaceChars, true)); // will attempt to retry replacement once again

        if (replaceChars == null)
            replaceChars = "";

        final int stringLength = string.length();
        final int searchCharsLength = searchChars.length();
        final StringBuilder stringBuilder = new StringBuilder(string);

        for (int i = 0; i < stringLength; i++) {
            final int start = stringBuilder.indexOf(searchChars, i);

            if (start == -1) {
                if (i == 0)
                    return string;

                return stringBuilder.toString();
            }

            stringBuilder.replace(start, start + searchCharsLength, replaceChars);
        }

        String result = stringBuilder.toString();
        stringReplaceCache.put(string, result);
        stringRegexCache.put(searchChars, replaceChars);

        return result;
    }
}
