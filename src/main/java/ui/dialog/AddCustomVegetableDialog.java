package ui.dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.SaladService;
import vegetables.CustomVegetable;
import vegetables.Salad;
import vegetables.Vegetable;

public class AddCustomVegetableDialog {

    private final SaladService service;
    private final Salad        salad;
    private final int          saladId;

    public AddCustomVegetableDialog(SaladService service, Salad salad, int saladId) {
        this.service = service;
        this.salad   = salad;
        this.saladId = saladId;
    }

    public void show() {
        Dialog<Vegetable> dialog = new Dialog<>();
        dialog.setTitle("Власний овоч");
        dialog.setHeaderText(null);

        TextField nameField     = new TextField();
        TextField weightField   = new TextField();
        TextField caloriesField = new TextField();
        nameField.setPromptText("Назва");
        weightField.setPromptText("Вага (г)");
        caloriesField.setPromptText("Калорійність (ккал/100г)");

        VBox box = new VBox(8,
                new Label("Назва:"),     nameField,
                new Label("Вага (г):"),  weightField,
                new Label("Ккал/100г:"), caloriesField);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);

        ButtonType addType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == addType) {
                return buildVegetable(
                        nameField.getText().trim(),
                        weightField.getText().trim(),
                        caloriesField.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(veg -> {
            if (veg != null) {
                Vegetable saved = service.createCustomVegetable(
                        veg.getName(), veg.getWeight(),
                        ((CustomVegetable) veg).getCaloriesPer100g());
                service.addVegetable(salad, saved, saladId);
            }
        });
    }

    // package-private для тестування
    CustomVegetable buildVegetable(String name, String weightStr, String caloriesStr) {
        if (name == null || name.isBlank()) return null;
        try {
            double weight   = Double.parseDouble(weightStr.trim());
            double calories = Double.parseDouble(caloriesStr.trim());
            if (weight <= 0 || calories < 0) return null;
            return new CustomVegetable(name, weight, calories);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}