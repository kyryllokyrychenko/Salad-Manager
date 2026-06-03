package commands;

import menu.Menu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.SaladService;
import vegetables.Salad;

import java.util.Map;
import java.util.Scanner;

public class OpenSaladCommand implements Command {

    private static final Logger logger = LogManager.getLogger(OpenSaladCommand.class);

    private final SaladService service;
    private final Menu         menu;
    private final Scanner      sc = new Scanner(System.in);

    public OpenSaladCommand(Salad salad, SaladService service, Menu menu) {
        this.service = service;
        this.menu    = menu;
    }

    @Override
    public void execute() {
        Map<Integer, String> salads = service.getAllSalads();

        if (salads.isEmpty()) {
            System.out.println("У базі немає жодного салату.");
            return;
        }

        System.out.println("\n===== СПИСОК САЛАТІВ =====");
        salads.forEach((id, desc) -> System.out.println(id + " — " + desc));
        System.out.println("0 — Скасувати");
        System.out.print("Оберіть id салату: ");

        int chosenId;
        try {
            chosenId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Некоректний ввід — скасовано.");
            return;
        }

        if (chosenId == 0) { System.out.println("Скасовано."); return; }

        if (!salads.containsKey(chosenId)) {
            System.out.println("Салату з таким id не існує.");
            return;
        }

        String rawDesc   = salads.get(chosenId);
        String saladName = rawDesc.contains(" (")
                ? rawDesc.substring(0, rawDesc.indexOf(" ("))
                : rawDesc;

        Salad loaded = service.loadSalad(chosenId, saladName);
        menu.switchSalad(loaded, chosenId);

        System.out.printf("Відкрито '%s' (%d овочів).%n", saladName, loaded.getVegetables().size());
    }

    @Override
    public String getDesc() { return "Відкрити існуючий салат із бази даних"; }
}