package ui.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import service.SaladService;
import ui.MainApp;
import vegetables.Carrot;
import vegetables.Salad;
import vegetables.Tomato;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class SaladViewControllerTest extends ApplicationTest {

    private SaladService service;
    private Salad        testSalad;
    private int          testSaladId;

    @Override
    public void start(Stage stage) throws Exception {
        service = new SaladService(
                new VegetableRepository(),
                new SaladRepository(),
                new VegetableTypeRepository()
        );
        new MainApp().start(stage);
        testSaladId = service.createSalad("TestSalad_UI");
        testSalad   = new Salad("TestSalad_UI");
        testSalad.add(new Carrot(100));
        testSalad.add(new Tomato(80));
        loadSaladView();
        sleep(300);
    }

    private void loadSaladView() {
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/SaladView.fxml"));
                MainApp.primaryStage.getScene().setRoot(loader.load());
                SaladViewController ctrl = loader.getController();
                ctrl.init(testSalad, testSaladId, service);
            } catch (Exception e) { e.printStackTrace(); }
        });
        sleep(300);
    }

    private SaladViewController getController() {
        SaladViewController[] ctrl = new SaladViewController[1];
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/SaladView.fxml"));
                MainApp.primaryStage.getScene().setRoot(loader.load());
                ctrl[0] = loader.getController();
                ctrl[0].init(testSalad, testSaladId, service);
            } catch (Exception e) { e.printStackTrace(); }
        });
        sleep(300);
        return ctrl[0];
    }

    @Test void testSaladViewLoads() { verifyThat("#tableView", isVisible()); }
    @Test void testCaloriesLabelExists() { assertNotNull(lookup("#caloriesLabel").query()); }
    @Test void testSaladNameLabelExists() { assertNotNull(lookup("#saladNameLabel").query()); }
    @Test void testAddButtonExists() { assertNotNull(lookup("+ Додати").queryButton()); }
    @Test void testBackButtonExists() { assertNotNull(lookup("← Назад").queryButton()); }
    @Test void testSortButtonExists() { assertNotNull(lookup("#sortBtn").queryButton()); }
    @Test void testShowAllButtonExists() { assertNotNull(lookup("Показати всі").queryButton()); }
    @Test void testFindButtonExists() { assertNotNull(lookup("Знайти").queryButton()); }
    @Test void testUpdateButtonExists() { assertNotNull(lookup("Оновити").queryButton()); }
    @Test void testDeleteButtonExists() { assertNotNull(lookup("Видалити").queryButton()); }

    @Test
    void testSortAscending() {
        Button sortBtn = lookup("#sortBtn").queryButton();
        assertEquals("Сортувати ↑", sortBtn.getText());
        clickOn("#sortBtn");
        assertEquals("Сортувати ↓", sortBtn.getText());
    }

    @Test
    void testSortDescending() {
        clickOn("#sortBtn");
        clickOn("#sortBtn");
        assertEquals("Сортувати ↑", lookup("#sortBtn").queryButton().getText());
    }

    @Test void testShowAllDoesNotThrow() { assertDoesNotThrow(() -> clickOn("Показати всі")); }

    @Test
    void testDeleteWithNoSelectionShowsAlert() {
        clickOn("Видалити");
        assertNotNull(lookup(".dialog-pane").query());
        clickOn("OK");
    }

    @Test
    void testUpdateWithNoSelectionShowsAlert() {
        clickOn("Оновити");
        assertNotNull(lookup(".dialog-pane").query());
        clickOn("OK");
    }

    @Test
    void testAddVegetableDialogOpens() {
        clickOn("+ Додати");
        assertNotNull(lookup(".dialog-pane").query());
        press(javafx.scene.input.KeyCode.ESCAPE);
    }

    @Test
    void testFindDialogOpens() {
        clickOn("Знайти");
        assertNotNull(lookup(".dialog-pane").query());
        press(javafx.scene.input.KeyCode.ESCAPE);
    }

    @Test
    void testFindDialogConfirmWithResults() {
        clickOn("Знайти");
        var minField = lookup(".text-field").nth(0).query();
        var maxField = lookup(".text-field").nth(1).query();
        doubleClickOn(minField).write("0");
        doubleClickOn(maxField).write("100");
        clickOn("Знайти");
        sleep(200);
        verifyThat("#tableView", isVisible());
    }

    @Test
    void testFindDialogConfirmNoResults() {
        clickOn("Знайти");
        sleep(500);
        var fields = lookup(".text-field").queryAllAs(javafx.scene.control.TextField.class);
        if (fields.size() >= 2) {
            var list = new java.util.ArrayList<>(fields);
            doubleClickOn((javafx.scene.Node) list.get(0)).write("999");
            doubleClickOn((javafx.scene.Node) list.get(1)).write("9999");
        }
        // Клікаємо через TestFX clickOn — він сам запускає на FX потоці
        lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().equals("Знайти") && b.isVisible() && b.getScene() != null)
                .findFirst().ifPresent(this::clickOn);
        sleep(500);
        lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().equals("OK") && b.isVisible())
                .findFirst().ifPresent(this::clickOn);
    }

    @Test
    void testBackButtonNavigatesToList() {
        clickOn("← Назад");
        sleep(300);
        assertNotNull(lookup("+ Новий салат").queryButton());
    }

    @Test
    void testAddStandardVegetableValidWeight() {
        clickOn("+ Додати");
        sleep(200);
        assertNotNull(lookup(".dialog-pane").query());
        clickOn("OK");
        sleep(200);
        var weightPane = lookup(".dialog-pane").queryAs(javafx.scene.control.DialogPane.class);
        if (weightPane != null) {
            var tf = lookup(".text-field").query();
            doubleClickOn(tf).write("150");
            clickOn("OK");
        }
        sleep(200);
    }

    @Test
    void testAddStandardVegetableInvalidWeight() {
        clickOn("+ Додати");
        sleep(200);
        if (lookup(".dialog-pane").query() != null) {
            clickOn("OK");
            sleep(200);
        }
        var weightPane = lookup(".dialog-pane").queryAs(javafx.scene.control.DialogPane.class);
        if (weightPane != null) {
            doubleClickOn((javafx.scene.Node) lookup(".text-field").query()).write("абвг");
            clickOn("OK");
            sleep(200);
            var alert = lookup(".dialog-pane").queryAs(javafx.scene.control.DialogPane.class);
            if (alert != null) clickOn("OK");
        }
    }

    @Test
    void testAddCustomVegetableViaDialog() {
        clickOn("+ Додати");
        sleep(500);
        press(javafx.scene.input.KeyCode.ESCAPE);
        sleep(200);
    }

    @Test
    void testUpdateSelectedWithItem() {
        SaladViewController ctrl = getController();
        javafx.application.Platform.runLater(() ->
                ctrl.tableView.getSelectionModel().select(0));
        sleep(200);
        clickOn("Оновити");
        sleep(300);
        var tf = lookup(".text-field").query();
        if (tf != null) { doubleClickOn(tf).write("200"); clickOn("OK"); sleep(200); }
    }

    @Test
    void testUpdateSelectedNegativeWeight() {
        SaladViewController ctrl = getController();
        javafx.application.Platform.runLater(() ->
                ctrl.tableView.getSelectionModel().select(0));
        sleep(200);
        clickOn("Оновити");
        sleep(300);
        var tf = lookup(".text-field").query();
        if (tf != null) {
            doubleClickOn(tf).write("-50");
            clickOn("OK");
            sleep(200);
            lookup(".button").queryAllAs(Button.class).stream()
                    .filter(b -> b.getText().equals("OK"))
                    .findFirst().ifPresent(this::clickOn);
        }
    }

    @Test
    void testDeleteSelectedWithItem() {
        SaladViewController ctrl = getController();
        javafx.application.Platform.runLater(() ->
                ctrl.tableView.getSelectionModel().select(0));
        sleep(100);
        clickOn("Видалити");
        sleep(200);
    }

    @Test
    void testAddCustomVegetableDirectCallCancel() {
        SaladViewController ctrl = getController();
        javafx.application.Platform.runLater(() ->
                new ui.dialog.AddCustomVegetableDialog(service, testSalad, testSaladId).show());
        sleep(500);
        lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().equals("Cancel") || b.getText().equals("Скасувати"))
                .findFirst().ifPresent(this::clickOn);
        sleep(200);
    }

    @Test
    void testAddCustomVegetableDirectCallWithValidData() {
        javafx.application.Platform.runLater(() ->
                new ui.dialog.AddCustomVegetableDialog(service, testSalad, testSaladId).show());
        sleep(500);
        var fields = lookup(".text-field").queryAllAs(javafx.scene.control.TextField.class);
        var fieldList = new java.util.ArrayList<>(fields);
        if (fieldList.size() >= 3) {
            doubleClickOn((javafx.scene.Node) fieldList.get(0)).write("Баклажан");
            doubleClickOn((javafx.scene.Node) fieldList.get(1)).write("100");
            doubleClickOn((javafx.scene.Node) fieldList.get(2)).write("25");
        }
        clickOn("Додати");
        sleep(300);
    }

    @Test
    void testAddCustomVegetableWithEmptyNameShowsNull() {
        javafx.application.Platform.runLater(() ->
                new ui.dialog.AddCustomVegetableDialog(service, testSalad, testSaladId).show());
        sleep(500);
        // Залишаємо поля порожніми і натискаємо Додати
        clickOn("Додати");
        sleep(300);
        // veg == null — нічого не додається
        assertEquals(2, testSalad.getVegetables().size()); // Carrot + Tomato
    }

    @Test
    void testAddStandardVegetableZeroWeight() {
        clickOn("+ Додати");
        sleep(200);
        if (lookup(".dialog-pane").query() != null) {
            clickOn("OK");
            sleep(200);
        }
        var weightPane = lookup(".dialog-pane").queryAs(javafx.scene.control.DialogPane.class);
        if (weightPane != null) {
            var tf = lookup(".text-field").query();
            doubleClickOn((javafx.scene.Node) tf).write("0");
            clickOn("OK");
            sleep(200);
            // Alert "Вага має бути більше нуля!"
            var alert = lookup(".dialog-pane").queryAs(javafx.scene.control.DialogPane.class);
            if (alert != null) clickOn("OK");
        }
    }
}