package com.essentials.customerapp.firebase.models;


public class CategoryModel {

    private String Cat_Id;
    private String Cat_Name;
    private String Cat_Image_Url;
    private String Cat_Created_Date;
    private String Cat_Modified_Date;
    private String Cat_Details;
    private String Offer;

    public String getOffer() {
        return Offer;
    }

    public void setOffer(String offer) {
        Offer = offer;
    }

    public String getCat_Id() {
        return Cat_Id;
    }

    public void setCat_Id(String cat_Id) {
        Cat_Id = cat_Id;
    }

    public String getCat_Name() {
        return Cat_Name;
    }

    public void setCat_Name(String cat_Name) {
        Cat_Name = cat_Name;
    }

    public String getCat_Image_Url() {
        return Cat_Image_Url;
    }

    public void setCat_Image_Url(String cat_Image_Url) {
        Cat_Image_Url = cat_Image_Url;
    }

    public String getCat_Created_Date() {
        return Cat_Created_Date;
    }

    public void setCat_Created_Date(String cat_Created_Date) {
        Cat_Created_Date = cat_Created_Date;
    }

    public String getCat_Modified_Date() {
        return Cat_Modified_Date;
    }

    public void setCat_Modified_Date(String cat_Modified_Date) {
        Cat_Modified_Date = cat_Modified_Date;
    }

    public String getCat_Details() {
        return Cat_Details;
    }

    public void setCat_Details(String cat_Details) {
        Cat_Details = cat_Details;
    }
}
