package com.example.android.skoob.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.skoob.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

}
