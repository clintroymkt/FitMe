package demo.minttihealth.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.annotation.NonNull;

import demo.minttihealth.health.BR;
import lib.linktop.obj.LoadSpO2Bean;

/**
 * Created by ccl on 2017/2/7.
 */

public class SpO2 extends BaseObservable implements LoadSpO2Bean {

    private long ts = 0L;
    private int value = 0;
    private int hr = 0;

    public SpO2() {
    }

    public SpO2(long ts, int value, int hr) {
        this.ts = ts;
        this.value = value;
        this.hr = hr;
    }

    @Bindable
    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        if (this.value != value) {
            this.value = value;
            notifyPropertyChanged(BR.value);
        }
    }

    @Bindable
    @Override
    public int getHr() {
        return hr;
    }

    @Override
    public void setHr(int hr) {
        if (this.hr != hr) {
            this.hr = hr;
            notifyPropertyChanged(BR.hr);
        }
    }

    @Override
    public long getTs() {
        return ts;
    }

    @Override
    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public void reset() {
        value = 0;
        hr = 0;
        ts = 0L;
        notifyChange();
    }

    @NonNull
    @Override
    public String toString() {
        return "SpO2{" +
                "value=" + value +
                ", hr=" + hr +
                ", ts=" + ts +
                '}';
    }

    public boolean isEmptyData() {
        return value == 0 || hr == 0 || ts == 0L;
    }
}
