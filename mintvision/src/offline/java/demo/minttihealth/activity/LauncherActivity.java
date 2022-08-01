package demo.minttihealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import demo.minttihealth.health.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by ccl on 2017/3/22.
 * <p>
 * APP启动页
 */
public class LauncherActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luancher);

        doJump();
    }

    /**
     * 执行跳转页面动作
     */
    private void doJump() {
        final Observable<Long> timer = Observable.timer(2300L, TimeUnit.MILLISECONDS);
        Observable<Boolean> observable = timer.map(aLong -> aLong == 0);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {

                    private String message;
                    private boolean isSuccess = false;

                    @Override
                    public void onCompleted() {
                        if (isSuccess) {
                            Intent intent = new Intent(getBaseContext(), MintActivity.class);
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
