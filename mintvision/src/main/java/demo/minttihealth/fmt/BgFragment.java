package demo.minttihealth.fmt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.TestPaper;
import com.linktop.infs.OnTestPaperResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.TestPaperTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import demo.minttihealth.bean.Bg;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentBgBinding;

/**
 * Created by ccl on 2018/1/23.
 * 血糖
 */

public class BgFragment extends MeasureFragment implements OnTestPaperResultListener {

    protected final ObservableField<String> event = new ObservableField<>("");
    protected Bg model;
    protected TestPaperTask mTestPaperTask;
    protected String[] mTestPaperCodes;
    protected String mManufacturer;

    public BgFragment() {
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_bg;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentBgBinding binding = (FragmentBgBinding) viewDataBinding;
        binding.setContent(this);
        this.btnMeasure = binding.btnMeasure;
        model = new Bg();
        binding.setModel(model);
        binding.setEvent(event);
        String[] manufacturers = TestPaper.Manufacturer.values();
        ArrayAdapter<String> adapterManufacturer = new ArrayAdapter<>(getContext()
                , android.R.layout.simple_spinner_dropdown_item
                , getManufacturerList(manufacturers));
        binding.spinTestPaperManufacturer.setAdapter(adapterManufacturer);
        binding.spinTestPaperManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mManufacturer = manufacturers[position];
                mTestPaperCodes = TestPaper.Code.values(mManufacturer, getTestPaperMeasureType());
                ArrayAdapter<String> adapterTestPaper = new ArrayAdapter<>(getContext()
                        , android.R.layout.simple_spinner_dropdown_item
                        , Arrays.asList(mTestPaperCodes));
                binding.spinTestPaperCode.setAdapter(adapterTestPaper);
                if (TestPaper.Manufacturer.YI_CHENG.equals(mManufacturer)) {
                    //Default value select TestPaperCode.C20.
                    binding.spinTestPaperCode.setSelection(TestPaper.Code.indexOf(mTestPaperCodes
                            , TestPaper.Code.C20));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinTestPaperCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MonitorDataTransmissionManager.getInstance().setTestPaper(
                        getTestPaperMeasureType(), TestPaper.create(
                                mManufacturer, mTestPaperCodes[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinTestPaperManufacturer.setSelection(TestPaper.Manufacturer.indexOf(TestPaper.Manufacturer.YI_CHENG));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHcService != null) {
            mTestPaperTask = mHcService.getBleDevManager().getTestPaperTask();
            mTestPaperTask.setTestPaperResultListener(getTestPaperMeasureType(), this);
        } else {
            MonitorDataTransmissionManager.getInstance().setOnTestPaperResultListener(getTestPaperMeasureType(), this);
        }
    }

    @Override
    public boolean startMeasure() {
        model.setTs(System.currentTimeMillis());
        if (mTestPaperTask != null) {
            if (mTestPaperTask.isModuleExist()) {
                mTestPaperTask.start(getTestPaperMeasureType());
                return true;
            } else {
                toast("This Device's Test Paper module is not exist.");
                return false;
            }
        } else {
            if (MonitorDataTransmissionManager.getInstance().isTestPaperModuleExist()) {
                MonitorDataTransmissionManager.getInstance().startMeasure(getTestPaperMeasureType());
                return true;
            } else {
                toast("This Device's Test Paper module is not exist.");
                return false;
            }
        }
    }

    @Override
    public void stopMeasure() {
        if (mTestPaperTask != null) {
            mTestPaperTask.stop();
        } else {
            MonitorDataTransmissionManager.getInstance().stopMeasure();
        }
        event.set("");
    }

    @Override
    public int getTitle() {
        return R.string.blood_glucose;
    }

    @Override
    public void reset() {
        model.reset();
    }

    /**
     * 返回事件
     */
    @Override
    public void onTestPaperEvent(int eventId, Object obj) {
        switch (eventId) {
            case TestPaperTask.EVENT_PAPER_IN:
                event.set(getString(R.string.test_paper_inserted));
                break;
            case TestPaperTask.EVENT_PAPER_READ:
                event.set(getString(R.string.test_paper_ready));
                break;
            case TestPaperTask.EVENT_BLOOD_SAMPLE_DETECTING:
                event.set(getString(R.string.test_paper_value_calculating));
                break;
            case TestPaperTask.EVENT_TEST_RESULT:
                event.set(getString(R.string.test_paper_result));
                model.setValue((double) obj);
                resetState();
                break;
            default:
                Log.e("onTestPaperEvent", "eventId:" + eventId + ", obj:" + obj);
                break;
        }
    }

    /**
     * 返回异常
     * 当返回事件{@link TestPaperTask#EVENT_PAPER_IN} 后任意时候拔出试条，则报异常{@link TestPaperTask#EXCEPTION_PAPER_OUT};
     * 当返回事件{@link TestPaperTask#EVENT_PAPER_IN} 后设备检测到试条是被使用过的，则报异常{@link TestPaperTask#EXCEPTION_PAPER_USED};
     * 当返回事件{@link TestPaperTask#EVENT_BLOOD_SAMPLE_DETECTING}后60secs內未计算出结果，则报异常{@link TestPaperTask#EXCEPTION_TIMEOUT_FOR_DETECT_BLOOD_SAMPLE};
     * 所有异常上报的同时，SDK内部将自动结束血糖的测试。
     */
    @Override
    public void onTestPaperException(int exception) {
        switch (exception) {
            case TestPaperTask.EXCEPTION_PAPER_OUT:
                toast(R.string.test_paper_is_not_inserted);
                break;
            case TestPaperTask.EXCEPTION_PAPER_USED:
                toast(R.string.test_paper_is_used);
                break;
            case TestPaperTask.EXCEPTION_TESTING_PAPER_OUT:
                toast(R.string.test_paper_out);
                break;
//            case BgTask.EXCEPTION_TIMEOUT_FOR_CHECK_BLOOD_SAMPLE:
//                toast(R.string.collecting_sample_timeout);
//                break;
            case TestPaperTask.EXCEPTION_TIMEOUT_FOR_DETECT_BLOOD_SAMPLE:
                toast(R.string.calculate_bg_value_timeout);
                break;
            default:
                Log.e("onTestPaperException", "exception:" + exception);
                break;
        }
        event.set("");
        resetState();
    }

    protected int getTestPaperMeasureType() {
        return MeasureType.BG;
    }

    private List<String> getManufacturerList(String[] array) {
        List<String> list = new ArrayList<>();
        for (String name : array) {
            switch (name) {
                case TestPaper.Manufacturer.BENE_CHECK:
                    list.add(getString(R.string.manufacturer_bene_check));
                    break;
                case TestPaper.Manufacturer.YI_CHENG:
                    list.add(getString(R.string.manufacturer_yi_cheng));
                    break;
            }
        }
        return list;
    }
}
