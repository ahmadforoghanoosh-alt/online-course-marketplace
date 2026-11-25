package kz.kaznaru.capstone.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;

/**
 * Second Repository implementation: courses are saved in a SQLite database file through JDBC.
 * Services only know CourseRepository, so they do not change when App picks this class.
 */
public class SqliteCourseRepository implements CourseRepository {
    private final String url;

    /** @param url a JDBC url, for example "jdbc:sqlite:marketplace.db" */
    public SqliteCourseRepository(String url) {
        this.url = url;
        execute("CREATE TABLE IF NOT EXISTS courses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "instructor TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "price REAL NOT NULL CHECK (price > 0))");
    }

    @Override
    public Course add(String title, String instructor, String description, double price) {
        Course draft = Course.builder()  // same checks as the domain, before we save
                .title(title)
                .instructor(instructor)
                .description(description)
                .price(price)
                .build();
        String sql = "INSERT INTO courses (title, instructor, description, price) VALUES (?, ?, ?, ?)";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, draft.title());
            ps.setString(2, draft.instructor());
            ps.setString(3, draft.description());
            ps.setDouble(4, draft.price());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return Course.builder()
                        .id(keys.getLong(1))
                        .title(draft.title())
                        .instructor(draft.instructor())
                        .description(draft.description())
                        .price(draft.price())
                        .build();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save the course", e);
        }
    }

    @Override
    public Optional<Course> findById(long id) {
        String sql = "SELECT id, title, instructor, description, price FROM courses WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(toCourse(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not read the course", e);
        }
    }

    @Override
    public List<Course> findAll() {
        String sql = "SELECT id, title, instructor, description, price FROM courses ORDER BY id";
        try (Connection c = connect(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Course> result = new ArrayList<>();
            while (rs.next()) {
                result.add(toCourse(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not read the courses", e);
        }
    }

    private Course toCourse(ResultSet rs) throws SQLException {
        return Course.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .instructor(rs.getString("instructor"))
                .description(rs.getString("description"))
                .price(rs.getDouble("price"))
                .build();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void execute(String sql) {
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not open the database " + url, e);
        }
    }
}
