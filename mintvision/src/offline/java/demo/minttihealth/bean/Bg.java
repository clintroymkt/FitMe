package demo.minttihealth.bean;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import demo.minttihealth.health.BR;

/**
 * Created by ccl on 2018/1/25.
 */

public class Bg extends BaseObservable {

    private long ts = 0;
    private double value = 0.0d;

    public Bg() {
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Bindable
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            notifyPropertyChanged(BR.value);
        }
    }

    public void reset() {
        value = 0.0d;
        ts = 0L;
        notifyChange();
    }

    public boolean isEmptyData() {
        return value == 0.0d || ts == 0L;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bg{" +
                "ts=" + ts +
                ", value=" + value +
                '}';
    }
}
