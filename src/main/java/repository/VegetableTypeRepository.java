package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class VegetableTypeRepository {
    private static final Logger logger = LogManager.getLogger(VegetableTypeRepository.class);
    private final Connection connection;

    public VegetableTypeRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // всі типи: id -> назва
    public Map<Integer, String> findAll() {
        String sql = "SELECT id, name FROM vegetable_types ORDER BY id";
        Map<Integer, String> result = new LinkedHashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            logger.error("Помилка читання типів овочів", e);
        }
        return result;
    }

    public double getCalories(String name) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT calories FROM vegetable_types WHERE name = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("calories");
            }
        } catch (SQLException e) {
            logger.error("Помилка читання калорійності", e);
        }
        return 0;
    }

    public void save(String name, double calories) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO vegetable_types (name, calories) VALUES (?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, calories);
            stmt.executeUpdate();
            logger.info("Додано тип овоча: {} ({}ккал)", name, calories);
        } catch (SQLException e) {
            logger.error("Помилка збереження типу овоча", e);
        }
    }
}