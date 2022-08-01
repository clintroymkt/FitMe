package com.kl.minttisdkdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.clj.fastble.data.BleDevice;
import com.kl.minttisdk.ble.BleUtils;
import com.kl.minttisdk.ble.callback.ScanResultCallback;
import com.kl.minttisdkdemo.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class StethoActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = StethoActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private ListView deviceList;
    private Button btnScanDevices;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    public String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
    public List<String> permissionList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stetho);
        checkPermission();
        initView();
    }

    private void initView() {
        deviceList = findViewById(R.id.lv_devices_list);
        btnScanDevices = findViewById(R.id.btn_scan_devices);
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        deviceList.setAdapter(mLeDeviceListAdapter);
        deviceList.setOnItemClickListener(this);
        btnScanDevices.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_scan_devices) {
            onBtnScanClicked();
        }
    }

    private void onBtnScanClicked(){

        if (!BleUtils.getInstance().isSupportBle()){
            Toast.makeText(this,"Ble is not supported",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!BleUtils.getInstance().isBluetoothEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQUEST_ENABLE_BT);
            return;

        }
        if (btnScanDevices.isSelected()) {
            btnScanDevices.setText(R.string.start_scan);
            stopScanBLE();
            btnScanDevices.setSelected(false);

        } else {
            btnScanDevices.setText(R.string.stop_scan);
            startScanBLE();
            btnScanDevices.setSelected(true);
        }
    }

    private void startScanBLE(){
        BleUtils.getInstance().startScan(new ScanResultCallback() {
            @Override
            public void onScanResult(int i, ScanResult scanResult) {
                BluetoothDevice bluetoothDevice = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bluetoothDevice = scanResult.getDevice();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                    Log.d(TAG,bluetoothDevice.getAddress());
                }
                String deviceName = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                    deviceName = bluetoothDevice.getName();
                }
                if (!TextUtils.isEmpty(deviceName) && deviceName.equals("Mintti")){
                    BleDevice bleDevice = new BleDevice(bluetoothDevice);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bleDevice.setRssi(scanResult.getRssi());
                    }
                    mLeDeviceListAdapter.addDevice(bleDevice);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> list) {

            }

            @Override
            public void onScanFailed(int i) {

            }
        });

    }
    private void stopScanBLE(){
        if (BleUtils.getInstance().isScanning()){
            BleUtils.getInstance().stopScan();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BleDevice bleDevice = mLeDeviceListAdapter.getDevice(position);

        if (bleDevice == null)
            return;
        if (btnScanDevices.isSelected()) {
            btnScanDevices.setText(R.string.start_scan);
            btnScanDevices.setSelected(false);
        }
        stopScanBLE();
        Intent intent = new Intent(StethoActivity.this, MeasureActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            intent.putExtra("BLE_ADDRESS", bleDevice.getDevice().getAddress());
        }
        startActivity(intent);
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(StethoActivity.this, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (!permissionList.isEmpty()) {
                String[] requestPermission = permissionList.toArray(new String[permissionList
                        .size()]);
                ActivityCompat.requestPermissions(StethoActivity.this, requestPermission, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Toast.makeText(StethoActivity.this, "Deny permission program will not run", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
            onBtnScanClicked();
        }
    }

    class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BleDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BleDevice>();
            mInflator = StethoActivity.this.getLayoutInflater();
        }

        public void addDevice(BleDevice dev) {
            int i = 0;
            int listSize = mLeDevices.size();
            for (i = 0; i < listSize; i++) {
                if (mLeDevices.get(i).getDevice().equals(dev.getDevice())) {
                    mLeDevices.get(i).setRssi(dev.getRssi());
                    break;
                }
            }

            if (i >= listSize) {
                mLeDevices.add(dev);
            }

        }

        public BleDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.item_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceSignal = (TextView) view.findViewById(R.id.signal);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BleDevice bleDevice = mLeDevices.get(i);
            final String deviceName = bleDevice.getName();

            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("Unknown device");
            }

            viewHolder.deviceAddress.setText(bleDevice.getMac());
            viewHolder.deviceSignal.setText(bleDevice.getRssi() + "dBm");
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceSignal;
    }

}
