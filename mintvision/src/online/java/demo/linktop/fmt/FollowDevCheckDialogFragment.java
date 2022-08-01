package demo.minttihealth.fmt;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.linktop.MonitorDataTransmissionManager;

import demo.minttihealth.health.BR;
import demo.minttihealth.health.R;
import demo.minttihealth.utils.GetVerificationCodeUtil;
import lib.linktop.common.CssSubscriber;
import lib.linktop.sev.CssServerApi;
import lib.linktop.sev.HmLoadDataTool;

/**
 * Created by ccl on 2016/11/26.
 */
public class FollowDevCheckDialogFragment extends BaseDialogFragment
        implements View.OnClickListener,
        TextWatcher {

    private String[] info;
    private GetVerificationCodeUtil verificationCodeUtil;
    private String verificationCode = "";


    public FollowDevCheckDialogFragment() {
        super();
    }

    public void setInfo(String[] info) {
        this.info = info;
    }

    @Override
    protected boolean isDataBinding() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_dialog_follow_dev_check;
    }

    @Override
    protected void onInit() {
        setDialogTitle("关注设备");
        verificationCodeUtil = new GetVerificationCodeUtil(getResources());
        mBinding.setVariable(BR.acc, info[2]);
        mBinding.setVariable(BR.verification, verificationCodeUtil);
        mBinding.setVariable(BR.verificationCodeListener, this);
        mBinding.setVariable(BR.textChangeListener, this);
        onClick(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPositiveButton() != null) {
            getPositiveButton().setEnabled(verificationCode.length() == 4);
        }
    }

    @Override
    public void onDestroy() {
        if (verificationCodeUtil != null) verificationCodeUtil.destroy();
        super.onDestroy();
    }

    @Override
    protected int getNeutralText() {
        return 0;
    }

    @Override
    protected int getNegativeText() {
        return android.R.string.cancel;
    }

    @Override
    protected View.OnClickListener getNegativeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonitorDataTransmissionManager.getInstance().disConnectBle();
                HmLoadDataTool.getInstance().destroyCssSocket();
                dismiss();
            }
        };
    }

    @Override
    protected View.OnClickListener getPositiveListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CssServerApi.followDevCheck(info, verificationCode)
                        .subscribe(new CssSubscriber<Integer>() {
                            @Override
                            public void onNextRequestSuccess(Integer state) {
                                switch (state) {
                                    case 0://关注成功
                                        toast("关注成功");
                                        setAlias();
                                        break;
                                    case 1://子账号邮箱格式
                                        Log.e("followDevCheck", "子账号邮箱格式");
                                        break;
                                    case 2://主账号邮箱格式
                                        Log.e("followDevCheck", "主账号邮箱格式");
                                        break;
                                    case 4://设备不为主账号所有
                                        toast("设备ID--" + info[0] + "不为手机号用户--" + info[2] + "所有.");
                                        break;
                                    case 3://关注验证码格式不对
                                    case 5://验证码不一致
                                        toast("验证码不匹配");
                                        break;
                                }
                            }

                            @Override
                            public void onNextRequestFailed(int status) {

                            }

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

//                        .subscribe(new Subscriber<Object>() {
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                UToast.show(getContext(), e.getMessage());
//                            }
//
//                            @Override
//                            public void onNext(Object o) {
//                                UToast.show(getContext(), R.string.follow_success);
//                                String userName = AccInfo.getInstance().getUserName();
//                                if (TextUtils.isEmpty(userName)) {
//                                    if (listener != null) {
//                                        listener.onUpdateSuccess();
//                                        dismiss();
//                                    }
//                                } else {
//                                    setAlias(userName);
//                                }
//                            }
//                        });

            }
        };
    }

    @Override
    public void onClick(View view) {
        verificationCodeUtil.setAccTextEnable(false);
        verificationCodeUtil.startRunning();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        verificationCode = charSequence.toString();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (getPositiveButton() != null) {
            getPositiveButton().setEnabled(verificationCode.length() == 4);
        }
    }

    /**
     * 设置关注账号对于设备的别名（alias）
     * */
    private void setAlias() {
        /*
        * param alias String 类型，无论中英文，字符串长度均不能超过15， 禁止包含英文冒号特殊字符
        *             允许空字符串（""）但不建议，更不可为对象空（null）;
        *             接口内不对参数的合理性进行判断，故建议在调用接口前对参数‘alias’进行判断，满足条件再调用接口。
        *             通常取当前用户昵称设进行设置，所以以上条件应同时适用用户昵称设置。
        * */
        final String alias = "关注者";
        CssServerApi.setAlias(info[0], alias)
                .subscribe(new CssSubscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNextRequestSuccess(Object o) {
                        Log.e("setAlias","设置成功");
                        listener.onUpdateSuccess();
                        dismiss();
                    }

                    @Override
                    public void onNextRequestFailed(int status) {
                        Log.e("setAlias","设置失败");
                    }
                });
    }
}
