package demo.minttihealth.fmt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.Constants;
import com.linktop.constant.IUserProfile;
import com.linktop.infs.OnEcgResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.EcgTask;

import demo.minttihealth.activity.ECGLargeActivity;
import demo.minttihealth.bean.ECG;
import demo.minttihealth.bean.UserProfile;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentEcgBinding;
import demo.minttihealth.utils.AlertDialogBuilder;
import demo.minttihealth.widget.WaveSurfaceView;
import demo.minttihealth.widget.wave.ECGDrawWave;

/**
 * Created by ccl on 2017/2/7.
 */

public class ECGFragment extends MeasureFragment implements OnEcgResultListener {

    public final static int EVENT_PAPER_SPEED = 1;
    public final static int EVENT_GAIN = 2;
    public final static int EVENT_FINGER_DETECT = 3;
    public final static int EVENT_SQ = 4;

    private static final String TAG = "ECGFragment";

    protected ECG model;
    private final ObservableArrayMap<Integer, String> event = new ObservableArrayMap<>();
    private final ObservableBoolean toggleCountDown = new ObservableBoolean(false);
    private final StringBuilder ecgWaveBuilder = new StringBuilder();
    private EcgTask mEcgTask;
    private WaveSurfaceView waveView;
    private ECGDrawWave ecgDrawWave;

    public ECGFragment() {
    }

    @Override
    public boolean startMeasure() {
        event.put(EVENT_FINGER_DETECT, "");
        event.put(EVENT_SQ, "");
        if (mEcgTask != null) {
            if (mEcgTask.isModuleExist()) {
                waveView.reply();
                mEcgTask.start();
                return true;
            } else {
                toast("This Device's ECG module is not exist.");
                return false;
            }
        } else {
            if (MonitorDataTransmissionManager.getInstance().isEcgModuleExist()) {
                waveView.reply();
                MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.ECG
                        //if you want "onDrawWave(Object data)" output raw data,config this:
//                        , Pair.create(Constants.CONFIG_ECG_OUTPUT_RAW_DATA, true)
                        // if you want "onDrawWave(Object data)" output array data,config this:
//                        , Pair.create(Constants.CONFIG_ECG_OUTPUT_ARRAY_DATA, true)
                );
                return true;
            } else {
                toast("This Device's ECG module is not exist.");
                return false;
            }
        }
    }

    @Override
    public void stopMeasure() {
        waveView.pause();
        if (mEcgTask != null) {
            mEcgTask.stop();
        } else {
            MonitorDataTransmissionManager.getInstance().stopMeasure();
        }
    }

    @Override
    public int getTitle() {
        return R.string.ecg;
    }


    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_ecg;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentEcgBinding binding = (FragmentEcgBinding) viewDataBinding;
        binding.setContent(this);
        this.btnMeasure = binding.btnMeasure;
        model = new ECG();
        binding.setModel(model);
        ecgDrawWave = new ECGDrawWave();
        ecgDrawWave.setPaperSpeed(ECGDrawWave.PaperSpeed.VAL_25MM_PER_SEC);
        ecgDrawWave.setGain(ECGDrawWave.Gain.VAL_10MM_PER_MV);
        waveView = binding.waveView;
        waveView.setDrawWave(ecgDrawWave);
        binding.setEvent(event);
        binding.setToggleCountDown(toggleCountDown);
        binding.countDown.setOnCountDownFinishCallback(() -> {
            toggleCountDown.set(false);
            onEcgDuration(30L);
        });
        event.put(EVENT_PAPER_SPEED, ecgDrawWave.getPaperSpeed().getDesc());
        event.put(EVENT_GAIN, ecgDrawWave.getGain().getDesc());
        event.put(EVENT_FINGER_DETECT, "");
        event.put(EVENT_SQ, "");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IUserProfile userProfileDefault = new UserProfile("ccl", UserProfile.MALE
                , 633715200, 170, 60);
        if (mHcService != null) {
            if (null == mHcService.getBleDevManager().getUserProfile()) {
                mHcService.getBleDevManager().setUserProfile(userProfileDefault);
            }
            mEcgTask = mHcService.getBleDevManager().getEcgTask();
            mEcgTask.setOnEcgResultListener(this);
            //Import user profile makes the result more accurate.
        } else {
            if (MonitorDataTransmissionManager.getInstance().getUserProfile() == null) {
                MonitorDataTransmissionManager.getInstance().setUserProfile(userProfileDefault);
            }
            MonitorDataTransmissionManager.getInstance().setOnEcgResultListener(this);
        }
        waveView.pause();
    }

    @Override
    public void reset() {
        model.reset();
        ecgWaveBuilder.setLength(0);
        ecgDrawWave.clear();
    }


    /*
     * 心电图数据点
     * */
    @Override
    public void onDrawWave(Object data) {
        //将数据点在心电图控件里描绘出来
        if (data instanceof int[]) {
//            Log.e("CCL", "onDrawWave -> array");
            int[] rawDataArray = (int[]) data;
            for (int d : rawDataArray) {
                //将数据点在心电图控件里描绘出来
                ecgDrawWave.addData(d);
                //将数据点存入容器，查看大图使用
                ecgWaveBuilder.append(data).append(",");
            }
        } else if (data instanceof Integer) {
//            Log.e("CCL", "onDrawWave -> int");
            int d = (Integer) data;
            ecgDrawWave.addData(d);
            //将数据点存入容器，查看大图使用
            ecgWaveBuilder.append(data).append(",");
        }
    }

    @Override
    public void onSignalQuality(@Constants.ECGSignalQuality int quality) {
        event.put(EVENT_SQ, formatSignalQuality(quality));
    }

    @Override
    public void onECGValues(@Constants.ECGKey int key, int value) {
        switch (key) {
            case Constants.ECG_KEY_HEART_RATE:
                model.setHr(value);
                break;
            case Constants.ECG_KEY_HRV:
                model.setHrv(value);
                break;
            case Constants.ECG_KEY_MOOD:
                model.setMood(value);
                break;
            case Constants.ECG_KEY_RESPIRATORY_RATE://Respiratory rate.
                model.setRr(value);
                break;
            case Constants.ECG_KEY_R2R:
                model.setR2r(value);
                break;
            case Constants.ECG_KEY_HEART_AGE:
                model.setHa(value);
                break;
            case Constants.ECG_KEY_HEART_BEAT:
                model.setHb(value);
                break;
            case Constants.ECG_KEY_ROBUST_HR:
                model.setRhr(value);
                break;
            case Constants.ECG_KEY_STRESS:
                model.setStress(value);
                break;
            default:
                break;
        }
    }

    /**
     * 心电图测量持续时间,该回调一旦触发说明一次心电图测量结束
     */
    private void onEcgDuration(long duration) {
        stopMeasure();
        model.setDuration(duration);
        model.setTs(System.currentTimeMillis() / 1000L);
        String ecgWave = ecgWaveBuilder.toString();
        ecgWave = ecgWave.substring(0, ecgWave.length() - 1);
        model.setWave(ecgWave);
        event.put(EVENT_FINGER_DETECT, "");
        resetState();
    }

    @Override
    public void onFingerDetection(boolean fingerDetected) {
        event.put(EVENT_FINGER_DETECT, fingerDetected ? getString(R.string.finger_detection_ok)
                : getString(R.string.finger_detection_fail));
        if (fingerDetected) {
            if (!toggleCountDown.get()) {
                ecgDrawWave.clear();
                model.reset();
            }
            toggleCountDown.set(true);
        } else {
            toggleCountDown.set(false);
        }
    }

    public void openECGLarge(View v) {
        Intent intent = new Intent(mActivity, ECGLargeActivity.class);
        intent.putExtra("paperSpeed", ecgDrawWave.getPaperSpeed());
        intent.putExtra("gain", ecgDrawWave.getGain());
        intent.putExtra("model", model);
        startActivity(intent);
    }

    /*
     * 点击设置时间基准(走纸速度)
     * 该值反应心电图x轴的幅度，设置的值这里没做保存，请自行保存，以便下次启动该页面时自动设置已保存的值
     * */
    public void clickSetPagerSpeed(View v) {
        int checkedItem = 0;
        final ECGDrawWave.PaperSpeed[] paperSpeeds = ECGDrawWave.PaperSpeed.values();
        for (int i = 0; i < paperSpeeds.length; i++) {
            if (paperSpeeds[i].equals(ecgDrawWave.getPaperSpeed())) {
                checkedItem = i;
                break;
            }
        }
        onShowSingleChoiceDialog(R.string.paper_speed, paperSpeeds, checkedItem
                , (dialog, which) -> {
                    ECGDrawWave.PaperSpeed paperSpeed = paperSpeeds[which];
                    ecgDrawWave.setPaperSpeed(paperSpeed);
                    event.put(EVENT_PAPER_SPEED, paperSpeed.getDesc());
                    dialog.dismiss();
                });
    }

    /*
     * 点击设置增益
     * 该值反应心电图y轴的幅度，设置的值这里没做保存，请自行保存，以便下次启动该页面时自动设置已保存的值
     * */
    public void clickSetGain(View v) {
        int checkedItem = 0;
        final ECGDrawWave.Gain[] gains = ECGDrawWave.Gain.values();
        for (int i = 0; i < gains.length; i++) {
            if (gains[i].equals(ecgDrawWave.getGain())) {
                checkedItem = i;
                break;
            }
        }
        onShowSingleChoiceDialog(R.string.gain, gains, checkedItem
                , (dialog, which) -> {
                    ECGDrawWave.Gain gain = gains[which];
                    ecgDrawWave.setGain(gain);
                    event.put(EVENT_GAIN, gain.getDesc());
                    dialog.dismiss();
                });
    }

    private void onShowSingleChoiceDialog(@StringRes int titleResId, ECGDrawWave.ECGValImp[] arrays
            , int checkedItem, DialogInterface.OnClickListener listener) {
        int length = arrays.length;
        CharSequence[] items = new CharSequence[length];
        for (int i = 0; i < length; i++) {
            items[i] = arrays[i].getDesc();
        }
        new AlertDialogBuilder(mActivity)
                .setTitle(titleResId)
                .setSingleChoiceItems(items, checkedItem, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    @NonNull
    private static String formatSignalQuality(@Constants.ECGSignalQuality int quality) {
        switch (quality) {
            case Constants.ECG_SQ_POOR:
                return "Poor";
            case Constants.ECG_SQ_MEDIUM:
                return "Medium";
            case Constants.ECG_SQ_GOOD:
                return "Good";
            case Constants.ECG_SQ_NOT_DETECTED:
                return "Not detected.";
            default:
                return "Unknown, code = " + quality;
        }
    }
}
