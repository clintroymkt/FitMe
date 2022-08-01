package demo.minttihealth.fmt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.linktop.constant.UserDataType;

import java.util.Arrays;

import demo.minttihealth.health.R;

/**
 * Created by ccl on 2017/5/2.
 * 用户基本信息（性别、年龄、身高、体重、参考收缩压、参考舒张压）
 * <p>
 * 参考收缩压：用户平时测量的收缩压平均值
 * 参考舒张压：用户平时测量的舒张压平均值
 */

public class SetUserInfoDialogFragment extends DialogFragment implements View.OnFocusChangeListener, TextWatcher {

    private DialogInterface.OnClickListener positiveButtonListener;
    private int[] userHealthInfo;
    private View rootView;
    @UserDataType
    private int type = UserDataType.AGE;

    public SetUserInfoDialogFragment() {
        super();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_set_user_info, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", positiveButtonListener)
                .setView(rootView);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        boolean setDefaultValue = true;
        if (userHealthInfo == null) {
            setDefaultValue = false;
            userHealthInfo = new int[4];
            Arrays.fill(userHealthInfo, 0);
        }
        final RadioButton rb0 = rootView.findViewById(R.id.rb0);
        final RadioButton rb1 = rootView.findViewById(R.id.rb1);
        EditText etAge = rootView.findViewById(R.id.et_age);
        EditText etHeight = rootView.findViewById(R.id.et_height);
        EditText etWeight = rootView.findViewById(R.id.et_weight);
//        EditText etRefSBP = rootView.findViewById(R.id.et_ref_sbp);
//        EditText etRefDBP = rootView.findViewById(R.id.et_ref_dbp);
//        EditText etRefHR = rootView.findViewById(R.id.et_ref_hr);

        rb0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userHealthInfo[0] = 0;
                    rb1.setChecked(false);
                }
            }
        });
        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userHealthInfo[0] = 1;
                    rb0.setChecked(false);
                }
            }
        });

        if (setDefaultValue) {
            final boolean sex = userHealthInfo[UserDataType.SEX] == 0;
            final int age = userHealthInfo[UserDataType.AGE];
            final int height = userHealthInfo[UserDataType.HEIGHT];
            final int weight = userHealthInfo[UserDataType.WEIGHT];
//            final int sbp = userHealthInfo[UserDataType.SBP.index];
//            final int dbp = userHealthInfo[UserDataType.DBP.index];
//            final int hr = userHealthInfo[UserDataType.HR.index];
            rb0.setChecked(sex);
            rb1.setChecked(!sex);
            etAge.setText(age == 0 ? "" : String.valueOf(age));
            etHeight.setText(height == 0 ? "" : String.valueOf(height));
            etWeight.setText(weight == 0 ? "" : String.valueOf(weight));
//            etRefSBP.setText(sbp == 0 ? "" : String.valueOf(sbp));
//            etRefDBP.setText(dbp == 0 ? "" : String.valueOf(dbp));
//            etRefHR.setText(hr == 0 ? "" : String.valueOf(hr));
        }

        etAge.setOnFocusChangeListener(this);
        etHeight.setOnFocusChangeListener(this);
        etWeight.setOnFocusChangeListener(this);
//        etRefSBP.setOnFocusChangeListener(this);
//        etRefDBP.setOnFocusChangeListener(this);
//        etRefHR.setOnFocusChangeListener(this);

        etAge.addTextChangedListener(this);
        etHeight.addTextChangedListener(this);
        etWeight.addTextChangedListener(this);
//        etRefSBP.addTextChangedListener(this);
//        etRefDBP.addTextChangedListener(this);
//        etRefHR.addTextChangedListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setOnPositiveListener(DialogInterface.OnClickListener positiveButtonListener) {
        this.positiveButtonListener = positiveButtonListener;
    }

    public int[] getUserHealthInfo() {
        return userHealthInfo;
    }

    public void setUserHealthInfo(int[] userHealthInfo) {
        this.userHealthInfo = userHealthInfo;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            int id = v.getId();
            if (R.id.et_age == id) {
                type = UserDataType.AGE;
            } else if (R.id.et_height == id) {
                type = UserDataType.HEIGHT;
            } else if (R.id.et_weight == id) {
                type = UserDataType.WEIGHT;
            } else {
                type = UserDataType.SEX;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (type != UserDataType.SEX) {
            final int value = Integer.parseInt(TextUtils.isEmpty(s) ? "0" : s.toString());
            userHealthInfo[type] = value;
            Log.e("onTextChanged", "type:" + type + ", value:" + value);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
