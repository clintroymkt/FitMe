package demo.minttihealth.fmt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import demo.minttihealth.activity.HealthMonitorActivity;
import demo.minttihealth.health.App;
import demo.minttihealth.health.HcService;
import demo.minttihealth.utils.UToast;

/**
 * Created by ccl on 2017/2/7.
 */

public abstract class BaseFragment extends Fragment {

    protected HealthMonitorActivity mActivity;
    protected HcService mHcService;

    public BaseFragment() {
    }

    @StringRes
    public abstract int getTitle();

    @LayoutRes
    protected abstract int onLayoutRes();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, onLayoutRes(), container
                , false);
        onViewBindingCreated(binding, savedInstanceState);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (HealthMonitorActivity) getActivity();
        if (App.isUseCustomBleDevService)
            mHcService = mActivity.mHcService;
    }

    protected abstract void onViewBindingCreated(ViewDataBinding viewDataBinding
            , @Nullable Bundle savedInstanceState);

    public abstract void reset();


    protected void toast(@NonNull String text) {
        UToast.show(getActivity(), text);
    }

    protected void toast(@StringRes int text) {
        UToast.show(getActivity(), text);
    }

}
