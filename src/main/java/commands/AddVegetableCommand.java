package commands;

import service.SaladService;
import vegetables.Salad;
import vegetables.Vegetable;

import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddVegetableCommand implements Command {

    private static final Logger logger = LogManager.getLogger(AddVegetableCommand.class);

    private final Salad        salad;
    private final SaladService service;
    private final int          currentSaladId;
    private final Scanner      sc;

    public AddVegetableCommand(Salad salad, SaladService service, int currentSaladId) {
        this(salad, new Scanner(System.in), service, currentSaladId);
    }

    public AddVegetableCommand(Salad salad, Scanner sc, SaladService service, int currentSaladId) {
        this.salad          = salad;
        this.sc             = sc;
        this.service        = service;
        this.currentSaladId = currentSaladId;
    }

    @Override
    public void execute() {
        Map<Integer, String> types = service.getAllTypes();

        System.out.println("\nОберіть овоч:");
        types.forEach((id, name) -> System.out.println(id + " — " + name));
        int customOption = types.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
        System.out.println(customOption + " — Власний овоч");
        System.out.print("Ваш вибір: ");

        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Помилка: введіть число!");
            return;
        }

        if (choice == customOption) {
            addCustomVegetable();
            return;
        }

        if (!types.containsKey(choice)) {
            System.out.println("Невірний вибір.");
            return;
        }

        String typeName = types.get(choice);
        double weight;
        try {
            System.out.print("Введіть вагу (в грамах): ");
            weight = Double.parseDouble(sc.nextLine().trim());
            if (weight <= 0) { System.out.println("Вага має бути більше нуля!"); return; }
        } catch (NumberFormatException e) {
            System.out.println("Помилка: введіть коректне число для ваги!");
            return;
        }

        Vegetable veg = service.createVegetable(typeName, weight);
        service.addVegetable(salad, veg, currentSaladId);
        System.out.printf("Додано: %s, %.1f г%n", typeName, weight);
    }

    private void addCustomVegetable() {
        System.out.print("Назва овоча: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Назва не може бути порожньою!"); return; }

        double weight, calories;
        try {
            System.out.print("Вага (г): ");
            weight = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Калорійність (ккал/100г): ");
            calories = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Помилка: введіть коректне число!");
            return;
        }

        if (weight <= 0 || calories < 0) {
            System.out.println("Вага має бути > 0, калорійність >= 0!");
            return;
        }

        Vegetable veg = service.createCustomVegetable(name, weight, calories);
        service.addVegetable(salad, veg, currentSaladId);
        System.out.printf("Додано власний овоч '%s', %.1fг, %.1f ккал/100г%n", name, weight, calories);
    }

    @Override
    public String getDesc() { return "Додати овоч до салату"; }
}