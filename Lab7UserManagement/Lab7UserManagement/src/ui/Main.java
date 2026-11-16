package ui;

import db.DatabaseConnector;

import javax.swing.*;

/** Application entry point. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // keep the default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            if (!DatabaseConnector.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to MySQL.\n\n"
                      + "Checklist:\n"
                      + "  1. Is the MySQL server (or Docker container) running?\n"
                      + "  2. Did you run sql/DatabaseQueries.sql to create lab7db?\n"
                      + "  3. Are the user / password correct in DatabaseConnector.java?\n"
                      + "  4. Is mysql-connector-j-x.x.x.jar on the classpath?",
                        "Database Connection Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new MainFrame().setVisible(true);
        });
    }
}
