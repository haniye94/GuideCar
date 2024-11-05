package com.servicea.model;

public class FilterModel {
    private int id;
    private String title;
     private boolean isJobCategoryFilter;
     private boolean isCarFilter;

     public FilterModel(){
     }

    public FilterModel(int id, String title, boolean isJobCategoryFilter, boolean isCarFilter) {
        this.id = id;
        this.title = title;
        this.isJobCategoryFilter = isJobCategoryFilter;
        this.isCarFilter = isCarFilter;
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

    public boolean isJobCategoryFilter() {
        return isJobCategoryFilter;
    }

    public void setJobCategoryFilter(boolean jobCategoryFilter) {
        isJobCategoryFilter = jobCategoryFilter;
    }

    public boolean isCarFilter() {
        return isCarFilter;
    }

    public void setCarFilter(boolean carFilter) {
        isCarFilter = carFilter;
    }
}
