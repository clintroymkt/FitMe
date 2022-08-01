package com.kl.minttisdkdemo.base;

import android.app.Application;

import com.kl.minttisdk.ble.BleUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by gaoyingjie on 2019/8/9
 * Description:
 */
public class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        initBle(this);
        initBugly();

    }

    private void initBle(Application application){
        BleUtils.getInstance().init(application);
    }
    private void initBugly() {

        CrashReport.initCrashReport(getApplicationContext(), "8ed13e8f2d", true);

    }
}
