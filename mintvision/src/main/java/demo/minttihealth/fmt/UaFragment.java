package demo.minttihealth.fmt;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.TestPaper;
import com.linktop.whealthService.MeasureType;

import java.util.Arrays;

import demo.minttihealth.bean.Bg;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentUaBinding;

/**
 * Created by ccl on 2020/12/09.
 * Uric Acid（尿酸）
 */
public class UaFragment extends BgFragment {

    public UaFragment() {
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_ua;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentUaBinding binding = (FragmentUaBinding) viewDataBinding;
        binding.setContent(this);
        this.btnMeasure = binding.btnMeasure;
        model = new Bg();
        binding.setModel(model);
        binding.setEvent(event);
        mManufacturer = TestPaper.Manufacturer.BENE_CHECK;
        mTestPaperCodes = TestPaper.Code.values(mManufacturer, getTestPaperMeasureType());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext()
                , android.R.layout.simple_spinner_dropdown_item
                , Arrays.asList(mTestPaperCodes));
        binding.spinTestPaperCode.setAdapter(adapter);
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
        binding.spinTestPaperCode.setSelection(0);
    }

    @Override
    public int getTitle() {
        return R.string.uric_acid;
    }

    @Override
    protected int getTestPaperMeasureType() {
        return MeasureType.UA;
    }
}
