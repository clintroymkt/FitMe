package demo.minttihealth.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import demo.minttihealth.health.R;

/**
 * Created by ccl on 2016/6/13.
 * AlertDialogBuilder
 */
public final class AlertDialogBuilder extends AlertDialog.Builder {

    public AlertDialogBuilder(Context context) {
        this(context, R.style.AppDialogStyle);
    }

    public AlertDialogBuilder(Context context, int theme) {
        super(context, theme);
    }
}
