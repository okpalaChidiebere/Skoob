package com.example.android.skoob.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.skoob.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selectedFragment = null;

                MenuItem temp_menuItem = bottomNavigationView.getMenu().findItem(menuItem.getItemId());
                temp_menuItem.setChecked(true);

                switch (menuItem.getItemId()){
                    case R.id.action_home:
                        selectedFragment = HomeFragment.getInstance();
                        break;
                    case R.id.action_post:
                        selectedFragment = PostFragment.getInstance();
                        break;
                    case R.id.action_account:
                        selectedFragment = AccountFragment.getInstance();
                        break;
                    case R.id.action_settings:
                        selectedFragment = SettingsFragment.getInstance();
                        break;

                }


                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame,selectedFragment);
                transaction.commit();

                return false;
            }
        });

        setDefaultFragment();
    }

    private void setDefaultFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame,HomeFragment.getInstance());
        transaction.commit();
    }
}
