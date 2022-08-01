package com.kl.minttisdkdemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * author : gaoyingjie
 * time   : 2018/12/13
 * desc   : todo
 */
public class AudioTrackPlayer {

    private static AudioTrack mAudioTrack;
    private ExecutorService mSigleExecutorService ;
    private int minBufferSize;
    private int streamType;
    private int sampleRate;
    private int channelConfig;
    private int audioFormat;
    private int mode;

    private static LinkedList<short[]> mInputQuene = new LinkedList<>();
    private static boolean isStart = false; //控制是否播放


    public AudioTrackPlayer(){
        streamType = AudioManager.STREAM_MUSIC;
        sampleRate = 8000;
        channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        mode = AudioTrack.MODE_STREAM;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            minBufferSize = AudioTrack.getMinBufferSize(sampleRate,channelConfig,audioFormat);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            mAudioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat, minBufferSize*2, mode);
        }
        mSigleExecutorService = Executors.newSingleThreadExecutor();

    }

    public AudioTrackPlayer(int streamType, int sampleRate, int channelConfig, int audioFormat, int mode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            minBufferSize = AudioTrack.getMinBufferSize(sampleRate,channelConfig,audioFormat);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            mAudioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat, minBufferSize, mode);
        }
        mSigleExecutorService = Executors.newSingleThreadExecutor();
    }

    public void writeData(short[] data){

        if (!isStart){
            return;
        }
        synchronized (mInputQuene){
            mInputQuene.add(data);
        }

    }

    public void play(){
        isStart = true;
        mInputQuene.clear();
        mSigleExecutorService.execute(playRun);

    }

    private Runnable playRun = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                mAudioTrack.play();
            }
            while (isStart){
                synchronized (mInputQuene){

                    if (mInputQuene!=null && mInputQuene.size()>0){
                        short[] data = mInputQuene.getFirst().clone();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                            mAudioTrack.write(data,0,data.length);
                        }
                        mInputQuene.removeFirst();

                    }

                }
            }
        }
    };






    public void stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if ((mAudioTrack != null) && (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED)) {
                if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                    isStart = false;
                    mAudioTrack.pause();
                    mAudioTrack.flush();


                }
            }
        }

    }

    public void release(){

        try {
            if (mAudioTrack != null) {
                isStart = false;
                stop();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    mAudioTrack.release();
                }
                mAudioTrack = null;
                mInputQuene.clear();
                mInputQuene = null;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

}
