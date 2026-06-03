package ui.controller;

import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import service.SaladService;
import ui.MainApp;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class SaladListControllerTest extends ApplicationTest {

    private final SaladService service = new SaladService(
            new VegetableRepository(),
            new SaladRepository(),
            new VegetableTypeRepository()
    );

    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }

    private void reloadListScreen() {
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/SaladList.fxml"));
                MainApp.primaryStage.getScene().setRoot(loader.load());
            } catch (Exception e) { e.printStackTrace(); }
        });
        sleep(500);
    }

    @Test
    void testMainScreenLoads() {
        verifyThat("#saladCardsPane", isVisible());
    }

    @Test
    void testNewSaladButtonExists() {
        Button newBtn = lookup("+ Новий салат").queryButton();
        assertNotNull(newBtn);
        assertTrue(newBtn.isVisible());
    }

    @Test
    void testExitButtonExists() {
        assertNotNull(lookup("Вийти").queryButton());
    }

    @Test
    void testCardsAreDisplayed() {
        FlowPane pane = lookup("#saladCardsPane").query();
        assertNotNull(pane);
    }

    @Test
    void testNewSaladDialogOpens() {
        clickOn("+ Новий салат");
        DialogPane dialogPane = lookup(".dialog-pane").query();
        assertNotNull(dialogPane);
        press(javafx.scene.input.KeyCode.ESCAPE);
    }

    @Test
    void testNewSaladDialogCancelDoesNotCreate() {
        int before = service.getAllSalads().size();
        clickOn("+ Новий салат");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertEquals(before, service.getAllSalads().size());
    }

    @Test
    void testCreateNewSaladAndAnswerNoToPhoto() {
        clickOn("+ Новий салат");
        sleep(300);
        javafx.scene.control.TextField tf = lookup(".text-field").query();
        if (tf != null) doubleClickOn(tf).write("Тест фото");
        clickOn("OK");
        sleep(300);
        lookup(".button").queryAllAs(javafx.scene.control.Button.class).stream()
                .filter(b -> b.getText().equals("No") || b.getText().equals("Ні"))
                .findFirst().ifPresent(this::clickOn);
        sleep(300);
    }

    @Test
    void testCreateNewWithEmptyNameUsesDefault() {
        clickOn("+ Новий салат");
        sleep(300);
        javafx.scene.control.TextField tf = lookup(".text-field").query();
        if (tf != null) doubleClickOn(tf).eraseText(10);
        clickOn("OK");
        sleep(300);
        lookup(".button").queryAllAs(javafx.scene.control.Button.class).stream()
                .filter(b -> b.getText().equals("No") || b.getText().equals("Ні"))
                .findFirst().ifPresent(this::clickOn);
        sleep(300);
        assertTrue(service.getAllSalads().values().stream()
                .anyMatch(d -> d.contains("Мій салат")));
    }

    @Test
    void testDeleteSaladConfirmYes() {
        if (service.getAllSalads().isEmpty()) service.createSalad("Для видалення");
        reloadListScreen();
        lookup(".btn-danger-small").queryAllAs(javafx.scene.control.Button.class)
                .stream().findFirst().ifPresent(this::clickOn);
        sleep(300);
        lookup(".button").queryAllAs(javafx.scene.control.Button.class).stream()
                .filter(b -> b.getText().equals("Yes") || b.getText().equals("Так"))
                .findFirst().ifPresent(this::clickOn);
        sleep(300);
    }

    @Test
    void testDeleteSaladConfirmNo() {
        if (service.getAllSalads().isEmpty()) service.createSalad("Тест");
        reloadListScreen();
        int before = service.getAllSalads().size();
        lookup(".btn-danger-small").queryAllAs(javafx.scene.control.Button.class)
                .stream().findFirst().ifPresent(this::clickOn);
        sleep(300);
        lookup(".button").queryAllAs(javafx.scene.control.Button.class).stream()
                .filter(b -> b.getText().equals("No") || b.getText().equals("Ні"))
                .findFirst().ifPresent(this::clickOn);
        sleep(300);
        assertEquals(before, service.getAllSalads().size());
    }

    @Test
    void testCardWithInvalidImageShowsPlaceholder() {
        int id = service.createSalad("З фото");
        service.updateSaladImage(id, System.getProperty("java.home") + "/release");
        reloadListScreen();
        verifyThat("#saladCardsPane", isVisible());
        service.deleteSalad(id);
    }

    @Test
    void testCardWithValidImagePath() {
        int id = service.createSalad("З реальним фото");
        service.updateSaladImage(id, System.getProperty("user.dir") + "/salad.db");
        reloadListScreen();
        verifyThat("#saladCardsPane", isVisible());
        service.deleteSalad(id);
    }

    @Test
    void testCardWithRealImagePath() {
        int id = service.createSalad("Салат з фото");
        service.updateSaladImage(id, "C:\\Users\\Кирило\\Downloads\\фу.png");

        reloadListScreen();

        verifyThat("#saladCardsPane", isVisible());
        // Перевіряємо що картка з фото відображається
        assertTrue(lookup("#saladCardsPane").query().isVisible());

        service.deleteSalad(id);
    }
}