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
import android.widget.Button;
import android.widget.Toast;

import com.example.android.skoob.R;
import com.example.android.skoob.utils.Constants;
import com.google.android.gms.common.api.Status;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
    private static final Pattern ONLY_DIGIT_PATTERN = Pattern.compile("^\\d+$");//regex for only digit /^\d+$/

    private TextInputLayout mTextInputBookName;
    private TextInputLayout mTextInputIsbnNumber;
    private TextInputLayout mTextInputPrice;
    private TextInputLayout mTextInputDepartment;
    private TextInputLayout mTextInputSubject;
    private TextInputLayout mTextInputLocation;
    private Button mSubmitButton;

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

        mTextInputBookName = rootView.findViewById(R.id.tv_bookName);
        mTextInputIsbnNumber = rootView.findViewById(R.id.tv_IsbnNumber);
        mTextInputPrice = rootView.findViewById(R.id.tv_Price);
        mTextInputDepartment = rootView.findViewById(R.id.tv_Department);
        mTextInputSubject = rootView.findViewById(R.id.tv_Subject);
        mTextInputLocation = rootView.findViewById(R.id.tv_Location);
        mSubmitButton = rootView.findViewById(R.id.tv_submit_button);

        mTextInputLocation.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddPlaceClicked();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInput();
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
                    mTextInputLocation.getEditText().setText(placeAddress);

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

    private void confirmInput() {

        if (!validateBookName() | !validateIsbnNumber() | !validatePrice()
                | !validateDepartment() | !validateSubject() | !validateLocation()){
            return;
        }
        String input = "BookName: " + mTextInputBookName.getEditText().getText().toString().trim();
        input += "\n";
        input += "ISBN: " + mTextInputIsbnNumber.getEditText().getText().toString().trim();
        input += "\n";
        input += "Price: " + mTextInputPrice.getEditText().getText().toString().trim();
        input += "\n";
        input += "Department: " + mTextInputDepartment.getEditText().getText().toString().trim();
        input += "\n";
        input += "Subject: " + mTextInputSubject.getEditText().getText().toString().trim();
        input += "\n";
        input += "Location: " + mTextInputLocation.getEditText().getText().toString().trim();

        Toast.makeText(getContext(), input, Toast.LENGTH_LONG).show();
    }

    private boolean validateBookName() {
        String bookNameInput = mTextInputBookName.getEditText().getText().toString().trim();
        if (bookNameInput.isEmpty()) {
            mTextInputBookName.setError("Field can't be empty");
            return false;
        }else if(ONLY_DIGIT_PATTERN.matcher(bookNameInput).matches()){
            mTextInputBookName.setError("Field can't be only Digit");
            return false;
        }else {
            mTextInputBookName.setError(null);
            return true;
        }
    }

    private boolean validateIsbnNumber(){
        String isbnInput = mTextInputIsbnNumber.getEditText().getText().toString().trim();
        if (isbnInput.isEmpty()) {
            mTextInputIsbnNumber.setError("Field can't be empty");
            return false;
        }else if(!ONLY_DIGIT_PATTERN.matcher(isbnInput).matches()){
            mTextInputIsbnNumber.setError("Field must be digit only");
            return false;
        }else {
            mTextInputIsbnNumber.setError(null);
            return true;
        }
    }

    private boolean validatePrice(){
        String priceInput = mTextInputPrice.getEditText().getText().toString().trim();
        if (priceInput.isEmpty()) {
            mTextInputPrice.setError("Field can't be empty");
            return false;
        }else if(!ONLY_DIGIT_PATTERN.matcher(priceInput).matches()){
            mTextInputPrice.setError("Field must be digit only");
            return false;
        }else {
            mTextInputPrice.setError(null);
            return true;
        }
    }

    private boolean validateDepartment() {
        String departmentInput = mTextInputDepartment.getEditText().getText().toString().trim();
        if (departmentInput.isEmpty()) {
            mTextInputDepartment.setError("Field can't be empty");
            return false;
        }else if(ONLY_DIGIT_PATTERN.matcher(departmentInput).matches()){
            mTextInputDepartment.setError("Field can't be only Digit");
            return false;
        }else {
            mTextInputDepartment.setError(null);
            return true;
        }
    }

    private boolean validateSubject() {
        String subjectInput = mTextInputSubject.getEditText().getText().toString().trim();
        if (subjectInput.isEmpty()) {
            mTextInputSubject.setError("Field can't be empty");
            return false;
        }else if(ONLY_DIGIT_PATTERN.matcher(subjectInput).matches()){
            mTextInputSubject.setError("Field can't be only Digit");
            return false;
        }else {
            mTextInputSubject.setError(null);
            return true;
        }
    }

    private boolean validateLocation() {
        String locationInput = mTextInputLocation.getEditText().getText().toString().trim();
        if (locationInput.isEmpty()) {
            mTextInputLocation.setError("Field can't be empty");
            return false;
        }else {
            mTextInputLocation.setError(null);
            return true;
        }
    }
}
