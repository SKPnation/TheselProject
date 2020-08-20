package com.skiplab.theselproject.models;

public class Videos {
    private String name;
    private String videourl;
    private String search;
    private String username;
    private String vId;
    private String vLikes;
    private String vComments;

    public Videos() {
    }

    public Videos(String name, String videourl, String search, String username, String vId, String vLikes, String vComments) {
        this.name = name;
        this.videourl = videourl;
        this.search = search;
        this.username = username;
        this.vId = vId;
        this.vLikes = vLikes;
        this.vComments = vComments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getvId() {
        return vId;
    }

    public void setvId(String vId) {
        this.vId = vId;
    }

    public String getvLikes() {
        return vLikes;
    }

    public void setvLikes(String vLikes) {
        this.vLikes = vLikes;
    }

    public String getvComments() {
        return vComments;
    }

    public void setvComments(String vComments) {
        this.vComments = vComments;
    }
}
