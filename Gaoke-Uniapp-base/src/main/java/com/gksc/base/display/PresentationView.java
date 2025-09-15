package com.gksc.base.display;

import android.app.Presentation;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.VideoView;

import com.gksc.base.R;

public class PresentationView extends Presentation {

    private VideoView videoView;
    private int position = 0;
    private String videoUrl;

    public PresentationView(Context outerContext, Display display) {
        super(outerContext,display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation);
        videoView = findViewById(R.id.videoView);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                reStart();
            }
        });
    }

    public void stop(){
        videoView.pause();
        videoView.stopPlayback();
    }

    public void pause(){
        videoView.pause();
        position = videoView.getCurrentPosition();
    }

    public void resume(){
//        videoView.resume();
        videoView.seekTo(position);
        videoView.start();
    }

    public void show(String videoUrl) {
        this.videoUrl = videoUrl;
        this.show();
        try {
            //            "/sdcard/Download/1.mp4"
            //            Uri.parse("http://192.168.1.214:8077/video/20211103/mp4/1455783391340400640.mp4").toString()
            videoView.setVideoPath(Uri.parse(videoUrl).toString());
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reStart(){
        try {
            videoView.setVideoPath(Uri.parse(videoUrl).toString());
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
     if(videoView!=null){
         videoView.setOnErrorListener(onErrorListener);
     }
    }
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
     if(videoView!=null){
         videoView.setOnPreparedListener(onPreparedListener);
     }
    }
    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
     if(videoView!=null){
         videoView.setOnInfoListener(onInfoListener);
     }
    }
}