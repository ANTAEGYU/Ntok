package com.ntok.chatmodule.model;

/**
 * Created by Sonam on 08-05-2018.
 */

public class MessageModel {


    public static final String AUDIO_TYPE = "audio";
    public static final String VIDEO_TYPE = "video";
    public static final String IMAGE_TYPE = "image";
    public static final String LOCATION_TYPE = "location";
    public static final String TEXT_TYPE = "text";
    public static final String Contact_Type = "contact";


    private String id;
    private String from;
    private String to;
    private String body;
    private long timestamp;
    private PhoneContact contact;
    private VideoModelClass videoModelClass;
    private String location;
    private ImageUploadInfo imageModel;
    private AudioModelClass audioModelClass;
    private String type;
    private String isGroup;

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PhoneContact getContact() {
        return contact;
    }

    public void setContact(PhoneContact contact) {
        this.contact = contact;
    }

    public VideoModelClass getVideoModelClass() {
        return videoModelClass;
    }

    public void setVideoModelClass(VideoModelClass videoModelClass) {
        this.videoModelClass = videoModelClass;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ImageUploadInfo getImageModel() {
        return imageModel;
    }

    public void setImageModel(ImageUploadInfo imageModel) {
        this.imageModel = imageModel;
    }

    public AudioModelClass getAudioModelClass() {
        return audioModelClass;
    }

    public void setAudioModelClass(AudioModelClass audioModelClass) {
        this.audioModelClass = audioModelClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
