package com.example.android.skoob.view;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.skoob.R;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    View myFragment;
    TabLayout tabLayout;
    ViewPager viewPager;
    String userName;
    TextView textUsername;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment getInstance() {
        return new AccountFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment =  inflater.inflate(R.layout.fragment_account, container, false);

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = (ViewPager) myFragment.findViewById(R.id.news_viewPager);

        // Find the tab layout that shows the tabs
        tabLayout = (TabLayout) myFragment.findViewById(R.id.news_tabLayout);

        MainActivity activity = (MainActivity) getActivity();
        userName = activity.getUserName();
        textUsername = myFragment.findViewById(R.id.tv_account_username);
        textUsername.setText(userName);


        return myFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setUpViewPager(ViewPager viewPager){
        SectionFragmentAdapter adapter = new SectionFragmentAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new ListingsFragment(), getString(R.string.listings_category));
        adapter.addFragment(new FavouritesFragment(), getString(R.string.favorites_category));

        viewPager.setAdapter(adapter);
    }

}
