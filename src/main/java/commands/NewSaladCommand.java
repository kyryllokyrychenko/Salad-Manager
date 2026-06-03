package commands;

import menu.Menu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.SaladService;
import vegetables.Salad;

import java.util.Scanner;

public class NewSaladCommand implements Command {

    private static final Logger logger = LogManager.getLogger(NewSaladCommand.class);

    private final SaladService service;
    private final Menu         menu;
    private final Scanner      sc = new Scanner(System.in);

    public NewSaladCommand(Salad salad, SaladService service, Menu menu) {
        this.service = service;
        this.menu    = menu;
    }

    @Override
    public void execute() {
        System.out.print("Назва нового салату: ");
        String name = sc.nextLine().trim();

        int newId = service.createSalad(name);
        if (newId == -1) {
            System.out.println("Помилка: не вдалося створити салат у БД.");
            logger.error("createSalad() повернув -1 для назви '{}'", name);
            return;
        }

        String finalName = (name == null || name.isBlank()) ? "Мій салат" : name;
        Salad newSalad = new Salad(finalName);
        menu.switchSalad(newSalad, newId);

        System.out.printf("Створено та відкрито новий салат '%s' (id=%d).%n", finalName, newId);
    }

    @Override
    public String getDesc() { return "Створити новий порожній салат"; }
}