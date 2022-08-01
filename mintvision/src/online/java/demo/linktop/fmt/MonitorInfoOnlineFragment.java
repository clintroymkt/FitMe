package demo.minttihealth.fmt;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;
import com.linktop.constant.DeviceInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import demo.minttihealth.activity.ShowDownloadDataActivity;
import demo.minttihealth.adapter.BindDevListAdapter;
import demo.minttihealth.health.App;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentMonitorInfoOnlineBinding;
import demo.minttihealth.utils.ActivityHelper;
import demo.minttihealth.utils.AlertDialogBuilder;
import lib.linktop.common.CssSubscriber;
import lib.linktop.common.LogU;
import lib.linktop.common.ResultPair;
import lib.linktop.intf.OnCssSocketRunningListener;
import lib.linktop.obj.Device;
import lib.linktop.obj.LoadBean;
import lib.linktop.obj.Member;
import lib.linktop.sev.CssServerApi;
import lib.linktop.sev.HmLoadDataTool;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ccl on 2017/2/7.
 * MonitorInfoFragment
 * 健康检测仪基本操作Demo页面（除测量外）
 */

public class MonitorInfoOnlineFragment extends MonitorInfoFragment {


    private final ObservableBoolean isLogin = App.isLogin;
    private final ObservableInt isDevBind = new ObservableInt(0);

    private BindDevListAdapter mAdapter;

    private Subscription subscription;

    public MonitorInfoOnlineFragment() {
    }

    @Override
    public int getTitle() {
        return R.string.basic_info;
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_monitor_info_online;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentMonitorInfoOnlineBinding binding = (FragmentMonitorInfoOnlineBinding) viewDataBinding;
        super.onViewBindingCreated(binding.fragmentMonitorInfo, savedInstanceState);
        binding.setFrag(this);
        binding.setIsLogin(isLogin);
        binding.setIsBind(isDevBind);
        if (!App.isUseCustomBleDevService) {
            onBleState(MonitorDataTransmissionManager.getInstance().getBleState());
        }
        if (isLogin.get()) {
            mAdapter = new BindDevListAdapter(getContext(), id);
            binding.setRecyclerAdapter(mAdapter);
            getDevList(false);
//            CssServerApi.checkHostPorts(getActivity().getApplication())
//                    .subscribe(new Subscriber<Boolean>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onNext(Boolean isChecked) {
//                            if (isChecked) {
//                                getDevList(false);
//                            }
//                        }
//                    });
        }
    }

    @Override
    public void onDestroy() {
        if (isLogin.get()) {
            //该长连接，设备连接蓝牙时启动，设备断开蓝牙停止。
            HmLoadDataTool.getInstance().destroyCssSocket();
            isDevBind.set(0);
        }
        App.isShowUploadButton.set(false);
        super.onDestroy();
    }

    @Override
    public void onBleState(int bleState) {
        switch (bleState) {
            case BluetoothState.BLE_CLOSED:
                isDevBind.set(0);
                break;
            case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                try {
                    isDevBind.set(0);
                } catch (Exception ignored) {
                }
                break;
        }
        super.onBleState(bleState);
    }

    /**
     * 定时定次向服务器请求设备列表，以确认设备是否绑定。
     * 循环几次，每次间隔多长时间，自行讨论确定。
     */
    private void loopCheckDevIsBind() {
        subscription = Observable.interval(1, 5, TimeUnit.SECONDS)
                .take(5)
                .flatMap((Func1<Long, Observable<ResultPair<List<Device>>>>) aLong -> {
                    aLong++;
                    LogU.e("CSSHttpUtil", "第:" + aLong + "次向服务器确认是否绑定");
                    return CssServerApi.getDevList();
                })
                .flatMap((Func1<ResultPair<List<Device>>, Observable<Device>>) listResultPair -> {
                    if (listResultPair.first == 200) {
                        final List<Device> second = listResultPair.second;
                        return Observable.from(second);
                    }
                    return null;
                })
                .filter(device -> null != device && device.getDevId().equals(id.get()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Device>() {
                    private boolean isBindSuccess = false;

                    @Override
                    public void onCompleted() {
                        if (!isBindSuccess) {
                            toast("绑定失败");
                            MonitorDataTransmissionManager.getInstance().disConnectBle();
                            HmLoadDataTool.getInstance().destroyCssSocket();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Device device) {
                        isBindSuccess = device != null;
                        if (isBindSuccess) {
                            id.set(device.getDevId());
                            isDevBind.set(1);
                            mAdapter.addItem(device);
                            toast("绑定成功");
                            subscription.unsubscribe();
                        }
                    }
                });

    }

    private void getDevList(final boolean isToast) {
        CssServerApi.getDevList()
                .subscribe(new CssSubscriber<List<Device>>() {
                    @Override
                    public void onNextRequestSuccess(List<Device> devices) {
                        mAdapter.clearItems();
                        mAdapter.addItems(devices);
                        checkDevIsBind(devices, isToast);
                    }

                    @Override
                    public void onNextRequestFailed(int status) {
                        switch (status) {
                            case -1:
                                toast("网络断开了，检查网络");
                                break;
                            default:
                                toast("请求失败");
                                break;
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getDevList - onError", e.getMessage());
                    }
                });
    }

    private void checkDevIsBind(List<Device> list, final boolean isToast) {
        // 登录时，页面刚创建 id 为空，确定 所获取的绑定设备列表是否有设备
        if (TextUtils.isEmpty(id.get())) {
            //若有设备，拣选列表第一个设备作为当前选定设备。
            if (list.size() > 0) {
                final Device currDev = list.get(0);
                id.set(currDev.getDevId());
                isDevBind.set(currDev.isPrimaryBind() ? 1 : 2);
            }
        } else
            Observable.from(list)
                    .filter(device -> {
                        Log.e("checkDevIsBind - call", "mDevId:" + id.get() + ", deviceId:" + device.getDevId());
                        return device.getDevId().equals(id.get());
                    })
                    .subscribe(new Subscriber<Device>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Device device) {
                            id.set(device.getDevId());
                            isDevBind.set(device.isPrimaryBind() ? 1 : 2);
                            if (isToast)
                                toast(device.isPrimaryBind() ? "绑定成功" : "关注成功");
                        }
                    });
    }

    /**
     * 点击 绑定 or 解绑 设备
     */
    public void clickBindDev(View v) {
        if (isDevBind.get() > 0) {
            CssServerApi.unbindDev(id.get())
                    .subscribe(new CssSubscriber<Integer>() {
                        @Override
                        public void onNextRequestSuccess(Integer state) {
                            switch (state) {
                                case 0:
                                    toast("设备解绑成功");
                                    //该长连接，设备连接蓝牙时启动，设备断开蓝牙停止。
                                    if (MonitorDataTransmissionManager.getInstance().isConnected()) {
                                        MonitorDataTransmissionManager.getInstance().disConnectBle();
                                        HmLoadDataTool.getInstance().destroyCssSocket();
                                    }
                                    isDevBind.set(0);
                                    getDevList(false);
                                    break;
                                case 1:
                                    toast("账号格式不对");
                                    break;
                                case 2:
                                    toast("设备id格式不正确 / api_key来源非法");
                                    break;
                                case 3:
                                    toast("已经是解绑状态");
                                    break;
                                case 4:
                                    toast("设备不属于当前账号");
                                    break;
                                case 5:
                                    toast("子账号解关注成功");
                                    if (MonitorDataTransmissionManager.getInstance().isConnected()) {
                                        MonitorDataTransmissionManager.getInstance().disConnectBle();
                                        HmLoadDataTool.getInstance().destroyCssSocket();
                                    }
                                    isDevBind.set(0);
                                    getDevList(false);
                                    break;
                                case 6:
                                    toast("设备io参数以及主账号不匹配（实际上和2有些重复）");
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onNextRequestFailed(int status) {
                            switch (status) {
                                case -1:
                                    toast("请检查网络连接");
                                    break;
                                default:
                                    toast("请求失败");
                                    break;
                            }
                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            toast("请求失败，error：" + e.toString());
                        }
                    });
        } else {
            if (!MonitorDataTransmissionManager.getInstance().isConnected()) {
                toast("请先通过蓝牙连接设备");
                return;
            }
            CssServerApi.bindDev(id.get(), key.get())
                    .subscribe(new CssSubscriber<Integer>() {
                        @Override
                        public void onNextRequestSuccess(Integer state) {
                            switch (state) {
                                case 0://绑定成功,重新获取设备列表确认绑定
                                    getDevList(true);
                                    break;
                                case 1:// 等待设备回复（绑定中）；
                                    toast("等待设备回复（绑定中）");
                                    loopCheckDevIsBind();
                                    break;
                                case 2://工厂（设备）未登记
                                    toast("工厂（设备）未登记");
                                    break;
                                case 3://设备已被其他人绑定，可提示用户关注设备
                                    new AlertDialogBuilder(getActivity())
                                            .setCancelable(false)
                                            .setTitle("提示")
                                            .setMessage("该设备已被其他用户绑定，可以选择关注它。")
                                            .setNegativeButton("不了，谢谢", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    MonitorDataTransmissionManager.getInstance().disConnectBle();
                                                    HmLoadDataTool.getInstance().destroyCssSocket();
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .setPositiveButton("关注", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    requestPrimaryAcc();
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .create().show();
                                    break;
                                case 4://二维码错误
                                    toast("二维码错误");
                                    break;
                                case 5:// apikey 错误
                                    toast("apikey错误");
                                    break;
                                case 6:// app和设备不匹配
                                    toast("app和设备不匹配");
                                    break;
                                default://基本不会出现该种情况，但是，谁知道呢。。。
                                    toast("未知错误,错误码:" + state);
                                    break;
                            }
                        }

                        @Override
                        public void onNextRequestFailed(int status) {
                            switch (status) {
                                case -1:
                                    toast("网络断开了，检查网络");
                                    break;
                                default:
                                    toast("请求失败");
                                    break;
                            }
                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            toast("请求失败，error：" + e.toString());
                        }
                    });
        }
    }

    public void clickShowDownloadData(View v) {
        if (HmLoadDataTool.getInstance().hasMember()) {
            ActivityHelper.launcher(getActivity(), ShowDownloadDataActivity.class);
        } else {
            toast("请先点击获取家庭成员");
        }
    }

    public void clickGetFamilyMember(View v) {
        // 单个设备ID获取家庭成员
        CssServerApi.getFamilyMemberList(id.get())
                .subscribe(new CssSubscriber<List<Member>>() {
                    @Override
                    public void onNextRequestSuccess(List<Member> members) {
                        //只要是已绑定的设备，家庭成员列表至少有一个成员，也就是绑定这台设备的用户自己，且自己一定会排在列表首位
                        final Member myMember = members.get(0);
                        //所传入的Member对象一定匹配所建立的CssSocket时所传入的设备ID，否则数据无法上传成功
                        HmLoadDataTool.getInstance().setMember(myMember);
                        toast("家庭成员列表获取成功，请看Log。");
                        Log.e("clickGetFamilyMember", "**********start:devId = " + myMember.devId + "***********");
                        for (Member member : members) {
                            Log.e("clickGetFamilyMember", member.toString());
                        }
                        Log.e("clickGetFamilyMember", "**********end***********");
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
       /*   //若是想要获取设备列表中的所有设备ID对应的家庭成员列表，可以采用rxJava Observable的merge()函数，如下:
      List<Observable<ResultPair<List<Member>>>> list = new ArrayList<>();
        for (Device dev : devList) {
            final Observable<ResultPair<List<Member>>> familyMemberList = CssServerApi.getFamilyMemberList(dev.getDevId());
            list.add(familyMemberList);
        }
        Observable.merge(list)
                .subscribe(new CssSubscriber<List<Member>>() {
//                    根据merge的特性，onNextRequestSuccess、onNextRequestFailed、onError的总回调次数等于 list 的size，也就是等于设备列表的size
//                    最终响应回调onCompleted，代表整个merge过程结束。
                    @Override
                    public void onNextRequestSuccess(List<Member> members) {
                        //只要是已绑定的设备，家庭成员列表至少有一个成员，也就是绑定这台设备的用户自己，且一定会排在列表首位
                        Log.e("clickGetFamilyMember", "**********start:devId = " + members.get(0).devId + "***********");
                        for (Member member : members) {
                            Log.e("clickGetFamilyMember", member.toString());
                        }
                        Log.e("clickGetFamilyMember", "**********end***********");
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
                });*/
    }

    /**
     * 向主账号请求关注设备
     * 若请求成功，服务器将向主账号对应的手机号发送短信验证码，该过程是一个耗时过程。
     */
    private void requestPrimaryAcc() {
        CssServerApi.followDevReq(id.get(), key.get())
                .subscribe(new CssSubscriber<ResultPair<String[]>>() {
                    @Override
                    public void onNextRequestSuccess(ResultPair<String[]> resultPair) {
                        final int state = resultPair.first;
                        //打Log的若出现，说明SDK内部参数配置存在问题，toast的才是正常情况下有可能出现的请求异常。
                        switch (state) {
                            case 0://成功，同时返回值里包含id（表示pid），account(主账号)
                                String[] info = resultPair.second;
                                showCheckFollowPermissionDialog(info);
                                break;
                            case 1: //邮箱格式不对，正常能登录成功应该不会出现该情况
                                Log.e("requestPrimaryAcc", "邮箱格式不对，正常能登录成功应该不会出现该情况");
                                break;
                            case 2://qr码解析失败
                                Log.e("requestPrimaryAcc", "qr码解析失败");
                                break;
                            case 3://设备未被主账号绑定
                                toast("请求关注失败，设备未被绑定");
                                break;
                            case 4://已是主账号
                                toast("请求关注失败，您已是该设备的主账号");
                                break;
                            case 5://子账号数量已到上限
                                toast("请求关注失败，设备的关注账号已达上限");
                                break;
                            case 6://子账号电话号码格式不对
                                Log.e("requestPrimaryAcc", "子账号电话号码格式不对");
                                break;
                            case 7://主账号电话号码格式不对
                                Log.e("requestPrimaryAcc", "主账号电话号码格式不对");
                                break;
                            case 8://已是子账号
                                toast("您已关注该设备，不能重复关注");
                                break;
                            case 9://api_key验证不通过，用户账号和api_key不匹配
                                Log.e("requestPrimaryAcc", "api_key验证不通过");
                                break;
                            case 10://app和设备不匹配
                                Log.e("requestPrimaryAcc", "app和设备不匹配");
                                break;
                            case 11://主账号为社交属性，无法关注
                                /*
                                 *这种情况是因为主账号是用第三方平台账号登录（如facebook等，目前服务器也仅支持Facebook）。
                                 * 若工程没有导入第三方平台登录，可忽略该选项。
                                 * */
                                toast("主账号是社交社交账号属性，无法关注");
                                break;
                        }
                    }

                    @Override
                    public void onNextRequestFailed(int status) {
                        switch (status) {
                            case -1:
                                toast("网络断开了，检查网络");
                                break;
                            default:
                                toast("请求失败");
                                break;
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("requestPrimaryAcc", e.getMessage());
                    }
                });
    }

    /**
     * 确认关注设备
     * 要关注设备的账号用户向主账号用户索取验证码，确认成功即认为该账号已成功关注了该设备
     */
    private void showCheckFollowPermissionDialog(String[] info) {
        if (getActivity() != null) {
            FollowDevCheckDialogFragment dialogFragment = new FollowDevCheckDialogFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.setInfo(info);
            dialogFragment.setOnUpdateListener(() -> getDevList(false));
            dialogFragment.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onDeviceInfo(DeviceInfo device) {
        super.onDeviceInfo(device);
        if (isLogin.get()) {
            //从服务器确认是否绑定
            getDevList(false);
            startUpCssDev();
        }
    }

    private void startUpCssDev() {
        HmLoadDataTool.getInstance().createCssSocket(getActivity().getApplication(), id.get(), key.get(),
                new OnCssSocketRunningListener() {

                    @Override
                    public void onDataUploadSuccess(LoadBean bean) {
                        //                          ↑↑↑↑↑↑↑↑↑↑↑↑↑
                        //  返回的上传对象，可以在执行此保存到数据库的操作
                        Log.e("startUpCssDev", "数据上传成功");
                        toast("数据上传成功");
                        if (bean != null) {
                            Log.e("startUpCssDev", "onDataUploadSuccess:" + bean.toString());
                        }
                    }

                    @Override
                    public void onDataUploadFail() {
                        Log.e("startUpCssDev", "数据上传失败...");
                        toast("数据上传失败");
                    }

                    @Override
                    public void onActivating() {
                        Log.e("startUpCssDev", "CSS Socket模块激活中...");
                        HmLoadDataTool.getInstance().checkSocketActive();
                    }

                    /**
                     * 这里的激活成功指的是CSS Socket激活成功，此时Css Socket还要向服务器反馈激活成功的信息
                     * 所以设备是否激活成功，应该以服务器的设备列表是否有该设备为准，不要将此回调函数作为设备激活成功的依据
                     * {@link MonitorInfoOnlineFragment#loopCheckDevIsBind()}
                     **/
                    @Override
                    public void onActiveSuccess() {
                        Log.e("startUpCssDev", "CSS Socket模块激活成功");
                        App.isShowUploadButton.set(true);
//                        if (isDevBind.get() == 0) {
//                            getDevList(false);
//                            clickGetFamilyMember(null);
//                        }
                    }

                    @Override
                    public void onActiveFail(String reason) {
                        Log.e("startUpCssDev", "CSS Socket模块激活失败，reason:" + reason);
                        App.isShowUploadButton.set(false);
                        // CSS Socket模块激活失败，SDK内部已销毁CSS模块，此时可选择尝试重启模块，多次尝试不成功要及时断开蓝牙连接
                        // 也可选择立即断开蓝牙
                        // CSS Socket 模块与设备蓝牙连接模块相辅相成， 应该遵循如下原则：
                        // 蓝牙连接成功，启动该模块，只有该模块被初始化成功并激活成功，才能继续保持蓝牙的连接，当模块未初始化成功或未激活成功，
                        // SDK内部已直接销毁模块，所以此时也应该及时断开蓝牙连接。
                        // 同理，当蓝牙连接断开后，也应该及时销毁CSS Socket模块。
                        MonitorDataTransmissionManager.getInstance().disConnectBle();
                        HmLoadDataTool.getInstance().destroyCssSocket();
                    }

                    @Override
                    public void onFreeze() {
                        Log.e("startUpCssDev", "CSS Socket模块已被冻结");
                    }

                    @Override
                    public void onInitializeSuccess() {
                        Log.e("startUpCssDev", "CSS Socket模块初始化成功");
                        if (isDevBind.get() > 0) {
                            HmLoadDataTool.getInstance().checkSocketActive();
                        } else {
                            Log.e("startUpCssDev", "onInitializeSuccess" + "需要绑定");
                        }
                    }

                    @Override
                    public void onInitializeFail(String reason) {
                        Log.e("startUpCssDev", "CSS Socket模块接初始化失败，reason:" + reason);
                        MonitorDataTransmissionManager.getInstance().disConnectBle();
                        HmLoadDataTool.getInstance().destroyCssSocket();
                    }

                    @Override
                    public void onSocketDisconnect() {
                        //模块内部有断开重连机制，所以这里不需要销毁模块，也不需要断开蓝牙连接
                        // 当然可以自己增加判断，断开连接时，连续几次重连失败，再断开蓝牙连接和销毁CSS Socket模块
                        Log.e("startUpCssDev", "CSS Socket模块与服务器断开连接");
                    }
                });
    }
}
