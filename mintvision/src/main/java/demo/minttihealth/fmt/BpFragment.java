package demo.minttihealth.fmt;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.infs.OnBpResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.BpTask;

import demo.minttihealth.bean.Bp;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentBpBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ccl on 2017/2/7.
 * BpFragment
 */

public class BpFragment extends MeasureFragment
        implements OnBpResultListener {

    protected Bp model;
    private BpTask mBpTask;

    public BpFragment() {
    }

    @Override
    public boolean startMeasure() {
        if (mBpTask != null) {
            if (mHcService.getBleDevManager().getBatteryTask().getPower() < 20) {
                toast("设备电量过低，请充电\nLow power.Please charge.");
                return false;
            }
            mBpTask.start();
        } else {
            if (MonitorDataTransmissionManager.getInstance().getBatteryValue() < 20) {
                toast("设备电量过低，请充电\nLow power.Please charge.");
                return false;
            }
            MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.BP);
        }
        return true;
    }

    @Override
    public void stopMeasure() {
        if (mBpTask != null) {
            mBpTask.stop();
        } else {
            MonitorDataTransmissionManager.getInstance().stopMeasure();
        }
    }

    @Override
    public int getTitle() {
        return R.string.blood_pressure;
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_bp;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding
            , @Nullable Bundle savedInstanceState) {
        FragmentBpBinding binding = (FragmentBpBinding) viewDataBinding;
        binding.setContent(this);
        this.btnMeasure = binding.btnMeasure;
        model = new Bp();
        binding.setModel(model);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHcService != null) {
            mBpTask = mHcService.getBleDevManager().getBpTask();
            mBpTask.setOnBpResultListener(this);
        } else {
            //设置血压测量回调接口
            MonitorDataTransmissionManager.getInstance().setOnBpResultListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void reset() {
        model.reset();
    }

    @Override
    public void onBpResult(final int systolicPressure, final int diastolicPressure, final int heartRate) {
        //测量时间（包括本demo其他测量项目的测量时间），既可以以点击按钮开始测试的那个时间为准，
        // 也可以以测量结果出来时为准，看需求怎么定义
        //这里demo演示，为了方便，采用后者。
        model.setTs(System.currentTimeMillis() / 1000L);
        model.setSbp(systolicPressure);
        model.setDbp(diastolicPressure);
        model.setHr(heartRate);
        resetState();
    }

    @Override
    public void onBpResultError() {
        toast(R.string.bp_result_error);
        resetState();
    }

    @Override
    public void onLeakError(int errorType) {
        resetState();
        Observable.just(errorType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(error -> {
                    int textId = 0;
                    switch (error) {
                        case 0:
                            textId = R.string.leak_and_check;
                            break;
                        case 1:
                            textId = R.string.measurement_void;
                            break;
                        default:
                            break;
                    }
                    if (textId != 0)
                        Toast.makeText(getContext(), getString(textId), Toast.LENGTH_SHORT).show();
                });
    }


//    public void clickSaveData(View v) {
//        MonitorDataTransmissionManager instance = MonitorDataTransmissionManager.getInstance();
//        if (instance.isMeasuring()) return;
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 666);
//            return;
//        }
//        if (model.getDbp() == 0 && model.getSbp() == 0 && model.getHr() == 0) return;
//        //建议将文件夹名定义成：BP_收缩压_舒张压_心率_测量时间
//        String folder = String.format(Locale.getDefault(), "Bp_%1$d_%2$d_%3$d_%4$s"
//                , model.getSbp(), model.getDbp(), model.getHr()
//                , new SimpleDateFormat("yyyyMMDDHHmmss"
//                        , Locale.getDefault()).format(model.getTs() * 1000L));
//        /**
//         * 测量数据保存，@RequiresPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE)
//         *
//         * @param type:MeasureType 测量类型（目前支持吃BP，其他无效，后续可能会增加）
//         * @param parentFolder:String SD卡根目录下建立的文件夹名称
//         * @param folder:String parentFolder内的文件夹名称，由于一次BP测量将保存多个文件，建议将BP文件夹名称
//         *              定义成类似如上的格式。
//         * @param listener:OnSaveDataListener 数据保存回调
//         *                onFinish() - 文件保存完成；
//         *                onError(String e) - 文件保存出错。
//         */
//        instance.saveData(MeasureType.BP, "HealthMonitor", folder
//                , new OnSaveDataListener() {
//                    @Override
//                    public void onFinish() {
//                        toast("Bp data save successful.");
//                    }
//
//                    @Override
//                    public void onError(String e) {
//                        toast("Bp data save error:" + e);
//                    }
//                });
//
//    }

}
