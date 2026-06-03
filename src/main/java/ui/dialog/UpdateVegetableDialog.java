package ui.dialog;

import javafx.scene.control.*;
import service.SaladService;
import vegetables.Salad;
import vegetables.Vegetable;

public class UpdateVegetableDialog {

    private final SaladService service;
    private final Salad        salad;

    public UpdateVegetableDialog(SaladService service, Salad salad) {
        this.service = service;
        this.salad   = salad;
    }

    public void show(Vegetable selected) {
        if (selected == null) { showAlert("Виберіть овоч для оновлення."); return; }

        TextInputDialog d = new TextInputDialog(String.valueOf(selected.getWeight()));
        d.setTitle("Оновити вагу");
        d.setHeaderText(null);
        d.setContentText("Нова вага (г) для " + selected.getName() + ":");

        d.showAndWait().ifPresent(w -> {
            try {
                double newWeight = Double.parseDouble(w.trim());
                if (newWeight <= 0) { showAlert("Вага має бути більше нуля!"); return; }
                int idx = salad.getVegetables().indexOf(selected);
                service.updateWeight(salad, idx, newWeight);
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