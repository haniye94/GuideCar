package com.servicea.model;

import android.graphics.drawable.Drawable;

public class ModelJobCategory {

    int id;
    String title;
    int status;
    int icon;

    boolean selected = false;

    public ModelJobCategory() {
    }

    public ModelJobCategory(int id, String title, int status, int icon) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.icon = icon;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
