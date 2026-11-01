package com.banking.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Server-side input validation (never trust only the JavaScript checks). */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");
    private static final Pattern ACCOUNT_NO = Pattern.compile("^[0-9]{12}$");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000000"); // 1 crore per transaction

    private Validator() { }

    public static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    public static boolean validEmail(String s) { return s != null && s.length() <= 120 && EMAIL.matcher(s).matches(); }
    public static boolean validPhone(String s) { return s != null && PHONE.matcher(s).matches(); }
    public static boolean validAccountNumber(String s) { return s != null && ACCOUNT_NO.matcher(s).matches(); }

    /** At least 8 characters with a letter and a digit. */
    public static boolean validPassword(String s) {
        return s != null && s.length() >= 8 && s.length() <= 64
                && s.chars().anyMatch(Character::isLetter) && s.chars().anyMatch(Character::isDigit);
    }

    /** Returns a positive amount with at most 2 decimals, or null if invalid. */
    public static BigDecimal parseAmount(String s) {
        if (s == null) return null;
        try {
            BigDecimal a = new BigDecimal(s.trim());
            if (a.signum() <= 0 || a.stripTrailingZeros().scale() > 2 || a.compareTo(MAX_AMOUNT) > 0) return null;
            return a.setScale(2);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
