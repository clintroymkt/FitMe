package demo.minttihealth.fmt;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentOnlineBtBinding;
import lib.linktop.obj.DataFile;
import lib.linktop.sev.HmLoadDataTool;

public class BtOnlineFragment extends BtFragment implements IOnlineFragment {


    public BtOnlineFragment() {
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_online_bt;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentOnlineBtBinding binding = (FragmentOnlineBtBinding) viewDataBinding;
        binding.setContent(this);
        super.onViewBindingCreated(binding.fragmentBt, savedInstanceState);
    }

    @Override
    public void clickUploadData(View v) {
        if (model == null || model.isEmptyData()) {
            toast("不能上传空数据");
            return;
        }
        HmLoadDataTool.getInstance().uploadData(DataFile.DATA_BT, model);
    }
}
