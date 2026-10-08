package library.util;

public class IdFormatter {
    public static String formatMemberId(int id) {
        return "M" + String.format("%04d", id);
    }

    public static int parseMemberId(String formatted) {
        return Integer.parseInt(formatted.substring(1));
    }

    public static String formatLoanId(int id) {
        return "L" + String.format("%04d", id);
    }

    public static int parseLoanId(String formatted) {
        return Integer.parseInt(formatted.substring(1));
    }

    public static String formatBookId(int id, String genrePrefix) {
        return genrePrefix + String.format("%04d", id);
    }

    public static int parseBookId(String formatted) {
        return Integer.parseInt(formatted.substring(2));
    }
}
