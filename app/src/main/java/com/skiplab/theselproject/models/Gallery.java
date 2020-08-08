package com.skiplab.theselproject.models;

public class Gallery {

    private String date_created;
    private String image_path;
    private String photo_id;

    public Gallery() {
    }

    public Gallery(String date_created, String image_path, String photo_id) {
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }
}
