package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.MyHealth.R;


//calling placeholder screen. the idea is it loads info from the db on who's online and the example I used was based on firebase.
public class Calling extends AppCompatActivity {

    private ImageButton imageAudio;
    private ImageButton imageVideo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        imageAudio = (ImageButton) findViewById(R.id.imageAudioMeeting);
        imageVideo = (ImageButton) findViewById(R.id.imageVideoMeeting);

        imageAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOutgoingCall();
            }
        });

        imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOutgoingCall();
            }
        });



    }

    public void openOutgoingCall() {
        Intent intent = new Intent(this, OutgoingMeetingActivity.class);
        startActivity(intent);
    }
}