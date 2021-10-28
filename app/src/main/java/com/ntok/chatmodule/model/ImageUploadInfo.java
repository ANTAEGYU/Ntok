package com.ntok.chatmodule.model;

/**
 * Created by Sonam on 10-05-2018.
 */

public class ImageUploadInfo {

    public String imageName;

    public String imageURL;
    public String localImageURI;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url) {

        this.imageName = name;
        this.imageURL = url;
    }

    public String getLocalImageURI() {
        return localImageURI;
    }

    public void setLocalImageURI(String localImageURI) {
        this.localImageURI = localImageURI;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

}
