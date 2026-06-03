package service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.SaladRepository;
import repository.VegetableRepository;
import repository.VegetableTypeRepository;
import vegetables.*;

import java.util.List;
import java.util.Map;

public class SaladService {

    private static final Logger logger = LogManager.getLogger(SaladService.class);

    private final VegetableRepository     vegRepo;
    private final SaladRepository         saladRepo;
    private final VegetableTypeRepository typeRepo;

    public SaladService(VegetableRepository vegRepo,
                        SaladRepository saladRepo,
                        VegetableTypeRepository typeRepo) {
        this.vegRepo   = vegRepo;
        this.saladRepo = saladRepo;
        this.typeRepo  = typeRepo;
    }

    // ===== Салати =====

    public int createSalad(String name) {
        String trimmed = (name == null || name.isBlank()) ? "Мій салат" : name.trim();
        int id = saladRepo.create(trimmed);
        logger.info("Створено салат '{}' id={}", trimmed, id);
        return id;
    }

    public Salad loadSalad(int saladId, String name) {
        Salad salad = new Salad(name);
        vegRepo.findBySaladId(saladId).forEach(salad::add);
        logger.info("Завантажено салат '{}' id={}, овочів: {}", name, saladId, salad.getVegetables().size());
        return salad;
    }

    public Map<Integer, String> getAllSalads() {
        return saladRepo.findAll();
    }

    public void deleteSalad(int saladId) {
        saladRepo.delete(saladId);
        logger.info("Видалено салат id={}", saladId);
    }

    // ===== Овочі =====

    public void addVegetable(Salad salad, Vegetable veg, int saladId) {
        vegRepo.save(veg, saladId);
        salad.add(veg);
        logger.info("Додано овоч '{}' {}г до салату id={}", veg.getName(), veg.getWeight(), saladId);
    }

    public void removeVegetable(Salad salad, int index) {
        Vegetable veg = salad.getVegetables().get(index);
        vegRepo.delete(veg.getId());
        salad.remove(index);
        logger.info("Видалено овоч '{}' id={}", veg.getName(), veg.getId());
    }

    public void updateWeight(Salad salad, int index, double newWeight) {
        Vegetable veg = salad.getVegetables().get(index);
        veg.setWeight(newWeight);
        vegRepo.update(veg.getId(), newWeight);
        logger.info("Оновлено вагу овоча '{}' -> {}г", veg.getName(), newWeight);
    }

    // ===== Логіка салату =====

    public void sortByCalories(Salad salad) {
        salad.sortByCalories();
        logger.info("Відсортовано овочі салату '{}'", salad.getName());
    }

    public void sortByCaloriesDescending(Salad salad) {
        salad.getVegetables(); // доступ через існуючий метод
        salad.sortByCaloriesDescending();
        logger.info("Відсортовано овочі салату '{}' за спаданням", salad.getName());
    }

    public List<Vegetable> findByCalories(Salad salad, double min, double max) {
        List<Vegetable> result = salad.findByCalories(min, max);
        logger.info("Знайдено {} овочів у діапазоні {}-{} ккал", result.size(), min, max);
        return result;
    }

    public double getTotalCalories(Salad salad) {
        return salad.getTotalCalories();
    }

    // ===== Типи овочів =====

    public Map<Integer, String> getAllTypes() {
        return typeRepo.findAll();
    }

    public void saveCustomType(String name, double calories) {
        typeRepo.save(name, calories);
    }

    public double getCaloriesByType(String name) {
        return typeRepo.getCalories(name);
    }

    // ===== Фабрика овочів =====

    public Vegetable createVegetable(String typeName, double weight) {
        Vegetable veg = switch (typeName) {
            case "Морква"  -> new Carrot(weight);
            case "Помідор" -> new Tomato(weight);
            case "Огірок"  -> new Cucumber(weight);
            case "Лук"     -> new Onion(weight);
            case "Капуста" -> new Cabbage(weight);
            case "Перець"  -> new Pepper(weight);
            default        -> null;
        };
        if (veg == null) {
            // кастомний тип з БД
            double cal = typeRepo.getCalories(typeName);
            veg = new CustomVegetable(typeName, weight, cal);
        }
        return veg;
    }

    public Vegetable createCustomVegetable(String name, double weight, double calories) {
        saveCustomType(name, calories);
        return new CustomVegetable(name, weight, calories);
    }

    public void updateSaladImage(int saladId, String imagePath) {
        saladRepo.updateImagePath(saladId, imagePath);
    }

    public String getSaladImage(int saladId) {
        return saladRepo.getImagePath(saladId);
    }
}