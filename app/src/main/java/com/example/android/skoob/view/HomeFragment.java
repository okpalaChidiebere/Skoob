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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    public void setUserLocation(String loc){
        mUserLocation = loc;
    }

    public void setBooksData(List<Book> books){
        mBooks = books;
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

        mTextCity.setText(mUserLocation);
        mProgressBar.setVisibility(View.VISIBLE);


        if(mBooks !=null && !mBooks.isEmpty()){
            mProgressBar.setVisibility(View.GONE);
            List<Book> tempBooks = sortBooks(mBooks); //sort the list first
            mBookAdapter.setData(tempBooks); //load the adapter data with sorted list
        }else{
           //No books found in database
            mBookAdapter.setData(null);
        }

        return rootView;

    }

    //This function returns the list of books, with the book for sale that has the current user location in it FIRST
    private List<Book> sortBooks(List<Book> books){

        Collections.sort(books, new Comparator<Book>() {
            final String PREFIX = mUserLocation;

            @Override
            public int compare(Book a, Book b) {
                if (a.getPlace().contains(PREFIX) && b.getPlace().contains(PREFIX)) return a.getPlace().compareTo(b.getPlace());
                if (a.getPlace().contains(PREFIX) && !b.getPlace().contains(PREFIX)) return -1;
                if (!a.getPlace().contains(PREFIX) && b.getPlace().contains(PREFIX)) return 1;
                return 0;
            }
        });
        return books;
    }
}
