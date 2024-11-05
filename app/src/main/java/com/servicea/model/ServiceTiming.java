package com.servicea.model;

public class ServiceTiming {
    int id ;
    public  String product_group_name;
    public  String product_name;
    public  String car_name;
    public  String car_company_name;
    public  String service_date_time;
    public  String km_now;
    public  String km_next;
    public  String center_name;
    public  String created_at;
    public  boolean changed;

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public ServiceTiming() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_group_name() {
        return product_group_name;
    }

    public void setProduct_group_name(String product_group_name) {
        this.product_group_name = product_group_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getCar_company_name() {
        return car_company_name;
    }

    public void setCar_company_name(String car_company_name) {
        this.car_company_name = car_company_name;
    }

    public String getService_date_time() {
        return service_date_time;
    }

    public void setService_date_time(String service_date_time) {
        this.service_date_time = service_date_time;
    }

    public String getKm_now() {
        return km_now;
    }

    public void setKm_now(String km_now) {
        this.km_now = km_now;
    }

    public String getKm_next() {
        return km_next;
    }

    public void setKm_next(String km_next) {
        this.km_next = km_next;
    }

    public String getCenter_name() {
        return center_name;
    }

    public void setCenter_name(String center_name) {
        this.center_name = center_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
