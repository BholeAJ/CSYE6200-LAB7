package ui;

import dao.UserDAO;
import model.User;
import util.ImageUtil;
import util.InputValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * VIEW 2 - "View Users".
 *
 * The JTable is filled from MySQL (READ). Clicking a row loads the full record
 * into the panel on the left; Edit unlocks the fields, Update writes the change
 * back (UPDATE) and Delete removes the row (DELETE).
 */
public class ViewJPanel extends JPanel {

    private static final String[] COLUMNS =
            {"ID", "First name", "Last name", "Age", "Email", "Continent"};

    private final UserDAO dao = new UserDAO();

    /** Full records behind the table, indexed by model row. */
    private final List<User> users = new ArrayList<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;   // editing happens in the panel on the left
        }
    };
    private final JTable userTable = new JTable(tableModel);

    private final JTextField firstNameField = new JTextField(20);
    private final JTextField lastNameField  = new JTextField(20);

    private final JRadioButton maleRadio   = new JRadioButton("Male");
    private final JRadioButton femaleRadio = new JRadioButton("Female");

    private final JComboBox<Integer> dayCombo   = new JComboBox<>();
    private final JComboBox<String>  monthCombo = new JComboBox<>(FormJPanel.MONTHS);
    private final JComboBox<Integer> yearCombo  = new JComboBox<>();

    private final JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 120, 1));

    private final JTextField phoneField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);

    private final JComboBox<String> continentCombo = new JComboBox<>(FormJPanel.CONTINENTS);

    private final JTextArea experienceArea = new JTextArea(4, 20);
    private final JTextArea hobbiesArea    = new JTextArea(4, 20);

    private final JTextField photoField   = new JTextField(16);
    private final JButton browseButton    = new JButton("Browse...");
    private final JLabel photoPreview     = new JLabel("No photo", SwingConstants.CENTER);

    private final JButton editButton   = new JButton("Edit");
    private final JButton updateButton = new JButton("Update");
    private final JButton deleteButton = new JButton("Delete");

    private boolean editing = false;

    public ViewJPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        fillDateCombos();

        JLabel title = new JLabel("View Users");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(buildDetailsPanel()), buildTablePanel());
        split.setResizeWeight(0.45);
        split.setDividerLocation(470);
        add(split, BorderLayout.CENTER);

        wireEvents();
        setFieldsEnabled(false);
        updateButtonStates();
        populateTable();
    }

    // ---------- construction ----------

    private void fillDateCombos() {
        for (int d = 1; d <= 31; d++) {
            dayCombo.addItem(d);
        }
        for (int y = LocalDate.now().getYear(); y >= 1930; y--) {
            yearCombo.addItem(y);
        }
        ButtonGroup group = new ButtonGroup();
        group.add(maleRadio);
        group.add(femaleRadio);
    }

    private JPanel buildDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);

        int row = 0;
        addRow(panel, gbc, row++, "First Name:", firstNameField);
        addRow(panel, gbc, row++, "Last Name:",  lastNameField);
        addRow(panel, gbc, row++, "Gender:",     flow(maleRadio, femaleRadio));
        addRow(panel, gbc, row++, "Date of Birth:",
                flow(bold("Day"), dayCombo, bold("Month"), monthCombo, bold("Year"), yearCombo));
        addRow(panel, gbc, row++, "Age:", flow(ageSpinner));
        addRow(panel, gbc, row++, "Phone Number:", phoneField);
        addRow(panel, gbc, row++, "Email:", emailField);
        addRow(panel, gbc, row++, "Continent:", continentCombo);
        addRow(panel, gbc, row++, "Experience:", scrolled(experienceArea));
        addRow(panel, gbc, row++, "Hobbies:", scrolled(hobbiesArea));
        addRow(panel, gbc, row++, "Photo:", flow(photoField, browseButton));

        photoPreview.setPreferredSize(new Dimension(140, 180));
        photoPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoPreview.setOpaque(true);
        photoPreview.setBackground(Color.WHITE);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(photoPreview, gbc);

        ageSpinner.setPreferredSize(new Dimension(80, 26));
        return panel;
    }

    private void addRow(JPanel parent, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        parent.add(bold(label), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        parent.add(field, gbc);
    }

    private JLabel bold(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    private JPanel flow(JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);
        for (JComponent c : components) {
            panel.add(c);
        }
        return panel;
    }

    private JScrollPane scrolled(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return new JScrollPane(area);
    }

    private JPanel buildTablePanel() {
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(24);
        userTable.setAutoCreateRowSorter(true);
        userTable.getTableHeader().setReorderingAllowed(false);

        JLabel tip = new JLabel("Tip: click a row to select it, double-click to see full details.");
        tip.setFont(tip.getFont().deriveFont(Font.BOLD));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttons.add(editButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(tip, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void wireEvents() {
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRowSelected();
            }
        });

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    User selected = selectedUser();
                    if (selected != null) {
                        UserDetailsDialog.show(ViewJPanel.this, selected, "User details");
                    }
                }
            }
        });

        editButton.addActionListener(e -> {
            if (editing) {
                cancelEdit();
            } else {
                startEdit();
            }
        });
        updateButton.addActionListener(e -> updateSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        browseButton.addActionListener(e -> choosePhoto());
    }

    // ---------- READ ----------

    /** Loads every row from the database into the JTable. */
    public final void populateTable() {
        try {
            users.clear();
            users.addAll(dao.findAll());

            tableModel.setRowCount(0);
            for (User u : users) {
                tableModel.addRow(new Object[]{
                        u.getId(), u.getFirstName(), u.getLastName(),
                        u.getAge(), u.getEmail(), u.getContinent()
                });
            }
            editing = false;
            clearFields();
            setFieldsEnabled(false);
            updateButtonStates();
        } catch (SQLException ex) {
            error("Could not read from the database: " + ex.getMessage());
        }
    }

    private User selectedUser() {
        int viewRow = userTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        try {
            int modelRow = userTable.convertRowIndexToModel(viewRow);
            if (modelRow < 0 || modelRow >= users.size()) {
                return null;
            }
            return users.get(modelRow);
        } catch (IndexOutOfBoundsException e) {
            // the table model is mid-refresh; treat it as "nothing selected"
            return null;
        }
    }

    private void onRowSelected() {
        User user = selectedUser();
        editing = false;

        if (user == null) {
            clearFields();
        } else {
            loadIntoFields(user);
        }
        setFieldsEnabled(false);
        updateButtonStates();
    }

    private void loadIntoFields(User u) {
        firstNameField.setText(u.getFirstName());
        lastNameField.setText(u.getLastName());

        if ("Female".equalsIgnoreCase(u.getGender())) {
            femaleRadio.setSelected(true);
        } else {
            maleRadio.setSelected(true);
        }

        LocalDate dob = u.getDob() == null ? LocalDate.of(2000, 1, 1) : u.getDob();
        dayCombo.setSelectedItem(dob.getDayOfMonth());
        monthCombo.setSelectedIndex(dob.getMonthValue() - 1);
        yearCombo.setSelectedItem(dob.getYear());

        ageSpinner.setValue(u.getAge());
        phoneField.setText(u.getPhone());
        emailField.setText(u.getEmail());
        continentCombo.setSelectedItem(
                InputValidator.isBlank(u.getContinent()) ? FormJPanel.CONTINENTS[0] : u.getContinent());
        experienceArea.setText(u.getExperience());
        hobbiesArea.setText(u.getHobbies());
        photoField.setText(u.getPhotoPath());
        showPreview(u.getPhotoPath());
    }

    // ---------- UPDATE ----------

    private void startEdit() {
        if (selectedUser() == null) {
            error("Select a row in the table first.");
            return;
        }
        editing = true;
        setFieldsEnabled(true);
        updateButtonStates();
        firstNameField.requestFocusInWindow();
    }

    private void cancelEdit() {
        editing = false;
        User user = selectedUser();
        if (user != null) {
            loadIntoFields(user);   // discard any unsaved changes
        }
        setFieldsEnabled(false);
        updateButtonStates();
    }

    private void updateSelected() {
        User selected = selectedUser();
        if (selected == null) {
            error("Select a row in the table first.");
            return;
        }

        User edited = new User();
        edited.setId(selected.getId());
        edited.setFirstName(firstNameField.getText().trim());
        edited.setLastName(lastNameField.getText().trim());
        edited.setGender(femaleRadio.isSelected() ? "Female" : "Male");
        edited.setDob(selectedDob());
        edited.setAge(((Number) ageSpinner.getValue()).intValue());
        edited.setPhone(InputValidator.formatPhone(phoneField.getText()));
        edited.setEmail(emailField.getText().trim());
        edited.setContinent((String) continentCombo.getSelectedItem());
        edited.setExperience(experienceArea.getText().trim());
        edited.setHobbies(hobbiesArea.getText().trim());
        edited.setPhotoPath(photoField.getText().trim());

        String problem = InputValidator.validate(edited);
        if (problem != null) {
            error(problem);
            return;
        }

        try {
            if (dao.update(edited)) {
                UserDetailsDialog.show(this, edited, "Success - user updated");
                populateTable();
            } else {
                error("No row was updated - the record may have been deleted already.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == UserDAO.DUPLICATE_ENTRY) {
                error("That email is already registered. Please use a different email.");
            } else {
                error("Update failed: " + ex.getMessage());
            }
        }
    }

    // ---------- DELETE ----------

    private void deleteSelected() {
        User user = selectedUser();
        if (user == null) {
            error("Select a row in the table first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete " + user.getFullName().trim() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (dao.delete(user.getId())) {
                JOptionPane.showMessageDialog(this, "User deleted.",
                        "Success - user deleted", JOptionPane.INFORMATION_MESSAGE);
                populateTable();
            } else {
                error("Nothing was deleted - the record no longer exists.");
            }
        } catch (SQLException ex) {
            error("Delete failed: " + ex.getMessage());
        }
    }

    // ---------- helpers ----------

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
        ImageIcon icon = ImageUtil.loadScaled(path, 140, 180);
        if (icon == null) {
            photoPreview.setIcon(null);
            photoPreview.setText("No photo");
        } else {
            photoPreview.setText("");
            photoPreview.setIcon(icon);
        }
    }

    private LocalDate selectedDob() {
        Integer day  = (Integer) dayCombo.getSelectedItem();
        Integer year = (Integer) yearCombo.getSelectedItem();
        if (day == null || year == null) {
            return null;
        }
        return InputValidator.toDate(year, monthCombo.getSelectedIndex() + 1, day);
    }

    /** Fields stay locked until Edit is pressed, so records can't be changed by accident. */
    private void setFieldsEnabled(boolean enabled) {
        firstNameField.setEnabled(enabled);
        lastNameField.setEnabled(enabled);
        maleRadio.setEnabled(enabled);
        femaleRadio.setEnabled(enabled);
        dayCombo.setEnabled(enabled);
        monthCombo.setEnabled(enabled);
        yearCombo.setEnabled(enabled);
        ageSpinner.setEnabled(enabled);
        phoneField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        continentCombo.setEnabled(enabled);
        experienceArea.setEnabled(enabled);
        hobbiesArea.setEnabled(enabled);
        photoField.setEnabled(enabled);
        browseButton.setEnabled(enabled);
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedUser() != null;

        editButton.setText(editing ? "Cancel" : "Edit");
        editButton.setEnabled(hasSelection);
        updateButton.setEnabled(hasSelection && editing);
        deleteButton.setEnabled(hasSelection && !editing);
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        maleRadio.setSelected(true);
        dayCombo.setSelectedItem(1);
        monthCombo.setSelectedIndex(0);
        yearCombo.setSelectedItem(2000);
        ageSpinner.setValue(0);
        phoneField.setText("");
        emailField.setText("");
        continentCombo.setSelectedIndex(0);
        experienceArea.setText("");
        hobbiesArea.setText("");
        photoField.setText("");
        photoPreview.setIcon(null);
        photoPreview.setText("No photo");
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
