package vegetables;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaladTest {

    @Test
    void testAddVegetable() {
        Salad salad = new Salad();
        Vegetable veg = mock(Vegetable.class);

        salad.add(veg);

        assertEquals(1, salad.getVegetables().size());
    }

    @Test
    void testAddNullThrows() {
        Salad salad = new Salad();

        assertThrows(IllegalArgumentException.class, () -> salad.add(null));
    }

    @Test
    void testGetVegetablesIsUnmodifiable() {
        Salad salad = new Salad();
        salad.add(mock(Vegetable.class));

        List<Vegetable> list = salad.getVegetables();

        assertThrows(UnsupportedOperationException.class, () -> list.add(mock(Vegetable.class)));
    }

    @Test
    void testRemoveInvalidIndex() {
        Salad salad = new Salad();

        assertThrows(IndexOutOfBoundsException.class, () -> salad.remove(1));
    }

    @Test
    void testGetTotalCalories() {
        Salad salad = new Salad();

        Vegetable v1 = mock(Vegetable.class);
        Vegetable v2 = mock(Vegetable.class);

        when(v1.getTotalCalories()).thenReturn(30.0);
        when(v2.getTotalCalories()).thenReturn(100.0);

        salad.add(v1);
        salad.add(v2);

        assertEquals(130.0, salad.getTotalCalories());
    }

    @Test
    void testSortByCalories() {
        Salad salad = new Salad();

        Vegetable low = mock(Vegetable.class);
        Vegetable high = mock(Vegetable.class);

        when(low.getTotalCalories()).thenReturn(20.0);
        when(high.getTotalCalories()).thenReturn(50.0);

        salad.add(high);
        salad.add(low);

        salad.sortByCalories();

        assertEquals(low, salad.getVegetables().get(0));
    }

    @Test
    void testFindByCalories() {
        Salad salad = new Salad();

        Vegetable v1 = mock(Vegetable.class);
        Vegetable v2 = mock(Vegetable.class);

        when(v1.getTotalCalories()).thenReturn(30.0);
        when(v2.getTotalCalories()).thenReturn(80.0);

        salad.add(v1);
        salad.add(v2);

        List<Vegetable> result = salad.findByCalories(20, 60);

        assertEquals(1, result.size());
        assertEquals(v1, result.get(0));
    }

    @Test
    void testFindByCaloriesEmptyResult() {
        Salad salad = new Salad();

        Vegetable v1 = mock(Vegetable.class);
        when(v1.getTotalCalories()).thenReturn(100.0);

        salad.add(v1);

        List<Vegetable> result = salad.findByCalories(0, 50);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByCaloriesOnBoundsInclusive() {
        Salad salad = new Salad();

        Vegetable v1 = mock(Vegetable.class);
        Vegetable v2 = mock(Vegetable.class);
        Vegetable v3 = mock(Vegetable.class);

        when(v1.getTotalCalories()).thenReturn(10.0); // = min
        when(v2.getTotalCalories()).thenReturn(20.0); // inside
        when(v3.getTotalCalories()).thenReturn(30.0); // = max

        salad.add(v1);
        salad.add(v2);
        salad.add(v3);

        List<Vegetable> result = salad.findByCalories(10, 30);

        assertEquals(3, result.size());
        assertTrue(result.contains(v1));
        assertTrue(result.contains(v2));
        assertTrue(result.contains(v3));
    }

    @Test
    void testGetTotalCaloriesEmptySalad() {
        Salad salad = new Salad();
        assertEquals(0.0, salad.getTotalCalories());
    }

    @Test
    void testSortByCaloriesEmptyList() {
        Salad salad = new Salad();
        assertDoesNotThrow(salad::sortByCalories);
        assertTrue(salad.getVegetables().isEmpty());
    }

    @Test
    void testSortByCaloriesSingleElement() {
        Salad salad = new Salad();
        Vegetable v = mock(Vegetable.class);
        when(v.getTotalCalories()).thenReturn(42.0);

        salad.add(v);

        assertDoesNotThrow(salad::sortByCalories);
        assertEquals(1, salad.getVegetables().size());
        assertEquals(v, salad.getVegetables().get(0));
    }

    @Test
    void testConstructorNullNameUsesDefault() {
        Salad s = new Salad(null);
        assertEquals("Без назви", s.getName());
    }

    @Test
    void testConstructorBlankNameUsesDefault() {
        Salad s = new Salad("   ");
        assertEquals("Без назви", s.getName());
    }

    @Test
    void testRemoveValidIndex() {
        Salad s = new Salad();
        s.add(new Carrot(100));
        s.remove(0);
        assertTrue(s.getVegetables().isEmpty());
    }

    @Test
    void testRemoveNegativeIndexThrows() {
        Salad s = new Salad();
        s.add(new Carrot(100));
        assertThrows(IndexOutOfBoundsException.class, () -> s.remove(-1));
    }

    @Test
    void testRemoveIndexTooLargeThrows() {
        Salad s = new Salad();
        s.add(new Carrot(100));
        assertThrows(IndexOutOfBoundsException.class, () -> s.remove(5));
    }

    @Test
    void testSetName() {
        Salad s = new Salad("Старий");
        s.setName("Новий");
        assertEquals("Новий", s.getName());
    }

}
