package com.example.android.skoob.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.skoob.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private ProgressBar mProgressBar;
    private TextView mTextLongLat;
    private TextView mTextCity;
    private String mUserLocation;

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

        mProgressBar = rootView.findViewById(R.id.progressBar);
        mTextLongLat = rootView.findViewById(R.id.textLongLat);
        mTextCity = rootView.findViewById(R.id.tv_city);

        mTextCity.setText(mUserLocation);

        return rootView;

    }


}
