package demo.minttihealth.fmt;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.infs.OnSpO2ResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.OxTask;

import demo.minttihealth.bean.SpO2;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentSpo2Binding;
import demo.minttihealth.widget.wave.PPGDrawWave;

/**
 * Created by ccl on 2017/2/7.
 * SpO2测量界面
 */

public class SPO2Fragment extends MeasureFragment implements OnSpO2ResultListener {

    protected SpO2 model;
    private OxTask mOxTask;
    private PPGDrawWave oxWave;

    public SPO2Fragment() {
    }

    @Override
    public boolean startMeasure() {
        if (mOxTask != null) {
            mOxTask.start();
        } else {
            MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.SPO2);
        }
        return true;
    }

    @Override
    public void stopMeasure() {
        if (mOxTask != null) {
            mOxTask.stop();
        } else {
            MonitorDataTransmissionManager.getInstance().stopMeasure();
        }
    }

    @Override
    public int getTitle() {
        return R.string.spo2;
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_spo2;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentSpo2Binding binding = (FragmentSpo2Binding) viewDataBinding;
        binding.setContent(this);
        oxWave = new PPGDrawWave(4);
        binding.ppgWave.setDrawWave(oxWave);
        this.btnMeasure = binding.btnMeasure;
        model = new SpO2();
        binding.setModel(model);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHcService != null) {
            mOxTask = mHcService.getBleDevManager().getOxTask();
            mOxTask.setOnSpO2ResultListener(this);
        } else {
            MonitorDataTransmissionManager.getInstance().setOnSpO2ResultListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    int index;

    @Override
    public void reset() {
        index = 0;
        model.reset();
        oxWave.clear();
    }

    @Override
    public void onSpO2Result(int spo2, int hr) {
        model.setValue(spo2);
        model.setHr(hr);
    }

    @Override
    public void onSpO2Wave(int value) {
        oxWave.addData(value);
    }

    @Override
    public void onSpO2End() {
        model.setTs(System.currentTimeMillis() / 1000L);
        resetState();
    }

    @Override
    public void onFingerDetection(int state) {
        if (state == FINGER_NO_TOUCH) {
            stopMeasure();
            model.setValue(0);
            model.setHr(0);
            resetState();
            toast("No finger was detected on the SpO₂ sensor.");
        }
    }
}
