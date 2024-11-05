package com.servicea.model;

public class ModelReserveTime {
    String startTime;
    String endTime;
    String day;
    String monthName;
    String year;
    boolean is_reserved;

    public String getReserveDuration() {
        return reserveDuration;
    }

    public void setReserveDuration(String reserveDuration) {
        this.reserveDuration = reserveDuration;
    }

    String reserveDuration;

    public ModelReserveTime(){}

    public String getStartTime() {
//        return startTime + ":00";
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isIs_reserved() {
        return is_reserved;
    }

    public void setIs_reserved(boolean is_reserved) {
        this.is_reserved = is_reserved;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
