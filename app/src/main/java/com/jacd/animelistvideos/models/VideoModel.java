package com.jacd.animelistvideos.models;

public class VideoModel {
    private int id;
    private String title;
    private String capitule;
    private String season;
    private String image;
    private String state;
    private String day;
    private String type;
    private String color;
    private String date;
    private boolean done;
    private String content;

    public VideoModel() {
    }

    public VideoModel(int id, String title, String capitule, String season, String image, String state, String day, String type, String color, String date, boolean done, String content) {
        this.id = id;
        this.title = title;
        this.capitule = capitule;
        this.season = season;
        this.image = image;
        this.state = state;
        this.day = day;
        this.type = type;
        this.color = color;
        this.date = date;
        this.done = done;
        this.content = content;
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

    public String getCapitule() {
        return capitule;
    }

    public void setCapitule(String capitule) {
        this.capitule = capitule;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
