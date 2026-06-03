package commands;

import org.junit.jupiter.api.*;
import service.SaladService;
import vegetables.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UpdateVegetableCommandTest {

    private final SaladService service = mock(SaladService.class);
    private Salad     salad;
    private Vegetable veg1;
    private Vegetable veg2;

    @BeforeEach
    void setup() {
        salad = mock(Salad.class);
        veg1  = mock(Vegetable.class);
        veg2  = mock(Vegetable.class);
        when(salad.getVegetables()).thenReturn(List.of(veg1, veg2));
    }

    @Test
    void testExecuteValidIndexUpdatesWeight() {
        System.setIn(new ByteArrayInputStream("2\n150.5\n".getBytes()));
        new UpdateVegetableCommand(salad, service).execute();
        verify(service).updateWeight(salad, 1, 150.5);
    }

    @Test
    void testExecuteInvalidIndexDoesNotUpdate() {
        System.setIn(new ByteArrayInputStream("5\n".getBytes()));
        new UpdateVegetableCommand(salad, service).execute();
        verify(service, never()).updateWeight(any(), anyInt(), anyDouble());
    }

    @Test
    void testGetDesc() {
        assertEquals("Оновити вагу овоча за номером у салаті",
                new UpdateVegetableCommand(salad, service).getDesc());
    }
}