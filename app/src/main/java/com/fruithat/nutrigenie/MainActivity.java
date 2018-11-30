package com.fruithat.nutrigenie;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import sharefirebasepreferences.crysxd.de.lib.SharedFirebasePreferences;
import sharefirebasepreferences.crysxd.de.lib.SharedFirebasePreferencesContextWrapper;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    /*
     * Firebase Authentication
     */
    private static final int RC_SIGN_IN = 123;
    /*
     * Navigation Drawer
     */
    private DrawerLayout mDrawerLayout;
    /*
     * Firebase Database
     */
    private DatabaseReference mDatabase;

    /*
     * Shared Preferences
     */
    private SharedFirebasePreferences mPreferences;

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
    private LoadingFragment mLoadingFragment = new LoadingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setUpToolbar();

        mFragmentManager = getSupportFragmentManager();

        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fragment_container, mLoadingFragment);
        mFragmentTransaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPreferences != null) {
            mPreferences.keepSynced(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPreferences != null) {
            mPreferences.keepSynced(false);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new SharedFirebasePreferencesContextWrapper(newBase));
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
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    switch (menuItem.getItemId()) {
                        case R.id.sidebar_settings:
                            actionbar.setTitle("Preferences");
                            mFragmentTransaction.replace(R.id.fragment_container, mPreferencesFragment);
                            break;
                        case R.id.sidebar_history:
                            actionbar.setTitle("History");
                            mFragmentTransaction.replace(R.id.fragment_container, mHistoryFragment);

                            break;
                        case R.id.sidebar_about:
                            actionbar.setTitle("About");
                            mFragmentTransaction.replace(R.id.fragment_container, mAboutFragment);
                            break;
                    }
                    mFragmentTransaction.commit();
                    mFragmentManager.executePendingTransactions();

                    return true;
                });

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(
                item -> {
                    FragmentTransaction mFragmentTransaction1 = mFragmentManager.beginTransaction();

                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            actionbar.setTitle("Nutri Genie");
                            mFragmentTransaction1.replace(R.id.fragment_container, mHomeFragment);
                            break;
                        case R.id.navigation_scan:
                            actionbar.setTitle("Scan");
                            mFragmentTransaction1.replace(R.id.fragment_container, mScanFragment);
                            break;
                        case R.id.navigation_account:
                            actionbar.setTitle("Profile");
                            mFragmentTransaction1.replace(R.id.fragment_container, mAccountFragment);
                    }

                    mFragmentTransaction1.commit();
                    mFragmentManager.executePendingTransactions();

                    return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        // Get the current firebase user
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

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
        } else {
            mPreferences = SharedFirebasePreferences.getDefaultInstance(this);
            mPreferences.keepSynced(true);
            mPreferences.pull().addOnPullCompleteListener(new SharedFirebasePreferences.OnPullCompleteListener() {
                @Override
                public void onPullSucceeded(SharedFirebasePreferences preferences) {
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.fragment_container, mHomeFragment);
                    mFragmentTransaction.commit();
                    mFragmentManager.executePendingTransactions();
                }

                @Override
                public void onPullFailed(Exception e) {
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.fragment_container, mHomeFragment);
                    mFragmentTransaction.commit();
                    mFragmentManager.executePendingTransactions();
                    Toast.makeText(getApplicationContext(), "Fetch failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
