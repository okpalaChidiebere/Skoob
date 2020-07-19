package com.example.android.skoob.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.android.skoob.model.Book;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SettingsFragment.settingsAuthButtonOnClickListener{

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
    private String cityName, mUserEmail;
    private List<Book> mBooks = new ArrayList<>();

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase; //entry point for your app to access the database
    private DatabaseReference mBooksForSaleDatabaseReference; //represent s a specific part of the Firebase database
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        cityName = "";
        mUserEmail = "";

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
                        homeFragment.setBooksData(mBooks);
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
                        SettingsFragment settingsFragment = new SettingsFragment();
                        settingsFragment.setUserEmail(mUserEmail);
                        selectedFragment = settingsFragment;
                        temp_menuItem.setChecked(true);
                        break;

                }


                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame,selectedFragment);
                transaction.commit();

                return false;
            }
        });

        //mDisplayBooksListener = this;
        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBooksForSaleDatabaseReference = mFirebaseDatabase.getReference().child("booksForSale"); //getting the root node messages part of our database


        mValueEventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //AT this point, we are done loading the data
                setDefaultFragment(); //now we can load the home page fragment
            }

            public void onCancelled(DatabaseError databaseError) { }
        };
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Book bookForSale = dataSnapshot.getValue(Book.class); //the data of the POJO should match the EXACT name in of the key values in the database object
                mBooks.add(bookForSale);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };

    }

    private void setDefaultFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = HomeFragment.getInstance();
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setUserLocation(cityName);
        homeFragment.setBooksData(mBooks);
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
                            //When we are done getting the current user location, we now initialize event listener
                            //for firebase to get the list of books for sale from database
                            AddListenersForFirebase();
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
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                mUserEmail = user.getEmail();

                Toast.makeText(this, "You're now signed in. Welcome to SKOOB.", Toast.LENGTH_SHORT).show();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                Fragment f = getVisibleFragment();
                /*There are two screens that are visible to the user only when they have not log in.
                HomeScreen and Settings screen. We are trying to determine which fragment to load next
                after login based on the visible fragment*/
                if(f instanceof HomeFragment){
                    transaction.replace(R.id.main_frame, fragmentToLoad);
                    transaction.commit();
                }else{
                    SettingsFragment settingsFragment = new SettingsFragment();
                    settingsFragment.setUserEmail(mUserEmail);
                    fragmentToLoad = settingsFragment;
                    transaction.replace(R.id.main_frame, fragmentToLoad);
                    transaction.commit();
                }
            }
        }else if (resultCode == RESULT_CANCELED) {
            // Sign in was canceled by the user, finish the activity
            finish(); //this line was to prevent the Auth UI from appearing twice
        }
    }

    /*Returns the fragment that is loaded in UI
    * FYI: this functions will not work properly in a Two pane UI where you have two fragments visible at once
    * You will need to modify the code a bit to return two fragments visible*/
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        //if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        //}
        return null;
    }

    private void checkLogin(){

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in. Here we know the user had sign in once and never signed out of our app
                    mUserEmail = user.getEmail();
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

    @Override
    public void onSettingsAuthButtonSelected() {

        if(!mUserEmail.isEmpty()) {
            mFirebaseAuth.signOut(); //close the session

            if (mAuthStateListener != null) {
                mFirebaseAuth.removeAuthStateListener(mAuthStateListener); //remove the FireBase login screen
            }
            mUserEmail = "";

            //Update the setting s fragment UI
            SettingsFragment settingsFragment = new SettingsFragment();
            settingsFragment.setUserEmail(mUserEmail);
            selectedFragment = settingsFragment; //initialize the last selected fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, settingsFragment);
            transaction.commit();
        }else{

            mFirebaseAuth.addAuthStateListener(mAuthStateListener); //load back up thew login screen
        }

    }

    public String getUserEmail(){
        return mUserEmail;
    }

    public void AddListenersForFirebase(){
        mBooksForSaleDatabaseReference.addChildEventListener(mChildEventListener);
        mBooksForSaleDatabaseReference.addListenerForSingleValueEvent(mValueEventListener);
    }
}
