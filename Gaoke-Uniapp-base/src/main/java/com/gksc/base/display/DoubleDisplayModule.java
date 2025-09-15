package com.gksc.base.display;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Display;
import android.view.WindowManager;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Date 2021/12/8 15:00
 * @Created by cjw
 */
public class DoubleDisplayModule extends WXModule {

    //双屏显示
    DisplayManager mDisplayManager;//屏幕管理类
    Display[] displays;//屏幕数组
    private PresentationView mPresentation;
    int displayId = -999;//屏幕id
    String videoUrl = null;
    DisplayManager.DisplayListener displayListener = null;

    @JSMethod(uiThread = false)
    public int getDisplayNums(){
        try {
            mDisplayManager = (DisplayManager) this.mWXSDKInstance.getContext().getSystemService(Context.DISPLAY_SERVICE);
            displays = mDisplayManager.getDisplays();
            if(displayListener == null){
                displayListener = new DisplayManager.DisplayListener() {
                    @Override
                    public void onDisplayAdded(int displayId) {
                        System.out.println("onDisplayAdded+" + displayId);
                        if (videoUrl != null) {
                            SystemClock.sleep(500);
                            if(displays.length>1){
                                show(videoUrl);
                            }
                        }
                    }

                    @Override
                    public void onDisplayRemoved(int displayId) {
                        System.out.println("onDisplayRemoved+" + displayId);
                    }

                    @Override
                    public void onDisplayChanged(int displayId) {
                        System.out.println("onDisplayChanged+" + displayId);
                    }
                };
                mDisplayManager = (DisplayManager) this.mWXSDKInstance.getContext().getSystemService(Context.DISPLAY_SERVICE);
                mDisplayManager.registerDisplayListener(displayListener,new Handler());
            }

            if(displays!=null){
                return displays.length;
            }else {
                return 0;
            }
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception",e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }
        return 0;
    }

    @JSMethod(uiThread = false)
    public void show(String videoUrl){
        try {
            this.videoUrl = videoUrl;
            mDisplayManager = (DisplayManager) this.mWXSDKInstance.getContext().getSystemService(Context.DISPLAY_SERVICE);
            displays = mDisplayManager.getDisplays(); //得到显示器数组

                //displays[1]是副屏
            if(displayId == -999){//第一次show
                displayId = displays[1].getDisplayId();
                mPresentation = new PresentationView(this.mWXSDKInstance.getContext(), displays[1]);
                mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                System.out.println("第一次show,displayId:"+displayId);
            }else {
                System.out.println("displayId:"+displays[1].getDisplayId());
                if(displayId!=displays[1].getDisplayId()){//跟上次的屏幕id不一致
                    mPresentation = new PresentationView(this.mWXSDKInstance.getContext(), displays[1]);
                    mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    System.out.println("跟上次的屏幕id不一致,displayId:"+displays[1].getDisplayId());
                }
            }

            mPresentation.show(videoUrl);
            mPresentation.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    System.out.println("setOnPreparedListener: onPrepared");
                    Map<String,Object> params=new HashMap<>();
                    mWXSDKInstance.fireGlobalEventCallback("setOnPreparedListener", params);
                }
            });
            mPresentation.setOnErrorListener(new MediaPlayer.OnErrorListener(){
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    System.out.println("onMediaErrorListener: errorCode: "+what+",extra: "+extra);
                    Map<String,Object> params=new HashMap<>();
                    params.put("errorCode",what);
                    mWXSDKInstance.fireGlobalEventCallback("onMediaErrorListener", params);
                    hide();
                    return true;
                }
            });
            /*
            * 3   MEDIA_INFO_VIDEO_RENDERING_START  玩家只是推进了第一个视频帧进行渲染。
            * 701 MEDIA_INFO_BUFFERING_START MediaPlayer 暂时在内部暂停播放以缓冲更多数据
            * 702 MEDIA_INFO_BUFFERING_END MediaPlayer 在令人震惊后恢复播放。
            * */
            mPresentation.setOnInfoListener(new MediaPlayer.OnInfoListener(){
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    System.out.println("setOnInfoListener: infoCode: "+what+",extra: "+extra);
                    Map<String,Object> params=new HashMap<>();
                    params.put("infoCode",what);
                    mWXSDKInstance.fireGlobalEventCallback("setOnInfoListener", params);
                    return false;
                }
            });
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception","show(): "+e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }
    }

    @JSMethod(uiThread = false)
    public void stop(){
        try {
            if(mPresentation!=null){
                mPresentation.stop();
            }
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception",e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }
    }

    @JSMethod(uiThread = false)
    public void hide(){
        try {
            if(mPresentation!=null){
                mPresentation.stop();
                mPresentation.hide();
            }
            videoUrl = null;
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception",e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }
    }

    @JSMethod(uiThread = false)
    public void pause(){
        try {
            if(mPresentation!=null){
                mPresentation.pause();
            }
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception",e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }
    }

    @JSMethod(uiThread = false)
    public void resume(){
        try {
            if(mPresentation!=null){
                mPresentation.resume();
            }
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception",e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }
    }

    @JSMethod(uiThread = false)
    public void volumeOff(){
        try {
            AudioManager am=(AudioManager)this.mWXSDKInstance.getContext().getSystemService(Context.AUDIO_SERVICE);
            if (Build.VERSION.SDK_INT < 23 ){
                am.setStreamVolume(AudioManager.STREAM_MUSIC,  0 ,   AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE  );
                am.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
            else{
                am.setStreamVolume(AudioManager.STREAM_MUSIC,  0 ,  AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE );
                am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE );
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE );
            }
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception",e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }

    }

    @JSMethod(uiThread = false)
    public void volumeOn(){
        try {
            AudioManager am=(AudioManager)this.mWXSDKInstance.getContext().getSystemService(Context.AUDIO_SERVICE);
            if (Build.VERSION.SDK_INT < 23 )
                am.setStreamMute(AudioManager.STREAM_MUSIC, false);
            else
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            am.setMicrophoneMute(false);
        } catch (Exception e) {
            Map<String,Object> params=new HashMap<>();
            params.put("exception",e.getMessage());
            mWXSDKInstance.fireGlobalEventCallback("exceptionListener", params);
        }
    }
}
