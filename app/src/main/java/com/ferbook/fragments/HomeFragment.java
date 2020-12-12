package com.ferbook.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ferbook.R;
import com.ferbook.activities.PostActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


// * A simple {@link Fragment} subclass.

public class HomeFragment extends Fragment {

    View                    mView;
    FloatingActionButton    mFab;
    
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView   = inflater.inflate(R.layout.fragment_home, container, false);
        mFab    = mView.findViewById(R.id.fab);
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
}