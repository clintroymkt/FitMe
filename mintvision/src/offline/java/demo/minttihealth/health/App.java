package demo.minttihealth.health;

import android.app.Application;

import com.linktop.MonitorDataTransmissionManager;

/**
 * Created by ccl on 2017/2/7.
 */

public class App extends Application {

    //If you are using your own custom Bluetooth connection code，set this parameter to true；
    //If you are using Bluetooth connection code had been built in SDK library,set this parameter to false;
    //You can see in this demo project separately how to build the code when this boolean parameter value is true or false.
    public final static boolean isUseCustomBleDevService = false;

    @Override
    public void onCreate() {
        //true: enable SDK logs,false :disable SDK logs
        MonitorDataTransmissionManager.isDebug(true);
        super.onCreate();
    }
}
