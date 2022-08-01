package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.MyHealth.R;


public class ManagementPlan extends AppCompatActivity {
 private Button btnSave;

 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_plan);

        btnSave = (Button) findViewById(R.id.btnsavePlan);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(ManagementPlan.this, "Plan Updated",Toast.LENGTH_SHORT).show();
            }
        });
    }
}