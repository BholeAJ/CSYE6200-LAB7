package dao;

import db.DatabaseConnector;
import model.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object - every SQL statement for the `users` table lives here.
 * PreparedStatement is used throughout so the app is safe from SQL injection.
 */
public class UserDAO {

    /** MySQL error code for a violated UNIQUE constraint. */
    public static final int DUPLICATE_ENTRY = 1062;

    private static final String COLUMNS =
            "id, first_name, last_name, gender, dob, age, phone, email, continent, experience, hobbies, photo_path";

    private static final String INSERT_SQL =
            "INSERT INTO users (first_name, last_name, gender, dob, age, phone, email, continent, experience, hobbies, photo_path) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT " + COLUMNS + " FROM users ORDER BY id";

    private static final String SEARCH_SQL =
            "SELECT " + COLUMNS + " FROM users "
          + "WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR continent LIKE ? ORDER BY id";

    private static final String UPDATE_SQL =
            "UPDATE users SET first_name = ?, last_name = ?, gender = ?, dob = ?, age = ?, phone = ?, "
          + "email = ?, continent = ?, experience = ?, hobbies = ?, photo_path = ? WHERE id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM users WHERE id = ?";

    /** CREATE - returns the auto-generated id. */
    public int insert(User u) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            bind(ps, u);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return 0;
        }
    }

    /** READ - all rows. */
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** READ - filtered by keyword. */
    public List<User> search(String keyword) throws SQLException {
        List<User> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_SQL)) {

            for (int i = 1; i <= 4; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /** UPDATE */
    public boolean update(User u) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            bind(ps, u);
            ps.setInt(12, u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** DELETE */
    public boolean delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- helpers ----------

    private void bind(PreparedStatement ps, User u) throws SQLException {
        ps.setString(1, u.getFirstName());
        ps.setString(2, u.getLastName());
        ps.setString(3, u.getGender());
        if (u.getDob() == null) {
            ps.setNull(4, java.sql.Types.DATE);
        } else {
            ps.setDate(4, Date.valueOf(u.getDob()));
        }
        ps.setInt(5, u.getAge());
        ps.setString(6, u.getPhone());
        ps.setString(7, u.getEmail());
        ps.setString(8, u.getContinent());
        ps.setString(9, u.getExperience());
        ps.setString(10, u.getHobbies());
        ps.setString(11, u.getPhotoPath());
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setGender(rs.getString("gender"));

        Date dob = rs.getDate("dob");
        u.setDob(dob == null ? null : dob.toLocalDate());

        u.setAge(rs.getInt("age"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setContinent(rs.getString("continent"));
        u.setExperience(rs.getString("experience"));
        u.setHobbies(rs.getString("hobbies"));
        u.setPhotoPath(rs.getString("photo_path"));
        return u;
    }
}
