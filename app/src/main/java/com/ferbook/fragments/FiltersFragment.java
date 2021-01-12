package com.ferbook.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ferbook.R;
import com.ferbook.activities.FiltersActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersFragment extends Fragment {

    View mView;

    CardView mCV_ps4;
    CardView mCV_xbox;
    CardView mCV_nintendo;
    CardView mCV_pc;

    public FiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filters, container, false);

        mCV_ps4 = mView.findViewById(R.id.cardView_ps4);
        mCV_ps4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("PS4");
            }
        });

        mCV_xbox = mView.findViewById(R.id.cardView_xbox);
        mCV_xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("XBOX");
            }
        });

        mCV_nintendo = mView.findViewById(R.id.cardView_nintendo);
        mCV_nintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("NINTENDO");
            }
        });

        mCV_pc = mView.findViewById(R.id.cardView_pc);
       mCV_pc.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               goToFilterActivity("PC");
           }
       });

        return mView;
    }

    private void goToFilterActivity(String category) {
        Intent intent = new Intent(getContext(), FiltersActivity.class);

        intent.putExtra("category", category);
        startActivity(intent);
    }
}