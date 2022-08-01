package com.example.MyHealth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.MyHealth.R;

public class DocOnlineFrag extends Fragment {
    private ImageButton imageAudio;
    private ImageButton imageVideo;






    public static DocOnlineFrag getInstance(){
        DocOnlineFrag docOnlineFrag = new DocOnlineFrag();
        return docOnlineFrag;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.row_doconline, container,false);

        imageAudio = view.findViewById(R.id.imageAudioMeeting);
        imageVideo = view.findViewById(R.id.imageVideoMeeting);


        imageAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OutgoingMeetingActivity.class);
                startActivity(intent);
            }
        });

        imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OutgoingMeetingActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }


}
