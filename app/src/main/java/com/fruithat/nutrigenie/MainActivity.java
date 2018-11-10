package com.fruithat.nutrigenie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /*
     * Navigation Drawer
     */
    private static DrawerLayout mDrawerLayout;

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
    private FrameLayout mFrameLayout;
    private HomeFragment mHomeFragment;
    private PreferencesFragment mPreferencesFragment;
    private AccountFragment mAccountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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
                        mFragmentTransaction.replace(R.id.fragment_container, mPreferencesFragment);
                        mFragmentTransaction.commit();
                        mFragmentManager.executePendingTransactions();

                        return true;
                    }
                });

        mFrameLayout = findViewById(R.id.fragment_container);
        mFragmentManager = getSupportFragmentManager();

        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mHomeFragment = new HomeFragment();
        mAccountFragment = new AccountFragment();
        mPreferencesFragment = new PreferencesFragment();
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
                                break;
                            case R.id.navigation_account:
                                mFragmentTransaction.replace(R.id.fragment_container, mAccountFragment);
                        }

                        mFragmentTransaction.commit();
                        mFragmentManager.executePendingTransactions();

                        return false;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get the current firebase user
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // If the current user is null, then prompt the user to sign in
        if (currentUser == null) {
            signIn();
        }

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

    private void setUpNewUser(FirebaseUser currentUser) {
        Preferences preferences = new Preferences(2000);

        mDatabase.child("preferences").child(currentUser.getUid()).setValue(preferences);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                IdpResponse response = IdpResponse.fromResultIntent(data);

                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // ...
                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    // ...
                }
                break;
            default:
                break;
        }
    }
}
