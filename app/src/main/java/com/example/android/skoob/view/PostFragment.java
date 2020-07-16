package com.example.android.skoob.view;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;
import com.example.android.skoob.utils.Constants;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment implements
        setImageUrlListener,
        BookImageAdapter.BookImageAdapterOnLongClickHandler{

    // Constants
    public static final String TAG = PostFragment.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final Pattern ONLY_DIGIT_PATTERN = Pattern.compile("^\\d+$");//regex for only digit /^\d+$/
    private static final int RC_PHOTO_PICKER = 2; //a flag to know when the user is picking a photo

    private TextInputLayout mTextInputBookName;
    private TextInputLayout mTextInputIsbnNumber;
    private TextInputLayout mTextInputPrice;
    private TextInputLayout mTextInputDepartment;
    private TextInputLayout mTextInputSubject;
    private TextInputLayout mTextInputLocation;
    private TextView mImageHint;
    private Button mSubmitButton, mAddImage;

    private List<Place.Field> fields;
    private List<Uri> mBookImageList = new ArrayList<>();
    private BookImageAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BookImageAdapter.BookImageAdapterOnLongClickHandler longClickHandler;
    private String UserEmail, mPlaceCity;
    private List<String> mBookImageToUpload = new ArrayList<>(); //use to store final list of imageUrls from firebase Storage

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase; //entry point for your app to access the database
    private DatabaseReference mBooksForSaleDatabaseReference; //represent s a specific part of the Firebase database
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mBooksPhotosStorageReference;

    //this listener will be used to know when we are
    // done uploading book image to storage before we store book datashot in database
    private setImageUrlListener mSetImageCallBack;

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
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        longClickHandler = this;
        // Set up the recycler view
        mRecyclerView = rootView.findViewById(R.id.tv_recyclerView_images);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        mAdapter = new BookImageAdapter( null, longClickHandler);
        mRecyclerView.setAdapter(mAdapter);
        // End set up the recycler view


        mTextInputBookName = rootView.findViewById(R.id.tv_bookName);
        mTextInputIsbnNumber = rootView.findViewById(R.id.tv_IsbnNumber);
        mTextInputPrice = rootView.findViewById(R.id.tv_Price);
        mTextInputDepartment = rootView.findViewById(R.id.tv_Department);
        mTextInputSubject = rootView.findViewById(R.id.tv_Subject);
        mTextInputLocation = rootView.findViewById(R.id.tv_Location);
        mSubmitButton = rootView.findViewById(R.id.tv_submit_button);
        mAddImage = rootView.findViewById(R.id.tv_uploadImage_button);
        mImageHint = rootView.findViewById(R.id.tv_imageSideNote);

        MainActivity activity = (MainActivity) getActivity();
        UserEmail = activity.getUserEmail();

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mBooksForSaleDatabaseReference = mFirebaseDatabase.getReference().child("booksForSale"); //getting the root node messages part of our database
        mBooksPhotosStorageReference = mFirebaseStorage.getReference().child("book_photos"); //getting the location 'book_photos' in our Firebase storage console
        mSetImageCallBack = this;

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

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
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
                    LatLng latLng = place.getLatLng();
                    fetchCityFromPlaceLatLong(latLng);
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
        }else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData(); // <FILENAME> eg content://local_images/filename4
            mBookImageList.add(selectedImageUri);
            mAdapter.swapBookImageList(mBookImageList);
            if(mBookImageList != null){
                mImageHint.setVisibility(View.VISIBLE);
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
        uploadBookToFirebase();

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

    private void openFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    public void onLongClicked(final int imageAdapterPosition) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Do you want to remove image");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mBookImageList.remove(imageAdapterPosition);

                        if(mBookImageList.size() == 0){
                            mImageHint.setVisibility(View.GONE);
                        }

                        mAdapter.swapBookImageList(mBookImageList);
                        Snackbar.make(getView(), "Image removed", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
        builder1.setNegativeButton(android.R.string.no, null);

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void uploadBookToFirebase(){

        for (int i =0 ; i < mBookImageList.size(); i++) {

            final StorageReference photoRef = mBooksPhotosStorageReference.child(mBookImageList.get(i).getLastPathSegment());
            // Upload file to Firebase Storage
            UploadTask uploadTask = photoRef.putFile(mBookImageList.get(i));

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {

                            // When the image has successfully uploaded, we get its download URL
                            String photoStringLink = downloadUri.toString();
                            mSetImageCallBack.onSetImageUrlListener(photoStringLink);
                        }

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

    }

    @Override
    public void onSetImageUrlListener(String imageUrl) {
        mBookImageToUpload.add(imageUrl); //add imageUrl

        //When are are done uploading books images to file storage and getting their related url,
        // we can now upload book info to database
        if(mBookImageToUpload.size() == mBookImageList.size()){
            int tempPrice, tempIsbnNumber;
            tempPrice = Integer.parseInt(mTextInputPrice.getEditText().getText().toString().trim());
            tempIsbnNumber = Integer.parseInt(mTextInputIsbnNumber.getEditText().getText().toString().trim());

            Book bookForSale = new Book(mTextInputBookName.getEditText().getText().toString().trim(),
                    tempIsbnNumber,
                    tempPrice,
                    mTextInputDepartment.getEditText().getText().toString().trim(),
                    mTextInputSubject.getEditText().getText().toString().trim(),
                    mBookImageToUpload,
                    UserEmail,
                    mTextInputLocation.getEditText().getText().toString().trim(),
                    mPlaceCity);
            //this triggers the childEventListener, so our screen will be update with new data from firebase console database
            mBooksForSaleDatabaseReference.push().setValue(bookForSale);
            Toast.makeText(getContext(), "Successfully posted!", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchCityFromPlaceLatLong(LatLng latLng){

        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;

        try{
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
            mPlaceCity = addresses.get(0).getLocality(); //get the city
        }catch(Exception exception){
            mPlaceCity = exception.getMessage(); //assign error message to display
        }


    }
}
