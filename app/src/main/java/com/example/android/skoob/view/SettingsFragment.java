package com.example.android.skoob.view;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.skoob.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#getInstance} factory method to
 * create an instance of this fragment
 */
public class SettingsFragment extends Fragment {

    private TextView mTextEmail, mSignOutText, mAuthMessage;
    private String mUserEmail;


    settingsAuthButtonOnClickListener mCallback; //This will be overiden in the MainActivity to know when the sign in or out textView is clicked

    public interface settingsAuthButtonOnClickListener {
        void onSettingsAuthButtonSelected();
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    public void setUserEmail(String email){
        mUserEmail = email;
    }


    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (settingsAuthButtonOnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement settingsAuthButtonOnClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mTextEmail = rootView.findViewById(R.id.tv_userEmail);
        mSignOutText = rootView.findViewById(R.id.tv_signOut);
        mAuthMessage = rootView.findViewById(R.id.tv_info_message);

        if(mUserEmail !=null && !mUserEmail.isEmpty())
            mTextEmail.setText(mUserEmail);
        else{
            mSignOutText.setText(getString(R.string.signIn_message));
            mTextEmail.setText(getString(R.string.auth_info_message));
            mAuthMessage.setVisibility(View.GONE);
        }

        mSignOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger the callback method and pass in the position that was clicked
                mCallback.onSettingsAuthButtonSelected();
            }
        });

        return rootView;
    }

}
