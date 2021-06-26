package com.jacd.animelistvideos.models;

public class ImageModel {
    private int id;
    private String title;
    private String image;
    private String date;
    private String history;
    private String uri;
    private String tag;

    public ImageModel() {
    }

    public ImageModel(int id, String image, String uri) {
        this.id = id;
        this.image = image;
        this.uri = uri;
    }

    public ImageModel(int id, String title, String image, String date, String uri, String tag) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.date = date;
        this.uri = uri;
        this.tag = tag;
    }

    public ImageModel(int id, String title, String image, String date, String history, String uri, String tag) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.date = date;
        this.history = history;
        this.uri = uri;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
