package repository;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SaladRepositoryTest {

    private SaladRepository repo;

    @BeforeEach
    void setUp() {
        repo = new SaladRepository();
    }

    @Test
    void testCreateReturnsPositiveId() {
        int id = repo.create("Тестовий салат");
        assertTrue(id > 0, "create() має повертати позитивний id");
        repo.delete(id); // прибираємо за собою
    }

    @Test
    void testFindAllContainsCreatedSalad() {
        int id = repo.create("Салат для findAll");
        Map<Integer, String> all = repo.findAll();
        assertTrue(all.containsKey(id));
        assertTrue(all.get(id).contains("Салат для findAll"));
        repo.delete(id);
    }

    @Test
    void testFindAllDescriptionContainsDate() {
        int id = repo.create("Датований салат");
        String desc = repo.findAll().get(id);
        assertTrue(desc.contains("створено:"), "Опис має містити дату створення");
        repo.delete(id);
    }

    @Test
    void testDeleteRemovesSalad() {
        int id = repo.create("Салат для видалення");
        repo.delete(id);
        assertFalse(repo.findAll().containsKey(id), "Салат має бути видалений");
    }

    @Test
    void testFindAllReturnsMap() {
        Map<Integer, String> all = repo.findAll();
        assertNotNull(all);
    }

    @Test
    void testCreateMultipleSalads() {
        int id1 = repo.create("Салат А");
        int id2 = repo.create("Салат Б");
        assertNotEquals(id1, id2, "Різні салати мають мати різні id");
        repo.delete(id1);
        repo.delete(id2);
    }

    @Test
    void testUpdateImagePath() {
        int id = repo.create("Фото салат");
        repo.updateImagePath(id, "/path/to/photo.jpg");
        assertEquals("/path/to/photo.jpg", repo.getImagePath(id));
        repo.delete(id);
    }

    @Test
    void testGetImagePathReturnsNullForNoImage() {
        int id = repo.create("Без фото");
        assertNull(repo.getImagePath(id));
        repo.delete(id);
    }

    @Test
    void testGetImagePathUnknownIdReturnsNull() {
        assertNull(repo.getImagePath(-999));
    }

    @Test
    void testCreateReturnsMinusOneWhenNoGeneratedKey() throws Exception {
        // Мокаємо з'єднання щоб getGeneratedKeys повернув порожній ResultSet
        java.sql.Connection mockConn = mock(java.sql.Connection.class);
        java.sql.PreparedStatement mockStmt = mock(java.sql.PreparedStatement.class);
        java.sql.ResultSet mockKeys = mock(java.sql.ResultSet.class);

        when(mockConn.prepareStatement(anyString(), eq(java.sql.Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStmt);
        when(mockStmt.getGeneratedKeys()).thenReturn(mockKeys);
        when(mockKeys.next()).thenReturn(false); // ось це покриє false-гілку

        // Створюємо репозиторій з мок-з'єднанням через рефлексію
        SaladRepository mockRepo = new SaladRepository();
        java.lang.reflect.Field connField = SaladRepository.class.getDeclaredField("connection");
        connField.setAccessible(true);
        connField.set(mockRepo, mockConn);

        int result = mockRepo.create("Тест");
        assertEquals(-1, result);
    }
}