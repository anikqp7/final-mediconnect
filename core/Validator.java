package core;

/** Input format validation (FR17). All checks in one place. */
public class Validator {

    private Validator() {}

    public static boolean isEmail(String s) {
        return s != null && s.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isPhone(String s) {
        return s != null && s.matches("\\d{11}");
    }

    public static boolean isDate(String s) {
        return s != null && s.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    public static boolean isTime(String s) {
        return s != null && s.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }
}