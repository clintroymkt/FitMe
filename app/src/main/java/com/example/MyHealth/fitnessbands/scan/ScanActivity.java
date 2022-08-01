package com.example.MyHealth.fitnessbands.scan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crrepa.ble.CRPBleClient;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.listener.CRPBleFirmwareUpgradeListener;
import com.crrepa.ble.scan.bean.CRPScanDevice;
import com.crrepa.ble.scan.callback.CRPScanCallback;
import com.crrepa.ble.trans.upgrade.bean.HSFirmwareInfo;
import com.example.MyHealth.FitDash;
import com.example.MyHealth.R;
import com.example.MyHealth.PermissionUtils;
import com.example.MyHealth.SampleApplication;
import com.example.MyHealth.fitnessbands.device.DeviceActivity;
import com.example.MyHealth.ui.LoginActivity;
import com.example.MyHealth.ui.RecoverActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends AppCompatActivity {
    private static final String TAG = "ScanActivity";
    private static final int SCAN_PERIOD = 10 * 1000;

    private static final int REQUEST_UPDATEBANDCONFIG = 4;
    private static final String[] PERMISSION_UPDATEBANDCONFIG = new String[] {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"};


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private FirebaseAuth firebaseAuth;

    @BindView(R.id.btn_scan_toggle)
    Button scanToggleBtn;
    @BindView(R.id.scan_results)
    RecyclerView scanResults;
    @BindView(R.id.btnRecovery)
    Button recoverybtn;
    @BindView(R.id.btnlogout)
    Button logoutbtn;
  //  @BindView(R.id.tv_firmware_fix_state)
   // TextView tvFirmwareFixState;


    private CRPBleClient mBleClient;
    private ScanResultsAdapter mResultsAdapter;
    private boolean mScanState = false;


    private static final String UPGRADE_APP_FILE_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "crrepa" + File.separator + "app_band-hs.bin";
    private static final String UPGRADE_USER_FILE_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "crrepa" + File.separator + "usr.bin";
    private static final String USER_START_ADDRESS = "23000";
    //    private static final String BAND_ADDRESS = "C1:C4:7C:DE:44:5B";
//    private static final String BAND_ADDRESS = "D9:4D:C2:BB:F3:F4";
    private static final String BAND_ADDRESS = "FB:09:C5:C7:1A:90";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        mBleClient = SampleApplication.getBleClient(this);

        configureResultList();
        firebaseAuth = FirebaseAuth.getInstance();
        requestPermissions();

        recoverybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecovery();
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void openRecovery() {
        startActivity(new Intent(ScanActivity.this, RecoverActivity.class));
        finish();
    }

    private void logout() {
        firebaseAuth.signOut();

        startActivity(new Intent(ScanActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelScan();
    }

    @OnClick({R.id.btn_scan_toggle,
           // R.id.btn_firmware_fix, R.id.btn_hs_upgrade
    })
    public void onViewClicked(View view) {
        if (!mBleClient.isBluetoothEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            return;
        }

        switch (view.getId()) {
            case R.id.btn_scan_toggle:
                if (mScanState) {
                    cancelScan();
                } else {
                    startScan();
                }
                break;
         //   case R.id.btn_firmware_fix:

       //         break;
       //     case R.id.btn_hs_upgrade:
       //         CRPBleClient bleClient = SampleApplication.getBleClient(this);
       //         CRPBleDevice bleDevice = bleClient.getBleDevice(BAND_ADDRESS);
        //        HSFirmwareInfo upgradeInfo = new HSFirmwareInfo();
        //        upgradeInfo.setAppFilePath(UPGRADE_APP_FILE_PATH);
        //        upgradeInfo.setUserFilePath(UPGRADE_USER_FILE_PATH);
        //        upgradeInfo.setUserStartAddress(USER_START_ADDRESS);


          //      break;
        }

    }

    private void startScan() {
        boolean success = mBleClient.scanDevice(new CRPScanCallback() {
            @Override
            public void onScanning(final CRPScanDevice device) {
                Log.d(TAG, "address: " + device.getDevice().getAddress());
                if (TextUtils.isEmpty(device.getDevice().getName())) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mResultsAdapter.addScanResult(device);
                    }
                });
            }

            @Override
            public void onScanComplete(List<CRPScanDevice> results) {
                if (mScanState) {
                    mScanState = false;
                    updateButtonUIState();
                }
            }
        }, SCAN_PERIOD);
        if (success) {
            mScanState = true;
            updateButtonUIState();
            mResultsAdapter.clearScanResults();
        }
    }

    private void cancelScan() {
        mBleClient.cancelScan();
    }


    private void configureResultList() {
        scanResults.setHasFixedSize(true);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        scanResults.setLayoutManager(recyclerLayoutManager);
        mResultsAdapter = new ScanResultsAdapter();
        scanResults.setAdapter(mResultsAdapter);
        mResultsAdapter.setOnAdapterItemClickListener(new ScanResultsAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterViewClick(View view) {
                final int childAdapterPosition = scanResults.getChildAdapterPosition(view);
                final CRPScanDevice itemAtPosition = mResultsAdapter.getItemAtPosition(childAdapterPosition);
                onAdapterItemClick(itemAtPosition);
            }
        });
    }


    private void onAdapterItemClick(CRPScanDevice scanResults) {
        final String macAddress = scanResults.getDevice().getAddress();
        mBleClient.cancelScan();

       // final Intent intent = new Intent(this, DeviceActivity.class);
        final Intent intent = new Intent(this, FitDash.class);
        intent.putExtra(DeviceActivity.DEVICE_MACADDR, macAddress);
        startActivity(intent);
    }


    private void updateButtonUIState() {
        scanToggleBtn.setText(mScanState ? R.string.stop_scan : R.string.start_scan);
    }


    void updateTextView(final TextView view, final String con) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(con);
            }
        });
    }


    void requestPermissions() {
        if (!PermissionUtils.hasSelfPermissions(this, PERMISSION_UPDATEBANDCONFIG)) {
            ActivityCompat.requestPermissions(
                    this, PERMISSION_UPDATEBANDCONFIG, REQUEST_UPDATEBANDCONFIG);
        }
    }
}
