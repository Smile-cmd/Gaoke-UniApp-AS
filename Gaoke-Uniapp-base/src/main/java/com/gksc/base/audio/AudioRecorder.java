package com.gksc.base.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorder implements Runnable {

    private static final String FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/testAudioRec.pcm";

    private static final String TAG = "AudioRecorder";
    private AudioRecord mAudioRecord = null;
    private static final int DEFAULT_SAMPLE_RATE = 16000;
    // 单声道
    private static final int DEFAULT_CHANNEL_LAYOUT = AudioFormat.CHANNEL_IN_MONO;
    // 双声道
    //private static final int DEFAULT_CHANNEL_LAYOUT = AudioFormat.CHANNEL_IN_STEREO;
    private static final int DEFAULT_SAMPLE_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private final AudioRecorderCallback mRecorderCallback;

    private boolean shutdown =false;

    private boolean isRec = false;

    private int dataLength = 1920;

    public AudioRecorder(AudioRecorderCallback callback, boolean isRec) {
        this.mRecorderCallback = callback;
        this.isRec = isRec;
    }

    public AudioRecorder(AudioRecorderCallback callback, int dataLength) {
        this.mRecorderCallback = callback;
        this.dataLength = dataLength;
    }

    public AudioRecorder(AudioRecorderCallback callback) {
        this.mRecorderCallback = callback;
    }

    @Override
    public void run() {
        final int mMinBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_LAYOUT, DEFAULT_SAMPLE_FORMAT);
        Log.d(TAG, "录音已开始 mMinBufferSize=" + mMinBufferSize);

        mAudioRecord = new AudioRecord(android.media.MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_LAYOUT, DEFAULT_SAMPLE_FORMAT, mMinBufferSize);
        try {
            mAudioRecord.startRecording();
        } catch (IllegalStateException e) {
            Log.e(TAG, "startRecording 录音异常，原因："+e.getMessage());
            e.printStackTrace();
            mRecorderCallback.onError(e.getMessage() + " [startRecording failed]");
            return;
        }
        // 创建录音文件输出路径
        //FileOutputStream fos = createFileWritePath();

        byte[] sampleBuffer = new byte[dataLength];
        try {
            while (!shutdown) {
                int result = mAudioRecord.read(sampleBuffer, 0, dataLength);
                // 写录音数据
                //writeData(fos, sampleBuffer, result);
                if (result > 0) {
                    Log.i(TAG, "result = " + result + "sampleBuffer : " + sampleBuffer.length);
                    mRecorderCallback.onAudioData(sampleBuffer, result);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "录音异常，原因："+e.getMessage());
            e.printStackTrace();
            mRecorderCallback.onError(e.getMessage());
        }finally {
            Log.d(TAG, "录音已结束");
            if (mAudioRecord != null) {
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }
        // 关闭数据流
        //closeIo(fos);
    }

    private void closeIo(FileOutputStream fos) {
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeData(FileOutputStream fos, byte[] sampleBuffer, int result) {
        if (fos != null && AudioRecord.ERROR_INVALID_OPERATION != result) {
            try {
                fos.write(sampleBuffer);
                Log.i(TAG, "写录音数据->" );
            } catch (IOException e) {
                Log.e(TAG, "写录音数据异常，原因："+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private FileOutputStream createFileWritePath() {
        FileOutputStream fos = null;
        if (!isRec) {
            return fos;
        }
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "目录创建异常，原因："+e.getMessage());
            return fos;
        }

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "临时缓存文件未找到");
        }
        return fos;
    }

    public String shutdown(){
        this.shutdown = true;
        return FILE_NAME;
    }
}