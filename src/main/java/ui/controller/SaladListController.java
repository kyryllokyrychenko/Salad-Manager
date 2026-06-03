package ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import service.SaladService;
import ui.MainApp;
import ui.component.SaladCard;
import ui.controller.SaladViewController;
import ui.dialog.NewSaladDialog;
import vegetables.Salad;

import java.util.Map;

public class SaladListController {

    @FXML private FlowPane   saladCardsPane;

    private final SaladService service = new SaladService(
            new VegetableRepository(),
            new SaladRepository(),
            new VegetableTypeRepository()
    );

    @FXML
    public void initialize() {
        loadSalads();
    }

    private void loadSalads() {
        saladCardsPane.getChildren().clear();

        service.getAllSalads().forEach((id, desc) -> {
            String name = extractName(desc);
            String imagePath = service.getSaladImage(id);
            saladCardsPane.getChildren().add(new SaladCard(
                    name, imagePath,
                    () -> openSaladById(id, name),
                    () -> deleteSaladById(id, name)
            ));
        });
    }

    @FXML
    private void createNew() {
        new NewSaladDialog(service, MainApp.primaryStage, newId -> {
            String rawDesc = service.getAllSalads().getOrDefault(newId, "Мій салат");
            String name    = extractName(rawDesc);
            openSaladView(new Salad(name), newId);
        }).show();
    }

    private void openSaladById(int saladId, String name) {
        openSaladView(service.loadSalad(saladId, name), saladId);
    }

    private void deleteSaladById(int saladId, String name) {
        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION,
                "Видалити салат '" + name + "'?",
                javafx.scene.control.ButtonType.YES,
                javafx.scene.control.ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == javafx.scene.control.ButtonType.YES) {
                service.deleteSalad(saladId);
                loadSalads();
            }
        });
    }

    private void openSaladView(Salad salad, int saladId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/SaladView.fxml"));
            MainApp.primaryStage.getScene().setRoot(loader.load());
            SaladViewController ctrl = loader.getController();
            ctrl.init(salad, saladId, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitApp() {
        javafx.application.Platform.exit();
    }

    private String extractName(String desc) {
        return desc.contains(" (") ? desc.substring(0, desc.indexOf(" (")) : desc;
    }
}