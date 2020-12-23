package com.ferbook.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ferbook.R;
import com.ferbook.activities.MainActivity;
import com.ferbook.activities.PostActivity;
import com.ferbook.providers.Authprovider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


// * A simple {@link Fragment} subclass.

public class HomeFragment extends Fragment {

    View                    mView;
    FloatingActionButton    mFab;
    Toolbar                 mToolbar;
    Authprovider            mAuthprovider;
    
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView   = inflater.inflate(R.layout.fragment_home, container, false);
        mFab    = mView.findViewById(R.id.fab);
        mToolbar = mView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Publicaciones");

        setHasOptionsMenu(true);
        mAuthprovider = new Authprovider();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost();
            }
        });

        return mView;
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.itemLogout){
            logout();
        }

        return true;
    }

    private void logout() {
        mAuthprovider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);

        //Evitar que desde el siguiente activity se pueda volver atras hacia esta activity con el botón back:
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

    }
}