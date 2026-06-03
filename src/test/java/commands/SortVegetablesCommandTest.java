package commands;

import org.junit.jupiter.api.Test;
import service.SaladService;
import vegetables.Salad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SortVegetablesCommandTest {

    private final SaladService service = mock(SaladService.class);

    @Test
    void testExecuteCallsSortByCalories() {
        Salad salad = new Salad();
        new SortVegetablesCommand(salad, service).execute();
        verify(service).sortByCalories(salad);
    }

    @Test
    void testGetDesc() {
        assertEquals("Сортувати овочі за калорійністю",
                new SortVegetablesCommand(new Salad(), service).getDesc());
    }
}