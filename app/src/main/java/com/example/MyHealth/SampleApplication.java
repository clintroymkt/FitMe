package com.example.MyHealth;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.crrepa.ble.CRPBleClient;
import com.kl.minttisdk.ble.BleUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class SampleApplication extends Application {

    private CRPBleClient mBleClient;

    public static CRPBleClient getBleClient(Context context) {
        SampleApplication application = (SampleApplication) context.getApplicationContext();
        return application.mBleClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBleClient = CRPBleClient.create(this);

        initBle(this);
        initBugly();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public final static boolean isUseCustomBleDevService = false;





    private void initBle(Application application) {
        BleUtils.getInstance().init(application);
    }

    private void initBugly() {

        CrashReport.initCrashReport(getApplicationContext(), "8ed13e8f2d", true);

    }
}
