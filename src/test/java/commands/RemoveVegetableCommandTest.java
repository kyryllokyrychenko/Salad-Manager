package commands;

import org.junit.jupiter.api.Test;
import service.SaladService;
import utils.TestUtils;
import vegetables.*;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RemoveVegetableCommandTest {

    private final SaladService service = mock(SaladService.class);

    @Test
    void testRemoveValidIndex() {
        Salad salad = mock(Salad.class);
        when(salad.getVegetables()).thenReturn(List.of(mock(Vegetable.class)));

        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad, new Scanner("1\n"), service);
        cmd.execute();
        verify(service).removeVegetable(salad, 0);
    }

    @Test
    void testRemoveInvalidIndex_EmptyList() {
        Salad salad = mock(Salad.class);
        when(salad.getVegetables()).thenReturn(List.of());

        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad, new Scanner("1\n"), service);
        cmd.execute();
        verify(service, never()).removeVegetable(any(), anyInt());
    }

    @Test
    void testRemoveIndexBelowRange() {
        Salad salad = mock(Salad.class);
        when(salad.getVegetables()).thenReturn(List.of(mock(Vegetable.class), mock(Vegetable.class)));

        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad, new Scanner("0\n"), service);
        cmd.execute();
        verify(service, never()).removeVegetable(any(), anyInt());
    }

    @Test
    void testRemoveIndexAboveRange() {
        Salad salad = mock(Salad.class);
        when(salad.getVegetables()).thenReturn(List.of(mock(Vegetable.class)));

        RemoveVegetableCommand cmd = new RemoveVegetableCommand(salad, new Scanner("99\n"), service);
        cmd.execute();
        verify(service, never()).removeVegetable(any(), anyInt());
    }

    @Test
    void testGetDesc() {
        RemoveVegetableCommand cmd = new RemoveVegetableCommand(mock(Salad.class), service);
        assertTrue(cmd.getDesc().contains("Видалити"));
    }
}