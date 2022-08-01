package demo.minttihealth.fmt;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.infs.OnBtResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.BtTask;

import demo.minttihealth.bean.Bt;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentBtBinding;

/**
 * Created by ccl on 2017/2/7.
 */

public class BtFragment extends MeasureFragment implements OnBtResultListener {

    private final ObservableBoolean isUnitF = new ObservableBoolean(false);
    protected Bt model;
    private BtTask mBtTask;

    public BtFragment() {
    }

    @Override
    public boolean startMeasure() {
        if (mBtTask != null) {
            mBtTask.start();
        } else {
            MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.BT);
        }
        return true;
    }

    @Override
    public void stopMeasure() {
        //BT module is not have method stop().Because it will return result in 2~4 seconds when you click to start measure.

//        if(mBtTask!= null) {
//            mBtTask.stop();
//        } else {
//            MonitorDataTransmissionManager.getInstance().stopMeasure();
//        }

    }

    @Override
    public int getTitle() {
        return R.string.body_temperature;
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_bt;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding
            , @Nullable Bundle savedInstanceState) {
        FragmentBtBinding binding = (FragmentBtBinding) viewDataBinding;
        binding.setContent(this);
        this.btnMeasure = binding.btnMeasure;
        model = new Bt();
        binding.setIsUnitF(isUnitF);
        binding.setModel(model);
        binding.switchUnit.setOnCheckedChangeListener((buttonView, isChecked) ->
                isUnitF.set(isChecked));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHcService != null) {
            mBtTask = mHcService.getBleDevManager().getBtTask();
            mBtTask.setOnBtResultListener(this);
        } else {
            MonitorDataTransmissionManager.getInstance().setOnBtResultListener(this);
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

    /*
     * 默认返回单位为摄氏度的温度值，若需要华氏度的温度值，根据公式转换
     * 本Demo使用Databinding的方式，详情请看该布局的温度显示控件TextView
     * */
    @Override
    public void onBtResult(double tempValue) {
        model.setTemp(tempValue);
        model.setTs(System.currentTimeMillis() / 1000L);
        resetState();
    }


}
