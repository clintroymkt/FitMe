package demo.minttihealth.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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
import com.linktop.whealthService.OnBLEService;

import demo.minttihealth.adapter.DataBindingAdapter;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.ActivityThermometerBinding;
import demo.minttihealth.utils.AlertDialogBuilder;
import demo.minttihealth.utils.PermissionManager;
import demo.minttihealth.widget.CustomRecyclerView;

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
    private final ObservableField<String> btnText = new ObservableField<>("打开蓝牙");
    private final ObservableBoolean stopScanTempBtnShow = new ObservableBoolean(false);
    private final ObservableBoolean connBtnShow = new ObservableBoolean(false);
    private DataBindingAdapter<OnBLEService.DeviceSort> mAdapter;
    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityThermometerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_thermometer);
        setUpActionBar(binding.toolbar, getString(R.string.thermometer), "", true);
        binding.setBtnText(btnText);
        mAdapter = new DataBindingAdapter<>(this, R.layout.item_ble_dev);
        binding.setRecyclerAdapter(mAdapter);
        binding.setListCount(mAdapter.getListSize());
        binding.setItemClickListener(this);
        binding.setStopScanTempBtnShowFlag(stopScanTempBtnShow);
        binding.setConnectBtnShowFlag(connBtnShow);
        binding.setTempText(tempText);
        /*
         * 绑定服务，DeviceType枚举类型类型是 Thermometer（体温计）。
         * Bind the service and the enum DeviceType is Thermometer.
         * */
        MonitorDataTransmissionManager.getInstance().bind(DeviceType.Thermometer, this,
                this);
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
                runOnUiThread(() -> toast("连接成功，温度停止扫描"));
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
                    if (PermissionManager.isObtain(this, PermissionManager.PERMISSION_LOCATION
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
    }

    private void reset() {
        runOnUiThread(() -> mAdapter.clearItems());
        mBluetoothDevice = null;
        tempText.set("");
        connBtnShow.set(false);
        stopScanTempBtnShow.set(false);
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
