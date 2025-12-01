package commands;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.TestUtils;
import vegetables.Salad;

import java.util.List;
import java.util.Scanner;

class RemoveVegetableCommandTest {

    @Test
    void testRemoveValidIndex() {
        Salad salad = Mockito.mock(Salad.class);

        Mockito.when(salad.getVegetables())
                .thenReturn(List.of(Mockito.mock(vegetables.Vegetable.class)));

        // Користувач вводить "1"
        Scanner fakeScanner = new Scanner("1\n");

        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad);
        TestUtils.setField(cmd, "sc", fakeScanner);

        cmd.execute();

        Mockito.verify(salad).remove(0);
    }

    @Test
    void testRemoveInvalidIndex() {
        Salad salad = Mockito.mock(Salad.class);
        Mockito.when(salad.getVegetables())
                .thenReturn(List.of()); // порожній список

        Scanner fakeScanner = new Scanner("1\n");

        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad);
        TestUtils.setField(cmd, "sc", fakeScanner);

        cmd.execute();

        Mockito.verify(salad, Mockito.never()).remove(Mockito.anyInt());
    }

    @Test
    void testDescription() {
        Salad salad = Mockito.mock(Salad.class);
        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad);

        assert cmd.getDesc().contains("Видалити");
    }

    @Test
    void testRemoveIndexBelowRange() {
        Salad salad = Mockito.mock(Salad.class);

        // Нехай є 2 овочі (size = 2)
        Mockito.when(salad.getVegetables())
                .thenReturn(List.of(
                        Mockito.mock(vegetables.Vegetable.class),
                        Mockito.mock(vegetables.Vegetable.class)
                ));

        // Користувач вводить "0" — нижче за мінімальний індекс
        Scanner fakeScanner = new Scanner("0\n");

        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad);
        TestUtils.setField(cmd, "sc", fakeScanner);

        cmd.execute();

        // remove НЕ повинен викликатись
        Mockito.verify(salad, Mockito.never()).remove(Mockito.anyInt());
    }

}
