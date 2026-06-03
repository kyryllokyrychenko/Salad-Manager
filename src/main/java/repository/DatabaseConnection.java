package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
    private static final String URL = "jdbc:sqlite:salad.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL);
            initSchema();
            logger.info("Підключення до БД успішне");
        } catch (SQLException e) {
            logger.fatal("Не вдалося підключитися до БД", e);
            throw new RuntimeException(e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vegetable_types (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    name     TEXT    NOT NULL UNIQUE,
                    calories REAL    NOT NULL
                );
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS salads (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    name       TEXT    NOT NULL,
                    created_at TEXT    NOT NULL
                );
                """);
            migrateAddColumnIfMissingSalads(stmt, "image_path", "TEXT");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vegetables (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    salad_id    INTEGER NOT NULL,
                    type        TEXT    NOT NULL,
                    weight      REAL    NOT NULL,
                    custom_name TEXT,
                    calories    REAL,
                    FOREIGN KEY (salad_id) REFERENCES salads(id)
                );
                """);
            // заповнюємо стандартні типи якщо таблиця порожня
            stmt.execute("""
                INSERT OR IGNORE INTO vegetable_types (name, calories) VALUES
                    ('Морква',   41),
                    ('Помідор',  18),
                    ('Огірок',   15),
                    ('Лук',      40),
                    ('Капуста',  25),
                    ('Перець',   31);
                """);
            migrateAddColumnIfMissing(stmt, "custom_name", "TEXT");
            migrateAddColumnIfMissing(stmt, "calories",    "REAL");
            migrateAddColumnIfMissing(stmt, "salad_id",    "INTEGER");
            logger.info("Схема БД ініціалізована");
        }
    }

    private void migrateAddColumnIfMissing(Statement stmt, String column, String type) {
        try {
            stmt.execute("ALTER TABLE vegetables ADD COLUMN " + column + " " + type);
            logger.info("Міграція: додано колонку {}", column);
        } catch (SQLException e) {
            logger.debug("Колонка {} вже існує, пропускаємо", column);
        }
    }

    private void migrateAddColumnIfMissingSalads(Statement stmt, String column, String type) {
        try {
            stmt.execute("ALTER TABLE salads ADD COLUMN " + column + " " + type);
            logger.info("Міграція salads: додано колонку {}", column);
        } catch (SQLException e) {
            logger.debug("Колонка {} вже існує в salads, пропускаємо", column);
        }
    }
}