package com.fruithat.nutrigenie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class ScanFragment extends Fragment {

    private FirebaseVisionImage image;
    private Bitmap bitmap;
    private FirebaseVisionTextRecognizer textRecognizer;
    private Button testScanButton;
    private Button scanButton;
    private NutritionInformation.NutritionInformationBuilder nutriInfoBuilder;
    private HashMap<String, Float> nutritionItems; // only used for debugging
    private ImageView imageView;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final String TAG = "NutriGenie";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        imageView = view.findViewById(R.id.retrieved_image);
        // Inflate the layout defined in quote_fragment.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        return view;
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
        testScanButton = getActivity().findViewById(R.id.test_scan_button);
        testScanButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nutrition_label_large);
                image = FirebaseVisionImage.fromBitmap(bitmap);
                processLabel();
            }
        });
        scanButton = getActivity().findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void startStatisticsActivity() {
        // Yan-Jen: change MainActivity to wherever you need the scanned data
        // maybe a new Statistics Activity or fragment?
        if (getActivity() != null) {
            Log.i(TAG, "GET ACTIVITY IS NOT NULL");
            Intent intent = new Intent(getActivity(), MainActivity.class);
            NutritionInformation nutriInfo = nutriInfoBuilder.build();
            for (String key : nutritionItems.keySet()) {
                Log.i(TAG, key + " => " + nutritionItems.get(key));
            }
            intent.putExtra("info", nutriInfo);
            startActivity(intent);
        } else {
            Log.i(TAG, "GET ACTIVITY IS NULL, OH NO");
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
//                imageView.setImageBitmap(bitmap);
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
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Protein", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.protein(grams);
                                } else if (lineText.contains("Total Fat")) {
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Total Fat", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.totalFat(grams);
                                } else if (lineText.contains("Sodium")) {
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Sodium", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.sodium(grams);
                                } else if (lineText.contains("Cholesterol")) {
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Cholesterol", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.cholesterol(grams);
                                } else if (lineText.contains("Saturated Fat")) {
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Saturated Fat", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.saturatedFat(grams);
                                 } else if (lineText.contains("Trans Fat")) {
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Trans Fat", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.transFat(grams);
                                } else if (lineText.contains("Dietary Fiber")) {
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Dietary Fiber", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.fiber(grams);
                                } else if (lineText.contains("Total Sugars")) {
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Total Sugars", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.sugar(grams);
                                } else if (lineText.contains("Vitamin D")) {
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin D", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminD(grams);
                                } else if (lineText.contains("Vitamin C")) {
                                    Float grams = parseTwoWords(lineText);
                                    nutritionItems.put("Vitamin C", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.vitaminC(grams);
                                } else if (lineText.contains("Calcium")) {
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Calcium", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.calcium(grams);
                                } else if (lineText.contains("Iron")) {
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Iron", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.iron(grams);
                                } else if (lineText.contains("Potassium")) {
                                    Float grams = parseOneWord(lineText);
                                    nutritionItems.put("Potassium", grams);
                                    if (grams >= 0 ) nutriInfoBuilder.potassium(grams);
                                } else if (lineText.contains("Serving size")) {
                                    Pattern pattern = Pattern.compile("Serving size (\\d) (\\w+).*");
                                    Matcher matcher = pattern.matcher(lineText);
                                    while (matcher.find()) {
                                        String servingSize = matcher.group(1);
                                        String servingType = matcher.group(2);
                                        int servingSizeInt = Integer.parseInt(servingSize);
                                        nutriInfoBuilder.servingSize(servingSizeInt);
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
                                Log.e(TAG, "FAILURE!");
                                Log.e(TAG, e.getMessage());
                            }
                        });
    }

    private float parseOneWord(String lineText) {
        String[] parsedNutritionItem = lineText.split(" ");
        if (parsedNutritionItem.length >= 2) {
            return parseGrams(parsedNutritionItem[1]);
        } else {
            return -1; // maybe throw an exception instead
        }
    }

    private float parseTwoWords(String lineText) {
        String[] parsedNutritionItem = lineText.split(" ");
        if (parsedNutritionItem.length >= 3) {
            return parseGrams(parsedNutritionItem[2]);
        } else {
            return -1; // maybe throw an exception instead
        }
    }

    private float parseGrams(String gramsString) {
        int idxM = gramsString.indexOf("m");
        int idxG = gramsString.indexOf("g");
        float result = -1;
        gramsString = gramsString.replaceAll("(o|O)+", "0");
        if (idxM >= 0) {
            String num = gramsString.substring(0, idxM);
            result = Float.parseFloat(num);
        } else if (idxG >= 0) {
            String num = gramsString.substring(0, idxG);
            result = Float.parseFloat(num);
        }
        return result;
    }
}
