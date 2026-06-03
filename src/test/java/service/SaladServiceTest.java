package service;

import org.junit.jupiter.api.*;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import vegetables.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaladServiceTest {

    private VegetableRepository     vegRepo;
    private SaladRepository         saladRepo;
    private VegetableTypeRepository typeRepo;
    private SaladService            service;

    @BeforeEach
    void setUp() {
        vegRepo   = mock(VegetableRepository.class);
        saladRepo = mock(SaladRepository.class);
        typeRepo  = mock(VegetableTypeRepository.class);
        service   = new SaladService(vegRepo, saladRepo, typeRepo);
    }

    // ===== Салати =====

    @Test
    void testCreateSaladWithValidName() {
        when(saladRepo.create("Літній")).thenReturn(5);
        assertEquals(5, service.createSalad("Літній"));
        verify(saladRepo).create("Літній");
    }

    @Test
    void testCreateSaladWithNullNameUsesDefault() {
        when(saladRepo.create("Мій салат")).thenReturn(1);
        service.createSalad(null);
        verify(saladRepo).create("Мій салат");
    }

    @Test
    void testCreateSaladWithBlankNameUsesDefault() {
        when(saladRepo.create("Мій салат")).thenReturn(1);
        service.createSalad("   ");
        verify(saladRepo).create("Мій салат");
    }

    @Test
    void testLoadSaladLoadsVegetables() {
        when(vegRepo.findBySaladId(1)).thenReturn(List.of(new Carrot(100), new Tomato(80)));
        Salad salad = service.loadSalad(1, "Тест");
        assertEquals("Тест", salad.getName());
        assertEquals(2, salad.getVegetables().size());
    }

    @Test
    void testGetAllSalads() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "Салат А");
        when(saladRepo.findAll()).thenReturn(map);
        assertEquals(map, service.getAllSalads());
    }

    @Test
    void testDeleteSalad() {
        service.deleteSalad(3);
        verify(saladRepo).delete(3);
    }

    // ===== Овочі =====

    @Test
    void testAddVegetable() {
        Salad salad = new Salad();
        Carrot carrot = new Carrot(100);
        service.addVegetable(salad, carrot, 1);
        verify(vegRepo).save(carrot, 1);
        assertEquals(1, salad.getVegetables().size());
    }

    @Test
    void testRemoveVegetable() {
        Salad salad = new Salad();
        Carrot carrot = new Carrot(100);
        salad.add(carrot);
        service.removeVegetable(salad, 0);
        verify(vegRepo).delete(carrot.getId());
        assertTrue(salad.getVegetables().isEmpty());
    }

    @Test
    void testUpdateWeight() {
        Salad salad = new Salad();
        Carrot carrot = new Carrot(100);
        salad.add(carrot);
        service.updateWeight(salad, 0, 250);
        assertEquals(250, carrot.getWeight());
        verify(vegRepo).update(carrot.getId(), 250);
    }

    // ===== Логіка =====

    @Test
    void testSortByCalories() {
        Salad salad = new Salad();
        salad.add(new Carrot(100));   // 41 ккал
        salad.add(new Cucumber(100)); // 15 ккал
        service.sortByCalories(salad);
        assertEquals("Огірок", salad.getVegetables().get(0).getName());
        assertEquals("Морква", salad.getVegetables().get(1).getName());
    }

    @Test
    void testSortByCaloriesDescending() {
        Salad salad = new Salad();
        salad.add(new Cucumber(100)); // 15 ккал
        salad.add(new Carrot(100));   // 41 ккал
        service.sortByCaloriesDescending(salad);
        assertEquals("Морква", salad.getVegetables().get(0).getName());
        assertEquals("Огірок", salad.getVegetables().get(1).getName());
    }

    @Test
    void testFindByCalories() {
        Salad salad = new Salad();
        salad.add(new Carrot(100));   // 41 ккал
        salad.add(new Tomato(100));   // 18 ккал
        salad.add(new Cucumber(100)); // 15 ккал
        List<Vegetable> result = service.findByCalories(salad, 10, 20);
        assertEquals(2, result.size());
    }

    @Test
    void testGetTotalCalories() {
        Salad salad = new Salad();
        salad.add(new Carrot(100));  // 41 ккал
        salad.add(new Tomato(100));  // 18 ккал
        assertEquals(59.0, service.getTotalCalories(salad), 0.01);
    }

    // ===== Типи овочів =====

    @Test
    void testGetAllTypes() {
        Map<Integer, String> types = new LinkedHashMap<>();
        types.put(1, "Морква");
        when(typeRepo.findAll()).thenReturn(types);
        assertEquals(types, service.getAllTypes());
    }

    @Test
    void testSaveCustomType() {
        service.saveCustomType("Баклажан", 25);
        verify(typeRepo).save("Баклажан", 25);
    }

    @Test
    void testGetCaloriesByType() {
        when(typeRepo.getCalories("Морква")).thenReturn(41.0);
        assertEquals(41.0, service.getCaloriesByType("Морква"), 0.01);
    }

    // ===== Фабрика =====

    @Test
    void testCreateVegetableCarrot() {
        assertInstanceOf(Carrot.class, service.createVegetable("Морква", 100));
    }

    @Test
    void testCreateVegetableTomato() {
        assertInstanceOf(Tomato.class, service.createVegetable("Помідор", 100));
    }

    @Test
    void testCreateVegetableCucumber() {
        assertInstanceOf(Cucumber.class, service.createVegetable("Огірок", 100));
    }

    @Test
    void testCreateVegetableOnion() {
        assertInstanceOf(Onion.class, service.createVegetable("Лук", 100));
    }

    @Test
    void testCreateVegetableCabbage() {
        assertInstanceOf(Cabbage.class, service.createVegetable("Капуста", 100));
    }

    @Test
    void testCreateVegetablePepper() {
        assertInstanceOf(Pepper.class, service.createVegetable("Перець", 100));
    }

    @Test
    void testCreateVegetableCustomType() {
        when(typeRepo.getCalories("Рукола")).thenReturn(20.0);
        Vegetable veg = service.createVegetable("Рукола", 100);
        assertInstanceOf(CustomVegetable.class, veg);
        assertEquals("Рукола", veg.getName());
    }

    @Test
    void testCreateCustomVegetable() {
        Vegetable veg = service.createCustomVegetable("Баклажан", 150, 25);
        assertInstanceOf(CustomVegetable.class, veg);
        assertEquals("Баклажан", veg.getName());
        assertEquals(150, veg.getWeight());
        verify(typeRepo).save("Баклажан", 25);
    }

    // ===== Фото =====

    @Test
    void testUpdateSaladImage() {
        service.updateSaladImage(1, "/path/to/image.jpg");
        verify(saladRepo).updateImagePath(1, "/path/to/image.jpg");
    }

    @Test
    void testGetSaladImage() {
        when(saladRepo.getImagePath(1)).thenReturn("/path/to/image.jpg");
        assertEquals("/path/to/image.jpg", service.getSaladImage(1));
    }

    @Test
    void testGetSaladImageReturnsNull() {
        when(saladRepo.getImagePath(99)).thenReturn(null);
        assertNull(service.getSaladImage(99));
    }
}