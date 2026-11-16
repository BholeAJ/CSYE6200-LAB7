package util;

import model.User;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/** Shared validation so the Create form and the Edit panel behave identically. */
public final class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]{2,}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("[A-Za-z][A-Za-z\\-' ]{1,49}");

    private InputValidator() {
    }

    /**
     * @return null when the user object is valid, otherwise a message to show.
     */
    public static String validate(User u) {
        if (isBlank(u.getFirstName())) return "First name is required.";
        if (isBlank(u.getLastName()))  return "Last name is required.";

        if (!NAME_PATTERN.matcher(u.getFirstName().trim()).matches())
            return "First name must be 2-50 letters.";

        if (!NAME_PATTERN.matcher(u.getLastName().trim()).matches())
            return "Last name must be 2-50 letters.";

        if (isBlank(u.getGender())) return "Please select a gender.";

        if (u.getDob() == null) return "Please choose a valid date of birth.";
        if (u.getDob().isAfter(LocalDate.now())) return "Date of birth cannot be in the future.";

        if (u.getAge() < 0 || u.getAge() > 120) return "Age must be between 0 and 120.";

        if (isBlank(u.getPhone())) return "Phone number is required.";
        if (digitsOnly(u.getPhone()).length() != 10)
            return "Phone number must contain exactly 10 digits (e.g. 416-399-0346).";

        if (isBlank(u.getEmail())) return "Email is required.";
        if (!EMAIL_PATTERN.matcher(u.getEmail().trim()).matches())
            return "Enter a valid email address (e.g. name@example.com).";

        if (isBlank(u.getContinent()) || u.getContinent().startsWith("--"))
            return "Please select a continent.";

        if (u.getExperience() != null && u.getExperience().length() > 500)
            return "Experience must be 500 characters or fewer.";

        if (u.getHobbies() != null && u.getHobbies().length() > 500)
            return "Hobbies must be 500 characters or fewer.";

        if (!isBlank(u.getPhotoPath()) && !new File(u.getPhotoPath()).isFile())
            return "The selected photo file could not be found.";

        return null;
    }

    /** Builds a LocalDate from the three combo boxes, or null when the date is impossible. */
    public static LocalDate toDate(int year, int month, int day) {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return null;   // e.g. 31 February
        }
    }

    /** Age in completed years, used to keep the Age field in sync with the date of birth. */
    public static int ageFrom(LocalDate dob) {
        if (dob == null || dob.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(dob, LocalDate.now()).getYears();
    }

    /** Formats 10 digits as 416-399-0346; anything else is returned trimmed. */
    public static String formatPhone(String raw) {
        String digits = digitsOnly(raw);
        if (digits.length() == 10) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        }
        return raw == null ? "" : raw.trim();
    }

    public static String digitsOnly(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
