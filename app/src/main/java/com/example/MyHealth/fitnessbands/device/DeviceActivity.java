package com.example.MyHealth.fitnessbands.device;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crrepa.ble.CRPBleClient;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.bean.CRPAlarmClockInfo;
import com.crrepa.ble.conn.bean.CRPFirmwareVersionInfo;
import com.crrepa.ble.conn.bean.CRPFunctionInfo;
import com.crrepa.ble.conn.bean.CRPFutureWeatherInfo;
import com.crrepa.ble.conn.bean.CRPHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPMessageInfo;
import com.crrepa.ble.conn.bean.CRPMovementHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPSleepInfo;
import com.crrepa.ble.conn.bean.CRPStepInfo;
import com.crrepa.ble.conn.bean.CRPStepsCategoryInfo;
import com.crrepa.ble.conn.bean.CRPSupportWatchFaceInfo;
import com.crrepa.ble.conn.bean.CRPTodayWeatherInfo;
import com.crrepa.ble.conn.bean.CRPUserInfo;
import com.crrepa.ble.conn.bean.CRPWatchFaceLayoutInfo;
import com.crrepa.ble.conn.callback.CRPDeviceAlarmClockCallback;
import com.crrepa.ble.conn.callback.CRPDeviceBreathingLightCallback;
import com.crrepa.ble.conn.callback.CRPDeviceDisplayWatchFaceCallback;
import com.crrepa.ble.conn.callback.CRPDeviceFirmwareVersionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceFunctionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceGoalStepCallback;
import com.crrepa.ble.conn.callback.CRPDeviceLanguageCallback;
import com.crrepa.ble.conn.callback.CRPDeviceMetricSystemCallback;
import com.crrepa.ble.conn.callback.CRPDeviceNewFirmwareVersionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceOtherMessageCallback;
import com.crrepa.ble.conn.callback.CRPDeviceQuickViewCallback;
import com.crrepa.ble.conn.callback.CRPDeviceSedentaryReminderCallback;
import com.crrepa.ble.conn.callback.CRPDeviceSupportWatchFaceCallback;
import com.crrepa.ble.conn.callback.CRPDeviceTimeSystemCallback;
import com.crrepa.ble.conn.callback.CRPDeviceVersionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceWatchFaceLayoutCallback;
import com.crrepa.ble.conn.listener.CRPBleConnectionStateListener;
import com.crrepa.ble.conn.listener.CRPBleECGChangeListener;
import com.crrepa.ble.conn.listener.CRPBleFirmwareUpgradeListener;
import com.crrepa.ble.conn.listener.CRPBloodOxygenChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodPressureChangeListener;
import com.crrepa.ble.conn.listener.CRPFindPhoneListener;
import com.crrepa.ble.conn.listener.CRPHeartRateChangeListener;
import com.crrepa.ble.conn.listener.CRPPhoneOperationListener;
import com.crrepa.ble.conn.listener.CRPSleepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepsCategoryChangeListener;
import com.crrepa.ble.conn.listener.CRPWatchFaceTransListener;
import com.crrepa.ble.conn.type.CRPBleMessageType;
import com.crrepa.ble.conn.type.CRPDeviceLanguageType;
import com.crrepa.ble.conn.type.CRPDeviceVersionType;
import com.crrepa.ble.conn.type.CRPEcgMeasureType;
import com.crrepa.ble.conn.type.CRPFirmwareUpgradeType;
import com.crrepa.ble.conn.type.CRPHeartRateType;
import com.crrepa.ble.conn.type.CRPMetricSystemType;
import com.crrepa.ble.conn.type.CRPPastTimeType;
import com.crrepa.ble.conn.type.CRPStepsCategoryDateType;
import com.crrepa.ble.conn.type.CRPTimeSystemType;
import com.crrepa.ble.conn.type.CRPWatchFaceLayoutType;
import com.crrepa.ble.conn.type.CRPWatchFaceType;
import com.crrepa.ble.conn.type.CRPWeatherId;
import com.example.MyHealth.R;
import com.example.MyHealth.SampleApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceActivity extends AppCompatActivity {
    private static final String TAG = "DeviceActivity";
    public static final String DEVICE_MACADDR = "device_macaddr";

    ProgressDialog mProgressDialog;
    CRPBleClient mBleClient;
    CRPBleDevice mBleDevice;
    CRPBleConnection mBleConnection;
    boolean isUpgrade = false;

    @BindView(R.id.tv_connect_state)
    TextView tvConnectState;
    @BindView(R.id.tv_firmware_version)
    TextView tvFirmwareVersion;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_step)
    TextView tvStep;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_calorie)
    TextView tvCalorie;
    @BindView(R.id.tv_restful)
    TextView tvRestful;
    @BindView(R.id.tv_light)
    TextView tvLight;
    @BindView(R.id.tv_heart_rate)
    TextView tvHeartRate;
    @BindView(R.id.tv_blood_pressure)
    TextView tvBloodPressure;
    @BindView(R.id.tv_upgrade_state)
    TextView tvUpgradeState;
    @BindView(R.id.btn_ble_connect_state)
    Button btnBleDisconnect;
    @BindView(R.id.tv_blood_oxygen)
    TextView tvBloodOxygen;
    @BindView(R.id.tv_new_firmware_version)
    TextView tvNewFirmwareVersion;

    private String bandFirmwareVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        initView();
        mProgressDialog = new ProgressDialog(this);
        String macAddr = getIntent().getStringExtra(DEVICE_MACADDR);
        if (TextUtils.isEmpty(macAddr)) {
            finish();
            return;
        }

        mBleClient = SampleApplication.getBleClient(this);
        mBleDevice = mBleClient.getBleDevice(macAddr);
        if (mBleDevice != null) {
            connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleDevice != null) {
            mBleDevice.disconnect();
        }
    }

    void initView() {
        updateStepInfo(0, 0, 0);
        updateSleepInfo(0, 0);
    }

    void connect() {
        mProgressDialog.show();
        mBleConnection = mBleDevice.connect();
        mBleConnection.setConnectionStateListener(new CRPBleConnectionStateListener() {
            @Override
            public void onConnectionStateChange(int newState) {
                Log.d(TAG, "onConnectionStateChange: " + newState);
                int state = -1;
                switch (newState) {
                    case CRPBleConnectionStateListener.STATE_CONNECTED:
                        state = R.string.state_connected;
                        mProgressDialog.dismiss();
                        updateTextView(btnBleDisconnect, getString(R.string.disconnect));
                        testSet();
                        break;
                    case CRPBleConnectionStateListener.STATE_CONNECTING:
                        state = R.string.state_connecting;
                        break;
                    case CRPBleConnectionStateListener.STATE_DISCONNECTED:
                        state = R.string.state_disconnected;
                        mProgressDialog.dismiss();
                        updateTextView(btnBleDisconnect, getString(R.string.connect));
                        break;
                }
                updateConnectState(state);
            }
        });

        mBleConnection.setStepChangeListener(mStepChangeListener);
        mBleConnection.setSleepChangeListener(mSleepChangeListener);
        mBleConnection.setHeartRateChangeListener(mHeartRateChangListener);
        mBleConnection.setBloodPressureChangeListener(mBloodPressureChangeListener);
        mBleConnection.setBloodOxygenChangeListener(mBloodOxygenChangeListener);
        mBleConnection.setFindPhoneListener(mFindPhoneListener);
        mBleConnection.setECGChangeListener(mECGChangeListener, CRPEcgMeasureType.TYHX);
        mBleConnection.setStepsCategoryListener(mStepsCategoryChangeListener);
    }

    private void testSet() {
        Log.d(TAG, "testSet");
        mBleConnection.syncTime();
        mBleConnection.queryPastHeartRate();
        mBleConnection.syncSleep();


//        sendFindBandMessage();
    }

    private void sendFindBandMessage() {
        handler.sendEmptyMessageDelayed(1, 5000);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mBleConnection.findDevice();
                sendFindBandMessage();
            }
        }
    };


    @OnClick(R.id.btn_ble_connect_state)
    public void onConnectStateClick() {
        //   if (mBleConnection ) {
        //      mBleDevice.disconnect();
        //    } else {
        connect();
        //   }
    }


    @OnClick({R.id.btn_query_firmware, R.id.btn_query_battery, R.id.btn_sync_time,
            R.id.btn_send_user_info, R.id.btn_sync_step,
            R.id.btn_sync_sleep, R.id.btn_send_metric_system, R.id.btn_send_time_system,
            R.id.btn_send_quick_view, R.id.btn_send_goal_step, R.id.btn_query_goal_step,
            R.id.btn_find_band, R.id.btn_send_message, R.id.btn_query_time_system,
            R.id.btn_query_metric_system, R.id.btn_query_quick_view, R.id.btn_send_alarm_clock,
            R.id.btn_query_alarm_clock, R.id.btn_send_dominant_hand,
            R.id.btn_query_dominant_hand, R.id.btn_send_device_language,
            R.id.btn_query_device_language, R.id.btn_music_control,
            R.id.btn_send_other_message, R.id.btn_query_other_message,
            R.id.btn_send_sedentary_reminder, R.id.btn_query_sedentary_reminder,
            R.id.btn_send_display_watch_face, R.id.btn_query_display_watch_face,
            R.id.btn_start_measure_heart_rate, R.id.btn_stop_measure_heart_rate,
            R.id.btn_send_device_version, R.id.btn_query_device_version,
            R.id.btn_start_measure_blood_pressure, R.id.btn_stop_measure_blood_pressure,
            R.id.btn_sync_past_step, R.id.btn_sync_past_sleep, R.id.btn_sync_last_heart_rate,
            R.id.btn_firmware_upgrade, R.id.btn_send_today_weather, R.id.btn_send_future_weather,
            R.id.btn_open_24_hreat_rate, R.id.btn_close_24_hreat_rate,
            R.id.btn_query_today_hreat_rate, R.id.btn_query_yesterday_hreat_rate,
            R.id.btn_start_measure_blood_oxygen, R.id.btn_stop_measure_blood_oxygen,
            R.id.btn_send_device_function, R.id.btn_query_device_function,
            R.id.btn_query_movement_hreat_rate, R.id.btn_send_breathing_light,
            R.id.btn_query_breathing_light, R.id.btn_switch_background,
            R.id.btn_send_watch_face_layout, R.id.btn_query_watch_face_layout,
            R.id.btn_check_firmware, R.id.btn_start_measure_ecg, R.id.btn_stop_measure_ecg,
            R.id.btn_query_last_ecg, R.id.btn_query_today_steps_category,
            R.id.btn_query_yesterday_steps_category, R.id.btn_query_support_watch_face,
            R.id.btn_show_fatigue_driving})
    public void onViewClicked(View view) {
        //  if (!mBleConnection.isConnected()) {
        //  return;
        // }
        switch (view.getId()) {
            case R.id.btn_send_device_function:
                List<Integer> functionList = new ArrayList<>();
                functionList.add(CRPFunctionInfo.TIME_VIEW);
                functionList.add(CRPFunctionInfo.STEP_VIEW);
                functionList.add(CRPFunctionInfo.SLEEP_VIEW);
                functionList.add(CRPFunctionInfo.HR_VIEW);
                functionList.add(CRPFunctionInfo.TRAINING_VIEW);
                functionList.add(CRPFunctionInfo.BP_VIEW);
                functionList.add(CRPFunctionInfo.BO_VIEW);
                functionList.add(CRPFunctionInfo.WEATHER_VIEW);
                functionList.add(CRPFunctionInfo.MSG_LIST_VIEW);
                functionList.add(CRPFunctionInfo.MUSIC_PLAYER_VIEW);
                functionList.add(CRPFunctionInfo.CAMERA_VIEW);
                functionList.add(CRPFunctionInfo.OTHER_VIEW);
                CRPFunctionInfo functionInfo = new CRPFunctionInfo();
                functionInfo.setFunctionList(functionList);
                mBleConnection.sendDislpayDeviceFunction(functionInfo);
                break;
            case R.id.btn_query_device_function:
                mBleConnection.queryDisplayDeviceFunction(new CRPDeviceFunctionCallback() {
                    @Override
                    public void onFunctionChenge(CRPFunctionInfo info) {
                        for (Integer integer : info.getFunctionList()) {
                            Log.d(TAG, "function: " + integer.intValue());
                        }
                    }
                });
                break;
            case R.id.btn_query_firmware:
                mBleConnection.queryFrimwareVersion(new CRPDeviceFirmwareVersionCallback() {
                    @Override
                    public void onDeviceFirmwareVersion(String version) {
                        DeviceActivity.this.bandFirmwareVersion = version;
                        updateTextView(tvFirmwareVersion, version);
                    }
                });
                break;
            case R.id.btn_check_firmware:
                mBleConnection.checkFirmwareVersion(
                        new CRPDeviceNewFirmwareVersionCallback() {
                            @Override
                            public void onNewFirmwareVersion(CRPFirmwareVersionInfo info) {
                                String version = info.getVersion();
                                updateTextView(tvNewFirmwareVersion, version);
                                Log.d(TAG, "onNewFirmwareVersion: " + version);
                                isUpgrade = true;
                            }

                            @Override
                            public void onLatestVersion() {
                                Log.d(TAG, "onLatestVersion");
                                isUpgrade = false;
                            }
                        },
                        DeviceActivity.this.bandFirmwareVersion,
                        CRPFirmwareUpgradeType.NORMAL_UPGEADE_TYPE);
                break;
            case R.id.btn_firmware_upgrade:
                if (isUpgrade) {
                    mBleConnection.startFirmwareUpgrade(mFirmwareUpgradeListener);
                }
                break;
            case R.id.btn_switch_background:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;

                Bitmap bitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.color_wheel, options);
                mBleConnection.sendWatchFaceBackground(bitmap, true, 177, new CRPWatchFaceTransListener() {
                    @Override
                    public void onTransProgressStarting() {
                        Log.d(TAG, "onTransProgressStarting");
                    }

                    @Override
                    public void onTransProgressChanged(int percent) {
                        Log.d(TAG, "percent: " + percent);
                    }

                    @Override
                    public void onTransCompleted() {
                        Log.d(TAG, "onTransCompleted");
                    }

                    @Override
                    public void onError(int i) {
                        Log.d(TAG, "onError: " + i);
                    }
                });

                break;
            case R.id.btn_query_battery:
                mBleConnection.queryDeviceBattery();
                break;
            case R.id.btn_sync_time:
                mBleConnection.syncTime();
                break;
            case R.id.btn_music_control:
                mBleConnection.setPhoneOperationListener(new CRPPhoneOperationListener() {
                    @Override
                    public void onOperationChange(int type) {
                        Log.d(TAG, "onOperationChange: " + type);
                    }
                });
                break;
            case R.id.btn_send_user_info:
                CRPUserInfo info = new CRPUserInfo(75, 178, CRPUserInfo.MALE, 24);
                mBleConnection.sendUserInfo(info);
                break;
            case R.id.btn_sync_step:
                mBleConnection.syncStep();
                testSet();
                break;
            case R.id.btn_sync_past_step:
                mBleConnection.syncPastStep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_STEPS);
                break;
            case R.id.btn_sync_sleep:
                mBleConnection.syncSleep();
                break;
            case R.id.btn_sync_past_sleep:
                mBleConnection.syncPastSleep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_SLEEP);
                break;
            case R.id.btn_send_metric_system:
                mBleConnection.sendMetricSystem(CRPMetricSystemType.METRIC_SYSTEM);
                break;
            case R.id.btn_query_metric_system:
                mBleConnection.queryMetricSystem(new CRPDeviceMetricSystemCallback() {
                    @Override
                    public void onMetricSystem(int type) {
                        Log.d(TAG, "onMetricSystem: " + type);
                    }
                });
                break;
            case R.id.btn_send_time_system:
                mBleConnection.sendTimeSystem(CRPTimeSystemType.TIME_SYSTEM_12);
                break;
            case R.id.btn_query_time_system:
                mBleConnection.queryTimeSystem(new CRPDeviceTimeSystemCallback() {
                    @Override
                    public void onTimeSystem(int type) {
                        Log.d(TAG, "onTimeSystem: " + type);
                    }
                });
                break;
            case R.id.btn_send_quick_view:
                mBleConnection.sendQuickView(false);
                break;
            case R.id.btn_query_quick_view:
                mBleConnection.queryQuickView(new CRPDeviceQuickViewCallback() {
                    @Override
                    public void onQuickView(boolean state) {
                        Log.d(TAG, "onQuickView: " + state);
                    }
                });
                break;
            case R.id.btn_send_goal_step:
                mBleConnection.sendGoalSteps(10000);
                break;
            case R.id.btn_query_goal_step:
                mBleConnection.queryGoalStep(new CRPDeviceGoalStepCallback() {
                    @Override
                    public void onGoalStep(int steps) {
                        Log.d(TAG, "onGoalStep: " + steps);
                    }
                });
                break;
            case R.id.btn_find_band:
                mBleConnection.findDevice();
                break;
            case R.id.btn_send_message:
                CRPMessageInfo messageInfo = new CRPMessageInfo();
                messageInfo.setHs(false);
                messageInfo.setSmallScreen(false);
                messageInfo.setMessage("hello world!");
                messageInfo.setType(CRPBleMessageType.MESSAGE_PHONE);
                messageInfo.setVersionCode(177);
                break;
            case R.id.btn_send_alarm_clock:
                CRPAlarmClockInfo clockInfo = new CRPAlarmClockInfo();
                clockInfo.setId(CRPAlarmClockInfo.FIRST_CLOCK);
                clockInfo.setHour(14);
                clockInfo.setMinute(24);
                clockInfo.setRepeatMode(CRPAlarmClockInfo.SINGLE);
                clockInfo.setDate(new Date());
                clockInfo.setEnable(true);
                mBleConnection.sendAlarmClock(clockInfo);
                break;
            case R.id.btn_query_alarm_clock:
                mBleConnection.queryAllAlarmClock(new CRPDeviceAlarmClockCallback() {
                    @Override
                    public void onAlarmClock(List<CRPAlarmClockInfo> list) {
                        for (CRPAlarmClockInfo alarmClockInfo : list) {
                            Date date = alarmClockInfo.getDate();
                            if (date != null) {
                                Log.d(TAG, "onAlarmClock: " + date.toString());
                            }
                        }
                    }
                });
                break;
            case R.id.btn_send_device_language:
                mBleConnection.sendDeviceLanguage(CRPDeviceLanguageType.LANGUAGE_UKRAINIAN);
                break;
            case R.id.btn_query_device_language:
                mBleConnection.queryDeviceLanguage(new CRPDeviceLanguageCallback() {
                    @Override
                    public void onDeviceLanguage(int type, int[] languageArray) {
                        Log.d(TAG, "onDeviceLanguage display language: " + type);
                        for (int i : languageArray) {
                            Log.d(TAG, "onDeviceLanguage languageArray " + i);
                        }
                    }
                });
                break;
            case R.id.btn_send_other_message:
                mBleConnection.sendOtherMessageState(true);
                break;
            case R.id.btn_query_other_message:
                mBleConnection.queryOtherMessageState(new CRPDeviceOtherMessageCallback() {
                    @Override
                    public void onOtherMessage(boolean state) {
                        Log.d(TAG, "onOtherMessage: " + state);
                    }
                });
                break;
            case R.id.btn_send_sedentary_reminder:
                mBleConnection.sendSedentaryReminder(true);
                break;
            case R.id.btn_query_sedentary_reminder:
                mBleConnection.querySedentaryReminder(new CRPDeviceSedentaryReminderCallback() {
                    @Override
                    public void onSedentaryReminder(boolean state) {
                        Log.d(TAG, "onSedentaryReminder: " + state);
                    }
                });
                break;
            case R.id.btn_send_display_watch_face:
                mBleConnection.sendDisplayWatchFace(CRPWatchFaceType.FIRST_WATCH_FACE);
                break;
            case R.id.btn_query_display_watch_face:
                mBleConnection.queryDisplayWatchFace(new CRPDeviceDisplayWatchFaceCallback() {
                    @Override
                    public void onDisplayWatchFace(int type) {
                        Log.d(TAG, "onDisplayWatchFace: " + type);
                    }
                });
                break;
            case R.id.btn_start_measure_heart_rate:
                mBleConnection.startMeasureDynamicRate();
//                mBleConnection.startMeasureOnceHeartRate();
                break;
            case R.id.btn_stop_measure_heart_rate:
                mBleConnection.stopMeasureDynamicRtae();
//                mBleConnection.stopMeasureOnceHeartRate();
                break;
            case R.id.btn_sync_last_heart_rate:
//                mBleConnection.queryLastDynamicRate();
                mBleConnection.queryMovementHeartRate();
                break;
            case R.id.btn_open_24_hreat_rate:
                mBleConnection.enableTimingMeasureHeartRate(1);
                break;
            case R.id.btn_close_24_hreat_rate:
                mBleConnection.disableTimingMeasureHeartRate();
                break;
            case R.id.btn_query_today_hreat_rate:
                mBleConnection.queryTodayHeartRate(CRPHeartRateType.ALL_DAY_HEART_RATE);
                break;
            case R.id.btn_query_yesterday_hreat_rate:
                mBleConnection.queryPastHeartRate();
                break;
            case R.id.btn_query_movement_hreat_rate:
                mBleConnection.queryMovementHeartRate();
                break;
            case R.id.btn_send_device_version:
                mBleConnection.sendDeviceVersion(CRPDeviceVersionType.INTERNATIONAL_EDITION);
                break;
            case R.id.btn_query_device_version:
                mBleConnection.queryDeviceVersion(new CRPDeviceVersionCallback() {
                    @Override
                    public void onDeviceVersion(int version) {
                        Log.d(TAG, "onDeviceVersion: " + version);
                    }
                });
                break;
            case R.id.btn_start_measure_blood_pressure:
                mBleConnection.startMeasureBloodPressure();
                break;
            case R.id.btn_stop_measure_blood_pressure:
                mBleConnection.stopMeasureBloodPressure();
                break;
            case R.id.btn_send_today_weather:
                CRPTodayWeatherInfo todayWeatherInfo = new CRPTodayWeatherInfo();
                todayWeatherInfo.setCity("深圳");
                todayWeatherInfo.setFestival("");
                todayWeatherInfo.setLunar("六月十四");
                todayWeatherInfo.setPm25(14);
                todayWeatherInfo.setTemp(27);
                todayWeatherInfo.setWeatherId(CRPWeatherId.CLOUDY);
                mBleConnection.sendTodayWeather(todayWeatherInfo);
                break;
            case R.id.btn_send_future_weather:
                CRPFutureWeatherInfo futureWeatherInfo = new CRPFutureWeatherInfo();
                List<CRPFutureWeatherInfo.FutureBean> list = new ArrayList<>();

                for (int i = 0; i < 7; i++) {
                    CRPFutureWeatherInfo.FutureBean bean = new CRPFutureWeatherInfo.FutureBean();
                    bean.setWeatherId(300);
                    bean.setLowTemperature(26);
                    bean.setHighTemperature(31);
                    list.add(bean);
                }
                futureWeatherInfo.setFuture(list);
                mBleConnection.sendFutureWeather(futureWeatherInfo);
                break;
            case R.id.btn_start_measure_blood_oxygen:
                mBleConnection.startMeasureBloodOxygen();
                break;
            case R.id.btn_stop_measure_blood_oxygen:
                mBleConnection.stopMeasureBloodOxygen();
                break;
            case R.id.btn_send_breathing_light:
                mBleConnection.sendBreathingLight(true);
                break;
            case R.id.btn_query_breathing_light:
                mBleConnection.queryBreathingLight(new CRPDeviceBreathingLightCallback() {
                    @Override
                    public void onBreathingLight(boolean enable) {
                        Log.d(TAG, "onBreathingLight: " + enable);
                    }
                });
                break;
            case R.id.btn_send_watch_face_layout:
                CRPWatchFaceLayoutInfo watchFaceLayoutInfo = new CRPWatchFaceLayoutInfo();
                watchFaceLayoutInfo.setTimePosition(CRPWatchFaceLayoutType.WATCH_FACE_TIME_BOTTOM);
                watchFaceLayoutInfo.setTimeTopContent(CRPWatchFaceLayoutType.WATCH_FACE_CONTENT_SLEEP);
                watchFaceLayoutInfo.setTimeBottomContent(CRPWatchFaceLayoutType.WATCH_FACE_CONTENT_STEP);
                int color = ContextCompat.getColor(this, R.color.color_watch_face_text_blue);
                watchFaceLayoutInfo.setTextColor(color);
                watchFaceLayoutInfo.setBackgroundPictureMd5(CRPWatchFaceLayoutType.DEFAULT_WATCH_FACE_BG_MD5);

                mBleConnection.sendWatchFaceLayout(watchFaceLayoutInfo);
                break;
            case R.id.btn_query_watch_face_layout:
                mBleConnection.queryWatchFaceLayout(new CRPDeviceWatchFaceLayoutCallback() {
                    @Override
                    public void onWatchFaceLayoutChange(CRPWatchFaceLayoutInfo info) {
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTimePosition());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTimeTopContent());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTimeBottomContent());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTextColor());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getBackgroundPictureMd5());
                    }
                });
                break;
            case R.id.btn_start_measure_ecg:
                mBleConnection.startECGMeasure();
                break;
            case R.id.btn_stop_measure_ecg:
                mBleConnection.stopECGMeasure();
                break;
            case R.id.btn_query_last_ecg:
                mBleConnection.queryLastMeasureECGData();
                break;
            case R.id.btn_query_today_steps_category:
                mBleConnection.queryStepsCategory(CRPStepsCategoryDateType.TODAY_STEPS_CATEGORY);
                break;
            case R.id.btn_query_yesterday_steps_category:
                mBleConnection.queryStepsCategory(CRPStepsCategoryDateType.YESTERDAY_STEPS_CATEGORY);
                break;
            case R.id.btn_query_support_watch_face:
                mBleConnection.querySupportWatchFace(new CRPDeviceSupportWatchFaceCallback() {
                    @Override
                    public void onSupportWatchFace(CRPSupportWatchFaceInfo info) {
                        for (Integer integer : info.getSupportWatchFaceList()) {
                            Log.d(TAG, "onSupportWatchFace: " + integer.intValue());
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    CRPStepsCategoryChangeListener mStepsCategoryChangeListener = new CRPStepsCategoryChangeListener() {
        @Override
        public void onStepsCategoryChange(CRPStepsCategoryInfo info) {
            List<Integer> stepsList = info.getStepsList();
            Log.d(TAG, "onStepsCategoryChange size: " + stepsList.size());
            for (int i = 0; i < stepsList.size(); i++) {
                Log.d(TAG, "onStepsCategoryChange: " + stepsList.get(i).intValue());
            }
        }
    };

    CRPStepChangeListener mStepChangeListener = new CRPStepChangeListener() {
        @Override
        public void onStepChange(CRPStepInfo info) {
            Log.d(TAG, "step: " + info.getSteps());
            updateStepInfo(info.getSteps(), info.getDistance(), info.getCalories());
        }

        @Override
        public void onPastStepChange(int timeType, CRPStepInfo info) {
            Log.d(TAG, "onPastStepChange: " + info.getSteps());

        }
    };

    CRPSleepChangeListener mSleepChangeListener = new CRPSleepChangeListener() {
        @Override
        public void onSleepChange(CRPSleepInfo info) {
            List<CRPSleepInfo.DetailBean> details = info.getDetails();
            if (details == null) {
                return;
            }
            Log.d(TAG, "soberTime: " + info.getSoberTime());
            updateSleepInfo(info.getRestfulTime(), info.getLightTime());
        }

        @Override
        public void onPastSleepChange(int timeType, CRPSleepInfo info) {
            Log.d(TAG, "onPastSleepChange: " + info.getTotalTime());
        }
    };

    CRPHeartRateChangeListener mHeartRateChangListener = new CRPHeartRateChangeListener() {

        @Override
        public void onMeasuring(int rate) {
            Log.d(TAG, "onMeasuring: " + rate);
            updateTextView(tvHeartRate, String.format(getString(R.string.string_heart_rate), rate));
        }

        @Override
        public void onOnceMeasureComplete(int rate) {
            Log.d(TAG, "onOnceMeasureComplete: " + rate);
        }

        @Override
        public void onMeasureComplete(CRPHeartRateInfo info) {
            if (info != null && info.getMeasureData() != null) {
                for (Integer integer : info.getMeasureData()) {
                    Log.d(TAG, "onMeasureComplete: " + integer);
                }
            }
        }

        @Override
        public void on24HourMeasureResult(CRPHeartRateInfo info) {
            List<Integer> data = info.getMeasureData();
            Log.d(TAG, "on24HourMeasureResult: " + data.size());
        }

        @Override
        public void onMovementMeasureResult(List<CRPMovementHeartRateInfo> list) {
            for (CRPMovementHeartRateInfo info : list) {
                if (info != null) {
                    Log.d(TAG, "onMovementMeasureResult: " + info.getStartTime());
                }
            }
        }

    };

    CRPBloodPressureChangeListener mBloodPressureChangeListener = new CRPBloodPressureChangeListener() {

        @Override
        public void onBloodPressureChange(int sbp, int dbp) {
            Log.d(TAG, "sbp: " + sbp + ",dbp: " + dbp);
            updateTextView(tvBloodPressure,
                    String.format(getString(R.string.watch_blood_pressure), sbp, dbp));
        }
    };

    CRPBloodOxygenChangeListener mBloodOxygenChangeListener = new CRPBloodOxygenChangeListener() {
        @Override
        public void onBloodOxygenChange(int bloodOxygen) {
            updateTextView(tvBloodOxygen,
                    String.format(getString(R.string.blood_oxygen), bloodOxygen));
        }
    };

    CRPBleECGChangeListener mECGChangeListener = new CRPBleECGChangeListener() {
        @Override
        public void onECGChange(int[] ecg) {
            for (int i = 0; i < ecg.length; i++) {
                Log.d(TAG, "ecg: " + ecg[i]);
            }
        }

        @Override
        public void onMeasureComplete() {
            Log.d(TAG, "onMeasureComplete");
        }

        @Override
        public void onTransCpmplete(Date date) {
            Log.d(TAG, "onTransCpmplete");
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    };


    private void queryLastMeasureECGData() {
        this.mBleConnection.queryLastMeasureECGData();
    }


    CRPFindPhoneListener mFindPhoneListener = new CRPFindPhoneListener() {
        @Override
        public void onFindPhone() {
            Log.d(TAG, "onFindPhone");
        }

        @Override
        public void onFindPhoneComplete() {
            Log.d(TAG, "onFindPhoneComplete");
        }
    };

    CRPBleFirmwareUpgradeListener mFirmwareUpgradeListener = new CRPBleFirmwareUpgradeListener() {
        @Override
        public void onFirmwareDownloadStarting() {
            Log.d(TAG, "onFirmwareDownloadStarting");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_download_starting));
        }

        @Override
        public void onFirmwareDownloadComplete() {
            Log.d(TAG, "onFirmwareDownloadComplete");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_download_complete));
        }

        @Override
        public void onUpgradeProgressStarting() {
            Log.d(TAG, "onUpgradeProgressStarting");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_starting));
        }

        @Override
        public void onUpgradeProgressChanged(int percent, float speed) {
            Log.d(TAG, "onUpgradeProgressChanged: " + percent);
            String status = String.format(getString(R.string.dfu_status_uploading_part), percent);
            updateTextView(tvUpgradeState, status);
        }

        @Override
        public void onUpgradeCompleted() {
            Log.d(TAG, "onUpgradeCompleted");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_completed));
            mBleConnection.abortFirmwareUpgrade();
            isUpgrade = true;
        }

        @Override
        public void onUpgradeAborted() {
            Log.d(TAG, "onUpgradeAborted");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_aborted));
        }

        @Override
        public void onError(int errorType, String message) {
            Log.d(TAG, "onError: " + errorType);
            updateTextView(tvUpgradeState, message);
            mBleConnection.abortFirmwareUpgrade();
        }
    };


    void updateStepInfo(int step, int distance, int calories) {
        updateTextView(tvStep, String.format(getString(R.string.step), step));
        updateTextView(tvDistance, String.format(getString(R.string.distance), distance));
        updateTextView(tvCalorie, String.format(getString(R.string.calorie), calories));
    }

    void updateSleepInfo(int restful, int light) {
        updateTextView(tvRestful, String.format(getString(R.string.restful), restful));
        updateTextView(tvLight, String.format(getString(R.string.light), light));
    }

    void updateConnectState(final int state) {
        if (state < 0) {
            return;
        }
        updateTextView(tvConnectState, getString(state));
    }

    void updateTextView(final TextView view, final String con) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(con);
            }
        });
    }

}
