package commands;

import service.SaladService;
import vegetables.Salad;
import vegetables.Vegetable;

import java.util.List;
import java.util.Scanner;

public class FindVegetablesByCaloriesCommand implements Command {

    private final Salad        salad;
    private final SaladService service;
    private final Scanner      sc;

    public FindVegetablesByCaloriesCommand(Salad salad, SaladService service) {
        this(salad, new Scanner(System.in), service);
    }

    public FindVegetablesByCaloriesCommand(Salad salad, Scanner sc, SaladService service) {
        this.salad   = salad;
        this.sc      = sc;
        this.service = service;
    }

    @Override
    public void execute() {
        System.out.print("Мінімум калорій: ");
        double min = Double.parseDouble(sc.nextLine());
        System.out.print("Максимум калорій: ");
        double max = Double.parseDouble(sc.nextLine());

        List<Vegetable> result = service.findByCalories(salad, min, max);
        System.out.println("\nОвочі в заданому діапазоні:");
        if (result.isEmpty()) {
            System.out.println("Нічого не знайдено.");
        } else {
            result.forEach(System.out::println);
        }
    }

    @Override
    public String getDesc() { return "Знайти овочі за діапазоном калорій"; }
}