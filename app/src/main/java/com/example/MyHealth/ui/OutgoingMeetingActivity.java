package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.MyHealth.R;

public class OutgoingMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_meeting);

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);

        ImageView imageStopInvitation = findViewById(R.id.imageStopCall);
        imageStopInvitation.setOnClickListener(v -> onBackPressed());
    }
}