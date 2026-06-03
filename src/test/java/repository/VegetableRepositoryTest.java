package repository;

import org.junit.jupiter.api.*;
import vegetables.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VegetableRepositoryTest {

    private VegetableRepository repo;
    private SaladRepository     saladRepo;
    private int                 testSaladId;

    @BeforeEach
    void setUp() {
        repo      = new VegetableRepository();
        saladRepo = new SaladRepository();
        testSaladId = saladRepo.create("Тестовий салат для овочів");
    }

    @AfterEach
    void tearDown() {
        saladRepo.delete(testSaladId); // видаляє і овочі (cascade в delete())
    }

    @Test
    void testSaveAndFindCarrot() {
        Carrot carrot = new Carrot(150);
        repo.save(carrot, testSaladId);
        assertTrue(carrot.getId() > 0, "Після save() овоч має отримати id");

        List<Vegetable> list = repo.findBySaladId(testSaladId);
        assertEquals(1, list.size());
        assertInstanceOf(Carrot.class, list.get(0));
        assertEquals(150, list.get(0).getWeight());
    }

    @Test
    void testSaveAllStandardTypes() {
        repo.save(new Carrot(100),   testSaladId);
        repo.save(new Tomato(80),    testSaladId);
        repo.save(new Cucumber(120), testSaladId);
        repo.save(new Onion(60),     testSaladId);
        repo.save(new Cabbage(200),  testSaladId);
        repo.save(new Pepper(90),    testSaladId);

        List<Vegetable> list = repo.findBySaladId(testSaladId);
        assertEquals(6, list.size());
    }

    @Test
    void testSaveCustomVegetable() {
        CustomVegetable cv = new CustomVegetable("Баклажан", 130, 25);
        repo.save(cv, testSaladId);

        List<Vegetable> list = repo.findBySaladId(testSaladId);
        assertEquals(1, list.size());
        assertInstanceOf(CustomVegetable.class, list.get(0));
        assertEquals("Баклажан", list.get(0).getName());
        assertEquals(25, ((CustomVegetable) list.get(0)).getCaloriesPer100g());
    }

    @Test
    void testDeleteVegetable() {
        Carrot carrot = new Carrot(100);
        repo.save(carrot, testSaladId);
        int vegId = carrot.getId();

        repo.delete(vegId);

        List<Vegetable> list = repo.findBySaladId(testSaladId);
        assertTrue(list.isEmpty(), "Після видалення список має бути порожнім");
    }

    @Test
    void testUpdateWeight() {
        Tomato tomato = new Tomato(100);
        repo.save(tomato, testSaladId);

        repo.update(tomato.getId(), 250);

        List<Vegetable> list = repo.findBySaladId(testSaladId);
        assertEquals(250, list.get(0).getWeight());
    }

    @Test
    void testFindBySaladIdReturnsEmptyForUnknownSalad() {
        List<Vegetable> list = repo.findBySaladId(-999);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testFindAllReturnsEmpty() {
        // findAll() завжди повертає порожній список (saladId = -1)
        List<Vegetable> list = repo.findAll();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testSaveSetIdOnVegetable() {
        Pepper pepper = new Pepper(70);
        assertEquals(0, pepper.getId(), "До save() id має бути 0");
        repo.save(pepper, testSaladId);
        assertTrue(pepper.getId() > 0, "Після save() id має бути встановлений");
    }

    @Test
    void testFindBySaladIdIgnoresUnknownType() throws Exception {
        // Вставляємо запис з невідомим типом напряму в БД
        var conn = repository.DatabaseConnection.getInstance().getConnection();
        var stmt = conn.prepareStatement(
                "INSERT INTO vegetables (salad_id, type, weight) VALUES (?, ?, ?)");
        stmt.setInt(1, testSaladId);
        stmt.setString(2, "НевідомийТип");
        stmt.setDouble(3, 100);
        stmt.executeUpdate();

        // findBySaladId має проігнорувати невідомий тип
        List<Vegetable> list = repo.findBySaladId(testSaladId);
        assertTrue(list.isEmpty());
    }

    @Test
    void testSaveReturnsNoGeneratedKey() throws Exception {
        java.sql.Connection mockConn = mock(java.sql.Connection.class);
        java.sql.PreparedStatement mockStmt = mock(java.sql.PreparedStatement.class);
        java.sql.ResultSet mockKeys = mock(java.sql.ResultSet.class);

        when(mockConn.prepareStatement(anyString(), eq(java.sql.Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStmt);
        when(mockStmt.getGeneratedKeys()).thenReturn(mockKeys);
        when(mockKeys.next()).thenReturn(false);

        VegetableRepository mockRepo = new VegetableRepository();
        java.lang.reflect.Field connField = VegetableRepository.class.getDeclaredField("connection");
        connField.setAccessible(true);
        connField.set(mockRepo, mockConn);

        Carrot carrot = new Carrot(100);
        mockRepo.save(carrot, 1);
        // id не встановлено бо keys.next() = false
        assertEquals(0, carrot.getId());
    }

}