package com.example.praktika_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import Fragments.FriendsFragment;
import Fragments.HomeFragment;
import Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            bottomNavigationView = findViewById(R.id.bottom_navigation);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    if (menuItem.getItemId() == R.id.nav_home) {
                        selectorFragment = new HomeFragment();
                    } else if (menuItem.getItemId() == R.id.nav_friends) {
                        selectorFragment = new FriendsFragment();
                    } else if (menuItem.getItemId() == R.id.nav_profile) {
                        selectorFragment = new ProfileFragment();
                    }

                    if (selectorFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                    }
                    return true;
                }
            });

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        else {
            startActivity(new Intent(MainActivity.this, Authorization.class));
        }
    }
}