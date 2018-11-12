package com.fruithat.nutrigenie;

import java.util.HashMap;

public interface NutritionHistoryCallback {
    void onDataReceived(HashMap<Long, NutritionInformation> nutritionInformation);
}
