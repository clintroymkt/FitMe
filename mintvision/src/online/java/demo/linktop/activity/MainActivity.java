package demo.minttihealth.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import demo.minttihealth.health.App;
import demo.minttihealth.health.BuildConfig;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.ActivityMainBinding;
import demo.minttihealth.utils.ActivityHelper;
import lib.linktop.sev.CssServerApi;

/**
 * Created by ccl on 2017/2/1.
 * 凌拓（LINKTOP）健康设备SDK
 * <p>
 * Demo中关于SDK的使用、配置要严格按照Demo中的方式使用，配置
 * 本人也是比较懒，Demo的有些逻辑都是大概能用就行，所以只是提供一种如何使用的SDK方式
 * 并非最终解决方案，也不是最佳的使用方案。
 * 但SDK所提供的接口应该能满足具体项目开发了
 * 开发者可以参考demo，让SDK能够更好配合具体项目的开发
 * <p>
 * SDK所提供的接口也许还存在瑕疵，SDK内部也可能存在逻辑处理不周到的地方，
 * 若发现有这类问题，可以反馈。
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = setBindingContentView(R.layout.activity_main);
        setUpActionBar(getString(R.string.app_name), false);
        binding.setIsLogin(App.isLogin);
        binding.setVerInfo(getAppVerInfo());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 未登录，点击登录
     * 设备绑定用，以便数据同步到服务器
     * ps：同步个测量数据而已还要绑定设备，麻烦的要死，所以一般不建议沿用我司服务器框架，
     * but，若你们服务器端已沿用了我司的服务器框架，那还是乖乖点进来登录，然后使用
     * SDK提供的相关方法吧。
     */
    public void clickLogin(View v) {
        if (App.isLogin.get()) {
            CssServerApi.logout();
            App.isLogin.set(false);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /*
     * 打开体温计页面
     * */
    public void clickOpenThermometerPage(View v) {
        ActivityHelper.launcher(this, ThermometerActivity.class);
    }

    /*
     * 打开健康检测仪页面
     * */
    public void clickOpenHealthMonitorPage(View v) {
        ActivityHelper.launcher(this, HealthMonitorActivity.class);
    }

    private String getAppVerInfo() {
        try {
            PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            long verCode;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                verCode = pkgInfo.getLongVersionCode();
            } else {
                verCode = pkgInfo.versionCode;
            }
            String verName = pkgInfo.versionName;
            return BuildConfig.FLAVOR + " " + verName + "(" + verCode + ")";
        } catch (PackageManager.NameNotFoundException ignored) {
            return "-";
        }
    }
}
