package ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.SaladService;
import ui.MainApp;
import ui.dialog.*;
import vegetables.Salad;
import vegetables.Vegetable;

public class SaladViewController {

    @FXML private Label                        saladNameLabel;
    @FXML private Label                        caloriesLabel;
    @FXML        TableView<Vegetable>          tableView;
    @FXML private TableColumn<Vegetable, String> nameCol;
    @FXML private TableColumn<Vegetable, Double> weightCol;
    @FXML private TableColumn<Vegetable, Double> caloriesCol;
    @FXML        Button                        sortBtn;

    private Salad        salad;
    private int          saladId;
    private SaladService service;
    private boolean      sortAscending = true;

    public void init(Salad salad, int saladId, SaladService service) {
        this.salad   = salad;
        this.saladId = saladId;
        this.service = service;

        saladNameLabel.setText(salad.getName());
        setupTable();
        refresh();
    }

    private void setupTable() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        caloriesCol.setCellValueFactory(new PropertyValueFactory<>("totalCalories"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    void refresh() {
        tableView.getItems().setAll(salad.getVegetables());
        caloriesLabel.setText(String.format("Загальна калорійність: %.1f ккал",
                service.getTotalCalories(salad)));
    }

    @FXML
    private void addVegetable() {
        new AddVegetableDialog(service, salad, saladId).show();
        refresh();
    }

    @FXML
    private void deleteSelected() {
        Vegetable selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Виберіть овоч для видалення."); return; }
        service.removeVegetable(salad, salad.getVegetables().indexOf(selected));
        refresh();
    }

    @FXML
    private void updateSelected() {
        new UpdateVegetableDialog(service, salad)
                .show(tableView.getSelectionModel().getSelectedItem());
        refresh();
    }

    @FXML
    private void sortByCalories() {
        if (sortAscending) service.sortByCalories(salad);
        else               service.sortByCaloriesDescending(salad);
        sortAscending = !sortAscending;
        sortBtn.setText(sortAscending ? "Сортувати ↑" : "Сортувати ↓");
        refresh();
    }

    @FXML
    private void findByCalories() {
        new FindCaloriesDialog(service, salad, result -> {
            tableView.getItems().setAll(result);
            caloriesLabel.setText("Знайдено: " + result.size() +
                    " овочів. Натисніть 'Показати всі' щоб повернутись.");
        }).show();
    }

    @FXML
    private void showAll() { refresh(); }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/SaladList.fxml"));
            MainApp.primaryStage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            showAlert("Помилка повернення: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}