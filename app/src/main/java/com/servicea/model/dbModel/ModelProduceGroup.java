package com.servicea.model.dbModel;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ModelProduceGroup implements Serializable {
    int id;
    String onvan;
    int job_id;
    int km_usage;
    int performance;
    boolean check;
    boolean send_msg;

    String title;

    int change_wage;

    public ModelProduceGroup(){
    }
    public ModelProduceGroup(int id, String onvan,int km_usage,boolean check,  boolean send_msg) {
        this.id = id;
        this.onvan = onvan;
        this.check = check;
        this.send_msg = send_msg;
        this.km_usage = km_usage;
    }

    public int getKm_usage() {
        return km_usage;
    }

    public void setKm_usage(int km_usage) {
        this.km_usage = km_usage;
    }

    public boolean isSend_msg() {
        return send_msg;
    }

    public void setSend_msg(boolean send_msg) {
        this.send_msg = send_msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOnvan() {
        return onvan;
    }

    public void setOnvan(String onvan) {
        this.onvan = onvan;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    public int getChange_wage() {
        return change_wage;
    }

    public void setChange_wage(int change_wage) {
        this.change_wage = change_wage;
    }
}
