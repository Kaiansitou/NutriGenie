package com.fruithat.nutrigenie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import android.content.Intent;
import com.fruithat.nutrigenie.NutritionInformation.NutritionInformationBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.github.mikephil.charting.*;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

public class MainActivity extends AppCompatActivity {

    /*
     * Navigation Drawer
     */
    private DrawerLayout mDrawerLayout;

    /*
     * Firebase Authentication
     */
    private static final int RC_SIGN_IN = 123;

    /*
     * Firebase Database
     */
    private DatabaseReference mDatabase;

    /*
     * Fragments
     */
    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment = new HomeFragment();
    private PreferencesFragment mPreferencesFragment = new PreferencesFragment();
    private AccountFragment mAccountFragment = new AccountFragment();
    private ScanFragment mScanFragment = new ScanFragment();
    private HistoryFragment mHistoryFragment = new HistoryFragment();
    private AboutFragment mAboutFragment = new AboutFragment();
    private ContactUsFragment mContactFragment = new ContactUsFragment();
    private CircleGraphFragment mCircleGraphFragment = new CircleGraphFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setUpToolbar();

        mFragmentManager = getSupportFragmentManager();

        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fragment_container, mHomeFragment);
        mFragmentTransaction.commit();
        mFragmentManager.executePendingTransactions();

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                mFragmentTransaction.replace(R.id.fragment_container, mHomeFragment);
                                break;
                            case R.id.navigation_scan:
                                mFragmentTransaction.replace(R.id.fragment_container, mScanFragment);
                                break;
                            case R.id.navigation_account:
                                mFragmentTransaction.replace(R.id.fragment_container, mAccountFragment);
                        }

                        mFragmentTransaction.commit();
                        mFragmentManager.executePendingTransactions();

                        return true;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        signIn();

        NutritionHistory history = NutritionHistory.getInstance();
//        history.addNutritionInformation("test", new NutritionInformationBuilder("cup", 1.5)
//                .calcium(15)
//                .calories(260)
//                .caloriesFromFat(120)
//                .carbohydrates(31)
//                .cholesterol(30)
//                .iron(4)
//                .protein(5)
//                .saturatedFat(5)
//                .servingsPerContainer(2)
//                .sodium(660)
//                .sugar(5)
//                .totalFat(13)
//                .transFat(2)
//                .vitaminA(4)
//                .vitaminC(2)
//                .build());
//
//        history.getNutritionInformation(new Date(0), new Date(), null);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                        switch (menuItem.getItemId()) {
                            case R.id.sidebar_settings:
                                mFragmentTransaction.replace(R.id.fragment_container, mPreferencesFragment);
                                break;
                            case R.id.sidebar_history:
                                mFragmentTransaction.replace(R.id.fragment_container, mHistoryFragment);
                                break;
                            case R.id.sidebar_about:
                                mFragmentTransaction.replace(R.id.fragment_container, mAboutFragment);
                                break;
                            case R.id.sidebar_contact:
                                mFragmentTransaction.replace(R.id.fragment_container, mContactFragment);
                                break;
                        }
                        mFragmentTransaction.commit();
                        mFragmentManager.executePendingTransactions();

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signIn() {
        // Get the current firebase user
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // If the current user is null, then prompt the user to sign in
        if (currentUser == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    private void setUpNewUser(FirebaseUser currentUser) {
        Preferences preferences = new Preferences(2000);

        mDatabase.child("preferences").child(currentUser.getUid()).setValue(preferences);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Create the user's preferences if they do not already exist
                DatabaseReference user_preferences = mDatabase.child("preferences").child(currentUser.getUid());
                user_preferences.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("calories")) {
                            setUpNewUser(currentUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Failed to sign in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
