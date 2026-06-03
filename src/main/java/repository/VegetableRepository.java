package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vegetables.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VegetableRepository {

    private static final Logger logger = LogManager.getLogger(VegetableRepository.class);
    private final Connection connection;

    public VegetableRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void save(Vegetable vegetable, int saladId) {
        String sql = "INSERT INTO vegetables (salad_id, type, weight, custom_name, calories) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, saladId);
            stmt.setString(2, vegetable.getClass().getSimpleName());
            stmt.setDouble(3, vegetable.getWeight());
            if (vegetable instanceof CustomVegetable cv) {
                stmt.setString(4, cv.getName());
                stmt.setDouble(5, cv.getCaloriesPer100g());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
                stmt.setNull(5, java.sql.Types.DOUBLE);
            }
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) vegetable.setId(keys.getInt(1));
            }
            logger.info("Збережено овоч: {} в салат id={}", vegetable.getName(), saladId);
        } catch (SQLException e) {
            logger.error("Помилка збереження овоча", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM vegetables WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Видалено овоч з id={}", id);
        } catch (SQLException e) {
            logger.error("Помилка видалення овоча", e);
        }
    }

    public void update(int id, double newWeight) {
        String sql = "UPDATE vegetables SET weight = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newWeight);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            logger.info("Оновлено вагу овоча id={} -> {}г", id, newWeight);
        } catch (SQLException e) {
            logger.error("Помилка оновлення овоча", e);
        }
    }

    public List<Vegetable> findBySaladId(int saladId) {
        String sql = "SELECT * FROM vegetables WHERE salad_id = ?";
        List<Vegetable> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, saladId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vegetable v = mapRow(rs);
                    if (v != null) list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка читання овочів салату id={}", saladId, e);
        }
        return list;
    }

    public List<Vegetable> findAll() {
        return findBySaladId(-1); // поверне порожній список
    }

    private Vegetable mapRow(ResultSet rs) throws SQLException {
        String type   = rs.getString("type");
        double weight = rs.getDouble("weight");
        int    id     = rs.getInt("id");

        Vegetable v = switch (type) {
            case "Carrot"          -> new Carrot(weight);
            case "Tomato"          -> new Tomato(weight);
            case "Cucumber"        -> new Cucumber(weight);
            case "Onion"           -> new Onion(weight);
            case "Cabbage"         -> new Cabbage(weight);
            case "Pepper"          -> new Pepper(weight);
            case "CustomVegetable" -> new CustomVegetable(
                    rs.getString("custom_name"),
                    weight,
                    rs.getDouble("calories")
            );
            default -> {
                logger.warn("Невідомий тип: {}", type);
                yield null;
            }
        };

        if (v != null) v.setId(id);
        return v;
    }
}