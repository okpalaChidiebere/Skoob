package com.example.android.skoob.view;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.skoob.R;
import com.example.android.skoob.utils.Constants;
import com.google.android.gms.common.api.Status;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    // Constants
    public static final String TAG = PostFragment.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;

    private TextInputEditText mLocationEditText;
    private List<Place.Field> fields;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment getInstance() {
        return new PostFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        initializePlace();
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        mLocationEditText = rootView.findViewById(R.id.locationEditText);
        mLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddPlaceClicked();
            }
        });

        return rootView;
    }

    /*Before the user can add location, we want to make sure the location permission is enables*/
    public void onAddPlaceClicked() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getContext());
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
    }

    private void initializePlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), Constants.GEO_API_KEY);
        }
    }

    /*We need to check if the request code is thesame one that we have used when starting our place
    Picker activity in startActivityForResult*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {

            switch (resultCode) {
                case RESULT_OK:
                    Place place = Autocomplete.getPlaceFromIntent(data);

                    // Extract the place information from the API
                    String placeName = place.getName();
                    String placeAddress = place.getAddress();
                    String placeID = place.getId();
                    Log.i(TAG, "PlaceName: " + placeName + ", " + "PlaceAddress: " + placeAddress + placeID);
                    mLocationEditText.setText(placeAddress);

                    //TODO Encrypt the placeID and store it in the fireBase
                    /*We only store the placeID because google terms and conditions require us not to store
                    any place information with the exception of IDs for longer than 30days*/

                    break;
                case AutocompleteActivity.RESULT_ERROR:
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }

    }
}
