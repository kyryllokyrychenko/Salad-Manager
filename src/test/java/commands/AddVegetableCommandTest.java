package commands;

import org.junit.jupiter.api.Test;
import service.SaladService;
import vegetables.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddVegetableCommandTest {

    private final SaladService service = mock(SaladService.class);

    private Map<Integer, String> defaultTypes() {
        Map<Integer, String> m = new LinkedHashMap<>();
        m.put(1, "Морква");
        m.put(2, "Помідор");
        m.put(3, "Огірок");
        m.put(4, "Лук");
        m.put(5, "Капуста");
        m.put(6, "Перець");
        return m;
    }

    private AddVegetableCommand cmd(Salad salad, Scanner sc) {
        when(service.getAllTypes()).thenReturn(defaultTypes());
        when(service.createVegetable(anyString(), anyDouble())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            double w    = inv.getArgument(1);
            return switch (name) {
                case "Морква"  -> new Carrot(w);
                case "Помідор" -> new Tomato(w);
                case "Огірок"  -> new Cucumber(w);
                case "Лук"     -> new Onion(w);
                case "Капуста" -> new Cabbage(w);
                case "Перець"  -> new Pepper(w);
                default        -> new CustomVegetable(name, w, 20);
            };
        });
        doAnswer(inv -> { salad.add(inv.getArgument(1)); return null; }) // ← було 0, стало 1
                .when(service).addVegetable(any(), any(), anyInt());
        return new AddVegetableCommand(salad, sc, service, 1);
    }

    @Test
    void testExecuteAddsCarrotToSalad() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("1\n200\n")).execute();
        assertEquals(1, salad.getVegetables().size());
        assertInstanceOf(Carrot.class, salad.getVegetables().get(0));
        assertEquals(200, salad.getVegetables().get(0).getWeight());
    }

    @Test
    void testExecuteInvalidChoiceDoesNotAdd() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("99\n")).execute();
        assertEquals(0, salad.getVegetables().size());
    }

    @Test
    void testExecuteAddsCustomVegetable() {
        Salad salad = new Salad();
        when(service.createCustomVegetable("Баклажан", 100, 25))
                .thenReturn(new CustomVegetable("Баклажан", 100, 25));
        // doAnswer прибрали — він вже є в cmd()
        cmd(salad, new Scanner("7\nБаклажан\n100\n25\n")).execute();
        assertEquals(1, salad.getVegetables().size());
        assertInstanceOf(CustomVegetable.class, salad.getVegetables().get(0));
        verify(service).createCustomVegetable("Баклажан", 100, 25);
    }

    @Test
    void testExecuteCustomVegetableEmptyNameDoesNotAdd() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("7\n\n")).execute();
        assertEquals(0, salad.getVegetables().size());
    }

    @Test
    void testExecuteNonNumberInputDoesNotAdd() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("абвг\n")).execute();
        assertEquals(0, salad.getVegetables().size());
    }

    @Test
    void testExecuteNegativeWeightDoesNotAdd() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("1\n-50\n")).execute();
        assertEquals(0, salad.getVegetables().size());
    }

    @Test
    void testExecuteInvalidWeightStringDoesNotAdd() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("1\nабвг\n")).execute();
        assertEquals(0, salad.getVegetables().size());
    }

    @Test
    void testExecuteCustomInvalidWeightDoesNotAdd() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("7\nБаклажан\nабвг\n")).execute();
        assertEquals(0, salad.getVegetables().size());
    }

    @Test
    void testExecuteCustomNegativeWeightDoesNotAdd() {
        Salad salad = new Salad();
        cmd(salad, new Scanner("7\nБаклажан\n-10\n20\n")).execute();
        assertEquals(0, salad.getVegetables().size());
    }

    @Test
    void testExecuteCustomTypeFromDbCreatesCustomVegetable() {
        Map<Integer, String> types = new LinkedHashMap<>();
        types.put(1, "Рукола");
        when(service.getAllTypes()).thenReturn(types);
        when(service.createVegetable("Рукола", 100))
                .thenReturn(new CustomVegetable("Рукола", 100, 20));

        Salad salad = new Salad();
        doAnswer(inv -> { salad.add(inv.getArgument(1)); return null; })
                .when(service).addVegetable(any(), any(), anyInt());

        new AddVegetableCommand(salad, new Scanner("1\n100\n"), service, 1).execute();
        assertEquals(1, salad.getVegetables().size());
        assertInstanceOf(CustomVegetable.class, salad.getVegetables().get(0));
    }

    @Test
    void testGetDesc() {
        assertEquals("Додати овоч до салату",
                new AddVegetableCommand(new Salad(), service, 1).getDesc());
    }
}