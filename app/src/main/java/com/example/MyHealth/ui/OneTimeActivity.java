package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.MyHealth.R;

public class OneTimeActivity extends AppCompatActivity {

    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time);

        submitBtn = (Button) findViewById(R.id.submitOTP);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocList();
            }
        });
    }

    private void openDocList() {
        Intent intent = new Intent(this, DocList.class);
        startActivity(intent);
        finish();
    }
}