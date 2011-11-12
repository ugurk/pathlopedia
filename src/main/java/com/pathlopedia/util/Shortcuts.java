package com.pathlopedia.util;

public final class Shortcuts {
    public static void validateStringLength(
            String field, String text, int max, boolean nullok)
            throws IllegalArgumentException {
        if (text != null && text.length() > max)
            throw new IllegalArgumentException(
                    "'" + field + "' field (" + text +
                            ") cannot be greater than " +
                            max + " characters.");
        if (text == null && !nullok)
            throw new IllegalArgumentException("NULL '" + field + "' field!");
    }

    public static void validateStringLength(
            String field, String text, int min, int max, boolean nullok)
            throws IllegalArgumentException {
        validateStringLength(field, text, max, nullok);
        if (text != null && text.length()  < min)
            throw new IllegalArgumentException(
                    "'" + field + "' field (" + text +
                            ") cannot be smaller than " +
                            min + " characters.");
    }

    public static String trim(String inp) {
        if (inp == null) return null;
        String out = inp.trim();
        if (out.length() == 0) return null;
        return out;
    }

    public static boolean parseBoolean(String s) {
        if (s != null) {
            if (s.equals("true")) return true;
            if (s.equals("false")) return false;
        }
        throw new IllegalArgumentException("Invalid boolean expression: " + s);
    }
}
