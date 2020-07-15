package com.example.android.skoob.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.skoob.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1; //it is a flag for when we come back to starting the activity for result

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment, fragmentToLoad;
    MenuItem temp_menuItem;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        mFirebaseAuth = FirebaseAuth.getInstance();
        checkLogin();
        selectedFragment = null;
        temp_menuItem = null;
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                temp_menuItem = bottomNavigationView.getMenu().findItem(menuItem.getItemId());

                switch (menuItem.getItemId()){
                    case R.id.action_home:
                        selectedFragment = HomeFragment.getInstance();
                        temp_menuItem.setChecked(true);
                        break;
                    case R.id.action_post:
                        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
                        fragmentToLoad = PostFragment.getInstance();
                        break;
                    case R.id.action_account:
                        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
                        fragmentToLoad = AccountFragment.getInstance();
                        break;
                    case R.id.action_settings:
                        selectedFragment = SettingsFragment.getInstance();
                        temp_menuItem.setChecked(true);
                        break;

                }


                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame,selectedFragment);
                transaction.commit();

                return false;
            }
        });

        setDefaultFragment();
    }

    private void setDefaultFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = HomeFragment.getInstance();
        transaction.replace(R.id.main_frame,HomeFragment.getInstance());
        transaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                //Here this user ust have signed out of our app or is signing up for the first time
                temp_menuItem.setChecked(true);
                Toast.makeText(this, "You're now signed in. Welcome to SKOOB.", Toast.LENGTH_SHORT).show();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame,fragmentToLoad);
                transaction.commit();
            }
        }
    }

    private void checkLogin(){

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in. Here we know the user had sign in once and never signed out of our app
                    temp_menuItem.setChecked(true);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_frame,fragmentToLoad);
                    transaction.commit();

                }else{  //user is signed out or not yet registered

                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)// we don't want the phone to save ethe users credentials automatically
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }
}
