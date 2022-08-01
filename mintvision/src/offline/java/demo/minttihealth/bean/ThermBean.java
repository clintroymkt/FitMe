package demo.minttihealth.bean;

import androidx.annotation.NonNull;

/**
 * Created by ccl on 2017/2/21.
 */

public class ThermBean {

    private long ts = 0L;
    private double temp = 0.0d;

    public ThermBean(long ts, double temp) {
        this.ts = ts;
        this.temp = temp;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @NonNull
    @Override
    public String toString() {
        return "ThermBean{" +
                "ts=" + ts +
                ", temp=" + temp +
                '}';
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getTs() {
        return 0;
    }

    public void reset() {

    }
}
