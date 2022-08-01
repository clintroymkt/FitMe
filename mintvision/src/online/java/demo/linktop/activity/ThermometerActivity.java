package demo.minttihealth.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.linktop.DeviceType;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;
import com.linktop.infs.OnBleConnectListener;
import com.linktop.infs.OnScanTempListener;
import com.linktop.infs.OnThermInfoListener;
import com.linktop.whealthService.OnBLEService;

import java.util.ArrayList;
import java.util.List;

import demo.minttihealth.adapter.BindDevListAdapter;
import demo.minttihealth.adapter.DataBindingAdapter;
import demo.minttihealth.health.App;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.ActivityThermometerBinding;
import demo.minttihealth.utils.AlertDialogBuilder;
import demo.minttihealth.utils.PermissionManager;
import demo.minttihealth.widget.CustomRecyclerView;
import lib.linktop.common.CssSubscriber;
import lib.linktop.obj.Device;
import lib.linktop.obj.ThermBean;
import lib.linktop.sev.CssServerApi;
import lib.linktop.sev.ThermLoadDataTool;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by ccl on 2017/2/10.
 * 温度计demo界面
 */

public class ThermometerActivity extends BaseActivity
        implements MonitorDataTransmissionManager.OnServiceBindListener
        , CustomRecyclerView.RecyclerItemClickListener
        , OnBleConnectListener
        , OnScanTempListener {

    private static final int REQUEST_OPEN_BT = 0x23;

    private final ObservableField<String> tempText = new ObservableField<>("");
    private final ObservableField<String> qrCode = new ObservableField<>("");
    private final ObservableField<String> btnText = new ObservableField<>("打开蓝牙");
    private final ObservableBoolean stopScanTempBtnShow = new ObservableBoolean(false);
    private final ObservableBoolean connBtnShow = new ObservableBoolean(false);
    private final ObservableBoolean isDevBind = new ObservableBoolean(false);
    private final ObservableBoolean qrCodeBtnShow = new ObservableBoolean(false);
    private final ObservableBoolean isLogin = App.isLogin;
    private DataBindingAdapter<OnBLEService.DeviceSort> mAdapter;
    private DataBindingAdapter<ThermBean> mLoadListAdapter;
    private final List<Device> devList = new ArrayList<>();
    private BluetoothDevice mBluetoothDevice;
    private ObservableField<String> mDevId = new ObservableField<>("");
    private long currTs = 0L;
    private BindDevListAdapter mBindDevListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityThermometerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_thermometer);
        setUpActionBar(binding.toolbar, getString(R.string.thermometer), "", true);
        binding.setIsLogin(isLogin);
        binding.setBtnText(btnText);
        mAdapter = new DataBindingAdapter<>(this, R.layout.item_ble_dev);
        binding.setRecyclerAdapter(mAdapter);
        binding.setListCount(mAdapter.getListSize());
        binding.setItemClickListener(this);
        binding.setDevBind(isDevBind);
        binding.setStopScanTempBtnShowFlag(stopScanTempBtnShow);
        binding.setConnectBtnShowFlag(connBtnShow);
        binding.setQrCodeBtnShowFlag(qrCodeBtnShow);
        binding.setTempText(tempText);
        binding.setQrCode(qrCode);
        /*
         * 绑定服务，DeviceType枚举类型类型是 Thermometer（体温计）。
         * Bind the service and the enum DeviceType is Thermometer.
         * */
        MonitorDataTransmissionManager.getInstance().bind(DeviceType.Thermometer, this,
                this);

        mLoadListAdapter = new DataBindingAdapter<>(this, R.layout.item_therm_load_data);
        binding.setRecyclerAdapter3(mLoadListAdapter);

        if (isLogin.get()) {
            mBindDevListAdapter = new BindDevListAdapter(this, mDevId);
            binding.setRecyclerAdapter2(mBindDevListAdapter);
            //从服务器确认是否绑定
            getDevList(false);
        }
    }

    @Override
    protected void onDestroy() {
        //解绑服务
        MonitorDataTransmissionManager.getInstance().unBind();
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = PermissionManager.isPermissionGranted(grantResults);
        if (requestCode == PermissionManager.requestCode_location) {
            if (permissionGranted) {
                clickConnect(null);
            } else {
                toast("没有定位权限");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_BT) {//蓝牙启动结果
            toast(resultCode == Activity.RESULT_OK ? "蓝牙已打开" : "蓝牙打开失败");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (MonitorDataTransmissionManager.getInstance().isScanning())
            MonitorDataTransmissionManager.getInstance().autoScan(false);
        super.onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (MonitorDataTransmissionManager.getInstance().isScanning()) {
            final OnBLEService.DeviceSort item = mAdapter.getItem(position);
            mBluetoothDevice = item.bleDevice;
            MonitorDataTransmissionManager.getInstance().startScanTemp(mBluetoothDevice, this);
            stopScanTempBtnShow.set(true);
            if (isLogin.get()) {
                final String bleDevName = item.bleDevice.getName();
                final String devId = getDevIdByDevName(bleDevName);
                mDevId.set(devId);
                ThermLoadDataTool.getInstance().setDevId(devId);
                checkDevIsBind(false);
            }
        } else {
            toast("未开启蓝牙扫描");
        }
    }

    @Override
    public void onServiceBind() {
        //服务绑定成功后注册蓝牙连接回调接口
        MonitorDataTransmissionManager.getInstance().setOnBleConnectListener(this);
        //先初始化一下控件显示的蓝牙状态
        onBleState(MonitorDataTransmissionManager.getInstance().getBleState());
        /*
         * 为避免某些不是所要的设备出现在蓝牙设备扫描列表中，需要调用该API去设置蓝牙设备名称前缀白名单。
         * 蓝牙设备扫描时以白名单内的字段作为设备名前缀的设备将被添加到蓝牙设备扫描列表中，其余的过滤。
         * PS：不使用该API，设备列表显示所有同类健康设备，不过滤。
         *     若使用该API，请代入有效的资源ID。
         * */
        MonitorDataTransmissionManager.getInstance().setScanDevNamePrefixWhiteList(R.array.thermometer_dev_name_prefixes);
    }

    @Override
    public void onServiceUnbind() {

    }

    @Override
    public void onBLENoSupported() {
        toast("蓝牙不支持");
    }

    @Override
    public void onOpenBLE() {
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_OPEN_BT);
    }

    @Override
    public void onBleState(int bleState) {
        switch (bleState) {
            case BluetoothState.BLE_CLOSED:
                btnText.set(getString(R.string.turn_on_bluetooth));
                MonitorDataTransmissionManager.getInstance().stopScanTemp();
                reset();
                break;
            case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                boolean scanning = MonitorDataTransmissionManager.getInstance().isScanning();
                Log.e("onBleState", "scanning ? " + scanning);
                if (scanning) {
                    btnText.set(getString(R.string.scanning_click_stop));
                } else {
                    btnText.set(getString(R.string.scan));
                    reset();
                }
                break;
//            case BluetoothState.BLE_CONNECTING_DEVICE:
//                btnText.set("连接中");
//                break;
            case BluetoothState.BLE_CONNECTED_DEVICE:
                btnText.set(getString(R.string.disconnect));
                connBtnShow.set(false);
                tempText.set("");
                stopScanTempBtnShow.set(false);
                qrCodeBtnShow.set(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast("连接成功，温度停止扫描");
                    }
                });
                break;
            case BluetoothState.BLE_CONNECTING_DEVICE:
                btnText.set(getString(R.string.connecting));
                break;
//            case BluetoothState.BLE_NOTIFICATION_ENABLED:
//                MonitorDataTransmissionManager.getInstance().getDevInfo(this);
//                break;
        }
    }

    @Override
    public void onUpdateDialogBleList() {
        runOnUiThread(() -> {
            if (mAdapter != null) {
                mAdapter.setItems(MonitorDataTransmissionManager.getInstance().getDeviceList());
            }
        });
    }

    private String getDevIdByDevName(String devName) {
        //将字符串”21“和蓝牙设备名中‘-’后部分的字符串结合起来，就是该蓝牙设备的设备ID
        //设备ID组成由服务器组定义规则，所以“21”部分是参阅服务器的相关文档决定的。
        final String lastPart = devName.substring(devName.indexOf("-") + 1);
        return ("21" + lastPart).toLowerCase();
    }

    public void clickConnect(View v) {
        final MonitorDataTransmissionManager manager = MonitorDataTransmissionManager.getInstance();
        switch (manager.getBleState()) {
            case BluetoothState.BLE_CLOSED:
                //手机蓝牙模块未开启时，点击开启蓝牙模块
                manager.bleCheckOpen();
                break;
            case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                //手机蓝牙模块已开启但未连接设备
                if (manager.isScanning()) {
                    //停止扫描设备
                    manager.autoScan(false);
                    btnText.set(getString(R.string.scan));
                    reset();
                } else {
                    //开启扫描设备，Android 6.0及以上系统注意判断是否开启 【定位权限】和【GPS开关】，
                    // 少一项都无法扫描到设备，Android 6.0以下系统无此限制。
                    if (PermissionManager.isObtain(this,  PermissionManager.PERMISSION_LOCATION
                            , PermissionManager.requestCode_location)) {
                        if (PermissionManager.canScanBluetoothDevice(this)) {
                            //开始扫描设备
                            manager.autoScan(true);
                            btnText.set("扫描中，点击停止");
                        } else {
                            new AlertDialogBuilder(this)
                                    .setTitle("提示")
                                    .setMessage("Android 6.0及以上系统需要打开位置开关才能扫描蓝牙设备。")
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .setPositiveButton(R.string.turn_on_location
                                            , (dialog, which) ->
                                                    PermissionManager.openGPS(ThermometerActivity.this)).create().show();
                        }
                    }
                }
                break;
            case BluetoothState.BLE_CONNECTING_DEVICE:
                toast(R.string.connecting);
                break;
            case BluetoothState.BLE_CONNECTED_DEVICE:
                //设备与手机蓝牙已建立连接，点击断开连接，并重置相关控件初始状态
                manager.disConnectBle();
                break;
        }
    }

    /**
     * 点击停止接收温度，注意此时蓝牙仍未停止描设备
     */
    public void clickStopScanTemp(View v) {
        MonitorDataTransmissionManager.getInstance().stopScanTemp();
        tempText.set("");
        stopScanTempBtnShow.set(false);
        connBtnShow.set(false);
        qrCodeBtnShow.set(false);
        qrCode.set("");
    }

    /**
     * 设备与蓝牙建立连接后，点击可获取设备的二维码，
     */
    public void clickToGetQRCode(View v) {
        if (MonitorDataTransmissionManager.getInstance().isConnected()) {
            MonitorDataTransmissionManager.getInstance().getDevInfo(new OnThermInfoListener() {
                //回调方法获取到的二维码，显示成二维码图片，并开启绑定
                @Override
                public void onThermQRCode(String result) {
                    //Display the QR code.
                    qrCode.set(result);
                    //If are already logged in.Binding the device by qr code.
                    if (isLogin.get())
                        startBind(result);
                }
            });
        } else {
            toast(R.string.unable_to_get_qr_code);
        }
    }

    /**
     * 点击连接设备，目的是为了获取设备二维码并绑定
     * 点击解绑设备
     */
    public void clickToConnectDev(View v) {
        final String btnText = ((Button) v).getText().toString();
        if (btnText.contains("connect")) {
            if (null != mBluetoothDevice) {
                MonitorDataTransmissionManager.getInstance().connectToBle(mBluetoothDevice);
            }
        } else {
            CssServerApi.unbindDev(mDevId.get())
                    .subscribe(new CssSubscriber<Integer>() {
                        @Override
                        public void onNextRequestSuccess(Integer state) {
                            switch (state) {
                                case 0:
                                    isDevBind.set(false);
                                    toast("解绑成功");
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
                            if (status == -1) {
                                toast("请检查网络连接");
                            } else {
                                toast("请求失败");
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

    public void clickUpload(View v) {
        final List<ThermBean> items = mLoadListAdapter.getItems();
        //.list里的内容务必按时间戳降序排列,即第一个数据为list中最新数据，最后一个数据为list中最旧数据
        /* ***************************/
        if (items == null || items.size() == 0) {
            toast("没有可上传的数据");
            return;
        }
        /* *******沒有這部分條件約束，下面的訂閱者對象將響應onError************/
        if (items.size() > 100) {
            toast("一次同步上传最多只能有100条体温数据");
            return;
        }
        /* ***************************/
        ThermLoadDataTool.getInstance().upload(items)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("请求上传数据错误，error：" + e.toString());
                    }

                    @Override
                    public void onNext(Integer status) {
                        if (status == 200) {
                            toast("数据上传成功");
                            mLoadListAdapter.clearItems();
                        } else {
                            toast("请求上传数据失败，code：" + status);
                        }
                    }
                });
    }

    public void clickDownload(View v) {
        ThermLoadDataTool.getInstance().download()
                .subscribe(new CssSubscriber<List<ThermBean>>() {
                    @Override
                    public void onNextRequestSuccess(List<ThermBean> list) {
                        if (list.isEmpty()) {
                            toast("服务器无数据");
                        } else {
                            mLoadListAdapter.setItems(list);
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
    }

    public void clickCleanList(View v) {
        mLoadListAdapter.clearItems();
    }

    private void startBind(String qrCode) {
        CssServerApi.bindDev(qrCode)
                .subscribe(new CssSubscriber<Integer>() {
                    @Override
                    public void onNextRequestSuccess(Integer state) {
                        Log.e(getClass().getSimpleName() + " - startBind", "state :" + state);
                        switch (state) {
                            case 0:////绑定成功,重新获取设备列表确认绑定
                                getDevList(true);
                                break;
                            case 1:// 等待设备回复（绑定中）；
                                toast("等待设备回复（绑定中）");
                                break;
                            case 2://工厂（设备）未登记
                                toast("工厂（设备）未登记");
                                break;
                            case 3://设备已被其他人绑定，可提示用户关注设备
                                toast("设备已被其他人绑定");
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
                        if (status == -1) {
                            toast("网络断开了，检查网络");
                        } else {
                            toast("请求失败");
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

    private void reset() {
        runOnUiThread(() -> mAdapter.clearItems());
        mBluetoothDevice = null;
        ThermLoadDataTool.getInstance().setDevId("");
        tempText.set("");
        qrCode.set("");
        isDevBind.set(false);
        connBtnShow.set(false);
        stopScanTempBtnShow.set(false);
        qrCodeBtnShow.set(false);
    }


    private void getDevList(final boolean isToast) {
        CssServerApi.getDevList()
                .subscribe(new CssSubscriber<List<Device>>() {
                    @Override
                    public void onNextRequestSuccess(List<Device> devices) {
                        devList.clear();
                        mBindDevListAdapter.clearItems();
                        if (!devices.isEmpty()) {
                            mDevId.set(devices.get(0).getDevId());
                            devList.addAll(devices);
                            mBindDevListAdapter.addItems(devices);
                            checkDevIsBind(isToast);
                        }
                    }

                    @Override
                    public void onNextRequestFailed(int status) {
                        if (status == -1) {
                            toast("网络断开了，检查网络");
                        } else {
                            toast("请求失败");
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

    private void checkDevIsBind(final boolean isToast) {
        Observable.from(devList)
                .filter(device -> {
                    Log.e("checkDevIsBind - call", "mDevId:" + mDevId + ", deviceId:" + device.getDevId());
                    return device.getDevId().equals(mDevId.get());
                })
                .subscribe(new Subscriber<Device>() {

                    private boolean isBind = false;

                    @Override
                    public void onCompleted() {
                        isDevBind.set(isBind);
                        if (isBind && isToast)
                            toast("绑定成功");
                        Log.e("checkDevIsBind", "device id " + mDevId + (isDevBind.get() ? " has bind." : " is not bind."));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Device device) {
                        isBind = true;
                    }
                });
    }

    private void collectUploadData(long ts, double temp) {
        if (ts - currTs >= (10 * 1000L)) {
            if (temp > 33.0d) {
                currTs = ts;
                final ThermBean thermBean = new ThermBean(ts, temp);
                Log.e("collectUploadData", thermBean.toString());
                mLoadListAdapter.addItemToFirstAndNotifyAll(thermBean);
            }
        }
    }

    /**
     * @param devAddress 设备蓝牙地址
     * @param temp       温度
     * @param battery    0 有电，1没电
     */
    @Override
    public void onScanTempResult(String devAddress, double temp, int battery) {
        tempText.set("Address:" + devAddress + "\nTemp:" + temp + "℃\nbattery:" + (battery == 0 ? "有电" : "没电"));
        connBtnShow.set(true);
        if (isLogin.get() && isDevBind.get()) {
            collectUploadData(System.currentTimeMillis(), temp);
        }
    }

    @Override
    public void onNoTemp() {
        String msg = "长时间没有扫描到温度，可能情况：\n" +
                "（1）设备自动关机——设备连续大约3分钟扫描到的温度低于28.0℃，设备会认为没有在测量，故自动关机\n" +
                "（2）设备电池没电\n" +
                "（3）设备与手机距离过大，无法接收到扫描的温度。";
        Log.e("OnNoTemp", msg);
        toast("长时间没有扫描到温度,已停止扫描");
    }
}
