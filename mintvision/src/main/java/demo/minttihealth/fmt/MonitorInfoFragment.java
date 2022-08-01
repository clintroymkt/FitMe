package demo.minttihealth.fmt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;
import com.linktop.constant.DeviceInfo;
import com.linktop.constant.WareType;
import com.linktop.infs.OnBatteryListener;
import com.linktop.infs.OnBleConnectListener;
import com.linktop.infs.OnDeviceInfoListener;
import com.linktop.infs.OnDeviceVersionListener;
import com.linktop.whealthService.BleDevManager;

import demo.minttihealth.activity.UserProfileActivity;
import demo.minttihealth.health.App;
import demo.minttihealth.health.HcService;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentMonitorInfoBinding;
import demo.minttihealth.utils.AlertDialogBuilder;
import demo.minttihealth.utils.PermissionManager;

/**
 * Created by ccl on 2017/2/7.
 * MonitorInfoFragment
 * 健康检测仪基本操作Demo页面（除测量外）
 */

public class MonitorInfoFragment extends BaseFragment
        implements OnDeviceVersionListener, OnBleConnectListener, OnBatteryListener, OnDeviceInfoListener {

    private static final int REQUEST_OPEN_BT = 0x23;

    private final ObservableField<String> btnText = new ObservableField<>();
    private final ObservableField<String> power = new ObservableField<>("");
    protected final ObservableField<String> id = new ObservableField<>("");//当前选定的设备id
    protected final ObservableField<String> key = new ObservableField<>("");//当前选定的设备key
    private final ObservableField<String> softVer = new ObservableField<>("");
    private final ObservableField<String> hardVer = new ObservableField<>("");
    private final ObservableField<String> firmVer = new ObservableField<>("");
    private boolean showScanList;
    private BleDeviceListDialogFragment mBleDeviceListDialogFragment;

    public MonitorInfoFragment() {
    }

    @Override
    public int getTitle() {
        return R.string.basic_info;
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_monitor_info;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentMonitorInfoBinding binding = (FragmentMonitorInfoBinding) viewDataBinding;
        binding.setFrag(this);
        binding.setBtnText(btnText);
        binding.setPower(power);
        binding.setId(id);
        binding.setKey(key);
        binding.setSoftVer(softVer);
        binding.setHardVer(hardVer);
        binding.setFirmVer(firmVer);
        binding.radioG.setOnCheckedChangeListener((group, checkedId) -> showScanList = checkedId == R.id.radioB1);
        if (!App.isUseCustomBleDevService) {
            onBleState(MonitorDataTransmissionManager.getInstance().getBleState());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (App.isUseCustomBleDevService) {
            BleDevManager bleDevManager = mHcService.getBleDevManager();
            mHcService.setOnDeviceVersionListener(this);
            bleDevManager.getBatteryTask().setBatteryStateListener(this);
            bleDevManager.getDeviceTask().setOnDeviceInfoListener(this);
        } else {
            MonitorDataTransmissionManager.getInstance().setOnBleConnectListener(this);
            MonitorDataTransmissionManager.getInstance().setOnBatteryListener(this);
            MonitorDataTransmissionManager.getInstance().setOnDevIdAndKeyListener(this);
            MonitorDataTransmissionManager.getInstance().setOnDeviceVersionListener(this);
        }
    }

    @Override
    public void onDestroy() {
        if (App.isUseCustomBleDevService) {
            if (mHcService.isConnected) {
                mHcService.disConnect();
            }
        } else {
            //demo中把该界面当成主界面（相对于健康检测仪而言，当然也可以认为是上层的Activity），当销毁主界面前，应该把蓝牙连接断掉
            if (MonitorDataTransmissionManager.getInstance().isConnected())
                MonitorDataTransmissionManager.getInstance().disConnectBle();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = PermissionManager.isPermissionGranted(grantResults);
        if (requestCode == PermissionManager.requestCode_location) {
            if (permissionGranted) {
                try {
                    Thread.sleep(1000L);
                    clickConnect(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "没有定位权限", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_BT) {//蓝牙启动结果
            //蓝牙启动结果
            Toast.makeText(getContext(), resultCode == Activity.RESULT_OK ? "蓝牙已打开" : "蓝牙打开失败", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reset() {
        power.set("");
        id.set("");
        key.set("");
        softVer.set("");
        hardVer.set("");
        firmVer.set("");
    }

    /**
     * 设备版本号
     *
     * @param wareType 版本类型
     *                 {@link WareType#VER_FIRMWARE 固件版本}
     *                 {@link WareType#VER_HARDWARE 硬件版本}
     *                 {@link WareType#VER_SOFTWARE 软件版本}
     */
    @Override
    public void onDeviceVersion(@WareType int wareType, String version) {
        switch (wareType) {
            case WareType.VER_SOFTWARE:
                softVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_HARDWARE_VER);
                }
                break;
            case WareType.VER_HARDWARE:
                hardVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_FIRMWARE_VER);
                }
                break;
            case WareType.VER_FIRMWARE:
                firmVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_CONFIRM_ECG_MODULE_EXIST);
                }
                break;
        }

    }

    /******
     * 以上两个回调值，可以根据设备ID保存在SP里，
     * 这样可以在某些未连接设备但已知设备ID的情况下，
     * 直接获取并显示设备的软硬件版本号
     * 但是切记，设备升级软硬件，会更新版本号，所以每次连接蓝牙设备都应该读取软硬件版本号，
     * 若有做本地保存，及时更新本地保存，才能保证任何情况下显示版本号都是最新的。
     **************************************************************/

    @Override
    public void onBLENoSupported() {
        Toast.makeText(getContext(), "蓝牙不支持", Toast.LENGTH_SHORT).show();
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
                reset();
                break;
            case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                try {
                    btnText.set(getString(R.string.connect));
                    reset();
                } catch (Exception ignored) {
                }
                break;
            case BluetoothState.BLE_CONNECTING_DEVICE:
                try {
                    btnText.set(getString(R.string.connecting));
                } catch (Exception ignored) {
                }
                break;
            case BluetoothState.BLE_CONNECTED_DEVICE:
                btnText.set(getString(R.string.disconnect));
                break;
        }
    }

    @Override
    public void onUpdateDialogBleList() {
        mActivity.runOnUiThread(() -> {
            if (mBleDeviceListDialogFragment != null && mBleDeviceListDialogFragment.isShowing()) {
                mBleDeviceListDialogFragment.refresh();
            }
        });
    }

    /*
     * 设备插着USB充电线，未充满电的状态
     *
     *
     * */
    @Override
    public void onBatteryCharging() {
        power.set("充电中...");
    }

    /*
     * 设备拔掉USB充电线，正常使用
     * */
    @Override
    public void onBatteryQuery(int batteryValue) {
        power.set(batteryValue + "%");
    }

    /*
     * 设备插着USB充电线，已充满电的状态
     * */
    @Override
    public void onBatteryFull() {
        power.set("已充满");
    }


    public void clickSetUserInfo(View v) {
        startActivity(new Intent(v.getContext(), UserProfileActivity.class));
    }

    /**
     * 点击切换蓝牙连接状态
     */
    public void clickConnect(View v) {
        if (App.isUseCustomBleDevService) {
            if (!PermissionManager.isObtain(this, PermissionManager.PERMISSION_LOCATION
                    , PermissionManager.requestCode_location)) {
                return;
            } else {
                if (!PermissionManager.canScanBluetoothDevice(getContext())) {
                    new AlertDialogBuilder(mActivity)
                            .setTitle("提示")
                            .setMessage("Android 6.0及以上系统需要打开位置开关才能扫描蓝牙设备。")
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton("打开位置开关"
                                    , (dialog, which) -> PermissionManager.openGPS(mActivity)).create().show();
                    return;
                }
            }
            if (mHcService.isConnected) {
                mHcService.disConnect();
            } else {
                final int bluetoothEnable = mHcService.isBluetoothEnable();
                if (bluetoothEnable == -1) {
                    onBLENoSupported();
                } else if (bluetoothEnable == 0) {
                    onOpenBLE();
                } else {
                    mHcService.quicklyConnect();
                }
            }
        } else {
            final int bleState = MonitorDataTransmissionManager.getInstance().getBleState();
            Log.e("clickConnect", "bleState:" + bleState);
            switch (bleState) {
                case BluetoothState.BLE_CLOSED:
                    MonitorDataTransmissionManager.getInstance().bleCheckOpen();
                    break;
                case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                    if (MonitorDataTransmissionManager.getInstance().isScanning()) {
                        new AlertDialogBuilder(mActivity)
                                .setTitle("提示")
                                .setMessage("正在扫描设备，请稍后...")
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton("停止扫描"
                                        , (dialogInterface, i) ->
                                                MonitorDataTransmissionManager.getInstance().scan(false)).create().show();
                    } else {
                        if (PermissionManager.isObtain(this, PermissionManager.PERMISSION_LOCATION
                                , PermissionManager.requestCode_location)) {
                            if (PermissionManager.canScanBluetoothDevice(getContext())) {
                                if (showScanList) {
                                    connectByDeviceList();
                                } else {
                                    MonitorDataTransmissionManager.getInstance().scan(true);
                                }
                            } else {
                                new AlertDialogBuilder(mActivity)
                                        .setTitle("提示")
                                        .setMessage("Android 6.0及以上系统需要打开位置开关才能扫描蓝牙设备。")
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .setPositiveButton(R.string.turn_on_location, (dialog, which) -> PermissionManager.openGPS(mActivity)).create().show();
                            }
                        }
                    }
                    break;
                case BluetoothState.BLE_CONNECTING_DEVICE:
//                    Toast.makeText(mActivity, "蓝牙连接中...", Toast.LENGTH_SHORT).show();
                    MonitorDataTransmissionManager.getInstance().disConnectBle();
                    break;
                case BluetoothState.BLE_CONNECTED_DEVICE:
                case BluetoothState.BLE_NOTIFICATION_DISABLED:
                case BluetoothState.BLE_NOTIFICATION_ENABLED:
                    MonitorDataTransmissionManager.getInstance().disConnectBle();
                    break;
            }
        }

    }

    /*
     * 从设备列表中选择设备连接（用于周围环境有多台相同型号蓝牙设备的情况，避免连错）
     * */
    private void connectByDeviceList() {
        mBleDeviceListDialogFragment = new BleDeviceListDialogFragment();
        mBleDeviceListDialogFragment.show(mActivity.getSupportFragmentManager(), "");
    }


    @Override
    public void onDeviceInfo(DeviceInfo device) {
        Log.e("onDeviceInfo", device.toString());
        String deviceId = device.getDeviceId();
        String deviceKey = device.getDeviceKey();
//        如果需要id 和 key 中的字母参数小写，可以如下转换
        deviceId = deviceId.toLowerCase();
        deviceKey = deviceKey.toLowerCase();
        id.set(deviceId);
        key.set(deviceKey);
        if (mHcService != null) {
            mHcService.dataQuery(HcService.DATA_QUERY_BATTERY_INFO);
        }
    }

    @Override
    public void onReadDeviceInfoFailed() {
        id.set("Unable to read the device ID.");
        key.set("Unable to read the device key.");
        if (mHcService != null) {
            mHcService.dataQuery(HcService.DATA_QUERY_BATTERY_INFO);
        }
    }
}
