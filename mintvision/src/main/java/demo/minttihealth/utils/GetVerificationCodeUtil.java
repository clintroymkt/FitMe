package demo.minttihealth.utils;

import android.content.res.Resources;
import androidx.databinding.BaseObservable;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import demo.minttihealth.health.R;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by ccl on 2016/8/30.
 * GetVerificationCodeUtil
 */

public class GetVerificationCodeUtil extends BaseObservable {

    private final static int totalSec = 90;
    private final Resources res;
    private int nowSec;
    private boolean isRunning;
    private boolean btnEnable;
    private boolean accTextEnable;
    private String btnText;
    private String account = "";
    private Subscription subscribe;

    public GetVerificationCodeUtil(Resources res) {
        this.res = res;
        btnText = res.getString(R.string.get_verification_code);
    }

    public void startRunning() {
        subscribe = countdown(totalSec)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        setBtnEnable(false);
                        setBtnText(String.format("%1$s s", totalSec));
                        isRunning = true;
                        Log.e("post", "count down start...");
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        setBtnEnable(true);
                        setBtnText(res.getString(R.string.get_verification_code));
                        setAccTextEnable(true);
                        isRunning = false;
                        Log.e("post", "count down completed...");
                    }

                    @Override
                    public void onError(Throwable e) {
                        isRunning = false;
                        Log.e("post", "count down error," + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        nowSec = integer;
                        setBtnText(String.format("%1$s s", integer));
                        Log.e("post", "count down running..." + integer);
                    }
                });
    }

    public void create() {
        if (isRunning) setBtnText(String.format("%1$s s", nowSec));
        setBtnEnable(isRunning);
        setAccTextEnable(!isRunning);
    }

    public void destroy() {
        if (subscribe != null) subscribe.unsubscribe();
    }

    @NonNull
    private Observable<Integer> countdown(int time) {
        if (time < 0) time = 0;
        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Long, Integer>() {
                    @Override
                    public Integer call(Long increaseTime) {
                        return countTime - increaseTime.intValue();
                    }
                })
                .take(countTime + 2);
    }

    public boolean isBtnEnable() {
        return btnEnable;
    }

    public void setBtnEnable(boolean btnEnable) {
        this.btnEnable = btnEnable;
        notifyChange();
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
        notifyChange();
    }

    public void setAccTextEnable(boolean accTextEnable) {
        this.accTextEnable = accTextEnable;
        notifyChange();
    }

    public boolean isAccTextEnable() {
        return accTextEnable;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
