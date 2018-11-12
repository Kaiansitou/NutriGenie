package com.fruithat.nutrigenie;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class NutritionInformation {

    private HashMap<String, Object> nutrients;

    private NutritionInformation(String servingType, double servingSize, int servingsPerContainer, HashMap<String, Integer> nutrients) {
        this.nutrients = new HashMap<>(nutrients.size() + 3);

        this.nutrients.put("serving_type", servingType);
        this.nutrients.put("serving_size", servingSize);
        this.nutrients.put("servings_per_container", servingsPerContainer);
        this.nutrients.putAll(nutrients);
    }

    public static NutritionInformation parseSnapshot(DataSnapshot nutrientSnapshot) {
        String servingType = "";
        double servingSize = 1;
        int servingsPerContainer = 1;
        HashMap<String, Integer> nutrients = new HashMap<>((int) nutrientSnapshot.getChildrenCount());

        for (DataSnapshot nutrient : nutrientSnapshot.getChildren()) {
            System.out.println(nutrient.getKey());
            switch (nutrient.getKey()) {
                case "serving_type":
                    servingType = (String) nutrient.getValue();
                    break;
                case "serving_size":
                    servingSize = Double.parseDouble(String.valueOf(nutrient.getValue()));
                    break;
                case "servings_per_container":
                    servingsPerContainer = Math.toIntExact((long) nutrient.getValue());
                    break;
                default:
                    nutrients.put(nutrient.getKey(), Math.toIntExact((long) nutrient.getValue()));
            }
        }

        return new NutritionInformation(servingType, servingSize, servingsPerContainer, nutrients);
    }

    public String getServingType() {
        return (String) nutrients.getOrDefault("serving_type", "");
    }

    public double getServingSize() {
        return (double) nutrients.getOrDefault("serving_size", 0);
    }

    public int getServingsPerContainer() {
        return (int) nutrients.getOrDefault("servings_per_container", 0);
    }

    public int getCalories() {
        return (int) nutrients.getOrDefault("calories", 0);
    }

    public int getCaloriesFromFat() {
        return (int) nutrients.getOrDefault("calories_from_fat", 0);
    }

    public int getTotalFat() {
        return (int) nutrients.getOrDefault("total_fat", 0);
    }

    public int getSaturatedFat() {
        return (int) nutrients.getOrDefault("saturated_fat", 0);
    }

    public int getTransFat() {
        return (int) nutrients.getOrDefault("trans_fat", 0);
    }

    public int getCholesterol() {
        return (int) nutrients.getOrDefault("cholesterol", 0);
    }

    public int getSodium() {
        return (int) nutrients.getOrDefault("sodium", 0);
    }

    public int getCarbohydrates() {
        return (int) nutrients.getOrDefault("carbohydrates", 0);
    }

    public int getFiber() {
        return (int) nutrients.getOrDefault("fiber", 0);
    }

    public int getSugar() {
        return (int) nutrients.getOrDefault("sugar", 0);
    }

    public int getProtein() {
        return (int) nutrients.getOrDefault("protein", 0);
    }

    public int getVitaminA() {
        return (int) nutrients.getOrDefault("vitamin_a", 0);
    }

    public int getVitaminB() {
        return (int) nutrients.getOrDefault("vitamin_b", 0);
    }

    public int getVitaminC() {
        return (int) nutrients.getOrDefault("vitamin_c", 0);
    }

    public int getIron() {
        return (int) nutrients.getOrDefault("iron", 0);
    }

    public int getCalcium() {
        return (int) nutrients.getOrDefault("calcium", 0);
    }

    public HashMap<String, Object> getDatabaseHashMap() {
        return (HashMap<String, Object>) nutrients.clone();
    }

    public static class NutritionInformationBuilder {
        private String nServingType;
        private double nServingSize;
        private int nServingsPerContainer = 1;
        private HashMap<String, Integer> nNutrients = new HashMap<>();

        public NutritionInformationBuilder(String servingType, double servingSize) {
            this.nServingType = servingType;
            this.nServingSize = servingSize;
        }

        public NutritionInformationBuilder servingsPerContainer(final int servingsPerContainer) {
            this.nServingsPerContainer = servingsPerContainer;
            return this;
        }

        public NutritionInformationBuilder calories(final int calories) {
            this.nNutrients.put("calories", calories);
            return this;
        }

        public NutritionInformationBuilder caloriesFromFat(final int caloriesFromFat) {
            this.nNutrients.put("calories_from_fat", caloriesFromFat);
            return this;
        }

        public NutritionInformationBuilder totalFat(final int totalFat) {
            this.nNutrients.put("total_fat", totalFat);
            return this;
        }

        public NutritionInformationBuilder saturatedFat(final int saturatedFat) {
            this.nNutrients.put("saturated_fat", saturatedFat);
            return this;
        }

        public NutritionInformationBuilder transFat(final int transFat) {
            this.nNutrients.put("trans_fat", transFat);
            return this;
        }

        public NutritionInformationBuilder cholesterol(final int cholesterol) {
            this.nNutrients.put("cholesterol", cholesterol);
            return this;
        }

        public NutritionInformationBuilder sodium(final int sodium) {
            this.nNutrients.put("sodium", sodium);
            return this;
        }

        public NutritionInformationBuilder carbohydrates(final int carbohydrates) {
            this.nNutrients.put("carbohydrates", carbohydrates);
            return this;
        }

        public NutritionInformationBuilder fiber(final int fiber) {
            this.nNutrients.put("fiber", fiber);
            return this;
        }

        public NutritionInformationBuilder sugar(final int sugar) {
            this.nNutrients.put("sugar", sugar);
            return this;
        }

        public NutritionInformationBuilder protein(final int protein) {
            this.nNutrients.put("protein", protein);
            return this;
        }

        public NutritionInformationBuilder vitaminA(final int vitaminA) {
            this.nNutrients.put("vitamin_a", vitaminA);
            return this;
        }

        public NutritionInformationBuilder vitaminB(final int vitaminB) {
            this.nNutrients.put("vitamin_b", vitaminB);
            return this;
        }

        public NutritionInformationBuilder vitaminC(final int vitaminC) {
            this.nNutrients.put("vitamin_c", vitaminC);
            return this;
        }

        public NutritionInformationBuilder calcium(final int calcium) {
            this.nNutrients.put("calcium", calcium);
            return this;
        }

        public NutritionInformationBuilder iron(final int iron) {
            this.nNutrients.put("iron", iron);
            return this;
        }

        public NutritionInformation build() {
            return new NutritionInformation(nServingType, nServingSize, nServingsPerContainer, nNutrients);
        }
    }
}
