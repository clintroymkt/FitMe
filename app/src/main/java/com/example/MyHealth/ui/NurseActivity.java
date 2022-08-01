package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.MyHealth.R;

public class NurseActivity extends AppCompatActivity {
    private ImageButton logoutButton;
    private ImageButton virtconsultButton;
    private ImageButton patRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse);

        logoutButton = (ImageButton) findViewById(R.id.imageMenu);

        virtconsultButton = (ImageButton) findViewById(R.id.virtual_Consult);

        patRecords = (ImageButton) findViewById(R.id.imgbtn_patrecords);


        virtconsultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConsultancy();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openLogin();

            }
        });

        patRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openList();

            }
        });
    }

    public void openConsultancy() {
        Intent intent = new Intent(this, Consultancy.class);
        startActivity(intent);
    }

    public void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openList() {
        Intent intent = new Intent(this, PatientList.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}