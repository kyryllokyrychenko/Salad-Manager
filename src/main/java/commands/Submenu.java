package commands;

import menu.Menu;
import service.SaladService;
import vegetables.Salad;

public class Submenu extends Menu implements Command {

    public Submenu(Salad salad, SaladService service, int currentSaladId) {
        super(salad, service, currentSaladId);
    }

    @Override
    public void execute() { start(); }

    @Override
    public String getDesc() { return "Відкрити сабменю"; }

    @Override
    protected void init() {
        commands.put("додати",      new AddVegetableCommand(salad, service, currentSaladId));
        commands.put("показати",    new ShowSaladCommand(salad));
        commands.put("підрахувати", new CalculateCaloriesCommand(salad, service));
        commands.put("сортувати",   new SortVegetablesCommand(salad, service));
        commands.put("знайти",      new FindVegetablesByCaloriesCommand(salad, service));
        commands.put("видалити",    new RemoveVegetableCommand(salad, service));
        commands.put("оновити",     new UpdateVegetableCommand(salad, service));
    }
}