package model;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // PostgreSQL connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/your_db",
            "your_user",
            "your_password"
        );
    }

    // CREATE
    public void create(Student s) throws Exception {

        String sql = """
            INSERT INTO student (first_name, last_name, birth_date, creation_date, last_modified_date)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());

            ps.setObject(3, s.getBirthDate());
            ps.setObject(4, s.getCreationDate());
            ps.setObject(5, s.getLastModifiedDate());

            ps.executeUpdate();
        }
    }

    // READ ALL
    public List<Student> readAll() throws Exception {

        List<Student> list = new ArrayList<>();

        String sql = "SELECT * FROM student";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Student s = new Student();

                s.setId(rs.getInt("id"));
                s.setFirstName(rs.getString("first_name"));
                s.setLastName(rs.getString("last_name"));

                // PostgreSQL returns java.sql.Date → convert to LocalDate
                Date birth = rs.getDate("birth_date");
                if (birth != null) {
                    s.setBirthDate(birth.toLocalDate());
                }

                Date creation = rs.getDate("creation_date");
                if (creation != null) {
                    s.setCreationDate(creation.toLocalDate());
                }

                Date modified = rs.getDate("last_modified_date");
                if (modified != null) {
                    s.setLastModifiedDate(modified.toLocalDate());
                }

                list.add(s);
            }
        }

        return list;
    }

    // UPDATE
    public void update(Student s) throws Exception {

        String sql = """
            UPDATE student
            SET first_name = ?,
                last_name = ?,
                birth_date = ?,
                last_modified_date = ?
            WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setObject(3, s.getBirthDate());
            ps.setObject(4, s.getLastModifiedDate());
            ps.setInt(5, s.getId());

            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws Exception {

        String sql = "DELETE FROM student WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}