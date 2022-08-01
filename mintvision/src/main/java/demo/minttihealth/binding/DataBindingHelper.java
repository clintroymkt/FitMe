package demo.minttihealth.binding;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.linktop.constant.Constants;
import com.linktop.utils.Translate;

import java.util.Locale;

import demo.minttihealth.health.R;
import demo.minttihealth.utils.BarcodeImage;
import demo.minttihealth.widget.CountDownTextView;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by ccl on 2017/2/8.
 * DataBinding 的BindingAdapter类
 */

public class DataBindingHelper {

    @BindingAdapter("recyclerAdapter")
    public static void setRecyclerAdapter(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        if (adapter != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
    }

    @BindingAdapter("qrImage")
    public static void setQrImage(final ImageView imageView, final String qrCode) {
        Observable.just(qrCode)
                .map(s -> {
                    if (TextUtils.isEmpty(s)) {
                        return null;
                    }
                    return BarcodeImage.bitmap(imageView.getContext(), s);
                })
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    @BindingAdapter("addTextChangeListener")
    public static void addTextChangeListener(@NonNull TextView view, TextWatcher watcher) {
        view.addTextChangedListener(watcher);
    }

    @BindingAdapter("onViewClick")
    public static void setOnViewClick(@NonNull View view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }


    @BindingAdapter("toggleCountDown")
    public static void toggleCountDown(@NonNull CountDownTextView textView, boolean toggleCountDown) {
        if (toggleCountDown) {
            textView.start();
        } else {
            textView.cancel();
        }
    }

    @BindingAdapter("formatMood")
    public static void formatMood(TextView view, int mood) {
        Resources res = view.getResources();
        String moodDesc;
        if (mood > 0 && mood <= 20) {
            moodDesc = res.getString(R.string.mood_desc_calm);
        } else if (mood > 20 && mood <= 40) {
            moodDesc = res.getString(R.string.mood_desc_relaxed);
        } else if (mood > 40 && mood <= 60) {
            moodDesc = res.getString(R.string.mood_desc_balanced);
        } else if (mood > 60 && mood <= 80) {
            moodDesc = res.getString(R.string.mood_desc_motivated);
        } else if (mood > 80 && mood <= 100) {
            moodDesc = res.getString(R.string.mood_desc_agitated);
        } else {
            moodDesc = "-";
        }
        view.setText(res.getString(R.string.mood_value, moodDesc));
    }

    @BindingAdapter({"formatTemp", "tempUnitF"})
    public static void formatTemp(TextView txt, double temp, boolean unitF) {
        if (unitF) {
            temp = temp > 0.0d ? Translate.TempC2F(temp) : 0.0d;
            txt.setText(String.format(Locale.getDefault(), "%.1f ℉", temp));
        } else {
            txt.setText(String.format(Locale.getDefault(), "%.1f ℃", temp));
        }
    }

    @BindingAdapter("formatStressLevel")
    public static void formatStressLevel(@NonNull TextView textView, @Constants.ECGStressLevel int level) {
        String stress;
        switch (level) {
            case Constants.ECG_STRESS_LEVEL_INVALID:
                stress = "Invalid";
                break;
            case Constants.ECG_STRESS_LEVEL_NO:
                stress = "No";
                break;
            case Constants.ECG_STRESS_LEVEL_LOW:
                stress = "Low";
                break;
            case Constants.ECG_STRESS_LEVEL_MEDIUM:
                stress = "Medium";
                break;
            case Constants.ECG_STRESS_LEVEL_HIGH:
                stress = "High";
                break;
            case Constants.ECG_STRESS_LEVEL_VERY_HIGH:
                stress = "Very high";
                break;
            default:
                stress = "-";
                break;
        }
        textView.setText(String.format(Locale.getDefault(), "Stress level: %s", stress));
    }
}
