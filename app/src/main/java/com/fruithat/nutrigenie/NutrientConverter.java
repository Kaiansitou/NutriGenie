package com.fruithat.nutrigenie;

public class NutrientConverter {

    private double caloriesNeeded;

    public NutrientConverter(double caloriesNeeded) {
        this.caloriesNeeded = caloriesNeeded;
    }

    public float convert(String nutrient, float amount) {
        switch (nutrient) {
            case "Sugar":
                return 100f + amount;
            case "Protein":
                return (float) (amount / (caloriesNeeded / 2000 * 50)) * 100;
            case "Total Fat":
                return (float) (amount / (caloriesNeeded / 2000 * 65)) * 100;
            case "Trans Fat":
                return 100f + amount;
            case "Saturated Fat":
                return (float) (amount / (caloriesNeeded / 2000 * 20)) * 100;
            case "Carbohydrates":
                return (float) (amount / (caloriesNeeded / 2000 * 300)) * 100;
            case "Sodium":
                return (float) (amount / (caloriesNeeded / 2000 * 2400)) * 100;
            case "Fiber":
                return (float) (amount / (caloriesNeeded / 2000 * 25)) * 100;
            case "Cholesterol":
                return (float) (amount / (caloriesNeeded / 2000 * 300)) * 100;
            case "Potassium":
                return (float) (amount / (caloriesNeeded / 2000 * 3500)) * 100;
            default: return -1;
        }
    }
}