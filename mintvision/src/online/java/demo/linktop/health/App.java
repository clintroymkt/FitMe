package demo.minttihealth.health;

import android.app.Application;

import androidx.databinding.ObservableBoolean;

import com.linktop.MonitorDataTransmissionManager;

import lib.linktop.sev.CssServerApi;

/**
 * Created by ccl on 2017/2/7.
 */

public class App extends Application {

    public final static ObservableBoolean isLogin = new ObservableBoolean(false);
    public final static ObservableBoolean isShowUploadButton = new ObservableBoolean(false);
    //If you are using your own custom Bluetooth connection code，set this parameter to true；
    //If you are using Bluetooth connection code had been built in SDK library,set this parameter to false;
    //You can see in this demo project separately how to build the code when this boolean parameter value is true or false.
    public final static boolean isUseCustomBleDevService = false;

    @Override
    public void onCreate() {
        //true: enable SDK logs,false :disable SDK logs
        MonitorDataTransmissionManager.isDebug(true);
        //在Application初始化CssServerApi，不使用凌拓服务器进行数据同步的话，就不需要在此初始化CssServerAp。
//   accPrefix= "zydb_"： AndroidManifest.xml中设置的appKey&appSecret所对应的账号前缀
//        每组appKey&appSecret都有自己对应的accPrefix,具体什么值由凌拓服务器组决定并定义。
//        务必填对，否则账号系统相关接口无法响应成功。
        CssServerApi.init(this, "zydb_", true);
        isLogin.set(CssServerApi.isLogin());
        super.onCreate();
    }
}
