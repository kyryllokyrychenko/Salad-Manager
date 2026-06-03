package commands;

import menu.Menu;
import org.junit.jupiter.api.*;
import service.SaladService;
import utils.TestUtils;
import vegetables.Salad;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenSaladCommandTest {

    private final SaladService        service = mock(SaladService.class);
    private final Menu                menu    = mock(Menu.class);
    private ByteArrayOutputStream     out;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() { System.setOut(System.out); }

    private OpenSaladCommand buildCmd(String input) {
        OpenSaladCommand cmd = new OpenSaladCommand(new Salad(), service, menu);
        TestUtils.setField(cmd, "sc", new Scanner(input));
        return cmd;
    }

    private Map<Integer, String> twoSalads() {
        Map<Integer, String> m = new LinkedHashMap<>();
        m.put(1, "Літній (створено: 2024-01-01 10:00)");
        m.put(2, "Зимовий (створено: 2024-01-02 10:00)");
        return m;
    }

    @Test
    void testEmptyListShowsMessage() {
        when(service.getAllSalads()).thenReturn(new LinkedHashMap<>());
        buildCmd("").execute();
        assertTrue(out.toString().contains("немає"));
    }

    @Test
    void testCancelWithZero() {
        when(service.getAllSalads()).thenReturn(twoSalads());
        buildCmd("0\n").execute();
        assertTrue(out.toString().contains("Скасовано"));
        verify(menu, never()).switchSalad(any(), anyInt());
    }

    @Test
    void testInvalidInputCancels() {
        when(service.getAllSalads()).thenReturn(twoSalads());
        buildCmd("абвг\n").execute();
        assertTrue(out.toString().contains("Некоректний"));
        verify(menu, never()).switchSalad(any(), anyInt());
    }

    @Test
    void testUnknownIdCancels() {
        when(service.getAllSalads()).thenReturn(twoSalads());
        buildCmd("99\n").execute();
        assertTrue(out.toString().contains("не існує"));
        verify(menu, never()).switchSalad(any(), anyInt());
    }

    @Test
    void testValidChoiceSwitchesSalad() {
        when(service.getAllSalads()).thenReturn(twoSalads());
        when(service.loadSalad(1, "Літній")).thenReturn(new Salad("Літній"));
        buildCmd("1\n").execute();
        verify(menu).switchSalad(any(), eq(1));
        assertTrue(out.toString().contains("Літній"));
    }

    @Test
    void testGetDesc() {
        assertEquals("Відкрити існуючий салат із бази даних",
                new OpenSaladCommand(new Salad(), service, menu).getDesc());
    }
}