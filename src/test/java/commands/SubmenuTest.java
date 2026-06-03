package commands;

import org.junit.jupiter.api.*;
import service.SaladService;
import vegetables.Salad;

import java.util.Map;

import static org.mockito.Mockito.*;

public class SubmenuTest {

    private Submenu submenu;

    @BeforeEach
    void setup() {
        submenu = new Submenu(new Salad("Тест"), mock(SaladService.class), 1);
    }

    @Test
    void testInitCreatesAllCommands() {
        Map<String, Command> commands = submenu.getCommands();
        Assertions.assertEquals(7, commands.size());
        Assertions.assertInstanceOf(AddVegetableCommand.class,             commands.get("додати"));
        Assertions.assertInstanceOf(ShowSaladCommand.class,                commands.get("показати"));
        Assertions.assertInstanceOf(CalculateCaloriesCommand.class,        commands.get("підрахувати"));
        Assertions.assertInstanceOf(SortVegetablesCommand.class,           commands.get("сортувати"));
        Assertions.assertInstanceOf(FindVegetablesByCaloriesCommand.class,  commands.get("знайти"));
        Assertions.assertInstanceOf(RemoveVegetableCommand.class,          commands.get("видалити"));
        Assertions.assertInstanceOf(UpdateVegetableCommand.class,          commands.get("оновити"));
    }

    @Test
    void testGetDesc() {
        Assertions.assertEquals("Відкрити сабменю", submenu.getDesc());
    }
}