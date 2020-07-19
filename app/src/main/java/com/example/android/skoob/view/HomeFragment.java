package com.example.android.skoob.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private ProgressBar mProgressBar;
    private TextView mTextCity;
    private String mUserLocation;
    private HomeBooksAdapter mBookAdapter;
    private RecyclerView mRecyclerView;
    private List<Book> mBooks = new ArrayList<>();

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase; //entry point for your app to access the database
    private DatabaseReference mBooksForSaleDatabaseReference; //represent s a specific part of the Firebase database
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    public void setUserLocation(String loc){
        mUserLocation = loc;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up the recycler view
        mRecyclerView = rootView.findViewById(R.id.tv_home_books_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mBookAdapter = new HomeBooksAdapter(null);
        mRecyclerView.setAdapter(mBookAdapter);
        // End set up the recycler view

        mProgressBar = rootView.findViewById(R.id.progressBar);
        mTextCity = rootView.findViewById(R.id.tv_city);

        //Banner AdMob set up
        AdView mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //end banner ad set up

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBooksForSaleDatabaseReference = mFirebaseDatabase.getReference().child("booksForSale"); //getting the root node messages part of our database

        mTextCity.setText(mUserLocation);
        mProgressBar.setVisibility(View.VISIBLE);

        mValueEventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("We're done loading the initial "+dataSnapshot.getChildrenCount()+" items");
                mProgressBar.setVisibility(View.GONE);
            }

            public void onCancelled(DatabaseError databaseError) { }
        };
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Book bookForSale = dataSnapshot.getValue(Book.class); //the data of the POJO should match the EXACT name in of the key values in the database object
                mBooks.add(bookForSale);
                mBookAdapter.setData(mBooks);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        mBooksForSaleDatabaseReference.addChildEventListener(mChildEventListener);
        mBooksForSaleDatabaseReference.addListenerForSingleValueEvent(mValueEventListener);

        return rootView;

    }


}
