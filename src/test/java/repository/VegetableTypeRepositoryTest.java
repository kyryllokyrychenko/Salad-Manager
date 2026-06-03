package repository;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VegetableTypeRepositoryTest {

    private VegetableTypeRepository repo;

    @BeforeEach
    void setUp() {
        repo = new VegetableTypeRepository();
    }

    @Test
    void testFindAllReturnsStandardTypes() {
        Map<Integer, String> types = repo.findAll();
        assertNotNull(types);
        assertTrue(types.size() >= 6);
        assertTrue(types.containsValue("Морква"));
        assertTrue(types.containsValue("Помідор"));
        assertTrue(types.containsValue("Огірок"));
        assertTrue(types.containsValue("Лук"));
        assertTrue(types.containsValue("Капуста"));
        assertTrue(types.containsValue("Перець"));
    }

    @Test
    void testGetCaloriesForStandardTypes() {
        assertEquals(41, repo.getCalories("Морква"),  0.01);
        assertEquals(18, repo.getCalories("Помідор"), 0.01);
        assertEquals(15, repo.getCalories("Огірок"),  0.01);
        assertEquals(40, repo.getCalories("Лук"),     0.01);
        assertEquals(25, repo.getCalories("Капуста"), 0.01);
        assertEquals(31, repo.getCalories("Перець"),  0.01);
    }

    @Test
    void testGetCaloriesUnknownReturnsZero() {
        assertEquals(0, repo.getCalories("НеіснуючийОвоч"), 0.01);
    }

    @Test
    void testSaveNewType() {
        String testName = "ТестовийОвоч_" + System.currentTimeMillis();
        repo.save(testName, 99);

        Map<Integer, String> types = repo.findAll();
        assertTrue(types.containsValue(testName));
        assertEquals(99, repo.getCalories(testName), 0.01);
    }

    @Test
    void testSaveDuplicateIgnored() {
        // INSERT OR IGNORE — повторний виклик не кидає виняток
        assertDoesNotThrow(() -> {
            repo.save("Морква", 41);
            repo.save("Морква", 41);
        });
        // Кількість записів не збільшилась
        long count = repo.findAll().values().stream()
                .filter(v -> v.equals("Морква")).count();
        assertEquals(1, count);
    }
}