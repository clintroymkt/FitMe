package com.example.androidscreenrecorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenrecorderActivity extends AppCompatActivity {
private static final int REQUEST_CODE =1000;
private static final int REQUEST_PERMISSION=1001;
private static final SparseIntArray ORIENTATION = new SparseIntArray();

private MediaProjectionManager mediaProjectionManager;
private MediaProjection mediaProjection;
private VirtualDisplay virtualDisplay;
private MediaProjectionCallback mediaProjectionCallback;
private MediaRecorder mediaRecorder;

    private int mScreenDensity;
    private static  int DISPLAY_WIDTH =720 ;
    private static  int DISPLAY_HEIGHT =1280 ;

    static {
        ORIENTATION.append(Surface.ROTATION_0,90);
        ORIENTATION.append(Surface.ROTATION_90,0);
        ORIENTATION.append(Surface.ROTATION_180,270);
        ORIENTATION.append(Surface.ROTATION_270,180);
    }
    private ToggleButton toggleButton;
    private RelativeLayout relativeLayout;
    private VideoView videoView;
    private String videoUrl="";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenrecorder);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;


        DISPLAY_HEIGHT = metrics.heightPixels;
        DISPLAY_WIDTH = metrics.widthPixels;

        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        videoView = findViewById(R.id.videoView);
        relativeLayout = findViewById(R.id.rootLayout);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ScreenrecorderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                ContextCompat.checkSelfPermission(ScreenrecorderActivity.this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
                {
                 if(ActivityCompat.shouldShowRequestPermissionRationale(ScreenrecorderActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                         ActivityCompat.shouldShowRequestPermissionRationale(ScreenrecorderActivity.this,Manifest.permission.RECORD_AUDIO))
                 {
                     toggleButton.setChecked(false);
                     Snackbar.make(relativeLayout,"Permission",Snackbar.LENGTH_INDEFINITE)
                             .setAction("Enable", new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v)
                                 {
                                     ActivityCompat.requestPermissions(ScreenrecorderActivity.this,
                                             new String[]{
                                                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                     Manifest.permission.RECORD_AUDIO

                                             },REQUEST_PERMISSION);

                                 }
                             }).show();
                 }
                 else{
                     ActivityCompat.requestPermissions(ScreenrecorderActivity.this,
                             new String[]{
                                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                     Manifest.permission.RECORD_AUDIO

                             },REQUEST_PERMISSION);

                 }
                }
                else{
                    toggleScreenShare(v);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode!= REQUEST_CODE){
            Toast.makeText(this, "Unk error", Toast.LENGTH_SHORT).show();
        return ;
        }
        if(resultCode!=RESULT_OK){
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);
            return;
        }

        mediaProjectionCallback = new MediaProjectionCallback();
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,data);
        mediaProjection.registerCallback(mediaProjectionCallback,null);
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionCallback extends MediaProjection.Callback {

        public void onStop(){
            if(toggleButton.isChecked()){
                toggleButton.setChecked(false);
                mediaRecorder.stop();
                mediaRecorder.reset();

            }
            mediaProjection = null;
            stopRecordScreen();
            super.onStop();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void stopRecordScreen() {

        if(virtualDisplay==null){
            return;
        }
        virtualDisplay.release();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            destroyMediaProjection();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void destroyMediaProjection() {
        if(mediaProjection!=null){
        mediaProjection.unregisterCallback(mediaProjectionCallback);
        mediaProjection.stop();
        mediaProjection = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void toggleScreenShare(View v){
        if (((ToggleButton) v).isChecked() )
        {
            initRecorder();
            recordScreen();
        }
        else{
            mediaRecorder.stop();
            mediaRecorder.reset();
            stopRecordScreen();
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(videoUrl));
            videoView.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void recordScreen() {
        if(mediaProjection==null){
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),REQUEST_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
    return mediaProjection.createVirtualDisplay("ScreenrecorderActivity", DISPLAY_WIDTH,DISPLAY_HEIGHT,mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mediaRecorder.getSurface(),null,null);

    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void initRecorder() {
        try{
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                videoUrl= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
                        new StringBuilder("/TransmedRecord").append(new SimpleDateFormat("dd_MM_yyyy-hh_mm_ss")
                                .format(new Date())).append(".mp4").toString();
            }


            mediaRecorder.setOutputFile(videoUrl);
            mediaRecorder.setVideoSize(DISPLAY_WIDTH,DISPLAY_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mediaRecorder.setVideoEncodingBitRate(512*1000);
            }
            mediaRecorder.setVideoFrameRate(30);


            int rotation = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                rotation = getWindowManager().getDefaultDisplay().getRotation();
            }
            int orientation = ORIENTATION.get(rotation + 90);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                mediaRecorder.setOrientationHint(orientation);
            }
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
                if((grantResults.length>0)&& (grantResults[0]+grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                    toggleScreenShare(toggleButton);
                }
                else{
                    toggleButton.setChecked(false);
                    Snackbar.make(relativeLayout,"Permission",Snackbar.LENGTH_INDEFINITE)
                            .setAction("Enable", new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    ActivityCompat.requestPermissions(ScreenrecorderActivity.this,
                                            new String[]{
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.RECORD_AUDIO

                                            },REQUEST_PERMISSION);

                                }
                            }).show();
                }
                return;
        }
    }
}
