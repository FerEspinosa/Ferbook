package com.ferbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.ferbook.R;

public class FiltersActivity extends AppCompatActivity {

    String mExtraCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mExtraCategory = getIntent().getStringExtra("category");
        Toast.makeText(this, "La categoria seleccionada es: "+mExtraCategory, Toast.LENGTH_SHORT).show();
    }
}