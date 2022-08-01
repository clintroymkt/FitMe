package com.example.MyHealth;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.DragEvent;
import android.view.ViewGroup;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.MyHealth.ui.MachineActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class OnlineActivity extends AppCompatActivity  {
    WebView webView;
    private String webUrl= "http://ec2-3-142-145-245.us-east-2.compute.amazonaws.com/";
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
   // public static final int GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN = 0;
    public ExtendedFloatingActionButton addpInstrumentBtn;
    private int xDelta;
    private int yDelta;
    float dX,dY;

    private ViewGroup webLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        webView=findViewById(R.id.transWebview);
//        setContentView(webView);
        progressBar = findViewById(R.id.progress_Bar);
    //    webView.getSettings().setLightTouchEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setSoundEffectsEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        webView.loadUrl(webUrl);
        webView.requestFocus();


        addpInstrumentBtn = findViewById(R.id.addInstrumentflt);
        webLayout = (RelativeLayout)findViewById(R.id.weblayout);




        //  webView.getSettings().setAppCachePath(getApplication().getFilesDir().getAbsolutePath() + "/cache");
     //   webView.getSettings().setAppCachePath(getApplication().getFilesDir().getAbsolutePath() + "/databases");


        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
        }


        webView.getSettings().setJavaScriptEnabled(true);


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;

            }
        });



        addpInstrumentBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)

            @Override public void onClick(View v)  {
                Intent intent = new Intent(OnlineActivity.this, MachineActivity.class);
                //   startActivity(intent);


                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);

                Rect rect = new Rect(0, 0, 100, 100);
                ActivityOptions activityOptions = ActivityOptions.makeBasic();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    ActivityOptions bounds = activityOptions.setLaunchBounds(rect);

                    startActivity(intent);


                }


            }
        });


        webLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_LOCATION:
                        dX = event.getX();
                        dY = event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        addpInstrumentBtn.setX(dX-addpInstrumentBtn.getWidth()/2);
                        addpInstrumentBtn.setY(dY-addpInstrumentBtn.getHeight()/2);
                        break;
                }
                return true;
            }
        });

        addpInstrumentBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onLongClick(View v) {
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(addpInstrumentBtn);
                v.startDragAndDrop(null, myShadow, null, View.DRAG_FLAG_GLOBAL);
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