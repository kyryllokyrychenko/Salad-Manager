package ui.dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.SaladService;
import vegetables.Salad;
import vegetables.Vegetable;

import java.util.List;
import java.util.function.Consumer;

public class FindCaloriesDialog {

    private final SaladService        service;
    private final Salad               salad;
    private final Consumer<List<Vegetable>> onResult;

    public FindCaloriesDialog(SaladService service, Salad salad,
                              Consumer<List<Vegetable>> onResult) {
        this.service  = service;
        this.salad    = salad;
        this.onResult = onResult;
    }

    public void show() {
        Dialog<double[]> dialog = new Dialog<>();
        dialog.setTitle("Пошук за калоріями");
        dialog.setHeaderText(null);

        TextField minField = new TextField("0");
        TextField maxField = new TextField("100");
        VBox box = new VBox(8,
                new Label("Мінімум (ккал):"), minField,
                new Label("Максимум (ккал):"), maxField);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);

        ButtonType findType = new ButtonType("Знайти", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(findType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == findType) {
                try {
                    return new double[]{
                            Double.parseDouble(minField.getText().trim()),
                            Double.parseDouble(maxField.getText().trim())
                    };
                } catch (NumberFormatException e) {
                    showAlert("Введіть коректні числа!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(range -> {
            List<Vegetable> result = service.findByCalories(salad, range[0], range[1]);
            if (result.isEmpty()) {
                showAlert("Нічого не знайдено в діапазоні "
                        + range[0] + "—" + range[1] + " ккал.");
            } else {
                onResult.accept(result);
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}