package commands;

import service.SaladService;
import vegetables.Salad;

import java.util.Scanner;

public class UpdateVegetableCommand implements Command {

    private final Salad        salad;
    private final SaladService service;
    private final Scanner      sc;

    public UpdateVegetableCommand(Salad salad, SaladService service) {
        this(salad, new Scanner(System.in), service);
    }

    public UpdateVegetableCommand(Salad salad, Scanner sc, SaladService service) {
        this.salad   = salad;
        this.sc      = sc;
        this.service = service;
    }

    @Override
    public void execute() {
        System.out.print("Введіть номер овоча для оновлення: ");
        int index = Integer.parseInt(sc.nextLine());
        if (index < 1 || index > salad.getVegetables().size()) {
            System.out.println("Некоректний номер!");
            return;
        }
        System.out.print("Нова вага (г): ");
        double newWeight = Double.parseDouble(sc.nextLine());
        service.updateWeight(salad, index - 1, newWeight);
        System.out.println("Овоч оновлено.");
    }

    @Override
    public String getDesc() { return "Оновити вагу овоча за номером у салаті"; }
}