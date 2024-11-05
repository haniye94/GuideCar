package com.servicea.model;

public class ServiceCenter {
    int id ;
    public  String title;
    public  String desc;
    public  String score = "0.0";
    public  String percent;
    public  String category;
    public  String header;
    public  String profile;

    public  String servicesCount;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ServiceCenter() {
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getServicesCount() {
        return servicesCount;
    }

    public void setServicesCount(String servicesCount) {
        this.servicesCount = servicesCount;
    }
}
