package com.kl.minttisdkdemo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.clj.fastble.data.BleDevice;
import com.google.android.material.snackbar.Snackbar;
import com.kl.minttisdk.ble.BleUtils;
import com.kl.minttisdk.ble.callback.BleConnectStatusCallback;
import com.kl.minttisdk.ble.callback.BleDataCallback;
import com.kl.minttisdk.ble.callback.FirmInfoCallback;
import com.kl.minttisdk.ble.callback.ScanResultCallback;
import com.kl.minttisdk.ble.constant.EchoMode;
import com.kl.minttisdk.utils.ArrayUtils;
import com.kl.minttisdkdemo.base.BaseActivity;
import com.kl.minttisdkdemo.base.view.AudioWaveView;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MeasureActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MeasureActivity.class.getSimpleName();
    private TextView tvPower;
    private TextView tvHeartRate;
    private TextView tvMode;
    private Button btnConnect;
    private Button btnReceiveData;
    private Button btnModeSwitch;
    private Button btnFirmwateUpdate;
    private String bleAddress;
    private File rootFile;
    private File pcmFile;
    private FileOutputStream fos;
    private AudioTrackPlayer audioTrackPlayer;
//    private Button btnReadPower;
    private AudioWaveView waveView;


    private static final int REQUEST_CODE =1000;
    private static final int REQUEST_PERMISSION=1001;
    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    private MediaRecorder mediaRecorder;

    private Button shareRecBtn;

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
        setContentView(R.layout.activity_measure);
        bleAddress=getIntent().getStringExtra("BLE_ADDRESS");
        initViews();
        BleUtils.getInstance().setBleDataCallback(bleDataCallback);//设置数据回调
        rootFile = new File(Environment.getExternalStorageDirectory(),"MinttiSdkDemo");
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        audioTrackPlayer = new AudioTrackPlayer();

        shareRecBtn = findViewById(R.id.shareRecBtn);

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
                if(ContextCompat.checkSelfPermission(MeasureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                        ContextCompat.checkSelfPermission(MeasureActivity.this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MeasureActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                            ActivityCompat.shouldShowRequestPermissionRationale(MeasureActivity.this,Manifest.permission.RECORD_AUDIO))
                    {
                        toggleButton.setChecked(false);
                        Snackbar.make(relativeLayout,"Permission",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Enable", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        ActivityCompat.requestPermissions(MeasureActivity.this,
                                                new String[]{
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                        Manifest.permission.RECORD_AUDIO

                                                },REQUEST_PERMISSION);

                                    }
                                }).show();
                    }
                    else{
                        ActivityCompat.requestPermissions(MeasureActivity.this,
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

        shareRecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareRecording();
            }
        });

    }



    private void initViews() {
        tvPower = findViewById(R.id.tv_power);
        tvHeartRate = findViewById(R.id.tv_heart_rate);
        tvMode = findViewById(R.id.tv_echo_mode);
        btnConnect = findViewById(R.id.btn_connect);
        btnReceiveData = findViewById(R.id.btn_receive_data);
        btnModeSwitch = findViewById(R.id.btn_echo_mode);
        btnFirmwateUpdate = findViewById(R.id.btn_firmware_update);
        btnFirmwateUpdate.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnReceiveData.setOnClickListener(this);
        btnModeSwitch.setOnClickListener(this);
        tvPower.setText("0%");
        tvHeartRate.setText("0 BPM");
        tvMode.setText(EchoMode.MODE_BELL_ECHO.name());
        waveView = findViewById(R.id.wave_view);
//        btnReadPower = findViewById(R.id.btn_read_power);
//        btnReadPower.setOnClickListener(this);
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
        return mediaProjection.createVirtualDisplay("MeasureActivity", DISPLAY_WIDTH,DISPLAY_HEIGHT,mScreenDensity,
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
                        new StringBuilder("/IbtihajRecord").append(new SimpleDateFormat("dd_MM_yyyy-hh_mm_ss")
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
                                    ActivityCompat.requestPermissions(MeasureActivity.this,
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


    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private void shareRecording() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        File f = new File(videoUrl);


        Intent shareint;
        shareint= new Intent(Intent.ACTION_SEND);
        shareint.setType("video/*");
        shareint.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f));
        shareint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(Intent.createChooser(shareint, "share video"));
    }

    private BleDataCallback bleDataCallback = new BleDataCallback() {
        @Override
        public void onSpkData(short[] spkData) {

        }

        @Override
        public void onMicData(short[] micData) {

        }

        @Override
        public void onResultData(short[] resultData) {
            if (audioTrackPlayer!=null){
                audioTrackPlayer.writeData(resultData);
                waveView.addWaveData(resultData.clone());
            }
            try {
                if (fos!=null){

                    byte[] data = ArrayUtils.short2ByteArray(resultData);
                    fos.write(data,0,data.length);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPowerNotify(int power) {
            tvPower.setText(power+"%");
        }

            @Override
            public void onModeSwitch(EchoMode echoMode) {
                Log.d(TAG,"onModeSwitch = "+echoMode.name());
                tvMode.setText(echoMode.name());
            }

            @Override
            public void onHeartRate(final int heartRate) {
            tvHeartRate.post(new Runnable() {
                @Override
                public void run() {
                    tvHeartRate.setText(heartRate+" BPM");
                }
            });


        }

        @Override
        public void onError(String s) {
            Log.d(TAG,s);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_connect) {
            if (!BleUtils.getInstance().isBluetoothEnabled()) {
                showToast(R.string.ble_enable_bluetooth);
                return;
            }
            if (!btnConnect.isSelected()) {
                btnConnect.setText(R.string.disconnect);
                btnConnect.setSelected(true);
                showProgressDialog(getStr(R.string.ble_connecting), false);
                connectDevice();
            } else {
                btnConnect.setText(R.string.connect);
                btnConnect.setSelected(false);
                disconnect();
            }
        } else if (id == R.id.btn_receive_data) {
            if (!BleUtils.getInstance().isConnected()) {
                showToast(R.string.ble_device_not_connect);
                return;
            }
            if (!btnReceiveData.isSelected()) {
                btnReceiveData.setSelected(true);
                btnReceiveData.setText(R.string.stop_receive_data);
                createFile();
                audioTrackPlayer.play();
                BleUtils.getInstance().enableAudioDataNotify();


            } else {
                btnReceiveData.setSelected(false);
                btnReceiveData.setText(R.string.start_receive_data);
                audioTrackPlayer.stop();
                closeFile();
                BleUtils.getInstance().disableAudioDataNotify();
                waveView.clearDatas();
            }
        } else if (id == R.id.btn_echo_mode) {
            if (!BleUtils.getInstance().isConnected()) {
                showToast(R.string.ble_device_not_connect);
                return;
            }

            if (!btnModeSwitch.isSelected()) {
                btnModeSwitch.setSelected(true);
                BleUtils.getInstance().setEchoModeSwitch(EchoMode.MODE_DIAPHRAGM_ECHO);
            } else {
                btnModeSwitch.setSelected(false);
                BleUtils.getInstance().setEchoModeSwitch(EchoMode.MODE_BELL_ECHO);
            }
        } else if (id == R.id.btn_firmware_update) {
            Intent intent = new Intent(this, FirmwareListActivity.class);
            intent.putExtra("BLE_ADDRESS", bleAddress);
            startActivity(intent);
            //            case R.id.btn_read_power:
//                BleUtils.getInstance().readDevicePower();
//                break;
        }
    }

    private void createFile(){
        try {
            pcmFile = new File(rootFile,System.currentTimeMillis()+".pcm");
            fos = new FileOutputStream(pcmFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void closeFile(){
        try {
            if (fos!=null){
                fos.close();
                fos = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectDevice(){

        if (TextUtils.isEmpty(bleAddress)){
            showToast(R.string.mac_address_empty);
            finish();
            return;
        }
        BleUtils.getInstance().startScan(scanResultCallback);

    }

    private ScanResultCallback scanResultCallback = new ScanResultCallback() {
        @Override
        public void onScanResult(int i, ScanResult scanResult) {
            BluetoothDevice bluetoothDevice = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothDevice = scanResult.getDevice();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                if (bluetoothDevice != null && TextUtils.equals(bleAddress,bluetoothDevice.getAddress())){
                    Log.e(TAG, "onScanResult: 发现设备并连接" );
                    //通过扫描连接，避免部分手机通过mac连接成功问题
                    BleDevice bleDevice = new BleDevice(bluetoothDevice);
                    BleUtils.getInstance().stopScan();
                    BleUtils.getInstance().connect(bleDevice,bleConnectStatusCallback);
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> list) {

        }

        @Override
        public void onScanFailed(int i) {

        }
    };

    private void disconnect(){
        BleUtils.getInstance().disconnect();
    }


    BleConnectStatusCallback bleConnectStatusCallback = new BleConnectStatusCallback() {
        @Override
        public void onStartConnect() {
            //showProgressDialog(getStr(R.string.ble_connecting),false);
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, String s) {
            disProgressDialog();
            showToast(R.string.ble_connect_failed);
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int status) {
            progressDialog.setMessage(getStr(R.string.ble_update_params));


        }

        @Override
        public void onUpdateParamsSuccess() {//参数更新成功后再操作
            disProgressDialog();
            BleUtils.getInstance().readDevicePower();
        }

        @Override
        public void onUpdateParamsFail() {
            disProgressDialog();
            showToast(R.string.ble_update_params_failed);

        }

        @Override
        public void onDisConnected(boolean isActive, BluetoothDevice bluetoothDevice, BluetoothGatt bluetoothGatt, int status) {
            if (!isActive){//是否主动断开连接
                showToast(R.string.disconnect);

            }
            Log.e(TAG, "onDisConnected: "+isActive);
            if (btnConnect.isSelected()){
                btnConnect.setText(R.string.connect);
                btnConnect.setSelected(false);
            }

            if (btnReceiveData.isSelected()){
                btnReceiveData.setSelected(false);
                btnReceiveData.setText(R.string.start_receive_data);
            }

            if (btnModeSwitch.isSelected()){
                btnModeSwitch.setSelected(false);
            }
            tvPower.setText("0%");
            tvHeartRate.setText("0 BPM");
            tvMode.setText(EchoMode.MODE_BELL_ECHO.name());

        }
    };

    @Override
    public void onBackPressed() {
        if (BleUtils.getInstance().isConnected()){
            showToast(R.string.disconnect_first);
            return;
        }
        super.onBackPressed();
    }
}
