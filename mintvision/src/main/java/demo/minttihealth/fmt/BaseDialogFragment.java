package demo.minttihealth.fmt;


import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import demo.minttihealth.utils.UToast;

/**
 * Created by ccl on 2016/8/11.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    protected ViewDataBinding mBinding;

    private AlertDialog alertDialog;
    private Button positiveButton;
    private Button negativeButton;
    private Button neutralButton;
    protected OnUpdateListener listener;

    public BaseDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView;
        if (isDataBinding()) {
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), getLayoutResId(), null, false);
            rootView = mBinding.getRoot();
        } else {
            rootView = LayoutInflater.from(getActivity()).inflate(getLayoutResId(), null, false);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(rootView);
        if (getNeutralText() != 0) builder.setNeutralButton(getNeutralText(), null);
        if (getNegativeText() != 0) builder.setNegativeButton(getNegativeText(), null);
        if (getPositiveText() != 0) builder.setPositiveButton(getPositiveText(), null);
        alertDialog = builder.create();
        return alertDialog;
    }

    protected void setDialogTitle(@StringRes int title) {
        setDialogTitle(getString(title));
    }

    protected void setDialogTitle(String title) {
        if (alertDialog != null) {
            alertDialog.setTitle(title);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onInit();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (windowWidth$Height() != null) {
            final int[] ints = windowWidth$Height();
            final Window window = alertDialog.getWindow();
            if (window != null) window.setLayout(ints[0], ints[1]);
        }
        positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        neutralButton.setAllCaps(false);
        if (getPositiveListener() != null) positiveButton.setOnClickListener(getPositiveListener());
        if (getNegativeListener() != null) negativeButton.setOnClickListener(getNegativeListener());
        if (getNeutralListener() != null) neutralButton.setOnClickListener(getNeutralListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void show(FragmentManager manager, String tag) {
//        super.show(manager, tag);
//        mDismissed = false;
//        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }


    protected int[] windowWidth$Height() {
        return null;
    }

    protected abstract int getLayoutResId();

    protected abstract void onInit();

    protected boolean isDataBinding() {
        return false;
    }

    protected int getNeutralText() {
        return android.R.string.selectAll;
    }

    protected int getNegativeText() {
        return android.R.string.cancel;
    }

    protected int getPositiveText() {
        return android.R.string.ok;
    }

    protected void setPositiveText(String text) {
        if (positiveButton != null) positiveButton.setText(text);
    }

    protected void setNegativeText(String text) {
        if (negativeButton != null) negativeButton.setText(text);
    }

    protected void setNeutralText(String text) {
        if (neutralButton != null) neutralButton.setText(text);
    }

    protected void setPositiveText(int text) {
        if (positiveButton != null) positiveButton.setText(text);
    }

    protected void setNegativeText(int text) {
        if (negativeButton != null) negativeButton.setText(text);
    }

    protected void setNeutralText(int text) {
        if (neutralButton != null) neutralButton.setText(text);
    }

    protected View.OnClickListener getNeutralListener() {
        return null;
    }

    protected View.OnClickListener getNegativeListener() {
        return null;
    }

    protected View.OnClickListener getPositiveListener() {
        return null;
    }

    public boolean isShowing() {
        return alertDialog != null && alertDialog.isShowing();
    }

    public Button getPositiveButton() {
        return positiveButton;
    }


    protected void toast(@NonNull String text) {
        UToast.show(getActivity(), text);
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        this.listener = listener;
    }

    public interface OnUpdateListener {

        void onUpdateSuccess();

    }
}
