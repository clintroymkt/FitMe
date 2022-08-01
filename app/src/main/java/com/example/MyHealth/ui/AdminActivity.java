package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.MyHealth.R;
//home screen for the doctor's end. you'll notice certain options being different. The most important is still virtual cunsoult. Do'cs version is different because the first thing he sees is the online calle'rs placeholder screen , then from that screen he can check the notes of the patient first, then go on to start a call with them be it video or voice.Patient records will again be records based on patiets in system

public class AdminActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private ImageButton logoutButton;
    private ImageButton patRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        imageButton = (ImageButton) findViewById(R.id.virtual_Consult);

        logoutButton = (ImageButton) findViewById(R.id.imageMenu);

        patRecords = (ImageButton) findViewById(R.id.imgbtn_patrecords);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCallingAdmin();
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

    public void openCallingAdmin() {
        Intent intent = new Intent(this, CallingAdmin.class);
        startActivity(intent);
    }

    public void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openList() {
        Intent intent = new Intent(this, WaitingList.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}