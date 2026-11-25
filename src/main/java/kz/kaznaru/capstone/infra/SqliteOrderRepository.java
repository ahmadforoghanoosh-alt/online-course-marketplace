package kz.kaznaru.capstone.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kz.kaznaru.capstone.domain.OrderLine;
import kz.kaznaru.capstone.domain.OrderRepository;
import kz.kaznaru.capstone.domain.PaidOrder;

/**
 * Order history in the same SQLite file as the courses: one row per order in "orders",
 * one row per bought course in "order_items" (ADR-0010).
 */
public class SqliteOrderRepository implements OrderRepository {
    private final String url;

    public SqliteOrderRepository(String url) {
        this.url = url;
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS orders ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "reference TEXT NOT NULL UNIQUE, "
                    + "student TEXT NOT NULL, "
                    + "total REAL NOT NULL CHECK (total >= 0), "
                    + "paid_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            st.execute("CREATE TABLE IF NOT EXISTS order_items ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "order_id INTEGER NOT NULL REFERENCES orders(id), "
                    + "course_id INTEGER NOT NULL, "
                    + "title TEXT NOT NULL, "
                    + "instructor TEXT NOT NULL, "
                    + "price REAL NOT NULL)");
        } catch (SQLException e) {
            throw new IllegalStateException("Could not open the database " + url, e);
        }
    }

    /** The order and its items are saved in one transaction: all rows or nothing. */
    @Override
    public void save(PaidOrder order) {
        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try {
                long orderId = insertOrder(c, order);
                insertItems(c, orderId, order.lines());
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save the order", e);
        }
    }

    @Override
    public List<PaidOrder> findAll() {
        String sql = "SELECT o.id, o.reference, o.student, o.total, i.course_id, i.title, i.instructor, i.price "
                + "FROM orders o JOIN order_items i ON i.order_id = o.id ORDER BY o.id, i.id";
        Map<Long, String[]> heads = new LinkedHashMap<>();
        Map<Long, Double> totals = new LinkedHashMap<>();
        Map<Long, List<OrderLine>> lines = new LinkedHashMap<>();
        try (Connection c = connect(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                heads.putIfAbsent(id, new String[] {rs.getString("reference"), rs.getString("student")});
                totals.putIfAbsent(id, rs.getDouble("total"));
                lines.computeIfAbsent(id, k -> new ArrayList<>()).add(new OrderLine(rs.getLong("course_id"),
                        rs.getString("title"), rs.getString("instructor"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not read the orders", e);
        }
        List<PaidOrder> result = new ArrayList<>();
        for (Map.Entry<Long, String[]> head : heads.entrySet()) {
            long id = head.getKey();
            result.add(new PaidOrder(head.getValue()[0], head.getValue()[1], totals.get(id), lines.get(id)));
        }
        return result;
    }

    private long insertOrder(Connection c, PaidOrder order) throws SQLException {
        String sql = "INSERT INTO orders (reference, student, total) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.reference());
            ps.setString(2, order.student());
            ps.setDouble(3, order.total());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private void insertItems(Connection c, long orderId, List<OrderLine> items) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, course_id, title, instructor, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (OrderLine line : items) {
                ps.setLong(1, orderId);
                ps.setLong(2, line.courseId());
                ps.setString(3, line.title());
                ps.setString(4, line.instructor());
                ps.setDouble(5, line.price());
                ps.executeUpdate();
            }
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
