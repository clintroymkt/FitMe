package demo.minttihealth.widget.wave;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ccl on 2017/8/30.
 * 画PPG（光电容积脉搏波描记）波形图实例
 */

public class PPGDrawWave extends DrawWave<Integer> {

    //定义PPG波的颜色
    private final static int waveColor = 0xffff0000;
    //定义波的线粗
    private final static float waveStrokeWidth = 2f;
    //定义X轴方向data间距
    private int xInterval;
    private float mViewWidth;
    private float mViewHeight;
    private float dp;
    private final Paint mWavePaint;

    public PPGDrawWave(int xInterval) {
        super();
        this.xInterval = xInterval;
        mWavePaint = newPaint(waveColor, waveStrokeWidth);
    }

    public void updateInterval(int xInterval) {
        this.xInterval = xInterval;
        allDataSize = mViewWidth / xInterval;
    }

    @Override
    public void initWave(float width, float height) {
        mViewWidth = width;
        mViewHeight = height;
        allDataSize = mViewWidth / xInterval;
    }

    @Override
    public void clear() {
        super.clear();
        dp = 0f;
    }

    @Override
    public void drawWave(Canvas canvas) {
        final List<Integer> list = new ArrayList<>(dataList);
        int size = list.size();
        if (size >= 2) {
            float dataMax = list.get(0), dataMin = dataMax;
            for (int i = 0; i < size; i++) {
                try {
                    float dataI = list.get(i);
                    if (dataI < dataMin) {
                        dataMin = dataI;
                    }
                    if (dataI > dataMax) {
                        dataMax = dataI;
                    }
                } catch (NullPointerException e) {
                    e.fillInStackTrace();
                }
            }
            dp = (dataMax - dataMin) / (mViewHeight * 0.8f);
            if (dp == 0) {
                dp = 1f;
            }
            for (int i = 0; i < size - 1; i++) {
                Integer ppgDataCurr;
                Integer ppgDataNext;
                try {
                    ppgDataCurr = list.get(i);
                } catch (IndexOutOfBoundsException e) {
                    ppgDataCurr = list.get(i - 1);
                }
                try {
                    ppgDataNext = list.get(i + 1);
                } catch (IndexOutOfBoundsException e) {
                    ppgDataNext = list.get(i);
                }
                float x1 = getX(i, size);
                float x2 = getX(i + 1, size);
                float y1 = getY(ppgDataCurr);
                float y2 = getY(ppgDataNext);
                canvas.drawLine(x1, y1, x2, y2, mWavePaint);
            }
        }
    }

    @Override
    protected float getX(int value, int size) {
        try {
            return value * xInterval;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    protected float getY(Integer ppgData) {
        try {
            return mViewHeight / 2.f - (ppgData / dp);
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
