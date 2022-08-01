package demo.minttihealth.utils;

import android.content.Context;
import androidx.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by ccl on 2016/8/2.
 * UToast
 */

public class UToast {

    private static Toast mToast;

    public static void show(Context context, @StringRes int resId) {
        if (context == null) return;
        String text = context.getResources().getString(resId);
        show(context, text);
    }

    public static void showLong(Context context, @StringRes int resId) {
        if (context == null) return;
        String text = context.getResources().getString(resId);
        showLong(context, text);
    }

    public static void show(Context context, @StringRes int resId, int duration) {
        if (context == null) return;
        String text = context.getResources().getString(resId);
        show(context, text, duration);
    }

    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, String text) {
        show(context, text, Toast.LENGTH_LONG);
    }

    public static void show(Context context, String text, int duration) {
        if (context == null) return;
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }
}
