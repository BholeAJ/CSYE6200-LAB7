package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main window: blue header with the Form / View switcher, and a CardLayout
 * holding the two required screens.
 */
public class MainFrame extends JFrame {

    private static final Color HEADER_BLUE = new Color(45, 45, 245);
    private static final String FORM_CARD = "form";
    private static final String VIEW_CARD = "view";

    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);

    private final FormJPanel formPanel = new FormJPanel();
    private final ViewJPanel viewPanel = new ViewJPanel();

    public MainFrame() {
        setTitle("Lab 7 - User Management (JDBC + MySQL)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        cardPanel.add(new JScrollPane(formPanel), FORM_CARD);
        cardPanel.add(viewPanel, VIEW_CARD);

        add(buildHeader(), BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        showForm();
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("User Management System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        JButton formButton = new JButton("Form");
        JButton viewButton = new JButton("View");
        formButton.addActionListener(e -> showForm());
        viewButton.addActionListener(e -> showView());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttons.setOpaque(false);
        buttons.add(formButton);
        buttons.add(viewButton);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BLUE);
        header.add(title, BorderLayout.WEST);
        header.add(buttons, BorderLayout.CENTER);
        return header;
    }

    private void showForm() {
        cards.show(cardPanel, FORM_CARD);
    }

    /** Always reload from the database when the table screen is opened. */
    private void showView() {
        viewPanel.populateTable();
        cards.show(cardPanel, VIEW_CARD);
    }
}
