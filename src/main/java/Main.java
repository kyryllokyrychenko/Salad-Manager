import menu.Menu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ErrorNotifier;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Старт програми");

        ErrorNotifier.sendErrorEmail(new RuntimeException("Тестова помилка"));
//          можна розпакувати для перевірки відправлення на пошту

        try {
            Menu menu = new Menu();
            menu.start();
        } catch (Exception e) {
            logger.error("Фатальна помилка у головному методі!", e);
            ErrorNotifier.sendErrorEmail(e);
        }

        logger.info("Завершення програми");
    }
}
