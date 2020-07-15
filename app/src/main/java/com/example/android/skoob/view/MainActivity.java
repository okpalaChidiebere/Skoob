package com.example.android.skoob.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.skoob.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1; //it is a flag for when we come back to starting the activity for result
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;


    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment, fragmentToLoad;
    MenuItem temp_menuItem;
    private String cityName;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        cityName = "";

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            AddLocationPermission();
            getCurrentLocation();
        }else{
            getCurrentLocation();
        }

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
                        HomeFragment homeFragment = new HomeFragment();
                        homeFragment.setUserLocation(cityName);
                        selectedFragment = homeFragment;
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

    }

    private void setDefaultFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = HomeFragment.getInstance();
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setUserLocation(cityName);
        transaction.replace(R.id.main_frame, homeFragment);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_FINE_LOCATION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(MainActivity.this, getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void AddLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    private void getCurrentLocation(){

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null & locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            /*mTextLongLat.setText(String.format(
                                    "Latitude: %s\nLongitude: %s",
                                    latitude,
                                    longitude
                            ));*/

                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchCityFromLatLong(location);
                            setDefaultFragment();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void fetchCityFromLatLong(Location location){

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try{
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            cityName = addresses.get(0).getLocality(); //get the city
        }catch(Exception exception){
            cityName = exception.getMessage(); //assign error message to display
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
