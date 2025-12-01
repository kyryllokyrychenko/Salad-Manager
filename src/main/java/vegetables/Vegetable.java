package vegetables;

public abstract class Vegetable {

    private final String name;
    private double weight;       // г
    private final double caloriesPer100g;

    protected Vegetable(String name, double weight, double caloriesPer100g) {
        validateWeight(weight);

        this.name = name;
        this.weight = weight;
        this.caloriesPer100g = caloriesPer100g;
    }

    public double getTotalCalories() {
        return (weight / 100.0) * caloriesPer100g;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        validateWeight(weight);
        this.weight = weight;
    }

    private void validateWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Вага не може бути від’ємною.");
        }
    }

    @Override
    public String toString() {
        return name + " (" + weight + " г, " + caloriesPer100g + " ккал/100г)";
    }
}
