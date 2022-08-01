package demo.minttihealth.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ccl on 2016/11/3.
 */

public class ActivityHelper {

    public static void launcherForResult(Activity activity, Class<?> targetActivity, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, targetActivity);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launcher(Context context, Class<?> targetActivity) {
        Intent intent = new Intent();
        intent.setClass(context, targetActivity);
        context.startActivity(intent);
    }
}
