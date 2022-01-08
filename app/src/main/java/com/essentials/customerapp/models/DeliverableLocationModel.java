package com.essentials.customerapp.models;

public class DeliverableLocationModel {

    private String AreaName;
    private boolean active;
    private String District;
    private String Location_Modified_Date;
    private String Location_Id;
    private String State;
    private String Pincode;
    private String Location_Created_Date;

    public DeliverableLocationModel() {
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getLocation_Modified_Date() {
        return Location_Modified_Date;
    }

    public void setLocation_Modified_Date(String location_Modified_Date) {
        Location_Modified_Date = location_Modified_Date;
    }

    public String getLocation_Id() {
        return Location_Id;
    }

    public void setLocation_Id(String location_Id) {
        Location_Id = location_Id;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String pincode) {
        Pincode = pincode;
    }

    public String getLocation_Created_Date() {
        return Location_Created_Date;
    }

    public void setLocation_Created_Date(String location_Created_Date) {
        Location_Created_Date = location_Created_Date;
    }
}
