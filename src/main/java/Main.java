import menu.Menu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import service.SaladService;
import utils.ErrorNotifier;
import vegetables.Salad;

import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Старт програми");
        try {
            SaladService service = new SaladService(
                    new VegetableRepository(),
                    new SaladRepository(),
                    new VegetableTypeRepository()
            );

            int saladId = selectOrCreateSalad(service);

            String rawDesc   = service.getAllSalads().getOrDefault(saladId, "Мій салат");
            String saladName = rawDesc.contains(" (")
                    ? rawDesc.substring(0, rawDesc.indexOf(" ("))
                    : rawDesc;

            Salad salad = service.loadSalad(saladId, saladName);

            Menu menu = new Menu(salad, service, saladId);
            menu.start();
        } catch (Exception e) {
            logger.error("Фатальна помилка!", e);
            ErrorNotifier.sendErrorEmail(e);
        }
        logger.info("Завершення програми");
    }

    private static int selectOrCreateSalad(SaladService service) {
        Scanner sc = new Scanner(System.in);
        Map<Integer, String> salads = service.getAllSalads();

        if (salads.isEmpty()) {
            System.out.print("Салатів немає. Введіть назву нового салату: ");
            return service.createSalad(sc.nextLine().trim());
        }

        System.out.println("\n===== ОБЕРІТЬ САЛАТ =====");
        salads.forEach((id, desc) -> System.out.println(id + " — " + desc));
        System.out.println("0 — Створити новий салат");
        System.out.print("Ваш вибір: ");

        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice == 0 || !salads.containsKey(choice)) {
                System.out.print("Назва нового салату: ");
                return service.createSalad(sc.nextLine().trim());
            }
            return choice;
        } catch (NumberFormatException e) {
            return service.createSalad("Мій салат");
        }
    }
}