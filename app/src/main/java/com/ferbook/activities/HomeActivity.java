package com.ferbook.activities;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.ferbook.R;
import com.ferbook.fragments.ChatFragment;
import com.ferbook.fragments.FiltersFragment;
import com.ferbook.fragments.HomeFragment;
import com.ferbook.fragments.ProfileFragment;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.TokenProvider;
import com.ferbook.providers.UsersProvider;
import com.ferbook.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    Authprovider mAuthProvider;
    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        mTokenProvider  = new TokenProvider();
        mAuthProvider   = new Authprovider();
        mUsersProvider  = new UsersProvider();

        openFragment(new HomeFragment());
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, HomeActivity.this);
        mUsersProvider.updateOnline(mAuthProvider.getUid(),true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, HomeActivity.this);
        mUsersProvider.updateOnline(mAuthProvider.getUid(),false);
    }

    /*
    métodos borrados en el video 78:

    private void updateOnline(boolean connected) {
        mUsersProvider.updateOnline(mAuthProvider.getUid() ,connected);
    }

        @Override
    protected void onStop() {
        super.onStop();
        updateOnline(false);
    }

    */



    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_home:
                            openFragment(new HomeFragment());
                            return true;
                        case R.id.item_chat:
                            openFragment(new ChatFragment());
                            return true;
                        case R.id.item_profile:
                            openFragment(new ProfileFragment());
                            return true;
                        case R.id.item_filters:
                            openFragment(new FiltersFragment());
                            return true;
                    }
                    return false;
                }
            };

    private void createToken () {
        mTokenProvider.create(mAuthProvider.getUid());
    }

    /*
    @Override
    public void onBackPressed()
    {
    }
    */
}