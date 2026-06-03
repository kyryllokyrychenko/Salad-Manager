package commands;

import service.SaladService;
import vegetables.Salad;

public class CalculateCaloriesCommand implements Command {

    private final Salad        salad;
    private final SaladService service;

    public CalculateCaloriesCommand(Salad salad, SaladService service) {
        this.salad   = salad;
        this.service = service;
    }

    @Override
    public void execute() {
        double total = service.getTotalCalories(salad);
        System.out.printf("Загальна калорійність салату: %.2f ккал%n", total);
    }

    @Override
    public String getDesc() { return "Підрахувати калорійність салату"; }
}