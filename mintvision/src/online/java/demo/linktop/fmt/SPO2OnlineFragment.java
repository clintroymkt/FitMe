package demo.minttihealth.fmt;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import demo.minttihealth.health.BR;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentOnlineSpo2Binding;
import lib.linktop.obj.DataFile;
import lib.linktop.sev.HmLoadDataTool;

public class SPO2OnlineFragment extends SPO2Fragment implements IOnlineFragment {

    public SPO2OnlineFragment() {
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_online_spo2;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentOnlineSpo2Binding binding = (FragmentOnlineSpo2Binding) viewDataBinding;
        binding.setVariable(BR.content, this);
        super.onViewBindingCreated(binding.fragmentSpo2, savedInstanceState);
    }

    @Override
    public void clickUploadData(View v) {
        if (model == null || model.isEmptyData()) {
            toast("不能上传空数据");
            return;
        }
        HmLoadDataTool.getInstance().uploadData(DataFile.DATA_SPO2, model);
    }
}
