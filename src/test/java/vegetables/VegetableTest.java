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

//    @Test
//    void testToStringFormat() {
//        Vegetable v = new TestVegetable("Onion", 150, 40);
//        String expected = "Onion (150 г, 40.0 ккал/100г)";
//        assertEquals(expected, v.toString());
//    }
}
