package demo.minttihealth.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import demo.minttihealth.bean.AccInfo;
import demo.minttihealth.health.App;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.ActivityLoginBinding;
import demo.minttihealth.utils.ActivityHelper;
import lib.linktop.common.CssSubscriber;
import lib.linktop.sev.CssServerApi;

/**
 * Created by ccl on 2017/2/15.
 */
public class LoginActivity extends BaseActivity {

    private final AccInfo mAccInfo = new AccInfo();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = setBindingContentView(R.layout.activity_login);
        setUpActionBar(getString(R.string.login), true);
        binding.setAccInfo(mAccInfo);

    }

    /**
     * 登录点击事件(不向服务器注册推送的版本)
     */
    public void doLogin(View v) {
        CssServerApi.login(mAccInfo.getMobileNo(), mAccInfo.getPassword())
                .subscribe(new CssSubscriber<String>() {

                    @Override
                    public void onNextRequestSuccess(String token) {
                        //token 当前登录的用户的完整账号
                        toast(R.string.login_success);
                        App.isLogin.set(true);
                        finish();
                    }

                    @Override
                    public void onNextRequestFailed(int status) {
                        switch (status) {
                            case -1:
                                toast(R.string.network_connection_error);
                                break;
                            case 401:
                                toast(R.string.incorrect_acc_or_psw);
                                break;
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    /**
     * 登录点击事件(有向服务器注册推送的版本)
     * 使用该API前请根据使用的推送SDK文档在工程集成好推送模块。
     */
//    public void doLoginWithPush(View v) {
//        CssServerApi.login(mAccInfo.getMobileNo(), mAccInfo.getPassword())
//                .flatMap(new Func1<ResultPair<String>, Observable<Integer>>() {
//                    @Override
//                    public Observable<Integer> call(ResultPair<String> resultPair) {
//                        //判断是否登录成功，若登录成功，向服务器发起推送注册。
//                        final Integer first = resultPair.first;
//                        if (200 == first) {
//                            //以极光推送为例。
//                            final String jPushAppKey = "XXXXXXXXXXXXX";
//                            final String registrationID = "YYYYYYYYYYYYYYYYYY";//调用Jpush API获取的ID。
//                            return CssServerApi.registerPush(null, PushType.J_PUSH, jPushAppKey, registrationID);
//                        } else return Observable.just(first);
//                    }
//                })
//                .subscribe(new Subscriber<Integer>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Integer status) {
//                        switch (status) {
//                            case -1:
//                                toast("网络连接出错");
//                                break;
//                            case 200:
//                                toast("登录成功");
//                                App.isLogin.set(true);
//                                finish();
//                                break;
//                            case 401:
//                                toast("账号或密码错误");
//                                break;
//                            case 402:
//                                toast("推送注册失败");
//                                break;
//                        }
//                    }
//                });
//    }
    public void doRegisterOrRestPsw(View v) {
        ActivityHelper.launcher(this, SetAccountActivity.class);
    }
}
