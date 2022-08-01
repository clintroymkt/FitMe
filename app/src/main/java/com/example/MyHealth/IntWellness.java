package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class IntWellness extends AppCompatActivity {
    WebView webView;
    private String webUrl= "http://8dwellness.myhealth.co.zw/intellectual-wellness.html";
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    // public static final int GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN = 0;
    // public ExtendedFloatingActionButton addpInstrumentBtn;
    private int xDelta;
    private int yDelta;
    float dX,dY;

    private ViewGroup webLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_wellness);
        webView=findViewById(R.id.intWebview);
//        setContentView(webView);
        progressBar = findViewById(R.id.intprogress_Bar);
        //    webView.getSettings().setLightTouchEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setSoundEffectsEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        swipeRefreshLayout = findViewById(R.id.intswipeContainer);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        webView.loadUrl(webUrl);
        webView.requestFocus();



        webLayout = (RelativeLayout)findViewById(R.id.intweblayout);




        //  webView.getSettings().setAppCachePath(getApplication().getFilesDir().getAbsolutePath() + "/cache");
        //   webView.getSettings().setAppCachePath(getApplication().getFilesDir().getAbsolutePath() + "/databases");


        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
      //  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      //      webView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
      //  }


        webView.getSettings().setJavaScriptEnabled(true);


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;

            }
        });











        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public  void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();
                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                    progressDialog.dismiss();

                }
                super.onProgressChanged(view, newProgress);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(true);
                        webView.reload();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }





    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        else{
            //   AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //    builder.setMessage("Are you sure you want to exit?")
            //            .setNegativeButton("Now", null)
            //           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            //              @Override
            //                public void onClick(DialogInterface dialog, int which) {
            //                    finishAffinity();
            //                }
            //            }).show();
            super.onBackPressed();
        }

    }




    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}