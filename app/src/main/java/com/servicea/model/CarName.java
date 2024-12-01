package com.servicea.model;

import kr.ry4nkim.objectspinner.ObjectSpinner;

public class CarName implements ObjectSpinner.Delegate{
    public int id;
    public String name;
    private String car_company_id;
    private String car_image;
    @Override
    public String getSpinnerDelegate() {
        return name;
    }

    public CarName() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CarName(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getCar_company_id() {
        return car_company_id;
    }

    public void setCar_company_id(String car_company_id) {
        this.car_company_id = car_company_id;
    }

    public String getCar_image() {
        return car_image;
    }

    public void setCar_image(String car_image) {
        this.car_image = car_image;
    }
}
