package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.MyHealth.R;

public class ComplaintAdd extends AppCompatActivity {
    private Button btnSave;

    AutoCompleteTextView autoCompletecomplaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_add);

        autoCompletecomplaint =  findViewById(R.id.autoCompleteComplaint);

        String[]option = {"Hours", "Days" , "Weeks" , "Months" };
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.option_item2, option);
        autoCompletecomplaint.setText(arrayAdapter.getItem(0).toString(), false);

        autoCompletecomplaint.setAdapter(arrayAdapter);

        btnSave = (Button) findViewById(R.id.btnsaveComplaint);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(ComplaintAdd.this, "Complaint Saved",Toast.LENGTH_SHORT).show();
            }
        });
    }
}