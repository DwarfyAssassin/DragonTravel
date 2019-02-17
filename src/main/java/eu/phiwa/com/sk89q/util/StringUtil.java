/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.util;

import java.util.Collection;
import java.util.Map;

public final class StringUtil {
    private StringUtil() {
    }

    public static String trimLength(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        }
        return str;
    }

    public static String joinString(String[] str, String delimiter, int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(str[initialIndex]);
        for (int i = initialIndex + 1; i < str.length; ++i) {
            buffer.append(delimiter).append(str[i]);
        }
        return buffer.toString();
    }

    public static String joinQuotedString(String[] str, String delimiter, int initialIndex, String quote) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(quote);
        buffer.append(str[initialIndex]);
        buffer.append(quote);
        for (int i = initialIndex + 1; i < str.length; ++i) {
            buffer.append(delimiter).append(quote).append(str[i]).append(quote);
        }
        return buffer.toString();
    }

    public static String joinString(String[] str, String delimiter) {
        return StringUtil.joinString(str, delimiter, 0);
    }

    public static String joinString(Object[] str, String delimiter, int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(str[initialIndex].toString());
        for (int i = initialIndex + 1; i < str.length; ++i) {
            buffer.append(delimiter).append(str[i].toString());
        }
        return buffer.toString();
    }

    public static String joinString(int[] str, String delimiter, int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(Integer.toString(str[initialIndex]));
        for (int i = initialIndex + 1; i < str.length; ++i) {
            buffer.append(delimiter).append(Integer.toString(str[i]));
        }
        return buffer.toString();
    }

    public static String joinString(Collection<?> str, String delimiter, int initialIndex) {
        if (str.size() == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        for (Object o : str) {
            if (i >= initialIndex) {
                if (i > 0) {
                    buffer.append(delimiter);
                }
                buffer.append(o.toString());
            }
            ++i;
        }
        return buffer.toString();
    }

    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        int i = 0;
        while (i <= n) {
            p[i] = i++;
        }
        for (int j = 1; j <= m; ++j) {
            char tj = t.charAt(j - 1);
            d[0] = j;
            for (i = 1; i <= n; ++i) {
                int cost = s.charAt(i - 1) == tj ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }
            int[] _d = p;
            p = d;
            d = _d;
        }
        return p[n];
    }

    public static <T extends Enum<?>> T lookup(Map<String, T> lookup, String name, boolean fuzzy) {
        String testName = name.replaceAll("[ _]", "").toLowerCase();
        Enum<?> type = (Enum<?>)lookup.get(testName);
        if (type != null) {
            return (T)type;
        }
        if (!fuzzy) {
            return null;
        }
        int minDist = -1;
        for (Map.Entry<String, T> entry : lookup.entrySet()) {
            int dist;
            String key = entry.getKey();
            if (key.charAt(0) != testName.charAt(0) || (dist = StringUtil.getLevenshteinDistance(key, testName)) >= minDist && minDist != -1 || dist >= 2) continue;
            minDist = dist;
            type = (Enum<?>)entry.getValue();
        }
        return (T)type;
    }
}

