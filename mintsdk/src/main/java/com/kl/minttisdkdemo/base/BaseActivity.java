package com.kl.minttisdkdemo.base;

import android.app.ProgressDialog;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by gaoyingjie on 2019/8/10
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected ProgressDialog progressDialog;
    protected String TAG = this.getClass().getSimpleName();





    protected String getStr(int res){

        return getResources().getString(res);

    }



    protected void showToast(int msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    protected void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    /**
     * 显示等待加载框
     *
     * @param msg 显示消息
     */
    protected void showProgressDialog(@NonNull String msg, boolean cancelable) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);

        }
        progressDialog.setMessage(msg);
        // back 消失
        progressDialog.setCanceledOnTouchOutside(cancelable);
        progressDialog.show();

    }

    /**
     * 关闭等待加载框
     */
    protected void disProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


}
