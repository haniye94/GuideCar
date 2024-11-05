package com.servicea.model;

import org.json.JSONArray;

public class ModelMM {
    int id ;
    String title;
    JSONArray provs;

    public ModelMM() {
    }

    public ModelMM(int id, String title, JSONArray provs) {
        this.id = id;
        this.title = title;
        this.provs = provs;
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

    public JSONArray getProvs() {
        return provs;
    }

    public void setProvs(JSONArray provs) {
        this.provs = provs;
    }
}
