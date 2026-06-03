package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vegetables.Salad;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class SaladRepository {
    private static final Logger logger = LogManager.getLogger(SaladRepository.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Connection connection;

    public SaladRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // створити новий салат у БД, повертає його id
    public int create(String name) {
        String sql = "INSERT INTO salads (name, created_at) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, LocalDateTime.now().format(FMT));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    logger.info("Створено салат '{}' з id={}", name, id);
                    return id;
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка створення салату", e);
        }
        return -1;
    }

    // список всіх салатів: id -> "назва (дата)"
    public Map<Integer, String> findAll() {
        String sql = "SELECT id, name, created_at FROM salads ORDER BY id";
        Map<Integer, String> result = new LinkedHashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id       = rs.getInt("id");
                String name  = rs.getString("name");
                String date  = rs.getString("created_at");
                result.put(id, name + " (створено: " + date + ")");
            }
        } catch (SQLException e) {
            logger.error("Помилка читання салатів", e);
        }
        return result;
    }

    public void delete(int saladId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM vegetables WHERE salad_id = ?")) {
            stmt.setInt(1, saladId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Помилка видалення овочів салату", e);
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM salads WHERE id = ?")) {
            stmt.setInt(1, saladId);
            stmt.executeUpdate();
            logger.info("Видалено салат id={}", saladId);
        } catch (SQLException e) {
            logger.error("Помилка видалення салату", e);
        }
    }

    public void updateImagePath(int saladId, String imagePath) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE salads SET image_path = ? WHERE id = ?")) {
            stmt.setString(1, imagePath);
            stmt.setInt(2, saladId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Помилка збереження шляху до фото", e);
        }
    }

    public String getImagePath(int saladId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT image_path FROM salads WHERE id = ?")) {
            stmt.setInt(1, saladId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("image_path");
            }
        } catch (SQLException e) {
            logger.error("Помилка читання шляху до фото", e);
        }
        return null;
    }
}