package commands;

import menu.Menu;
import org.junit.jupiter.api.*;
import service.SaladService;
import utils.TestUtils;
import vegetables.Salad;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewSaladCommandTest {

    private final SaladService service = mock(SaladService.class);
    private final Menu         menu    = mock(Menu.class);
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() { System.setOut(System.out); }

    private NewSaladCommand buildCmd(String input) {
        NewSaladCommand cmd = new NewSaladCommand(new Salad(), service, menu);
        TestUtils.setField(cmd, "sc", new Scanner(input));
        return cmd;
    }

    @Test
    void testCreateWithValidName() {
        when(service.createSalad("Літній")).thenReturn(5);
        buildCmd("Літній\n").execute();
        verify(menu).switchSalad(any(), eq(5));
        assertTrue(out.toString().contains("Літній"));
    }

    @Test
    void testEmptyNameUsesDefault() {
        when(service.createSalad(anyString())).thenReturn(3);
        buildCmd("\n").execute();
        verify(service).createSalad(anyString());
        verify(menu).switchSalad(any(), eq(3));
    }

    @Test
    void testCreateFailsWhenReturnsMinusOne() {
        when(service.createSalad(anyString())).thenReturn(-1);
        buildCmd("Поганий\n").execute();
        verify(menu, never()).switchSalad(any(), anyInt());
        assertTrue(out.toString().contains("Помилка"));
    }

    @Test
    void testGetDesc() {
        assertEquals("Створити новий порожній салат",
                new NewSaladCommand(new Salad(), service, menu).getDesc());
    }
}