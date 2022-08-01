package com.kl.minttisdkdemo.base.view;

/**
 * Created by gaoyingjie on 2019/04/30.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
//import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AudioWaveView extends SurfaceView implements Callback ,Runnable{
    private short[] mWaveData;
    private final LinkedList<Short> pointList = new LinkedList<>();
    private Paint mPaint;
    private SurfaceHolder mHolder;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mCenterY = 0;
    //网格颜色
    protected int mGridColor = Color.parseColor("#D7D7D7");
    protected Paint paint ;
    public float points[];
    private boolean isRunning = false;
    private boolean isDraw = false;
    List<Short> nativeDatas  = null;
    volatile ThreadPoolExecutor singleThreadExecutor;//单线程池

    private ExecutorService mDrawWaveThread = Executors.newSingleThreadExecutor();


    //    private Runnable drawRunAble = new Runnable() {
//        @Override
//        public void run() {
//                drawFrame();
//        }
//    };
    @Override
    public void run() {
        while (isRunning){
            if (isDraw){
                drawFrame();
            }
        }
    }


    private final LinkedList<short[]> mWaveDataList = new LinkedList<>();

    public void addWaveData(short[] waveData){
//        if (BaseApplication.isMeasure && isRunning){
//            synchronized(mWaveDataList){
//                mWaveDataList.add(waveData);
//                //drawFrame();
//            }
//        }
        if (!isRunning || waveData == null) {
            return;
        }

        if (nativeDatas == null) {
            nativeDatas = new ArrayList<>();
        }
        for (int i = 0;i<waveData.length;i++){
            nativeDatas.add((short) (mCenterY - waveData[i]/(32768/mCenterY)));
        }
        addPointThreadExecutor(nativeDatas);
        nativeDatas = new ArrayList<>();

    }

    private void addPointThreadExecutor(final List<Short> nativeDatas) {

        if (!isRunning || nativeDatas == null){
            return;
        }
        if (singleThreadExecutor == null || singleThreadExecutor.isShutdown()) {
            startSingleThreadExecutor();
            return;
        }
        //Log.e("====>", "singleThreadExecutor.getQueue().size() = " + singleThreadExecutor.getQueue().size());
        if (singleThreadExecutor.getQueue().size() >= 5) {
            Log.e("====>", "singleThreadExecutor.getQueue().size() = " + singleThreadExecutor.getQueue().size());
            return;
        }

        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Short> dataList = nativeDatas;
                synchronized (pointList){
                    for (int i = 0;i<dataList.size();i += 10){
                        if (pointList.size()>=mWidth){
                            pointList.removeFirst();
                        }
                        pointList.add(dataList.get(i));
//                    if (i%4 == 0){
//                        SystemClock.sleep(1);
//                    }

                    }
                    isDraw = true;
                }

            }
        });

    }



    synchronized void drawFrame() {
        Canvas canvas = null;
        try {

            if (!isRunning){
                return;
            }


            canvas = mHolder.lockCanvas();

            if (canvas != null) {
                // draw something
                drawCube(canvas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (canvas != null)

                try {
                    mHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }


    }

    public AudioWaveView(Context context) {
        this(context,null);


    }

    public AudioWaveView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    private void initView(){
        if (mHolder == null){
            mHolder = getHolder();
            mHolder.addCallback(this);
            setZOrderOnTop(true);// 设置画布 背景透明
            mHolder.setFormat(PixelFormat.TRANSLUCENT);
        }
        Rect frame = mHolder.getSurfaceFrame();
        mHeight = frame.height();
        mCenterY = frame.centerY();
        mWidth = frame.width();

        points = new float[mWidth * 4];
        if (mPaint==null){
            mPaint=new Paint();
            mPaint.setColor(Color.parseColor("#0077D4"));
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(2);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStyle(Paint.Style.STROKE);
        }
        if (paint==null){
            paint=new Paint();
            paint.setColor(mGridColor);
            paint.setStrokeWidth(1);
        }
    }





    public void drawCube(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        /*画波形,数据在之前已经填充好了*/
        int len = pointList.size();
//        Path path = new Path();
//        if (len >= 2){
//
//            for (int i = 0;i<)
//            path.lineTo();
//        }

        if (len >= 2){
            int index = mWidth-len;
            for (int i = index + 1; i < mWidth; i++) {


                points[i * 4] = i - 1;
                points[i * 4 + 1] = pointList.get(i-index- 1);
                points[i * 4 + 2] = i;
                points[i * 4 + 3] = pointList.get(i-index);



            }

        }

//        for (int i = 0; i < mWidth; i++) {
//
//            if (len >= mWidth && i > 0) {
//                points[i * 4] = i - 1;
//                points[i * 4 + 1] = pointList.get(i - 1);
//                points[i * 4 + 2] = i;
//                points[i * 4 + 3] = pointList.get(i);
//
//            }
//
//        }
        canvas.drawLines(points, mPaint);
        isDraw = false;
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE ){
            //LoggerUtil.d("onVisibilityChanged : VISIBLE");
            isRunning = true;
            //mDrawWaveThread.execute(drawRunAble);
        }else if (visibility == INVISIBLE ){
//            LoggerUtil.d("onVisibilityChanged : INVISIBLE");
            isRunning = false;
            //pointList.clear();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        mCenterY = h/2;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        LoggerUtil.d("surfaceChanged");

    }

    public void surfaceCreated(SurfaceHolder holder) {
//        LoggerUtil.d("surfaceCreated");
        initView();
        isRunning = true;
        Thread thread = new Thread(this);
        thread.start();
        startSingleThreadExecutor();
    }



    public void surfaceDestroyed(SurfaceHolder holder) {

        isRunning = false;

    }

    private void startSingleThreadExecutor() {
        if (singleThreadExecutor != null && !singleThreadExecutor.isShutdown()) {
            singleThreadExecutor.shutdownNow();
        }

        singleThreadExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));
    }
    public void clearDatas() {
        if (pointList != null) {
            pointList.clear();
        }
        if (nativeDatas != null){
            nativeDatas.clear();
        }

        startSingleThreadExecutor();
        isDraw =true;
        points = new float[mWidth*4];
        drawFrame();

    }

}