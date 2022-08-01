package demo.minttihealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import java.util.ArrayList;
import java.util.List;

import demo.minttihealth.bean.ECG;
import demo.minttihealth.health.R;
import demo.minttihealth.widget.WaveView;
import demo.minttihealth.widget.wave.ECGDrawWave;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ccl on 2017/2/8.
 */

public class ECGLargeActivity extends BaseActivity {

    private ECGDrawWave ecgDrawWave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_large);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.ecg);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        WaveView waveView = findViewById(R.id.wave_view);
        Intent intent = getIntent();
        ECGDrawWave.PaperSpeed paperSpeed = (ECGDrawWave.PaperSpeed) intent.getSerializableExtra("paperSpeed");
        ECGDrawWave.Gain gain = (ECGDrawWave.Gain) intent.getSerializableExtra("gain");
        ECG model = (ECG) intent.getSerializableExtra("model");
        ecgDrawWave = new ECGDrawWave();
        ecgDrawWave.setGain(gain);
        ecgDrawWave.setPaperSpeed(paperSpeed);
        waveView.setDrawWave(ecgDrawWave);

        showChart(model.getWave());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChart(String wave) {
        Observable.just(wave)
                .subscribeOn(Schedulers.newThread()) // 指定 subscribe() 发生在子线程
                .map(s -> s.split(","))
                .map(strings -> {
                    final List<Integer> list = new ArrayList<>();
                    for (String str : strings) {
                        list.add(Integer.valueOf(str));
                    }
                    return list;
                })
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在UI程
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Integer> list) {
                        ecgDrawWave.addDataList(list);
                    }
                });

    }
}
