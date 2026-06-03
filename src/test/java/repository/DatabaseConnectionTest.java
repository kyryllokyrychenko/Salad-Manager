package repository;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testGetInstanceReturnsSameObject() {
        DatabaseConnection a = DatabaseConnection.getInstance();
        DatabaseConnection b = DatabaseConnection.getInstance();
        assertSame(a, b); // Singleton — той самий екземпляр
    }

    @Test
    void testConnectionIsNotNull() {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        assertNotNull(conn);
    }

    @Test
    void testConnectionIsOpen() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        assertFalse(conn.isClosed());
    }

    @Test
    void testTableVegetableTypesExists() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name='vegetable_types'")) {
            assertTrue(rs.next(), "Таблиця vegetable_types має існувати");
        }
    }

    @Test
    void testTableSaladsExists() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name='salads'")) {
            assertTrue(rs.next(), "Таблиця salads має існувати");
        }
    }

    @Test
    void testTableVegetablesExists() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name='vegetables'")) {
            assertTrue(rs.next(), "Таблиця vegetables має існувати");
        }
    }

    @Test
    void testDefaultVegetableTypesSeeded() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) as cnt FROM vegetable_types")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt("cnt") >= 6, "Має бути мінімум 6 стандартних типів");
        }
    }

    @Test
    void testStandardVegetableNamesPresent() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        for (String name : new String[]{"Морква", "Помідор", "Огірок", "Лук", "Капуста", "Перець"}) {
            try (var stmt = conn.prepareStatement(
                    "SELECT 1 FROM vegetable_types WHERE name = ?")) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next(), "Тип '" + name + "' має бути в БД");
                }
            }
        }
    }
}