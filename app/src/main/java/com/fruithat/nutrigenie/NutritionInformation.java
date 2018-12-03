package com.fruithat.nutrigenie;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class NutritionInformation implements Parcelable {

    public static final Creator<NutritionInformation> CREATOR = new Creator<NutritionInformation>() {
        @Override
        public NutritionInformation createFromParcel(Parcel source) {
            return new NutritionInformation(source);
        }

        @Override
        public NutritionInformation[] newArray(int size) {
            return new NutritionInformation[size];
        }
    };
    private HashMap<String, Object> nutrients;

    private NutritionInformation(String servingType, double servingSize, double servingsPerContainer, HashMap<String, Double> nutrients) {
        this.nutrients = new HashMap<>(nutrients.size() + 3);

        this.nutrients.put("serving_type", servingType);
        this.nutrients.put("serving_size", servingSize);
        this.nutrients.put("servings_per_container", servingsPerContainer);
        this.nutrients.putAll(nutrients);
    }

    protected NutritionInformation(Parcel in) {
        this.nutrients = (HashMap<String, Object>) in.readSerializable();
    }

    public static NutritionInformation parseSnapshot(DataSnapshot nutrientSnapshot) {
        String servingType = "";
        double servingSize = 1;
        double servingsPerContainer = 1;
        HashMap<String, Double> nutrients = new HashMap<>(Math.toIntExact(nutrientSnapshot.getChildrenCount()));

        for (DataSnapshot nutrient : nutrientSnapshot.getChildren()) {
            System.out.println(nutrient.getKey());
            switch (nutrient.getKey()) {
                case "serving_type":
                    servingType = (String) nutrient.getValue();
                    break;
                case "serving_size":
                    servingSize =  Double.parseDouble(nutrient.getValue().toString());
                    break;
                case "servings_per_container":
                    servingsPerContainer =  Double.parseDouble(nutrient.getValue().toString());
                    break;
                default:
                    nutrients.put(nutrient.getKey(), Double.parseDouble(nutrient.getValue().toString()));
            }
        }

        return new NutritionInformation(servingType, servingSize, servingsPerContainer, nutrients);
    }

    public String getServingType() {
        return (String) nutrients.getOrDefault("serving_type", "");
    }

    public double getServingSize() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("serving_size", 0)));
    }

    public double getServingsPerContainer() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("servings_per_container", 0)));
    }

    public double getCalories() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("calories", 0)));
    }

    public double getCaloriesFromFat() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("calories_from_fat", 0)));
    }

    public double getTotalFat() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("total_fat", 0)));
    }

    public double getSaturatedFat() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("saturated_fat", 0)));
    }

    public double getTransFat() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("trans_fat", 0)));
    }

    public double getCholesterol() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("cholesterol", 0)));
    }

    public double getSodium() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("sodium", 0)));
    }

    public double getCarbohydrates() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("carbohydrates", 0)));
    }

    public double getFiber() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("fiber", 0)));
    }

    public double getSugar() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("sugar", 0)));
    }

    public double getProtein() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("protein", 0)));
    }

    public double getVitaminA() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("vitamin_a", 0)));
    }

    public double getVitaminB6() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("vitamin_b6", 0)));
    }

    public double getVitaminB12() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("vitamin_b12", 0)));
    }

    public double getVitaminC() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("vitamin_c", 0)));
    }

    public double getVitaminD() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("vitamin_d", 0)));
    }

    public double getVitaminE() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("vitamin_e", 0)));
    }

    public double getVitaminK() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("vitamin_k", 0)));
    }

    public double getThiamin() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("thiamin", 0)));
    }

    public double getRiboflavin() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("riboflavin", 0)));
    }

    public double getNiacin() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("niacin", 0)));
    }

    public double getPantothenicAcid() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("pantothenic_acid", 0)));
    }

    public double getBiotin() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("biotin", 0)));
    }

    public double getFolate() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("folate", 0)));
    }

    public double getIron() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("iron", 0)));
    }

    public double getPotassium() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("potassium", 0)));
    }

    public double getCalcium() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("calcium", 0)));
    }

    public double getChloride() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("chloride", 0)));
    }

    public double getCopper() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("copper", 0)));
    }

    public double getChromium() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("chromium", 0)));
    }

    public double getIodine() {
        return Double.parseDouble(String.valueOf(nutrients.getOrDefault("iodine", 0)));
    }

    public HashMap<String, Object> getDatabaseHashMap() {
        return (HashMap<String, Object>) nutrients.clone();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.nutrients);
    }

    public static class NutritionInformationBuilder {
        private String nServingType = "";
        private double nServingSize = 0;
        private double nServingsPerContainer = 1;
        private HashMap<String, Double> nNutrients = new HashMap<>();

        public NutritionInformationBuilder() {
        }

        public NutritionInformationBuilder servingType(final String servingType) {
            this.nServingType = servingType;
            return this;
        }

        public NutritionInformationBuilder servingSize(final double servingSize) {
            this.nServingSize = servingSize;
            return this;
        }

        public NutritionInformationBuilder servingsPerContainer(final double servingsPerContainer) {
            this.nServingsPerContainer = servingsPerContainer;
            return this;
        }

        public NutritionInformationBuilder calories(final double calories) {
            this.nNutrients.put("calories", calories);
            return this;
        }

        public NutritionInformationBuilder caloriesFromFat(final double caloriesFromFat) {
            this.nNutrients.put("calories_from_fat", caloriesFromFat);
            return this;
        }

        public NutritionInformationBuilder totalFat(final double totalFat) {
            this.nNutrients.put("total_fat", totalFat);
            return this;
        }

        public NutritionInformationBuilder saturatedFat(final double saturatedFat) {
            this.nNutrients.put("saturated_fat", saturatedFat);
            return this;
        }

        public NutritionInformationBuilder transFat(final double transFat) {
            this.nNutrients.put("trans_fat", transFat);
            return this;
        }

        public NutritionInformationBuilder cholesterol(final double cholesterol) {
            this.nNutrients.put("cholesterol", cholesterol);
            return this;
        }

        public NutritionInformationBuilder sodium(final double sodium) {
            this.nNutrients.put("sodium", sodium);
            return this;
        }

        public NutritionInformationBuilder carbohydrates(final double carbohydrates) {
            this.nNutrients.put("carbohydrates", carbohydrates);
            return this;
        }

        public NutritionInformationBuilder fiber(final double fiber) {
            this.nNutrients.put("fiber", fiber);
            return this;
        }

        public NutritionInformationBuilder sugar(final double sugar) {
            this.nNutrients.put("sugar", sugar);
            return this;
        }

        public NutritionInformationBuilder protein(final double protein) {
            this.nNutrients.put("protein", protein);
            return this;
        }

        public NutritionInformationBuilder vitaminA(final double vitaminA) {
            this.nNutrients.put("vitamin_a", vitaminA);
            return this;
        }

        public NutritionInformationBuilder vitaminB6(final double vitaminB6) {
            this.nNutrients.put("vitamin_b6", vitaminB6);
            return this;
        }

        public NutritionInformationBuilder vitaminB12(final double vitaminB12) {
            this.nNutrients.put("vitamin_b12", vitaminB12);
            return this;
        }

        public NutritionInformationBuilder vitaminC(final double vitaminC) {
            this.nNutrients.put("vitamin_c", vitaminC);
            return this;
        }

        public NutritionInformationBuilder vitaminD(final double vitaminD) {
            this.nNutrients.put("vitamin_d", vitaminD);
            return this;
        }

        public NutritionInformationBuilder vitaminE(final double vitaminE) {
            this.nNutrients.put("vitamin_e", vitaminE);
            return this;
        }

        public NutritionInformationBuilder vitaminK(final double vitaminK) {
            this.nNutrients.put("vitamin_k", vitaminK);
            return this;
        }

        public NutritionInformationBuilder thiamin(final double thiamin) {
            this.nNutrients.put("thiamin", thiamin);
            return this;
        }

        public NutritionInformationBuilder riboflavin(final double riboflavin) {
            this.nNutrients.put("riboflavin", riboflavin);
            return this;
        }

        public NutritionInformationBuilder niacin(final double niacin) {
            this.nNutrients.put("niacin", niacin);
            return this;
        }

        public NutritionInformationBuilder pantothenicAcid(final double pantothenicAcid) {
            this.nNutrients.put("pantothenic_acid", pantothenicAcid);
            return this;
        }

        public NutritionInformationBuilder biotin(final double biotin) {
            this.nNutrients.put("biotin", biotin);
            return this;
        }

        public NutritionInformationBuilder folate(final double folate) {
            this.nNutrients.put("folate", folate);
            return this;
        }

        public NutritionInformationBuilder calcium(final double calcium) {
            this.nNutrients.put("calcium", calcium);
            return this;
        }

        public NutritionInformationBuilder chloride(final double chloride) {
            this.nNutrients.put("chloride", chloride);
            return this;
        }

        public NutritionInformationBuilder chromium(final double chromium) {
            this.nNutrients.put("chromium", chromium);
            return this;
        }

        public NutritionInformationBuilder copper(final double copper) {
            this.nNutrients.put("copper", copper);
            return this;
        }

        public NutritionInformationBuilder iodine(final double iodine) {
            this.nNutrients.put("iodine", iodine);
            return this;
        }

        public NutritionInformationBuilder iron(final double iron) {
            this.nNutrients.put("iron", iron);
            return this;
        }

        public NutritionInformationBuilder potassium(final double potassium) {
            this.nNutrients.put("potassium", potassium);
            return this;
        }

        public NutritionInformation build() {
            return new NutritionInformation(nServingType, nServingSize, nServingsPerContainer, nNutrients);
        }
    }
}
