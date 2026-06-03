package ui.dialog;

import org.junit.jupiter.api.Test;
import service.SaladService;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewSaladDialogTest {

    private final SaladService service = mock(SaladService.class);

    private NewSaladDialog buildDialog() {
        return new NewSaladDialog(service, null, id -> {});
    }

    @Test
    void testProcessPhotoSelectionWithFile() {
        NewSaladDialog dialog = buildDialog();
        File file = new File("C:\\Users\\Кирило\\Downloads\\фу.png");
        dialog.processPhotoSelection(1, file);
        verify(service).updateSaladImage(1, file.getAbsolutePath());
    }

    @Test
    void testProcessPhotoSelectionWithNullFile() {
        NewSaladDialog dialog = buildDialog();
        dialog.processPhotoSelection(1, null);
        verify(service, never()).updateSaladImage(anyInt(), anyString());
    }
}