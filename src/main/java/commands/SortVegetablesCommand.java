package commands;

import service.SaladService;
import vegetables.Salad;

public class SortVegetablesCommand implements Command {

    private final Salad        salad;
    private final SaladService service;

    public SortVegetablesCommand(Salad salad, SaladService service) {
        this.salad   = salad;
        this.service = service;
    }

    @Override
    public void execute() {
        service.sortByCalories(salad);
        System.out.println("Овочі відсортовано за калорійністю.");
    }

    @Override
    public String getDesc() { return "Сортувати овочі за калорійністю"; }
}