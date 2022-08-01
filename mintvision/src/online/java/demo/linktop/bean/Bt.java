package demo.minttihealth.bean;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.util.Log;

import lib.linktop.obj.LoadBtBean;
import demo.minttihealth.health.BR;

/**
 * Created by ccl on 2017/2/23.
 * Body Temperature.
 */

public class Bt extends BaseObservable implements LoadBtBean {

    private long ts = 0;
    private double temp = 0.0d;

    public Bt() {
    }

    public Bt(long ts, double temp) {
        this.ts = ts;
        this.temp = temp;
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
    public double getTemp() {
        return temp;
    }

    @Override
    public void setTemp(double temp) {
        if (this.temp != temp) {
            this.temp = temp;
            notifyPropertyChanged(BR.temp);
        }
    }

    @Override
    public void reset() {
        Log.e("BT", "reset");
        temp = 0.0d;
        ts = 0L;
        notifyChange();
    }

    public boolean isEmptyData() {
        return temp == 0.0d || ts == 0L;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bt{" +
                "ts=" + ts +
                ", temp=" + temp +
                '}';
    }
}
