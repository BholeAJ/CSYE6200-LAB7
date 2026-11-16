# Lab 7 — User Management System (Java Swing + JDBC + MySQL)

A desktop CRUD application. Users are created in a validated form, listed in a `JTable`, and updated or deleted from the same screen. All database access goes through JDBC with `PreparedStatement`.

## Screens
<img width="1917" height="1015" alt="1_LAB7Screenshot 2026-11-15 221016" src="https://github.com/user-attachments/assets/55b7ecd4-bd76-43b4-8681-dd1659a101d3" />
<img width="1917" height="1020" alt="2_LAB7Screenshot 2026-11-15 221102" src="https://github.com/user-attachments/assets/6ee04dd0-be09-4869-a938-4b5fa82e3217" />
<img width="1917" height="1015" alt="3_LAB7Screenshot 2026-11-15 221145" src="https://github.com/user-attachments/assets/bea8941f-76ae-403a-b81e-012ec11875b0" />
<img width="1917" height="1020" alt="4_LAB7Screenshot 2026-11-15 221248" src="https://github.com/user-attachments/assets/c252a0a5-df1e-4809-8622-f544d2b1d715" />



**Form** — Create User: first/last name, gender, date of birth, age, phone, email, continent, experience, hobbies, and a photo chosen with a file browser and previewed before saving. Saving shows a confirmation dialog with the photo and the stored values.

**View** — View Users: a `JTable` of every record on the right, the full record on the left. Fields stay disabled until **Edit** is pressed; **Update** writes the change back, **Delete** asks for confirmation first, and double-clicking a row opens its full details.

| Operation | SQL |
|---|---|
| Create | `INSERT INTO users (...) VALUES (?, ...)` |
| Read | `SELECT ... FROM users ORDER BY id` |
| Update | `UPDATE users SET ... WHERE id = ?` |
| Delete | `DELETE FROM users WHERE id = ?` |

## Project structure

```
Lab7UserManagement/
├── src/
│   ├── db/DatabaseConnector.java     JDBC connection + startup check
│   ├── model/User.java               POJO
│   ├── dao/UserDAO.java              all SQL statements
│   ├── util/
│   │   ├── InputValidator.java       name/email/phone/date/age rules
│   │   └── ImageUtil.java            loads and scales photos
│   └── ui/
│       ├── Main.java                 entry point
│       ├── MainFrame.java            header + Form/View card switching
│       ├── FormJPanel.java           VIEW 1 — Create User
│       ├── ViewJPanel.java           VIEW 2 — JTable + edit/update/delete
│       └── UserDetailsDialog.java    photo + details dialog
├── sql/DatabaseQueries.sql           schema, unique index, sample rows
├── lib/                              put mysql-connector-j.jar here
├── nbproject/                        NetBeans project configuration
├── build.xml                         Ant build script
├── screenshots/
└── README.md
```

## Setup

1. **Create the database.** Run `sql/DatabaseQueries.sql` in MySQL Workbench. It creates the `lab7db` schema, the `users` table, the unique index on `email`, and 3 sample rows.
2. **Add the driver.** Download Connector/J from https://dev.mysql.com/downloads/connector/j/ (*Platform Independent* → ZIP) and put `mysql-connector-j-9.x.x.jar` in `lib/`.
3. **Set your credentials.** In `src/db/DatabaseConnector.java`, change `USER` and `PASSWORD`.

Connection URL:

```
jdbc:mysql://localhost:3306/lab7db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

## Running

### NetBeans (this folder is already a NetBeans project)

1. Put the MySQL driver in `lib/` and rename it to exactly **`mysql-connector-j.jar`** (see `lib/README.txt`). The project classpath already points at that name.
2. In NetBeans: **File → Open Project**, select the `Lab7UserManagement` folder, click *Open Project*.
3. Press **F6** (Run). The main class `ui.Main` is already set.

NetBeans regenerates `nbproject/build-impl.xml` the first time the project is opened, so it isn't included here.

The GUI is written in code rather than with the Matisse form designer, so the Design tab stays empty — open the files in the Source tab. Built for JDK 8 or newer; if your JDK complains about source level 1.8, raise it in **Project Properties → Sources → Source/Binary Format**.

**IntelliJ / Eclipse:** create a Java project, copy `src/` in, add the JAR as a library, run `ui.Main`.

**Command line:**

```bash
javac -cp "lib/mysql-connector-j.jar" -d bin $(find src -name "*.java")
java  -cp "bin:lib/mysql-connector-j.jar" ui.Main      # macOS/Linux
java  -cp "bin;lib\mysql-connector-j.jar" ui.Main      # Windows
```

## Validation

- First and last name: 2–50 letters.
- Date of birth: must be a real calendar date and not in the future. Age is recalculated automatically but can be overridden.
- Phone: exactly 10 digits, stored as `416-399-0346`.
- Email: standard format, and `UNIQUE` in the database — a duplicate raises MySQL error 1062, which is caught and shown as *"That email is already registered. Please use a different email."*
- Photo: optional; the file must exist. HTML in free-text fields is escaped before being shown in dialogs.

## Known limitations

- The photo is stored as an absolute file path, so images only display on the machine where they were selected. Storing a `BLOB`, or copying files into a project-relative `photos/` folder, would make it portable.
- Credentials are hard-coded in `DatabaseConnector`; a real app would read them from a config file or environment variables.

## Troubleshooting

| Message | Fix |
|---|---|
| MySQL JDBC driver not found | The Connector/J JAR isn't on the classpath |
| Public Key Retrieval is not allowed | Keep the URL flags shown above |
| Access denied for user | Wrong username/password in `DatabaseConnector` |
| Unknown database 'lab7db' | Run `sql/DatabaseQueries.sql` first |
| Communications link failure | MySQL server or Docker container isn't running |
| Error 1062 on `ALTER TABLE` | Duplicate emails exist — run the cleanup `DELETE` in the SQL file |
