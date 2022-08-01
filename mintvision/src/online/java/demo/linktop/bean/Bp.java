package demo.minttihealth.bean;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;

import lib.linktop.obj.LoadBpBean;

/**
 * Created by ccl on 2017/2/7.
 */

public class Bp extends BaseObservable implements LoadBpBean {

    private long ts;
    private int sbp;
    private int dbp;
    private int hr;

    public Bp() {
    }

    public Bp(long ts, int sbp, int dbp, int hr) {
        this.ts = ts;
        this.sbp = sbp;
        this.dbp = dbp;
        this.hr = hr;
    }

    @Override
    public int getSbp() {
        return sbp;
    }

    @Override
    public void setSbp(int sbp) {
        this.sbp = sbp;
        notifyChange();
    }

    @Override
    public int getDbp() {
        return dbp;
    }

    @Override
    public void setDbp(int dbp) {
        this.dbp = dbp;
        notifyChange();
    }

    @Override
    public int getHr() {
        return hr;
    }

    @Override
    public void setHr(int hr) {
        this.hr = hr;
        notifyChange();
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
        Log.e("BP", "reset");
        sbp = 0;
        dbp = 0;
        hr = 0;
        ts = 0L;
        notifyChange();
    }

    public boolean isEmptyData() {
        return sbp == 0 || dbp == 0 || hr == 0 || ts == 0L;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bp{" +
                "sbp=" + sbp +
                ", dbp=" + dbp +
                ", hr=" + hr +
                '}';
    }
}
