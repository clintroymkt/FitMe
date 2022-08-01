package demo.minttihealth.widget.wave;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import demo.minttihealth.widget.EcgBackgroundView;

/**
 * Created by ccl on 2017/8/30.
 * 画ECG&PPG波形图实例
 */

public class ECG$PPGDrawWave extends DrawWave<Pair<Integer, Float>> {

    //定义ECG波的颜色
    private final static int waveColorECG = 0xffa80bfd;
    //定义PPG波的颜色
    private final static int waveColorPPG = 0xffe71473;
    //定义波的线粗
    private final static float waveStrokeWidth = 4f;
    private float mViewWidth;
    private float mViewHeight;
    private float xS;
    private float dataSpacing;
    private float dataMax;
    private float dataMin;
    private float dp;
    private Paint mWavePaintPPG;
    private Paint mWavePaintECG;

    public ECG$PPGDrawWave() {
        super();
        mWavePaintECG = newPaint(waveColorECG, waveStrokeWidth);
        mWavePaintPPG = newPaint(waveColorPPG, waveStrokeWidth);
    }

    @Override
    public void initWave(float width, float height) {
        mViewWidth = width;
        mViewHeight = height;
        xS = EcgBackgroundView.xS;//控件每毫米的像素宽
        final float dataPerLattice = EcgBackgroundView.DATA_PER_SEC / (25.0f);//每格波形数据点数
        allDataSize = (int) (EcgBackgroundView.totalLattices * dataPerLattice);
        dataSpacing = xS / dataPerLattice;//每个数据点间距。
    }

    @Override
    public void clear() {
        dataMax = dataMin = 0;
        dp = 0f;
        super.clear();
    }

    @Override
    public int getWidthMeasureSpec() {
        return (int) ((2 + dataList.size()) * dataSpacing);
    }

    @Override
    public void drawWave(Canvas canvas) {
        final List<Pair<Integer, Float>> list = new ArrayList<>(dataList);
        int size = list.size();
        if (size >= 2) {
            dataMax = dataMin = list.get(0).second;
            for (int i = 0; i < size; i++) {
                try {
                    float dataI = list.get(i).second;
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
            dp = (dataMax - dataMin) / (mViewHeight - mViewHeight / 10 * 2);
            if (dp == 0) {
                dp = 1f;
            }
            for (int i = 0; i < size - 1; i++) {
                Pair<Integer,Float> ppgDataCurr;
                Pair<Integer,Float> ppgDataNext;
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
                float ppgY1 = getY4PPG(ppgDataCurr);
                float ppgY2 = getY4PPG(ppgDataNext);
                float ecgY1 = getY(ppgDataCurr);
                float ecgY2 = getY(ppgDataNext);
//                mWavePaintPPG.setAlpha(ppgDataCurr.ppgSq == 0 ? 255 : 100);
//                mWavePaintECG.setAlpha(ppgDataCurr.ecgSq == 0 ? 255 : 100);
                canvas.drawLine(x1, ppgY1, x2, ppgY2, mWavePaintPPG);
                canvas.drawLine(x1, ecgY1, x2, ecgY2, mWavePaintECG);
            }
        }
    }

    @Override
    protected float getX(int value, int size) {
        try {
            return mViewWidth - (dataSpacing * (size - 1 - value));
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    protected float getY(Pair<Integer,Float>  ppgData) {
        try {
            return (float) (mViewHeight / 2 + ppgData.first * 18.3 / 128 * xS / 100 * 1);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    private float getY4PPG(Pair<Integer,Float> ppgData) {
        try {
            return mViewHeight - mViewHeight / 10 - (ppgData.second- dataMin) / (dp);
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
