package com.ntok.chatmodule.model;

/**
 * Created by Sonam on 15-05-2018.
 */

public class AudioModelClass {
    private long time;
    private String audioLink;
    private String localAudioLink;

    public AudioModelClass() {
    }

    public AudioModelClass(long time, String audioLink) {
        this.time = time;
        this.audioLink = audioLink;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getLocalAudioLink() {
        return localAudioLink;
    }

    public void setLocalAudioLink(String localAudioLink) {
        this.localAudioLink = localAudioLink;
    }
}
