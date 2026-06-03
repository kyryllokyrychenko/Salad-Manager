package vegetables;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VegetableTest {

    // Допоміжний клас для тестів (бо Vegetable абстрактний)
    static class TestVegetable extends Vegetable {
        public TestVegetable(String name, double weight, double caloriesPer100g) {
            super(name, weight, caloriesPer100g);
        }
    }

    @Test
    void testTotalCaloriesCalculation() {
        Vegetable v = new TestVegetable("TestVeg", 200, 50);
        // 200 г → 2 * 50 = 100 ккал
        assertEquals(100.0, v.getTotalCalories());
    }

    @Test
    void testGetWeight() {
        Vegetable v = new TestVegetable("Carrot", 120, 41);
        assertEquals(120, v.getWeight());
    }

    @Test
    void testSetWeightValid() {
        Vegetable v = new TestVegetable("Potato", 100, 80);
        v.setWeight(250);
        assertEquals(250, v.getWeight());
    }

    @Test
    void testSetWeightNegativeThrowsException() {
        Vegetable v = new TestVegetable("Tomato", 100, 20);

        assertThrows(IllegalArgumentException.class, () -> v.setWeight(-10));
    }

    @Test
    void testConstructorNegativeWeightThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new TestVegetable("BadVeg", -30, 10)
        );
    }

    @Test
    void testToStringFormat() {
        Vegetable v = new TestVegetable("Onion", 150, 40);
        String expected = "Onion (150.0 г, 40.0 ккал/100г)";
        assertEquals(expected, v.toString());
    }

    @Test void testCarrot()   { var v = new Carrot(100);   assertEquals("Морква",  v.getName()); assertEquals(100, v.getWeight()); }
    @Test void testTomato()   { var v = new Tomato(80);    assertEquals("Помідор", v.getName()); assertEquals(80,  v.getWeight()); }
    @Test void testCucumber() { var v = new Cucumber(120); assertEquals("Огірок",  v.getName()); assertEquals(120, v.getWeight()); }
    @Test void testOnion()    { var v = new Onion(60);     assertEquals("Цибуля",     v.getName()); assertEquals(60,  v.getWeight()); }
    @Test void testCabbage()  { var v = new Cabbage(200);  assertEquals("Капуста", v.getName()); assertEquals(200, v.getWeight()); }
    @Test void testPepper()   { var v = new Pepper(90);    assertEquals("Перець",  v.getName()); assertEquals(90,  v.getWeight()); }

    @Test
    void testCustomVegetable() {
        var v = new CustomVegetable("Баклажан", 150, 25);
        assertEquals("Баклажан", v.getName());
        assertEquals(150, v.getWeight());
        assertEquals(25,  v.getCaloriesPer100g());
        assertEquals(37.5, v.getTotalCalories());
    }

    @Test
    void testCaloriesPerVegetable() {
        assertEquals(41.0, new Carrot(100).getTotalCalories());
        assertEquals(18.0, new Tomato(100).getTotalCalories());
        assertEquals(16.0, new Cucumber(100).getTotalCalories());
        assertEquals(40.0, new Onion(100).getTotalCalories());
        assertEquals(25.0, new Cabbage(100).getTotalCalories());
        assertEquals(26.0, new Pepper(100).getTotalCalories());
    }
}
