package com.essentials.customerapp.firebase.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by RAJ ARYAN on 2020-01-22.
 */
public class ProductModel implements Parcelable{

    private String Product_Cat_Id;
    private Integer POSITION;
    private String Product_Created_Date;
    private String Product_Grand_Cat_Id;
    private String Product_Details;
    private String Product_Id;
    private List<String> Product_Image_Url;
    private String Product_MRP;
    private String Product_Modified_Date;
    private String Product_Name;
    private String Product_Offer_Id;
    private String Product_Out_Of_Stock;
    private String Product_Quantitiy;
    private String Product_Quantity_Type;
    private String Product_SP;
    private String Product_Sub_Cat_Id;
    private String productDiscount="";
    private String Product_Grand_Cat_Name;
    private String keywords;
    private int ProductCount;
    private String DeliveryCharge;
    private String MoreInfo;
    private String currency="₹ ";

    public ProductModel() {
    }

    public String getMoreInfo() {
        return MoreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        MoreInfo = moreInfo;
    }

    protected ProductModel(Parcel in) {
        Product_Cat_Id = in.readString();
        if (in.readByte() == 0) {
            POSITION = null;
        } else {
            POSITION = in.readInt();
        }
        Product_Created_Date = in.readString();
        Product_Grand_Cat_Id = in.readString();
        Product_Details = in.readString();
        Product_Id = in.readString();
        Product_Image_Url = in.createStringArrayList();
        Product_MRP = in.readString();
        Product_Modified_Date = in.readString();
        Product_Name = in.readString();
        Product_Offer_Id = in.readString();
        Product_Out_Of_Stock = in.readString();
        Product_Quantitiy = in.readString();
        Product_Quantity_Type = in.readString();
        Product_SP = in.readString();
        Product_Sub_Cat_Id = in.readString();
        productDiscount = in.readString();
        Product_Grand_Cat_Name = in.readString();
        keywords = in.readString();
        ProductCount = in.readInt();
        DeliveryCharge = in.readString();
        MoreInfo = in.readString();
        currency = in.readString();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getDeliveryCharge() {
        return DeliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        DeliveryCharge = deliveryCharge;
    }


    public String getProduct_Grand_Cat_Name() {
        return Product_Grand_Cat_Name;
    }

    public void setProduct_Grand_Cat_Name(String product_Grand_Cat_Name) {
        Product_Grand_Cat_Name = product_Grand_Cat_Name;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getProductCount() {
        return ProductCount;
    }

    public void setProductCount(int productCount) {
        ProductCount = productCount;
    }



    public Integer getPOSITION() {
        return POSITION;
    }

    public void setPOSITION(Integer POSITION) {
        this.POSITION = POSITION;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProduct_Cat_Id() {
        return Product_Cat_Id;
    }

    public void setProduct_Cat_Id(String product_Cat_Id) {
        Product_Cat_Id = product_Cat_Id;
    }

    public String getProduct_Created_Date() {
        return Product_Created_Date;
    }

    public void setProduct_Created_Date(String product_Created_Date) {
        Product_Created_Date = product_Created_Date;
    }

    public String getProduct_Grand_Cat_Id() {
        return Product_Grand_Cat_Id;
    }

    public void setProduct_Grand_Cat_Id(String product_Grand_Cat_Id) {
        Product_Grand_Cat_Id = product_Grand_Cat_Id;
    }

    public String getProduct_Details() {
        return Product_Details;
    }

    public void setProduct_Details(String product_Details) {
        Product_Details = product_Details;
    }

    public String getProduct_Id() {
        return Product_Id;
    }

    public void setProduct_Id(String product_Id) {
        Product_Id = product_Id;
    }

    public List<String> getProduct_Image_Url() {
        return Product_Image_Url;
    }

    public void setProduct_Image_Url(List<String> product_Image_Url) {
        Product_Image_Url = product_Image_Url;
    }

    public String getProduct_MRP() {
        return Product_MRP;
    }

    public void setProduct_MRP(String product_MRP) {
        Product_MRP = product_MRP;
    }

    public String getProduct_Modified_Date() {
        return Product_Modified_Date;
    }

    public void setProduct_Modified_Date(String product_Modified_Date) {
        Product_Modified_Date = product_Modified_Date;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getProduct_Offer_Id() {
        return Product_Offer_Id;
    }

    public void setProduct_Offer_Id(String product_Offer_Id) {
        Product_Offer_Id = product_Offer_Id;
    }

    public String getProduct_Out_Of_Stock() {
        return Product_Out_Of_Stock;
    }

    public void setProduct_Out_Of_Stock(String product_Out_Of_Stock) {
        Product_Out_Of_Stock = product_Out_Of_Stock;
    }

    public String getProduct_Quantitiy() {
        return Product_Quantitiy;
    }

    public void setProduct_Quantitiy(String product_Quantitiy) {
        Product_Quantitiy = product_Quantitiy;
    }

    public String getProduct_Quantity_Type() {
        return Product_Quantity_Type;
    }

    public void setProduct_Quantity_Type(String product_Quantity_Type) {
        Product_Quantity_Type = product_Quantity_Type;
    }

    public String getProduct_SP() {
        return Product_SP;
    }

    public void setProduct_SP(String product_SP) {
        Product_SP = product_SP;
    }

    public String getProduct_Sub_Cat_Id() {
        return Product_Sub_Cat_Id;
    }

    public void setProduct_Sub_Cat_Id(String product_Sub_Cat_Id) {
        Product_Sub_Cat_Id = product_Sub_Cat_Id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Product_Cat_Id);
        if (POSITION == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(POSITION);
        }
        parcel.writeString(Product_Created_Date);
        parcel.writeString(Product_Grand_Cat_Id);
        parcel.writeString(Product_Details);
        parcel.writeString(Product_Id);
        parcel.writeStringList(Product_Image_Url);
        parcel.writeString(Product_MRP);
        parcel.writeString(Product_Modified_Date);
        parcel.writeString(Product_Name);
        parcel.writeString(Product_Offer_Id);
        parcel.writeString(Product_Out_Of_Stock);
        parcel.writeString(Product_Quantitiy);
        parcel.writeString(Product_Quantity_Type);
        parcel.writeString(Product_SP);
        parcel.writeString(Product_Sub_Cat_Id);
        parcel.writeString(productDiscount);
        parcel.writeString(Product_Grand_Cat_Name);
        parcel.writeString(keywords);
        parcel.writeInt(ProductCount);
        parcel.writeString(DeliveryCharge);
        parcel.writeString(MoreInfo);
        parcel.writeString(currency);
    }
}
