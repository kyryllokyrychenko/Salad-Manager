package commands;

import service.SaladService;
import utils.ErrorNotifier;
import vegetables.Salad;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoveVegetableCommand implements Command {

    private static final Logger logger = LogManager.getLogger(RemoveVegetableCommand.class);

    private final Salad        salad;
    private final SaladService service;
    private final Scanner      sc;

    public RemoveVegetableCommand(Salad salad, SaladService service) {
        this(salad, new Scanner(System.in), service);
    }

    public RemoveVegetableCommand(Salad salad, Scanner sc, SaladService service) {
        this.salad   = salad;
        this.sc      = sc;
        this.service = service;
    }

    @Override
    public void execute() {
        try {
            System.out.print("Введіть номер овоча для видалення: ");
            int index = Integer.parseInt(sc.nextLine());
            if (index < 1 || index > salad.getVegetables().size()) {
                logger.warn("Некоректний індекс: {}", index);
                System.out.println("Некоректний номер!");
                return;
            }
            service.removeVegetable(salad, index - 1);
            System.out.println("Овоч видалено.");
        } catch (Exception e) {
            logger.error("Помилка видалення овоча", e);
            ErrorNotifier.sendErrorEmail(e);
        }
    }

    @Override
    public String getDesc() { return "Видалити овоч за номером у салаті"; }
}