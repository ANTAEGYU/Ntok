package com.ntok.chatmodule.model;

/**
 * Created by Sonam on 16-05-2018.
 */

public class VideoModelClass {
    private long time;
    private String videoThumb;
    private String videoLink;
    private String localVideoLink;

    public VideoModelClass() {
    }

    public VideoModelClass(long time, String videoLink, String videoThumb) {
        this.time = time;
        this.videoLink = videoLink;
        this.videoThumb = videoThumb;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getLocalVideoLink() {
        return localVideoLink;
    }

    public void setLocalVideoLink(String localVideoLink) {
        this.localVideoLink = localVideoLink;
    }
}
