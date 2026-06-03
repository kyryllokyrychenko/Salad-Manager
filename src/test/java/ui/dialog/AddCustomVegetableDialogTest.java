package ui.dialog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import service.SaladService;
import vegetables.CustomVegetable;
import vegetables.Salad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddCustomVegetableDialogTest {

    private AddCustomVegetableDialog dialog;

    @BeforeEach
    void setUp() {
        SaladService service = mock(SaladService.class);
        dialog = new AddCustomVegetableDialog(service, new Salad(), 1);
    }

    @Test
    void testBuildVegetableValid() {
        CustomVegetable veg = dialog.buildVegetable("Баклажан", "100", "25");
        assertNotNull(veg);
        assertEquals("Баклажан", veg.getName());
        assertEquals(100, veg.getWeight());
        assertEquals(25, veg.getCaloriesPer100g());
    }

    @Test
    void testBuildVegetableEmptyName() {
        assertNull(dialog.buildVegetable("", "100", "25"));
        assertNull(dialog.buildVegetable("  ", "100", "25"));
        assertNull(dialog.buildVegetable(null, "100", "25"));
    }

    @Test
    void testBuildVegetableNegativeWeight() {
        assertNull(dialog.buildVegetable("Баклажан", "-10", "25"));
    }

    @Test
    void testBuildVegetableZeroWeight() {
        assertNull(dialog.buildVegetable("Баклажан", "0", "25"));
    }

    @Test
    void testBuildVegetableNegativeCalories() {
        assertNull(dialog.buildVegetable("Баклажан", "100", "-5"));
    }

    @Test
    void testBuildVegetableInvalidWeight() {
        assertNull(dialog.buildVegetable("Баклажан", "абвг", "25"));
    }

    @Test
    void testBuildVegetableInvalidCalories() {
        assertNull(dialog.buildVegetable("Баклажан", "100", "абвг"));
    }

    @Test
    void testBuildVegetableZeroCaloriesAllowed() {
        CustomVegetable veg = dialog.buildVegetable("Огірок", "100", "0");
        assertNotNull(veg);
        assertEquals(0, veg.getCaloriesPer100g());
    }
}