package com.fruithat.nutrigenie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScanFragment extends Fragment {

    private FirebaseVisionImage image;
    private Bitmap bitmap;
    private FirebaseVisionTextRecognizer textRecognizer;
    private Button testScanButton;
    private HashMap<String, Float> nutritionItems;

    private static final String TAG = "NutriGenie";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout defined in quote_fragment.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
        setRetainInstance(true);

        nutritionItems = new HashMap<String, Float>();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nutrition_label_large);
        image = FirebaseVisionImage.fromBitmap(bitmap);

        textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        testScanButton = getActivity().findViewById(R.id.scan_button);
        testScanButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                processLabel();
            }
        });
    }

    private void startStatisticsActivity() {
        // Yan-Jen: change MainActivity to wherever you need the scanned data
        // maybe a new Statistics Activity or fragment?
        Intent intent = new Intent(getActivity(), MainActivity.class);
        for (String key : nutritionItems.keySet()) {
            Log.i(TAG, key + " => " + nutritionItems.get(key));
            intent.putExtra(key, nutritionItems.get(key));
        }
        startActivity(intent);
    }

    private void processLabel() {
        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                            for (FirebaseVisionText.Line line: block.getLines()) {
                                String lineText = line.getText();
                                if (lineText.contains("Protein")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    Float grams = parseGrams(parsedNutritionItem[1]);
                                    nutritionItems.put(parsedNutritionItem[0], grams);
                                    parseGrams(parsedNutritionItem[1]);
                                } else if (lineText.contains("Total Fat")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    String key = parsedNutritionItem[0] + " " + parsedNutritionItem[1];
                                    Float grams = parseGrams(parsedNutritionItem[2]);
                                    nutritionItems.put(key, grams);
                                    parseGrams(parsedNutritionItem[2]);
                                } else if (lineText.contains("Sodium")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    Float grams = parseGrams(parsedNutritionItem[1]);
                                    nutritionItems.put(parsedNutritionItem[0], grams);
                                    parseGrams(parsedNutritionItem[1]);
                                } else if (lineText.contains("Cholesterol")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    Float grams = parseGrams(parsedNutritionItem[1]);
                                    nutritionItems.put(parsedNutritionItem[0], grams);
                                    parseGrams(parsedNutritionItem[1]);
                                } else if (lineText.contains("Saturated Fat")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    String key = parsedNutritionItem[0] + " " + parsedNutritionItem[1];
                                    Float grams = parseGrams(parsedNutritionItem[2]);
                                    nutritionItems.put(key, grams);
                                    parseGrams(parsedNutritionItem[2]);
                                 } else if (lineText.contains("Trans Fat")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    String key = parsedNutritionItem[0] + " " + parsedNutritionItem[1];
                                    Float grams = parseGrams(parsedNutritionItem[2]);
                                    nutritionItems.put(key, grams);
                                    parseGrams(parsedNutritionItem[2]);
                                } else if (lineText.contains("Dietary Fiber")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    String key = parsedNutritionItem[0] + " " + parsedNutritionItem[1];
                                    Float grams = parseGrams(parsedNutritionItem[2]);
                                    nutritionItems.put(key, grams);
                                    parseGrams(parsedNutritionItem[2]);
                                } else if (lineText.contains("Total Sugars")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    String key = parsedNutritionItem[0] + " " + parsedNutritionItem[1];
                                    Float grams = parseGrams(parsedNutritionItem[2]);
                                    nutritionItems.put(key, grams);
                                    parseGrams(parsedNutritionItem[2]);
                                } else if (lineText.contains("Vitamin D")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    String key = parsedNutritionItem[0] + " " + parsedNutritionItem[1];
                                    Float grams = parseGrams(parsedNutritionItem[2]);
                                    nutritionItems.put(key, grams);
                                    parseGrams(parsedNutritionItem[2]);
                                } else if (lineText.contains("Vitamin C")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    String key = parsedNutritionItem[0] + " " + parsedNutritionItem[1];
                                    Float grams = parseGrams(parsedNutritionItem[2]);
                                    nutritionItems.put(key, grams);
                                    parseGrams(parsedNutritionItem[2]);
                                } else if (lineText.contains("Calcium")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    Float grams = parseGrams(parsedNutritionItem[1]);
                                    nutritionItems.put(parsedNutritionItem[0], grams);
                                    parseGrams(parsedNutritionItem[1]);
                                } else if (lineText.contains("Iron")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    Float grams = parseGrams(parsedNutritionItem[1]);
                                    nutritionItems.put(parsedNutritionItem[0], grams);
                                    parseGrams(parsedNutritionItem[1]);
                                } else if (lineText.contains("Potassium")) {
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    Float grams = parseGrams(parsedNutritionItem[1]);
                                    nutritionItems.put(parsedNutritionItem[0], grams);
                                    parseGrams(parsedNutritionItem[1]);
                                }
                            }
                        }

                        startStatisticsActivity();
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "FAILURE!");
                                Log.i(TAG, e.getMessage());
                            }
                        });
    }

    private float parseGrams(String gramsString) {
        int idxM = gramsString.indexOf("m");
        int idxG = gramsString.indexOf("g");
        float result = -1;
        if (idxM >= 0) {
            String num = gramsString.substring(0, idxM);
            // Sometimes Firebase interprests zeroes as O's so we have to check for this
            // still need to check for cases where there could be more than one misinterpreted zero
            if (num.equals("O")) {
                num = "0";
            }
            result = Float.parseFloat(num);
        } else if (idxG >= 0) {
            String num = gramsString.substring(0, idxG);
            if (num.equals("O")) {
                num = "0";
            }
            result = Float.parseFloat(num);
        }

        return result;
    }
}
