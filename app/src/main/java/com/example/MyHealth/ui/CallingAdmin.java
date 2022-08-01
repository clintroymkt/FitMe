package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.MyHealth.R;

//doc's calling screen, placeholder will show online users then he can click their notes first and go on to start the consult or start that consult immediatel.
public class CallingAdmin extends AppCompatActivity {
    private ImageButton imageButton;
    private ImageButton imageAudio;
    private ImageButton imageVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_admin);

        imageButton = (ImageButton) findViewById(R.id.consultNotesBtn);
        imageAudio = (ImageButton) findViewById(R.id.imageAudioMeeting);
        imageVideo = (ImageButton) findViewById(R.id.imageVideoMeeting);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConsultancyAdmin();
            }
        });

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

    public void openConsultancyAdmin() {
        Intent intent = new Intent(this, ConsultancyAdmin.class);
        startActivity(intent);
    }

    public void openOutgoingCall() {
        Intent intent = new Intent(this, OutgoingMeetingActivity.class);
        startActivity(intent);
    }



}