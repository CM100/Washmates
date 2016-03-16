package library.singularity.com.data.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicValidator {

    public static boolean isValidString(String string) {
        if (string == null || (string != null && string.trim().length() == 0)) return false;

        return true;
    }

    public static boolean isValidString(String string, int minLength) {
        if (!isValidString(string)) return false;

        if (minLength <= 0) return true;

        if (string.length() >= minLength) return true;

        return false;
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        boolean emailValid = matcher.matches();

        if (email == null || (email != null && email.trim().length() == 0) || !emailValid) {
            return false;
        }

        return true;
    }

    public static boolean isNumberInRange(long number, long min, long max) {
        if (number >= min && number <= max) {
            return true;
        }

        return false;
    }

    public static boolean isDateValid(Date date) {
        if (date == null) return false;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        if (calendar.getTime().after(date)) return false;

        return true;
    }

    public static boolean areDatesValid(Date startDate, Date endDate) {
        if (!isDateValid(startDate) || !isDateValid(endDate)) return false;

        if (startDate.after(endDate) || startDate.equals(endDate)) return false;

        return true;
    }
}
