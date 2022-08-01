package demo.minttihealth.fmt;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.FragmentOnlineBpBinding;
import lib.linktop.obj.DataFile;
import lib.linktop.sev.HmLoadDataTool;

public class BpOnlineFragment extends BpFragment implements IOnlineFragment {

    public BpOnlineFragment() {
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_online_bp;
    }

    @Override
    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
        FragmentOnlineBpBinding binding = (FragmentOnlineBpBinding) viewDataBinding;
        binding.setContent(this);
        super.onViewBindingCreated(binding.fragmentBp, savedInstanceState);
    }


    @Override
    public void clickUploadData(View v) {
        if (model == null || model.isEmptyData()) {
            toast("不能上传空数据");
            return;
        }
        HmLoadDataTool.getInstance().uploadData(DataFile.DATA_BP, model
                //1.设置哪些测量数据要在推送中显示，SDK内已默认了推送参数的设定，app集成了推送后就可以在每次测量后收到相应推送，
                // 要推送哪些参数，就是类似如下方式设置了，若不满意可如下格式进行自定义设置。
//                , DataPair.create(DataKey.DESC_SBP, model.getSbp() + "mmHg")
//                , DataPair.create(DataKey.DESC_DBP, model.getDbp() + "mmHg")
//                , DataPair.create(DataKey.DESC_HR, model.getHr() + "bpm")
                // 2.若不想要有推送，这样设置
//                , (DataPair[]) null
        );
    }
}
