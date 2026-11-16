package ui;

import dao.UserDAO;
import model.User;
import util.ImageUtil;
import util.InputValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * VIEW 1 - "Create User".
 * Collects input, validates it, and INSERTs a new row into MySQL.
 */
public class FormJPanel extends JPanel {

    static final String[] MONTHS =
            {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    static final String[] CONTINENTS =
            {"-- Select --", "Africa", "Asia", "Europe", "North America", "South America", "Oceania", "Antarctica"};

    private final UserDAO dao = new UserDAO();

    private final JTextField firstNameField = new JTextField(24);
    private final JTextField lastNameField  = new JTextField(24);

    private final JRadioButton maleRadio   = new JRadioButton("Male", true);
    private final JRadioButton femaleRadio = new JRadioButton("Female");

    private final JComboBox<Integer> dayCombo   = new JComboBox<>();
    private final JComboBox<String>  monthCombo = new JComboBox<>(MONTHS);
    private final JComboBox<Integer> yearCombo  = new JComboBox<>();

    private final JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(18, 0, 120, 1));

    private final JTextField phoneField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);

    private final JComboBox<String> continentCombo = new JComboBox<>(CONTINENTS);

    private final JTextArea experienceArea = new JTextArea(4, 24);
    private final JTextArea hobbiesArea    = new JTextArea(4, 24);

    private final JTextField photoField = new JTextField(18);
    private final JLabel photoPreview   = new JLabel("No photo", SwingConstants.CENTER);

    public FormJPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        fillDateCombos();
        add(buildContent(), new GridBagConstraints());
    }

    // ---------- construction ----------

    private void fillDateCombos() {
        for (int d = 1; d <= 31; d++) {
            dayCombo.addItem(d);
        }
        int thisYear = LocalDate.now().getYear();
        for (int y = thisYear; y >= 1930; y--) {
            yearCombo.addItem(y);
        }
        dayCombo.setSelectedItem(1);
        monthCombo.setSelectedIndex(0);
        yearCombo.setSelectedItem(2000);

        // Keep Age in step with the date of birth (the spinner can still be overridden).
        dayCombo.addActionListener(e -> syncAge());
        monthCombo.addActionListener(e -> syncAge());
        yearCombo.addActionListener(e -> syncAge());
        syncAge();
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Create User");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 10, 25, 10);
        content.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);

        int row = 1;
        addRow(content, gbc, row++, requiredLabel("First Name:"), firstNameField);
        addRow(content, gbc, row++, requiredLabel("Last Name:"),  lastNameField);
        addRow(content, gbc, row++, requiredLabel("Gender:"),     buildGenderPanel());
        addRow(content, gbc, row++, requiredLabel("Date of Birth:"), buildDobPanel());
        addRow(content, gbc, row++, requiredLabel("Age:"),        sizedSpinner());
        addRow(content, gbc, row++, requiredLabel("Phone Number:"), phoneField);
        addRow(content, gbc, row++, requiredLabel("Email:"),      emailField);
        addRow(content, gbc, row++, requiredLabel("Continent:"),  continentCombo);
        addRow(content, gbc, row++, plainLabel("Experience:"),    scrolled(experienceArea));
        addRow(content, gbc, row++, plainLabel("Hobbies:"),       scrolled(hobbiesArea));
        addRow(content, gbc, row++, plainLabel("Photo:"),         buildPhotoPanel());

        // preview, buttons and the required-field note sit under the fields
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        content.add(buildPreviewPanel(), gbc);

        gbc.gridy = row++;
        content.add(buildButtonPanel(), gbc);

        JLabel note = new JLabel("<html><font color='red'>*</font> indicates a required field</html>");
        note.setFont(note.getFont().deriveFont(Font.BOLD));
        gbc.gridy = row;
        content.add(note, gbc);

        return content;
    }

    private void addRow(JPanel parent, GridBagConstraints gbc, int row, JLabel label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        parent.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        parent.add(field, gbc);
    }

    private JLabel requiredLabel(String text) {
        JLabel label = new JLabel("<html><font color='red'>*</font> <b>" + text + "</b></html>");
        return label;
    }

    private JLabel plainLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    private JPanel buildGenderPanel() {
        ButtonGroup group = new ButtonGroup();
        group.add(maleRadio);
        group.add(femaleRadio);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        panel.setOpaque(false);
        panel.add(maleRadio);
        panel.add(femaleRadio);
        return panel;
    }

    private JPanel buildDobPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);
        panel.add(plainLabel("Day"));
        panel.add(dayCombo);
        panel.add(plainLabel("Month"));
        panel.add(monthCombo);
        panel.add(plainLabel("Year"));
        panel.add(yearCombo);
        return panel;
    }

    private JPanel sizedSpinner() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        ageSpinner.setPreferredSize(new Dimension(80, 26));
        panel.add(ageSpinner);
        return panel;
    }

    private JScrollPane scrolled(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return new JScrollPane(area);
    }

    private JPanel buildPhotoPanel() {
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> choosePhoto());

        photoField.setEditable(false);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.add(photoField);
        panel.add(browseButton);
        return panel;
    }

    private JPanel buildPreviewPanel() {
        photoPreview.setPreferredSize(new Dimension(150, 200));
        photoPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoPreview.setOpaque(true);
        photoPreview.setBackground(Color.WHITE);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        panel.add(photoPreview);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JButton clearButton  = new JButton("Clear");
        JButton submitButton = new JButton("Submit");

        clearButton.addActionListener(e -> clearForm());
        submitButton.addActionListener(e -> submit());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setOpaque(false);
        panel.add(clearButton);
        panel.add(submitButton);
        return panel;
    }

    // ---------- behaviour ----------

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a photo");
        chooser.setFileFilter(new FileNameExtensionFilter("Images (jpg, jpeg, png, gif)",
                "jpg", "jpeg", "png", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            photoField.setText(file.getAbsolutePath());
            showPreview(file.getAbsolutePath());
        }
    }

    private void showPreview(String path) {
        ImageIcon icon = ImageUtil.loadScaled(path, 150, 200);
        if (icon == null) {
            photoPreview.setIcon(null);
            photoPreview.setText("No photo");
        } else {
            photoPreview.setText("");
            photoPreview.setIcon(icon);
        }
    }

    /** Recalculates the age whenever the date of birth changes. */
    private void syncAge() {
        LocalDate dob = selectedDob();
        if (dob != null) {
            ageSpinner.setValue(InputValidator.ageFrom(dob));
        }
    }

    private LocalDate selectedDob() {
        Integer day  = (Integer) dayCombo.getSelectedItem();
        Integer year = (Integer) yearCombo.getSelectedItem();
        int month    = monthCombo.getSelectedIndex() + 1;
        if (day == null || year == null) {
            return null;
        }
        return InputValidator.toDate(year, month, day);
    }

    private void submit() {
        User user = new User();
        user.setFirstName(firstNameField.getText().trim());
        user.setLastName(lastNameField.getText().trim());
        user.setGender(maleRadio.isSelected() ? "Male" : "Female");
        user.setDob(selectedDob());
        user.setAge(((Number) ageSpinner.getValue()).intValue());
        user.setPhone(InputValidator.formatPhone(phoneField.getText()));
        user.setEmail(emailField.getText().trim());
        user.setContinent((String) continentCombo.getSelectedItem());
        user.setExperience(experienceArea.getText().trim());
        user.setHobbies(hobbiesArea.getText().trim());
        user.setPhotoPath(photoField.getText().trim());

        String error = InputValidator.validate(user);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int newId = dao.insert(user);
            user.setId(newId);
            UserDetailsDialog.show(this, user, "Success - user saved to the database");
            clearForm();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == UserDAO.DUPLICATE_ENTRY) {
                JOptionPane.showMessageDialog(this,
                        "That email is already registered. Please use a different email.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        maleRadio.setSelected(true);
        dayCombo.setSelectedItem(1);
        monthCombo.setSelectedIndex(0);
        yearCombo.setSelectedItem(2000);
        phoneField.setText("");
        emailField.setText("");
        continentCombo.setSelectedIndex(0);
        experienceArea.setText("");
        hobbiesArea.setText("");
        photoField.setText("");
        photoPreview.setIcon(null);
        photoPreview.setText("No photo");
        syncAge();
        firstNameField.requestFocusInWindow();
    }
}
