package com.example.android.skoob.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;
import com.example.android.skoob.utils.AES;
import com.example.android.skoob.utils.AdMobUtil;
import com.example.android.skoob.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements BooksAdapter.BooksAdapterOnClickHandler{

    private ProgressBar mProgressBar;
    private TextView mTextCity;
    private String mUserLocation;
    private BooksAdapter mBookAdapter;
    private RecyclerView mRecyclerView;
    private List<Book> mBooks = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshing = false;


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
        mBookAdapter = new BooksAdapter(null, "SET_HOME_ADAPTER", this);
        mRecyclerView.setAdapter(mBookAdapter);
        // End set up the recycler view

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mTextCity = rootView.findViewById(R.id.tv_city);

        //Banner AdMob set up
        AdView mAdView = rootView.findViewById(R.id.adView);
        mAdView.setMinimumHeight(AdMobUtil.getAdViewHeightInDP(getActivity()));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //end banner ad set up

        mTextCity.setText(mUserLocation);
        mProgressBar.setVisibility(View.VISIBLE);


        if(mBooks !=null && !mBooks.isEmpty()){
            mProgressBar.setVisibility(View.GONE);
            List<Book> tempBooks = sortBooks(mBooks); //sort the list first
            mBookAdapter.setData(tempBooks); //load the adapter data with sorted list
            //mIsRefreshing = true;
        }else{
           //No books found in database
            mBookAdapter.setData(null);
        }

        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
                //usually you will run a service and have a broadcast receiver get your return value
                // to know when to disable/stop the refreshing
            }
        });
        return rootView;

    }

    //This function returns the list of books, with the book for sale that has the current user location in it FIRST
    private List<Book> sortBooks(List<Book> books){

        Collections.sort(books, new Comparator<Book>() {
            final String PREFIX = mUserLocation;

            @Override
            public int compare(Book a, Book b) {
                if (AES.decrypt(a.getPlace(), Constants.SECRET_AES_KEY).contains(PREFIX) && AES.decrypt(b.getPlace(), Constants.SECRET_AES_KEY).contains(PREFIX)) return a.getPlace().compareTo(b.getPlace());
                if (AES.decrypt(a.getPlace(), Constants.SECRET_AES_KEY).contains(PREFIX) && !AES.decrypt(b.getPlace(), Constants.SECRET_AES_KEY).contains(PREFIX)) return -1;
                if (!AES.decrypt(a.getPlace(), Constants.SECRET_AES_KEY).contains(PREFIX) && AES.decrypt(b.getPlace(), Constants.SECRET_AES_KEY).contains(PREFIX)) return 1;
                return 0;
            }
        });
        return books;
    }

    @Override
    public void onClick(Book book) {
        MainActivity activity = (MainActivity) getActivity();
        String userEmail = activity.getUserEmail();
        final Intent intent = new Intent(getContext(), BookDetails.class);
        intent.putExtra(Constants.EXTRA_BOOK_DETAILS, book);
        intent.putExtra(Constants.EXTRA_USER_LOGIN_EMAIL, userEmail);
        startActivity(intent);
    }
}
