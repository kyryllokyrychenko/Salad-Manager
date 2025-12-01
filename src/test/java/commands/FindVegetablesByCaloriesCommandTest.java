package commands;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.TestUtils;
import vegetables.Salad;
import vegetables.Vegetable;

import java.util.List;
import java.util.Scanner;

class FindVegetablesByCaloriesCommandTest {

    @Test
    void testFindVegetablesInRange() {
        Salad salad = Mockito.mock(Salad.class);

        // Нехай користувач вводить: 10, 50
        Scanner fakeScanner = new Scanner("10\n50\n");

        FindVegetablesByCaloriesCommand cmd =
                new FindVegetablesByCaloriesCommand(salad);

        // Підміна приватного поля через reflection
        TestUtils.setField(cmd, "sc", fakeScanner);

        List<Vegetable> mockList = List.of(Mockito.mock(Vegetable.class));
        Mockito.when(salad.findByCalories(10, 50)).thenReturn(mockList);

        cmd.execute();

        Mockito.verify(salad).findByCalories(10, 50);
    }

    @Test
    void testDescription() {
        Salad salad = Mockito.mock(Salad.class);
        FindVegetablesByCaloriesCommand cmd = new FindVegetablesByCaloriesCommand(salad);

        assert cmd.getDesc().contains("діапазоном");
    }

    private void printResult(List<Vegetable> vegetables) {
        if (vegetables.isEmpty()) {
            System.out.println("Нічого не знайдено.");
            return;
        }
        vegetables.forEach(System.out::println);
    }

}
