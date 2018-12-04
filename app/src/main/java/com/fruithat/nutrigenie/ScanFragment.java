package com.fruithat.nutrigenie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class ScanFragment extends Fragment {

    // Firebase ML Kit image and model
    private FirebaseVisionImage image;
    private FirebaseVisionTextRecognizer textRecognizer; // downloaded from google when the app is installed

    // Data structures for storing parsed information
    private NutritionInformation.NutritionInformationBuilder nutriInfoBuilder;
    private HashMap<String, Float> nutritionItems; // only used for debugging

    // Path where photo can be retrieved after takePictureIntent returns
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    // Needed so that startStatisticsActivity can reliably have access to this activity
    private FragmentActivity activity;

    private Button testScanButton;
    private Bitmap bitmap;

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
        nutriInfoBuilder = new NutritionInformation.NutritionInformationBuilder();

        textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         dispatchTakePictureIntent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    private void startStatisticsActivity() {
        if (activity != null) {
            Intent intent = new Intent(activity, StatisticsActivity.class);
            NutritionInformation nutriInfo = nutriInfoBuilder.build();

            // Debugging
            for (String key : nutritionItems.keySet()) {
                Log.i(TAG, key + " => " + nutritionItems.get(key));
            }

            intent.putExtra("info", nutriInfo);
            activity.startActivity(intent);
        } else {
            Log.e(TAG, "Current activity is null");
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.fruithat.nutrigenie.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().
                        getContentResolver(), contentUri);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            if (bitmap != null) {
                image = FirebaseVisionImage.fromBitmap(bitmap);
                processLabel();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Protein", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.protein(grams);
                                } else if (lineText.contains("Total Fat")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Total Fat", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.totalFat(grams);
                                } else if (lineText.contains("Sodium")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Sodium", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.sodium(grams);
                                } else if (lineText.contains("Cholesterol")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Cholesterol", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.cholesterol(grams);
                                } else if (lineText.contains("Saturated Fat")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Saturated Fat", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.saturatedFat(grams);
                                 } else if (lineText.contains("Trans Fat")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Trans Fat", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.transFat(grams);
                                } else if (lineText.contains("Dietary Fiber")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Dietary Fiber", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.fiber(grams);
                                } else if (lineText.contains("Total Sugars")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Total Sugars", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.sugar(grams);
                                } else if (lineText.contains("Sugars")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Sugars", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.sugar(grams);
                                } else if (lineText.contains("Vitamin D")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin D", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminD(grams);
                                } else if (lineText.contains("Vitamin C")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin C", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminC(grams);
                                } else if (lineText.contains("Vitamin B6")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin B6", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminB6(grams);
                                } else if (lineText.contains("Vitamin B12")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin B12", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminB12(grams);
                                } else if (lineText.contains("Vitamin E")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin E", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminE(grams);
                                } else if (lineText.contains("Vitamin K")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin K", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminK(grams);
                                } else if (lineText.contains("Calcium")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Calcium", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.calcium(grams);
                                } else if (lineText.contains("Fiber")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Fiber", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.fiber(grams);
                                } else if (lineText.contains("Iron")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Iron", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.iron(grams);
                                } else if (lineText.contains("Potassium")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Potassium", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.potassium(grams);
                                } else if (lineText.contains("Thiamin")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Thiamin", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.thiamin(grams);
                                } else if (lineText.contains("Riboflavin")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Riboflavin", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.riboflavin(grams);
                                } else if (lineText.contains("Niacin")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Niacin", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.niacin(grams);
                                } else if (lineText.contains("Biotin")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Biotin", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.biotin(grams);
                                } else if (lineText.contains("Folate")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Folate", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.folate(grams);
                                } else if (lineText.contains("Chloride")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Chloride", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.chloride(grams);
                                } else if (lineText.contains("Chromium")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Chromium", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.chromium(grams);
                                } else if (lineText.contains("Copper")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Copper", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.copper(grams);
                                } else if (lineText.contains("Iodine")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Iodine", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.iodine(grams);
                                } else if (lineText.contains("Pantothenic Acid")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Pantothenic Acid", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.pantothenicAcid(grams);
                                } else if (lineText.contains("Total Carbohydrate")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Total Carbohydrate", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.carbohydrates(grams);
                                } else if (lineText.contains("Servings Per Container")) {
                                    Log.i(TAG, lineText);
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    Float servings = (float)-1;
                                    try {
                                        if (parsedNutritionItem.length >= 5) {
                                            servings = Float.parseFloat(parsedNutritionItem[4]);
                                        } else if (parsedNutritionItem.length >= 4) {
                                            servings = Float.parseFloat(parsedNutritionItem[3]);
                                        }
                                        nutritionItems.put("Servings Per Container", servings);
                                        if (servings >= 0 ) nutriInfoBuilder.servingsPerContainer(servings);
                                    } catch (NumberFormatException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                } else if (lineText.contains("Carbohydrate")) {
                                    Log.i(TAG, lineText);
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Carbohydrate", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.carbohydrates(grams);
                                } else if (lineText.contains("Calories")) {
                                    Log.i(TAG, lineText);
                                    String[] parsedNutritionItem = lineText.split(" ");
                                    if (parsedNutritionItem.length >= 2) {
                                        try {
                                            Float amount = Float.parseFloat(parsedNutritionItem[1]);
                                            nutritionItems.put("Calories", amount);
                                            if (amount >= 0 ) nutriInfoBuilder.calories(amount);
                                        } catch (NumberFormatException e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }
                                } else if (lineText.contains("Serving size")) {
                                    Log.i(TAG, lineText);
                                    Pattern pattern = Pattern.compile("Serving size (\\d) (\\w+).*");
                                    Matcher matcher = pattern.matcher(lineText);
                                    while (matcher.find()) {
                                        String servingSize = matcher.group(1);
                                        String servingType = matcher.group(2);
                                        try {
                                            int servingSizeInt = Integer.parseInt(servingSize);
                                            nutriInfoBuilder.servingSize(servingSizeInt);
                                        } catch (NumberFormatException e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                        nutriInfoBuilder.servingType(servingType);
                                    }
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
                                Log.e(TAG, e.getMessage());
                            }
                        });
    }

    private float parseOneWord(String lineText) {
        String[] parsedNutritionItem = lineText.split(" ");
        if (parsedNutritionItem.length >= 2) {
            return parseGrams(parsedNutritionItem[1]);
        } else {
            return -1;
        }
    }

    private float parseTwoWords(String lineText) {
        String[] parsedNutritionItem = lineText.split(" ");
        if (parsedNutritionItem.length >= 3) {
            return parseGrams(parsedNutritionItem[2]);
        } else {
            return -1;
        }
    }

    private float parseGrams(String gramsString) {
        int idxM = gramsString.indexOf("m");
        int idxG = gramsString.indexOf("g");
        int idxP = gramsString.indexOf("%");
        float result = -1;
        gramsString = gramsString.replaceAll("(o|O)+", "0");
        if (idxM >= 0) {
            String num = gramsString.substring(0, idxM);
            try {
                result = Float.parseFloat(num);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
                result = -1;
            }
        } else if (idxG >= 0) {
            String num = gramsString.substring(0, idxG);
            try {
                result = Float.parseFloat(num);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
                result = -1;
            }
        } else if (idxP >= 0) {
            String num = gramsString.substring(0, idxP);
            try {
                result = Float.parseFloat(num);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
                result = -1;
            }
        }
        return result;
    }
}
