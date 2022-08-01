package demo.minttihealth.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;

import com.linktop.intf.OnAccountResultListener;

import java.util.Locale;

import demo.minttihealth.bean.AccInfo;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.ActivitySetAccountBinding;
import lib.linktop.sev.CssServerApi;

/**
 * Created by ccl on 2017/2/16.
 * <p>
 * 此界面模拟注册新账号和密码重置的的简化demo
 */

public class SetAccountActivity extends BaseActivity implements OnAccountResultListener {

    private boolean isRestPsw;
    private final AccInfo mAccInfo = new AccInfo();
    private final ObservableField<String> btnText = new ObservableField<>("");
    private int clickInt = 0;
    private CssServerApi mCssServerApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySetAccountBinding binding = setBindingContentView(R.layout.activity_set_account);
        setUpActionBar(getString(R.string.register_or_reset_psw), true);
        setUpType(false);
        binding.setBtnText(btnText);
        binding.setAccInfo(mAccInfo);
        binding.radioG.setOnCheckedChangeListener((group, checkedId) -> setUpType(checkedId == R.id.rb_rsp));

        mCssServerApi = new CssServerApi(getApplication(), this);
//        CssServerApi.login()
    }

    @Override
    public void onAccountResultSuccess(int state) {
        Log.e("SetAccountActivity", "onAccountResultSuccess  state = " + state);
        if (clickInt == 0) {//发送验证码
            if (isRestPsw) {
                switch (state) {
                    case 1:
                        toast("解密出错");
                        break;
                    case 0:
                    case 2:
                        toast("验证码请求成功");
                        return;
                    case 3:
                        toast("验证码请求失败,可能该手机号未注册过");
                        break;
                    case 4:
                        toast("旧密码错误");
                        break;
                    case 5:
                        toast("新密码过于简单");
                        break;
                    case 6:
                        toast("验证码请求失败,请求的账号属于社会化平台账号，不需更改密码");
                        break;
                    default:
                        toast(String.format(Locale.getDefault(), "验证码请求失败，错误码：%1$d。", state));
                        break;
                }
            } else {
                switch (state) {
                    case 1:
                        toast("解密出错");
                        break;
                    case 2:
                        toast("账号参数缺失");
                        break;
                    case 3:
                        toast("手机号参数缺失");
                        break;
                    case 4:
                        toast("密码参数缺失");
                        break;
                    case 5:
                        toast("验证码请求成功");
                        break;
                    case 6:
                        toast("请求验证码失败，该手机号被已注册，换别的手机号吧");
                        break;
                    case 7:
                        toast("请求验证码失败，账号格式错误");
                        break;
                    case 8:
                        toast("请求验证码失败，该手机号账号来源错误，非oem账号");
                        break;
                    case 9:
                        toast("请求验证码失败，该手机号账号格式禁止为email");
                        break;
                    default:
                        toast(String.format(Locale.getDefault(), "请求验证码失败，错误码：%1$d。", state));
                        break;
                }
            }
        } else if (clickInt == 1) {//注册或重置密码
            if (isRestPsw) {
                switch (state) {
                    case 0:
                        toast("密码重置成功，需要重新登录");
                        //TODO 清除登录数据
                        finish();
                        return;
                    case 1:
                        toast("api_key无需调用该接口（针对直接重置密码的方式）");
                        break;
                    case 2:
                        toast("解密失败");
                        break;
                    case 3:
                        toast("重置密码失败，参数缺失");
                        break;
                    case 4:
//              重置密码失败，验证码不匹配等原因
                        toast("重置密码失败，验证码不匹配");
                        break;
                    default:
                        toast(String.format(Locale.getDefault(), "重置密码失败，错误码：%1$d。", state));
                        break;
                }
            } else {
                switch (state) {
                    case 0:
                        toast("账号注册成功，需要重新登录");
                        //TODO 清除登录数据
                        finish();
                        return;
                    case 1:
                        toast("开发者key无需验证激活");
                        break;
                    case 2:
                        toast("账号注册失败，验证码不匹配");
                        break;
                    case 3:
                        toast("账号注册失败，账号格式不对");
                        break;
                    case 4:
                        toast("账号注册失败，解密错误");
                        break;
                    case 5:
                        toast("账号注册失败，参数缺失或key错误");
                        break;
                    default:
                        toast(String.format(Locale.getDefault(), "账号注册失败，错误码：%1$d。", state));
                        break;
                }
            }
//        } else if (clickInt == 3) {//验证账号是否已注册
//            switch (state) {
//                case 0://未注册
//                    toast("手机号未注册");
//                    break;
//                case 1://输入格式错误
//                    toast("输入格式错误");
//                    break;
//                case 2://已注册
//                    toast("手机号已注册");
//                    break;
//            }
        }
    }

    @Override
    public void onAccountResultFail(int i) {
        Log.e("SetAccountActivity", "onAccountResultFail  i = " + i);
    }

    @Override
    public void onAccountNetError() {
        Log.e("SetAccountActivity", "onAccountNetError");
    }


    private void setUpType(boolean isRestPsw) {
        this.isRestPsw = isRestPsw;
        final String title = isRestPsw ? getString(R.string.reset_psw) : getString(R.string.register);
        btnText.set(title);
        mAccInfo.clear();
    }

//    /**
//     * 验证账号是否已注册
//     * （第三方该验证无效）
//     */
//    public void clickCheckIsRegister(View v) {
//        clickInt = 3;
//        mCssServerApi.checkIsRegister(mAccInfo.getMobileNo());
//    }

    /**
     * 注册/重置密码
     */
    public void doSetAccount(View v) {
        clickInt = 1;
        if (isRestPsw) {
            mCssServerApi.resetPassword(mAccInfo.getMobileNo(), mAccInfo.getVerificationCode(), mAccInfo.getPassword());
        } else {
            mCssServerApi.register(mAccInfo.getMobileNo(), mAccInfo.getVerificationCode(), mAccInfo.getPassword());
        }
    }

    /**
     * 获取注册/重置密码 的验证码
     */
    public void clickGetVerificationCode(View v) {
        if (TextUtils.isEmpty(mAccInfo.getMobileNo())) {
            Log.e("GetVerificationCode", "MobileNo is Empty.");
        } else {
            clickInt = 0;
            Log.e("GetVerificationCode", "MobileNo is " + mAccInfo.getMobileNo());
            /*
             * type = 0 是请求重置密码的验证码
             * type = 1 是请求注册账号的验证码
             * */
            mCssServerApi.requestVerificationCode(isRestPsw ? 0 : 1, mAccInfo.getMobileNo());
        }
    }

}
