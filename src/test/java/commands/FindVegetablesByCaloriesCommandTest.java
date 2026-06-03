package commands;

import org.junit.jupiter.api.*;
import service.SaladService;
import vegetables.*;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindVegetablesByCaloriesCommandTest {

    private final SaladService service = mock(SaladService.class);
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() { System.setOut(System.out); }

    @Test
    void testFindsVegetablesInRange() {
        Salad salad = new Salad();
        when(service.findByCalories(salad, 10, 20))
                .thenReturn(List.of(new Tomato(100)));

        new FindVegetablesByCaloriesCommand(salad, new Scanner("10\n20\n"), service).execute();

        assertFalse(out.toString().contains("Нічого не знайдено."));
        verify(service).findByCalories(salad, 10, 20);
    }

    @Test
    void testNothingFoundPrintsMessage() {
        Salad salad = new Salad();
        when(service.findByCalories(salad, 100, 200)).thenReturn(List.of());

        new FindVegetablesByCaloriesCommand(salad, new Scanner("100\n200\n"), service).execute();

        assertTrue(out.toString().contains("Нічого не знайдено."));
    }

    @Test
    void testGetDesc() {
        assertEquals("Знайти овочі за діапазоном калорій",
                new FindVegetablesByCaloriesCommand(new Salad(), service).getDesc());
    }
}