package demo.minttihealth.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.annotation.NonNull;

import demo.minttihealth.health.BR;

/**
 * Created by ccl on 2016/8/23.
 * AccInfo
 */
public class AccInfo extends BaseObservable {

    private String mobileNo = "";

    private String password = "";

    private String countryCode = "";

    private String verificationCode = "";

    public AccInfo() {
    }

    public void clear() {
        mobileNo = "";
        password = "";
        countryCode = "";
        verificationCode = "";
        notifyChange();
    }

    @Bindable
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        if (!this.mobileNo.equals(mobileNo)) {
            this.mobileNo = mobileNo;
            notifyPropertyChanged(BR.mobileNo);
        }
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!this.password.equals(password)) {
            this.password = password;
            notifyPropertyChanged(BR.password);
        }
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "AccInfo{" +
                "mobileNo='" + mobileNo + '\'' +
                ", password='" + password + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }
}
