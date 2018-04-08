package com.xf.kxfdemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : lsl 408930131@qq.com
 * @version : v1.0
 * @description : 声音能量监控
 * @date : 2018-04-04
 */
public class AudioRecorder {
    private static final String TAG = "AudioRecorder";

    int inHz = 44100;
    int buffersize;
    byte[] buffer;
    AudioRecord audioRecord;
    boolean isWork;

    Lock lock;
    int locktime = 200;

    @MainThread
    public void init() {
        buffersize = AudioRecord.getMinBufferSize(inHz, AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, inHz,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, buffersize * 4);
        buffer = new byte[buffersize];
        lock = new ReentrantLock();
    }

    @WorkerThread
    public void start() {
        if (audioRecord == null) {
            throw new RuntimeException("audio record is null");
        }
        isWork = true;
        audioRecord.startRecording();
        while (isWork && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            int r = audioRecord.read(buffer, 0, buffersize);

            getVolume(buffer, r);

            synchronized (lock) {
                try {
                    lock.wait(locktime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @WorkerThread
    public void stop() {
        if (audioRecord == null) {
            return;
        }
        isWork = false;
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }


    private void getVolume(byte[] buffer, int len) {
        long v = 0;
        if (len == AudioRecord.ERROR_INVALID_OPERATION) {
            Log.e(TAG, "Error ERROR_INVALID_OPERATION");
        } else if (len == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Error ERROR_BAD_VALUE");
        } else {
            for (int i = 0; i < buffer.length; i++) {
                v += buffer[i] * buffer[i];
            }
            double m = v / len;
            double volume = 10 * Math.log10(m);

            Log.d(TAG, "分贝值:" + volume);
        }

    }

}
