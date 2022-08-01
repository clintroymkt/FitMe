package demo.minttihealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import demo.minttihealth.health.R;
import lib.linktop.sev.CssServerApi;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by ccl on 2017/3/22.
 * <p>
 * APP启动页
 * <p>
 * 获取服务器动态IP和端口。
 * <p>
 * 正式服务器需要调用{@link CssServerApi#checkHostPorts},确认成功才能进行服务器交互。
 * 建议写在第一屏（即启动页）， 成功则进入主界面，不成功则提示用户重试。
 * 切記不可在不成功的情況下調用服務器相關接口，雖然大部分接口仍然可用，但
 * 可能會出現部分接口調用不成功的情況，這是由於默認IP或端口與服務器實際IP和端口匹配不準確造成的。
 * <p>
 * 测试服务器不需要调用{@link CssServerApi#checkHostPorts}。
 * <p>
 * 不打算使用Linktop服務器作為數據同步手段的直接忽視就行。
 */
public class LauncherActivity extends BaseActivity {

    /**
     * 是否正式服务器 ，true：需要checkHostPorts，否：不需要
     * 切換正式測試服務器IP時記得在此切換Boolean值。
     */
    private final static boolean isReleaseServer = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luancher);

        doJump();
    }

    /**
     * 执行跳转页面动作
     * */
    private void doJump() {
        final Observable<Boolean> observable;
        final Observable<Long> timer = Observable.timer(2300L, TimeUnit.MILLISECONDS);
        if (isReleaseServer) {
            //2秒计时的同时确认服务器IP和端口，若2秒内确认服务器IP和端口完成，则等待到2秒时执行下一步。成功跳转界面并关掉本界面，失败则提示
            //若无法2秒确认服务器IP和端口完成，则等到何时确认完成，何时执行下一步。
            //10秒无法响应任何数据，认为是超时。
            final Observable<Boolean> checkHostPorts = CssServerApi.checkHostPorts(getApplication());
            final Observable<Boolean> error = Observable.error(new Throwable("Time out"));
            observable = Observable.zip(timer, checkHostPorts,
                    (aLong, aBoolean) -> aLong == 0L && aBoolean)
                    .timeout(10000L, TimeUnit.MILLISECONDS, error);
        } else {
            observable = timer.map(aLong -> aLong == 0);
        }
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {

                    private String message;
                    private boolean isSuccess = false;

                    @Override
                    public void onCompleted() {
                        if (isSuccess) {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            toast(TextUtils.isEmpty(message) ? "IP确认失败" : message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        message = e.getMessage();
                        onCompleted();
                    }

                    @Override
                    public void onNext(Boolean o) {
                        isSuccess = o;
                    }
                });
    }
}
