package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.berry_med.monitordemo.activity.MonitorActivity;
import com.example.MyHealth.R;
import com.kl.minttisdkdemo.StethoActivity;

import demo.minttihealth.activity.MintActivity;


public class MachineActivity extends AppCompatActivity {
    private ImageButton stethoButton;
    private ImageButton multiButton;
    private ImageButton mintButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);

        stethoButton = (ImageButton) findViewById(R.id.imageStethoscope);

        multiButton = (ImageButton) findViewById(R.id.imageMutlipara);

        mintButton = (ImageButton) findViewById(R.id.imageMint);



        multiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMultiP();
            }
        });

        stethoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStethoscope();
            }
        });

        mintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMint();
            }
        });


    }

    private void openMint() {
        Intent intent = new Intent(this, MintActivity.class);
        startActivity(intent);
    }

    private void openMultiP() {
        Intent intent = new Intent(this, MonitorActivity.class);
        startActivity(intent);
    }

    public void openStethoscope() {
        Intent intent = new Intent(this, StethoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
       // Intent intent = new Intent(this, OnlineActivity.class);
      //  startActivity(intent);
        super.onBackPressed();
    }
}