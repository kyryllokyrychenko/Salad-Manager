package menu;

import commands.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.SaladService;
import utils.ErrorNotifier;
import vegetables.Salad;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private static final Logger logger = LogManager.getLogger(Menu.class);
    protected Map<String, Command> commands = new LinkedHashMap<>();
    private final Scanner sc = new Scanner(System.in);

    protected Salad        salad;
    protected int          currentSaladId;
    protected SaladService service;

    public Menu(Salad salad, SaladService service, int currentSaladId) {
        this.salad          = salad;
        this.service        = service;
        this.currentSaladId = currentSaladId;
        init();
    }

    protected void init() {
        commands.put("додати",      new AddVegetableCommand(salad, service, currentSaladId));
        commands.put("показати",    new ShowSaladCommand(salad));
        commands.put("підрахувати", new CalculateCaloriesCommand(salad, service));
        commands.put("сортувати",   new SortVegetablesCommand(salad, service));
        commands.put("знайти",      new FindVegetablesByCaloriesCommand(salad, service));
        commands.put("видалити",    new RemoveVegetableCommand(salad, service));
        commands.put("оновити",     new UpdateVegetableCommand(salad, service));
        commands.put("новий",       new NewSaladCommand(salad, service, this));
        commands.put("відкрити",    new OpenSaladCommand(salad, service, this));
        commands.put("сабменю",     new Submenu(salad, service, currentSaladId));
    }

    public void switchSalad(Salad newSalad, int newSaladId) {
        this.salad          = newSalad;
        this.currentSaladId = newSaladId;
        commands.clear();
        init();
        logger.info("Переключено на салат '{}' id={}", newSalad.getName(), newSaladId);
    }

    public void show() {
        System.out.println("\n===== " + salad.getName() + " =====");
        commands.forEach((key, cmd) -> System.out.println(key + " — " + cmd.getDesc()));
        System.out.println("вихід — Вихід з програми");
    }

    public void execute(String choice) {
        Command command = commands.get(choice);
        if (command != null) {
            logger.info("Виконується команда: {}", choice);
            try {
                command.execute();
            } catch (Exception e) {
                logger.error("Помилка при виконанні команди '{}'", choice, e);
                ErrorNotifier.sendErrorEmail(e);
            }
            System.out.print("\nНатисніть Enter для повернення до меню...");
            sc.nextLine();
        } else {
            logger.warn("Невірний вибір: {}", choice);
            System.out.println("Невірний вибір!");
        }
    }

    public void start() {
        while (true) {
            show();
            System.out.print("Виберіть пункт меню: ");
            String choice = sc.nextLine().trim().toLowerCase();
            if (choice.equals("вихід")) {
                logger.info("Користувач завершив роботу програми");
                System.out.println("\nВихід з програми...");
                break;
            }
            execute(choice);
        }
    }

    public Map<String, Command> getCommands() { return commands; }
}