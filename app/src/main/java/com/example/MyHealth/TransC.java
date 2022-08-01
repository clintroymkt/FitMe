package com.example.MyHealth;

import android.app.Application;

import com.kl.minttisdk.ble.BleUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class TransC extends Application {
    public final static boolean isUseCustomBleDevService = false;

    @Override
    public void onCreate() {



        super.onCreate();


        initBle(this);
        initBugly();


    }

    private void initBle(Application application) {
        BleUtils.getInstance().init(application);
    }

    private void initBugly() {

        CrashReport.initCrashReport(getApplicationContext(), "8ed13e8f2d", true);

    }
}
