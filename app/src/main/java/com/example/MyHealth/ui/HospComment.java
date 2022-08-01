package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.MyHealth.R;

public class HospComment extends AppCompatActivity {
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosp_comment);

        btnSave = (Button) findViewById(R.id.btnsaveHosp);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(HospComment.this, "Hospital Saved",Toast.LENGTH_SHORT).show();
            }
        });
    }
}