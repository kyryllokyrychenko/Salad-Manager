package commands;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import utils.TestUtils;
import vegetables.Salad;
import vegetables.Vegetable;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UpdateVegetableCommandTest {

    private Salad salad;
    private Vegetable veg1;
    private Vegetable veg2;

    @BeforeEach
    void setup() {
        salad = mock(Salad.class);

        veg1 = mock(Vegetable.class);
        veg2 = mock(Vegetable.class);

        List<Vegetable> list = new ArrayList<>();
        list.add(veg1);
        list.add(veg2);

        when(salad.getVegetables()).thenReturn(list);
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    void testExecute_InvalidIndex() {
        // User inputs: "5\n" (index) → invalid
        setInput("5\n");

        UpdateVegetableCommand cmd = new UpdateVegetableCommand(salad);

        cmd.execute();

        // Нічого не має оновитись
        verify(veg1, never()).setWeight(anyDouble());
        verify(veg2, never()).setWeight(anyDouble());
    }

    @Test
    void testExecute_ValidIndexAndUpdate() {
        // User enters:
        // "2\n"  (index)
        // "150.5\n" (new weight)
        setInput("2\n150.5\n");

        UpdateVegetableCommand cmd = new UpdateVegetableCommand(salad);

        cmd.execute();

        // Перевіряємо, що встановлена вага для другого елемента (index 1)
        verify(veg2, times(1)).setWeight(150.5);
        verify(veg1, never()).setWeight(anyDouble());
    }

    @Test
    void testGetDesc() {
        UpdateVegetableCommand cmd = new UpdateVegetableCommand(salad);

        Assertions.assertEquals(
                "Оновити вагу овоча за номером у салаті",
                cmd.getDesc()
        );
    }

    @Test
    void testFindVegetablesEmptyResult() {
        Salad salad = Mockito.mock(Salad.class);

        // user input: min=10, max=20
        Scanner fakeScanner = new Scanner("10\n20\n");
        FindVegetablesByCaloriesCommand cmd =
                new FindVegetablesByCaloriesCommand(salad);

        TestUtils.setField(cmd, "sc", fakeScanner);

        Mockito.when(salad.findByCalories(10, 20)).thenReturn(List.of());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        cmd.execute();

        assertTrue(out.toString().contains("Нічого не знайдено."));
    }

}
