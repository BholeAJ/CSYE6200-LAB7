package ui;

import model.User;
import util.ImageUtil;
import util.InputValidator;

import javax.swing.*;
import java.awt.*;

/**
 * The "photo on the left, details on the right" dialog used after a save,
 * after an update, and when a table row is double-clicked.
 */
public final class UserDetailsDialog {

    private UserDetailsDialog() {
    }

    public static void show(Component parent, User user, String title) {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon icon = ImageUtil.loadScaled(user.getPhotoPath(), 150, 200);
        if (icon != null) {
            JLabel photo = new JLabel(icon);
            photo.setVerticalAlignment(SwingConstants.TOP);
            panel.add(photo, BorderLayout.WEST);
        }

        panel.add(new JLabel(buildHtml(user)), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(parent, panel, title, JOptionPane.PLAIN_MESSAGE);
    }

    private static String buildHtml(User u) {
        StringBuilder sb = new StringBuilder("<html><body style='font-size:11pt'>");
        line(sb, "First Name", u.getFirstName());
        line(sb, "Last Name", u.getLastName());
        line(sb, "Gender", u.getGender());
        line(sb, "Date of Birth", u.getDob() == null ? "" : u.getDob().toString());
        line(sb, "Age", String.valueOf(u.getAge()));
        line(sb, "Phone", u.getPhone());
        line(sb, "Email", u.getEmail());
        line(sb, "Continent", u.getContinent());
        line(sb, "Experience", u.getExperience());
        line(sb, "Hobbies", u.getHobbies());
        line(sb, "Photo Path", u.getPhotoPath());
        sb.append("</body></html>");
        return sb.toString();
    }

    private static void line(StringBuilder sb, String label, String value) {
        sb.append("<b>").append(label).append(": ")
          .append(escape(InputValidator.isBlank(value) ? "-" : value.trim()))
          .append("</b><br>");
    }

    /** User text goes into an HTML label, so angle brackets and ampersands must be escaped. */
    private static String escape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
