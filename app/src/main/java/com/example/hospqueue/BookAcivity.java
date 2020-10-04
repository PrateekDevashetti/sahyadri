package com.example.hospqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class BookAcivity extends AppCompatActivity {

    AutoCompleteTextView doctor;
    private static final String[] DOCTORS = new String[] {
            "Neurologist", "Physiologist", "ENT", "Pediatrician"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_acivity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, DOCTORS);
        doctor=findViewById(R.id.etdoctor);
        doctor.setAdapter(adapter);
    }
}