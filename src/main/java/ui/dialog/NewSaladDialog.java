package ui.dialog;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import service.SaladService;

import java.io.File;
import java.util.function.Consumer;

public class NewSaladDialog {

    private final SaladService     service;
    private final Window           owner;
    private final Consumer<Integer> onCreated; // передає newSaladId

    public NewSaladDialog(SaladService service, Window owner,
                          Consumer<Integer> onCreated) {
        this.service   = service;
        this.owner     = owner;
        this.onCreated = onCreated;
    }

    public void show() {
        TextInputDialog nameDialog = new TextInputDialog("Мій салат");
        nameDialog.setTitle("Новий салат");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Назва салату:");

        nameDialog.showAndWait().ifPresent(name -> {
            String trimmed = name.trim().isEmpty() ? "Мій салат" : name.trim();
            int newId = service.createSalad(trimmed);
            if (newId == -1) { showAlert("Помилка створення салату."); return; }

            askForPhoto(newId);
            onCreated.accept(newId);
        });
    }

    private void askForPhoto(int saladId) {
        Alert photoAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Бажаєте додати фото для салату?", ButtonType.YES, ButtonType.NO);
        photoAlert.setHeaderText(null);
        photoAlert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Оберіть фото");
                chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Зображення", "*.png", "*.jpg", "*.jpeg"));
                File file = chooser.showOpenDialog(owner);
                processPhotoSelection(saladId, file);
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    void processPhotoSelection(int saladId, File file) {
        if (file != null) {
            service.updateSaladImage(saladId, file.getAbsolutePath());
        }
    }
}