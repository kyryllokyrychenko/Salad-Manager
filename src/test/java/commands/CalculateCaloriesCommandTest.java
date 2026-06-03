package commands;

import org.junit.jupiter.api.Test;
import service.SaladService;
import vegetables.Salad;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculateCaloriesCommandTest {

    private final SaladService service = mock(SaladService.class);

    @Test
    void testExecutePrintsCalories() {
        Salad salad = new Salad();
        when(service.getTotalCalories(salad)).thenReturn(123.45);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        new CalculateCaloriesCommand(salad, service).execute();

        System.setOut(System.out);
        assertTrue(out.toString().contains("123,45") || out.toString().contains("123.45"));
        verify(service).getTotalCalories(salad);
    }

    @Test
    void testGetDesc() {
        assertEquals("Підрахувати калорійність салату",
                new CalculateCaloriesCommand(new Salad(), service).getDesc());
    }
}