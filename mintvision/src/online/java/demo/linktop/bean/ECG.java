package demo.minttihealth.bean;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;

import demo.minttihealth.health.BR;
import lib.linktop.obj.LoadECGBean;

/**
 * Created by ccl on 2017/2/8.
 */

public class ECG extends BaseObservable implements LoadECGBean, Serializable {

    private long ts;
    private long duration;
    private int r2r;//R-R interval
    private int hr;//Heart rate
    private int hrv;//Heart rate variability
    private int mood;
    private int rr;//Respiratory rate
    private int ha;//Heart age
    private int hb;// Heart beat
    private int rhr;//Robust heart rate
    private int stress;//Stress
    private String wave;

    public ECG() {
    }

    public ECG(long ts, long duration, int r2r, int hr, int hrv, int mood, int rr, String wave) {
        this.ts = ts;
        this.duration = duration;
        this.r2r = r2r;
        this.hr = hr;
        this.hrv = hrv;
        this.mood = mood;
        this.rr = rr;
        this.wave = wave;
    }

    @Bindable
    @Override
    public int getR2r() {
        return r2r;
    }

    @Override
    public void setR2r(int r2r) {
        if (this.r2r != r2r) {
            this.r2r = r2r;
            notifyPropertyChanged(BR.r2r);
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

    @Bindable
    @Override
    public int getHrv() {
        return hrv;
    }

    @Override
    public void setHrv(int hrv) {
        if (this.hrv != hrv) {
            this.hrv = hrv;
            notifyPropertyChanged(BR.hrv);
        }
    }

    @Bindable
    @Override
    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        if (this.mood != mood) {
            this.mood = mood;
            notifyPropertyChanged(BR.mood);
        }
    }

    @Bindable
    @Override
    public int getRr() {
        return rr;
    }

    @Override
    public void setRr(int rr) {
        if (this.rr != rr) {
            this.rr = rr;
            notifyPropertyChanged(BR.rr);
        }
    }

    @Bindable
    public int getHa() {
        return ha;
    }

    public void setHa(int ha) {
        if (this.ha != ha) {
            this.ha = ha;
            notifyPropertyChanged(BR.ha);
        }
    }

    @Bindable
    @Override
    public int getHb() {
        return hb;
    }

    @Override
    public void setHb(int hb) {
        if (this.hb != hb) {
            this.hb = hb;
            notifyPropertyChanged(BR.hb);
        }
    }

    @Bindable
    public int getRhr() {
        return rhr;
    }

    public void setRhr(int rhr) {
        if (this.rhr != rhr) {
            this.rhr = rhr;
            notifyPropertyChanged(BR.rhr);
        }
    }

    @Bindable
    @Override
    public int getStress() {
        return stress;
    }

    @Override
    public void setStress(int stress) {
        if (this.stress != stress) {
            this.stress = stress;
            notifyPropertyChanged(BR.stress);
        }
    }

    @Bindable
    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        if (this.duration != duration) {
            this.duration = duration;
            notifyPropertyChanged(BR.duration);
        }
    }

    @Override
    public void setWave(String wave) {
        this.wave = wave;
    }

    @Override
    public String getWave() {
        return wave;
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
        r2r = 0;
        hr = 0;
        hrv = 0;
        mood = 0;
        rr = 0;
        ha = 0;
        hb = 0;
        rhr = 0;
        stress = 0;
        duration = 0L;
        ts = 0L;
        wave = "";
        notifyChange();
    }

    public boolean isEmptyData() {
        return r2r == 0 ||
                hr == 0 ||
                hrv == 0 ||
                mood == 0 ||
                rr == 0 ||
                duration == 0L ||
                ts == 0L ||
                TextUtils.isEmpty(wave);
    }

}
