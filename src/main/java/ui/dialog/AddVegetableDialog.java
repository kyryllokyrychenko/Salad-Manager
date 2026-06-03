package ui.dialog;

import javafx.scene.control.*;
import service.SaladService;
import vegetables.Salad;
import vegetables.Vegetable;

import java.util.ArrayList;
import java.util.Map;

public class AddVegetableDialog {

    private final SaladService service;
    private final Salad        salad;
    private final int          saladId;

    public AddVegetableDialog(SaladService service, Salad salad, int saladId) {
        this.service  = service;
        this.salad    = salad;
        this.saladId  = saladId;
    }

    public void show() {
        Map<Integer, String> types = service.getAllTypes();

        ChoiceDialog<String> typeDialog = new ChoiceDialog<>();
        types.values().forEach(typeDialog.getItems()::add);
        typeDialog.getItems().add("Власний овоч");
        typeDialog.setSelectedItem(typeDialog.getItems().get(0));
        typeDialog.setTitle("Додати овоч");
        typeDialog.setHeaderText(null);
        typeDialog.setContentText("Тип овоча:");

        typeDialog.showAndWait().ifPresent(chosen -> {
            if (chosen.equals("Власний овоч")) {
                new AddCustomVegetableDialog(service, salad, saladId).show();
            } else {
                showWeightDialog(chosen);
            }
        });
    }

    private void showWeightDialog(String typeName) {
        TextInputDialog d = new TextInputDialog("100");
        d.setTitle("Вага");
        d.setHeaderText(null);
        d.setContentText("Вага (г):");

        d.showAndWait().ifPresent(w -> {
            try {
                double weight = Double.parseDouble(w.trim());
                if (weight <= 0) { showAlert("Вага має бути більше нуля!"); return; }
                Vegetable veg = service.createVegetable(typeName, weight);
                service.addVegetable(salad, veg, saladId);
            } catch (NumberFormatException e) {
                showAlert("Введіть коректне число!");
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}