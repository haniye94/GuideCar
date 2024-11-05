package com.servicea.model.dbModel;

public class ReserveModel {
    private int service_center_id;
    private int job_category_id;
    private int car_id;
    private String service_center_name;
    private String start_time;
    private String end_time;
    private String service_center_address;
    private String service_center_lat;
    private String service_center_lng;
    private String date;
    private String f_open;
    private String f_close;
    private String l_open;
    private String l_close;

    private String selectedDate;

    private String description;
    private String final_cost;
    private String reserve_request;

    public String getReserveDuration() {
        return reserveDuration;
    }

    public void setReserveDuration(String reserveDuration) {
        this.reserveDuration = reserveDuration;
    }

    private String reserveDuration;

    public int getService_center_id() {
        return service_center_id;
    }

    public void setService_center_id(int service_center_id) {
        this.service_center_id = service_center_id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public String getService_center_name() {
        return service_center_name;
    }

    public void setService_center_name(String service_center_name) {
        this.service_center_name = service_center_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getService_center_address() {
        return service_center_address;
    }

    public void setService_center_address(String service_center_address) {
        this.service_center_address = service_center_address;
    }

    public String getService_center_lat() {
        return service_center_lat;
    }

    public void setService_center_lat(String service_center_lat) {
        this.service_center_lat = service_center_lat;
    }

    public String getService_center_lng() {
        return service_center_lng;
    }

    public void setService_center_lng(String service_center_lng) {
        this.service_center_lng = service_center_lng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getF_open() {
        return f_open;
    }

    public void setF_open(String f_open) {
        this.f_open = f_open;
    }

    public String getF_close() {
        return f_close;
    }

    public void setF_close(String f_close) {
        this.f_close = f_close;
    }

    public String getL_open() {
        return l_open;
    }

    public void setL_open(String l_open) {
        this.l_open = l_open;
    }

    public String getL_close() {
        return l_close;
    }

    public void setL_close(String l_close) {
        this.l_close = l_close;
    }

    public int getJob_category_id() {
        return job_category_id;
    }

    public void setJob_category_id(int job_category_id) {
        this.job_category_id = job_category_id;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
//        this.selectedDate = selectedDate.concat(":00");
        this.selectedDate = selectedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFinal_cost() {
        return final_cost;
    }

    public void setFinal_cost(String final_cost) {
        this.final_cost = final_cost;
    }

    public String getReserve_request() {
        return reserve_request;
    }

    public void setReserve_request(String reserve_request) {
        this.reserve_request = reserve_request;
    }
}
