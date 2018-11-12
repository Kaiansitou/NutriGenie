package com.fruithat.nutrigenie;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Date;
import java.util.HashMap;

public class NutritionHistory {

    private static NutritionHistory singleton;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    private NutritionHistory() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static NutritionHistory getInstance() {
        if (singleton == null) {
            singleton = new NutritionHistory();
        }

        return singleton;
    }

    public void addNutritionInformation(String name, NutritionInformation nutritionInformation) {
        DatabaseReference foods = mDatabase.child("foods");
        String uniqueKey = foods.push().getKey();

        foods.child(name + "@" + uniqueKey).setValue(nutritionInformation.getDatabaseHashMap());

        DatabaseReference history = mDatabase.child("history").child(mCurrentUser.getUid());

        history.child(String.valueOf(System.currentTimeMillis())).setValue(name + "@" + uniqueKey);
    }

    public void getNutritionInformation(final Date from, final Date to, final NutritionHistoryCallback callback) {
        mDatabase.child("foods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot foodSnapshot) {
                mDatabase.child("history").child(mCurrentUser.getUid()).orderByKey().startAt(String.valueOf(from.getTime())).endAt(String.valueOf(to.getTime())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot histSnapshot) {
                        @SuppressLint("UseSparseArrays")
                        HashMap<Long, NutritionInformation> nutritionHistory = new HashMap<>((int) histSnapshot.getChildrenCount());

                        for (DataSnapshot event : histSnapshot.getChildren()) {
                            if (event.getKey() == null || !foodSnapshot.hasChild((String) event.getValue())) {
                                Log.e("NutritionHistory", "Failed to find food: " + event.getKey());
                                continue;
                            }

                            nutritionHistory.put(Long.valueOf(event.getKey()), NutritionInformation.parseSnapshot(foodSnapshot.child((String) event.getValue())));
                        }

                        callback.onDataReceived(nutritionHistory);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
