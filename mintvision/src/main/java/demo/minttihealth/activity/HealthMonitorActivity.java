package demo.minttihealth.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.linktop.DeviceType;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;

import demo.minttihealth.adapter.FragmentsAdapter;
import demo.minttihealth.fmt.BaseFragment;
import demo.minttihealth.fmt.MonitorInfoFragment;
import demo.minttihealth.health.App;
import demo.minttihealth.health.HcService;
import demo.minttihealth.health.R;
import demo.minttihealth.utils.AlertDialogBuilder;
import demo.minttihealth.widget.CustomViewPager;

/**
 * Created by ccl on 2017/2/7.
 * 健康检测仪演示界面
 */

public class HealthMonitorActivity extends BaseActivity
        implements MonitorDataTransmissionManager.OnServiceBindListener,
        TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener, ServiceConnection {

    public CustomViewPager viewPager;
    public View viewFocus;

    private final SparseArray<BaseFragment> sparseArray = new SparseArray<>();
    public HcService mHcService;
    private MonitorInfoFragment mMonitorInfoFragment;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HcService.BLE_STATE) {
                final int state = (int) msg.obj;
                Log.e("Message", "receive state:" + state);
                if (state == BluetoothState.BLE_NOTIFICATION_ENABLED) {
                    mHcService.dataQuery(HcService.DATA_QUERY_SOFTWARE_VER);
                } else {
                    mMonitorInfoFragment.onBleState(state);
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_monitor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.health_monitor);
        }

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setKeepScreenOn(true);
        viewPager = findViewById(R.id.view_paper);
        viewFocus = findViewById(R.id.view_focus);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPageMargin(10);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0, false);
        //Bind service about Bluetooth connection.
        if (App.isUseCustomBleDevService) {
            Intent serviceIntent = new Intent(this, HcService.class);
            bindService(serviceIntent, this, BIND_AUTO_CREATE);
        } else {
            //绑定服务，
            // 类型是 HealthMonitor（HealthMonitor健康检测仪），
            MonitorDataTransmissionManager.getInstance().bind(DeviceType.HealthMonitor, this,
                    this);
        }
        Log.e("HealthMonitorActivity", "onCreate: " +tabLayout+"  viewPager "+viewPager);
    }

    @Override
    protected void onDestroy() {
        if (App.isUseCustomBleDevService) {
            unbindService(this);
        } else {
            //解绑服务
            MonitorDataTransmissionManager.getInstance().unBind();
        }
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
    public void onBackPressed() {
        if (!App.isUseCustomBleDevService) {
            if (MonitorDataTransmissionManager.getInstance().isMeasuring()) {
                new AlertDialogBuilder(this)
                        .setTitle("提示")
                        .setMessage("测量中，若要退出界面请先停止测量。")
                        .setPositiveButton("好的", null)
                        .create().show();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onServiceBind() {
        /*
         * 为避免某些不是自己所要的设备出现在蓝牙设备扫描列表中，需要调用该API去设置蓝牙设备名称前缀白名单。
         * 蓝牙设备扫描时以白名单内的字段作为设备名前缀的设备将被添加到蓝牙设备扫描列表中，其余的过滤。
         * PS：不使用该API，设备列表将不过滤。
         *     若使用该API，请代入有效的资源ID。
         * */
//        MonitorDataTransmissionManager.getInstance().setScanDevNamePrefixWhiteList(R.array.health_monitor_dev_name_prefixes);
//        MonitorDataTransmissionManager.getInstance().setStrongEcgGain(true);//設置增強心電圖增益
        //服务绑定成功后加载各个测量界面
        FragmentsAdapter adapter = new FragmentsAdapter(this, getSupportFragmentManager());
        getMenusFragments();
        adapter.setFragments(sparseArray);
        viewPager.setAdapter(adapter);

//        new Handler().postDelayed(this::testTestPaperValue, 3000L);
    }

//    public void testTestPaperValue() {
//        double value = TestPaperTask.getValue(MeasureType.CHOL,
//                TestPaper.Manufacturer.BENE_CHECK,
//                TestPaper.Code.C5F2,
//                new double[]{17.0, 16.0, 14.0});
//        Log.e("CCL", "Value=" + value);
//    }

    @Override
    public void onServiceUnbind() {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void getMenusFragments() {
        String[] measureFragmentArray = getResources().getStringArray(R.array.measure_fragment_array);
        int i = 0;
        for (String measureFragmentCls : measureFragmentArray) {
            BaseFragment fragment;
            try {
                fragment = (BaseFragment) getSupportFragmentManager()
                        .getFragmentFactory()
                        .instantiate(getClassLoader(), measureFragmentCls);
            } catch (Exception ignored) {
                continue;
            }
            if (fragment instanceof MonitorInfoFragment) {
                mMonitorInfoFragment = (MonitorInfoFragment) fragment;
            }
            sparseArray.put(i, fragment);
            i++;
        }
    }

    public void reset() {
        viewPager.setCanScroll(true);
        viewFocus.setVisibility(View.GONE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mHcService = ((HcService.LocalBinder) service).getService();
        mHcService.setHandler(mHandler);
        mHcService.initBluetooth();
        //UI
        FragmentsAdapter adapter = new FragmentsAdapter(this, getSupportFragmentManager());
        getMenusFragments();
        adapter.setFragments(sparseArray);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mHcService = null;
    }
}
