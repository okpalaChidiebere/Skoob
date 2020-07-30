package com.example.android.skoob.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.skoob.R;
import com.google.firebase.messaging.FirebaseMessaging;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#getInstance} factory method to
 * create an instance of this fragment
 */
public class SettingsFragment extends Fragment {

    private static String LOG_TAG = SettingsFragment.class.getSimpleName();
    private TextView mTextEmail, mSignOutText, mAuthMessage;
    private String mUserEmail;

    static setBoolPrefBackButton mSetBoolPrefBackButtonCallBack;
    settingsAuthButtonOnClickListener mCallback; //This will be overiden in the MainActivity to know when the sign in or out textView is clicked

    public interface settingsAuthButtonOnClickListener {
        void onSettingsAuthButtonSelected();
    }

    public interface setBoolPrefBackButton{
        void onSetBoolPrefBackButton(boolean val);
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
            mSetBoolPrefBackButtonCallBack = (setBoolPrefBackButton) context;
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

        //MainActivity activity = (MainActivity) getActivity();
        Bundle extras = getActivity().getIntent().getExtras();
        // Checks if the extras exist and if the key "test" from our FCM message is in the intent
        if (extras != null && extras.containsKey("test")) {
            // If the key is there, print out the value of "test"
            Log.d(LOG_TAG, "Contains: " + extras.getString("test"));
        }

        return rootView;
    }


    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat{

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource in res->xml->pref_general
            setPreferencesFromResource(R.xml.pref_general, rootKey);
        }
    }

    public static class NotificationsPreferenceFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource in res->xml->pref_notifications
            setPreferencesFromResource(R.xml.pref_notifications, rootKey);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals(getString(R.string.pref_skoob_warn_before_exit_key))) {
                boolean tempValue = sharedPreferences.getBoolean(getString(R.string.pref_skoob_warn_before_exit_key),
                        getResources().getBoolean(R.bool.pref_warn_exit_default));

                mSetBoolPrefBackButtonCallBack.onSetBoolPrefBackButton(tempValue);
            }

            Preference preference = findPreference(key);
            if (null != preference) {
                if ((preference instanceof CheckBoxPreference)) {
                    // Get the current state of the CheckBox preference
                    boolean isChecked = sharedPreferences.getBoolean(key, false);
                    if (isChecked) {
                        /*The preference key matches the following key for the associated instructor in
                        FCM. For example, the key for Bump alert is key_bump_alert (as seen in
                        pref_notifications.xml). The topic for Bump alert's messages is /topics/key_bump_alert
                        So when u send a notificatoin through the console put the topic 'key_bump_alert' to send
                        messages to all subscribers of this topic*/
                        // Subscribe
                        FirebaseMessaging.getInstance().subscribeToTopic(key);
                        Log.d(LOG_TAG, "Subscribing to " + key);
                    }else{
                        // Un-subscribe
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                        Log.d(LOG_TAG, "Un-subscribing to " + key);
                    }
                }
            }

        }
            @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
