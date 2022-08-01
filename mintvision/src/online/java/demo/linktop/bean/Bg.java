package demo.minttihealth.bean;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import demo.minttihealth.health.BR;
import lib.linktop.obj.LoadBgBean;

/**
 * Created by ccl on 2018/1/25.
 */

public class Bg extends BaseObservable implements LoadBgBean {

    private long ts = 0;
    private double value = 0.0d;

    public Bg() {
    }

    @Override
    public long getTs() {
        return ts;
    }

    @Override
    public void setTs(long ts) {
        this.ts = ts;
    }

    @Bindable
    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            notifyPropertyChanged(BR.value);
        }
    }

    @Override
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
