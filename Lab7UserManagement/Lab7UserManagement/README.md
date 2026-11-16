# Lab 7 — User Management System (Java Swing + JDBC + MySQL)

**Student:** _your name_ · **Course:** CSYE 6200

A NetBeans desktop application that stores users in a MySQL database with JDBC. Users are created on the **Form** screen, listed in a `JTable` on the **View** screen, and updated or deleted from there. Every screen is built in the NetBeans GUI Builder, so each one opens in the **Design** tab.

## Screens

**Form (Create User):** first name, last name, gender, date of birth, phone, email, continent, experience, hobbies and an optional photo. The age is calculated from the date of birth. After saving, a confirmation pop-up shows the user's details (`User.toString()`) and photo.

**View (View Users):** a `JTable` of every record on the right and the selected user's details on the left. The fields stay disabled until **Edit** is pressed. **Update** saves the changes, **Cancel** undoes them, and **Delete** asks for confirmation first. Double-clicking a row shows all of the user's details.

| Operation | SQL |
|---|---|
| Create | `INSERT INTO users (...) VALUES (?, ...)` |
| Read | `SELECT * FROM users ORDER BY id` |
| Update | `UPDATE users SET ... WHERE id = ?` |
| Delete | `DELETE FROM users WHERE id = ?` |

## Swing controls

| Input | Control |
|---|---|
| First / last name, email | `JTextField` |
| Gender | `JRadioButton` (Male / Female) in a `ButtonGroup` |
| Date of birth | `JDateChooser` (JCalendar) |
| Age | Read-only `JTextField`, calculated from the date of birth |
| Phone | `JFormattedTextField` with the mask `###-###-####` |
| Continent | `JComboBox` |
| Experience, hobbies | `JTextArea` |
| Photo | `JButton` (file chooser) and a `JLabel` preview |
| Users list | `JTable` |

## Project Structure

```
Lab7UserManagement/
├── build.xml, manifest.mf, nbproject/    NetBeans (Ant) project files
├── lib/
│   ├── jcalendar-1.4.jar                 JDateChooser
│   └── mysql-connector-j-9.4.0.jar       MySQL JDBC driver
├── DatabaseQueries.sql                   Database, table and sample rows
├── screenshots/                          Screenshots of the running application
└── src/
    ├── model/User.java                   User data: getters, setters and toString()
    ├── utility/DatabaseConnector.java    JDBC connection and CRUD methods
    └── ui/
        ├── MainJFrame.java / .form       Main window: header with Form / View buttons
        ├── FormJPanel.java / .form       Create screen and input validation
        └── ViewJPanel.java / .form       JTable with Edit / Update / Delete
```

## How to Run

1. **Start MySQL** (Docker container from Lab 6) and run `DatabaseQueries.sql` in MySQL Workbench. It creates the `lab7db` database, the `users` table (with `email` set as `UNIQUE`) and 3 sample rows.
2. Check `URL`, `USERNAME` and `PASSWORD` in `src/utility/DatabaseConnector.java`. The defaults are `jdbc:mysql://localhost:3306/lab7db`, `root`, `my-secret-pw`.
3. In NetBeans, choose **File → Open Project** and select the `Lab7UserManagement` folder. The jars in `lib/` are already on the classpath.
4. Press **F6** (Run). The main class is `ui.MainJFrame`.

## Validation

- **First and last name:** required, 2–50 letters.
- **Gender:** required.
- **Date of birth:** required and not in the future. Dates after today can't be picked.
- **Phone:** 10 digits, typed into the `###-###-####` mask.
- **Email:** must be a valid format. A duplicate email is rejected by the database (MySQL error 1062) and shown as a message instead of crashing.
- **Continent:** required.

## Screenshots

![Create user](screenshots/01_create_user.png)
![View users](screenshots/02_view_users.png)
![Update user](screenshots/03_update_user.png)
![Delete user](screenshots/04_delete_user.png)
![Duplicate email](screenshots/05_duplicate_email.png)
![MySQL Workbench](screenshots/06_mysql_workbench.png)
