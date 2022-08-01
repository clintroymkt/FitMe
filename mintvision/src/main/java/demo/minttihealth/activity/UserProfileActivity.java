package demo.minttihealth.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.IUserProfile;

import java.util.Calendar;

import demo.minttihealth.bean.UserProfile;
import demo.minttihealth.health.R;
import demo.minttihealth.health.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends BaseActivity {

    private UserProfile userProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserProfileBinding binding = setBindingContentView(R.layout.activity_user_profile);
        setUpActionBar(getString(R.string.setting_user_information), true);
        IUserProfile iUserProfile = MonitorDataTransmissionManager.getInstance().getUserProfile();
        if (iUserProfile instanceof UserProfile) {
            userProfile = (UserProfile) iUserProfile;
        } else {
            userProfile = new UserProfile();
        }
        binding.setUserProfile(userProfile);
        binding.rgGender.setOnCheckedChangeListener((group, checkedId) ->
                userProfile.setGender(checkedId == R.id.rb_female ?
                        IUserProfile.FEMALE : IUserProfile.MALE));
    }

    public void clickSetBirthday(View v) {
        long birthday = userProfile.getBirthday();
        Calendar calendar = Calendar.getInstance();
        if (birthday != 0L) {
            calendar.setTimeInMillis(userProfile.getBirthday());
        }
        int yearInit = calendar.get(Calendar.YEAR);
        int monthInit = calendar.get(Calendar.MONTH);
        int dayOfMonthInit = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this
                , (view, year, month, dayOfMonth) -> {
            userProfile.setBirthday(year, month, dayOfMonth);
        }, yearInit, monthInit, dayOfMonthInit);
        datePickerDialog.show();
    }

    public void clickSetUserProfile(View v) {
        if (TextUtils.isEmpty(userProfile.getUsername())) {
            toast("Please insert username.");
            return;
        }
        if (0L == userProfile.getBirthday()) {
            toast("Please insert birthday.");
            return;
        }
        if (0L == userProfile.getHeight()) {
            toast("Please insert height.");
            return;
        }
        if (0L == userProfile.getWeight()) {
            toast("Please insert weight.");
            return;
        }
        MonitorDataTransmissionManager.getInstance().setUserProfile(userProfile);
        finish();
    }
}
