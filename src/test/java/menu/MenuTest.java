package menu;

import commands.Command;
import org.junit.jupiter.api.*;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import service.SaladService;
import vegetables.Salad;

import java.io.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream out;
    private SaladService service;
    private Salad        salad;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        service = mock(SaladService.class);
        salad   = new Salad("Тестовий салат");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private Menu buildMenu() {
        return new Menu(salad, service, 1);
    }

    @Test
    void testInitCreatesAllCommands() {
        Map<String, Command> map = buildMenu().getCommands();
        assertTrue(map.containsKey("додати"));
        assertTrue(map.containsKey("показати"));
        assertTrue(map.containsKey("підрахувати"));
        assertTrue(map.containsKey("сортувати"));
        assertTrue(map.containsKey("знайти"));
        assertTrue(map.containsKey("видалити"));
        assertTrue(map.containsKey("оновити"));
        assertTrue(map.containsKey("новий"));
        assertTrue(map.containsKey("відкрити"));
        assertTrue(map.containsKey("сабменю"));
        assertEquals(10, map.size());
    }

    @Test
    void testExecuteValidCommand() {
        System.setIn(new ByteArrayInputStream("\n".getBytes()));
        Menu menu = buildMenu();
        Command mockCommand = mock(Command.class);
        menu.commands.put("тест", mockCommand);
        menu.execute("тест");
        verify(mockCommand, times(1)).execute();
    }

    @Test
    void testExecuteInvalidCommand() {
        Menu menu = buildMenu();
        menu.execute("немаєтакої");
        assertTrue(out.toString().contains("Невірний вибір!"));
    }

    @Test
    void testShowDisplaysSaladName() {
        buildMenu().show();
        assertTrue(out.toString().contains("Тестовий салат"));
        assertTrue(out.toString().contains("вихід"));
    }

    @Test
    void testStartExecutesCommandAndExits() {
        String input = "показати\n\nвихід\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Menu menu = buildMenu();
        Command mockShow = mock(Command.class);
        menu.commands.put("показати", mockShow);
        menu.start();
        verify(mockShow, times(1)).execute();
        assertTrue(out.toString().contains("Вихід з програми"));
    }
}