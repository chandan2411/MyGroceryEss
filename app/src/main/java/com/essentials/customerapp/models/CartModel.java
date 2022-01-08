package com.essentials.customerapp.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.essentials.customerapp.firebase.models.ProductModel;

import java.util.List;

public class CartModel implements Parcelable {

    private String Item_Added_Date;
    private String Product_Cat_Id;
    private String Product_Created_Date;
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
    private String currency="₹ ";
    private Integer prodctQuantityUnit=0;
    private String Sub_Total;
    private String Order_Id;
    private Boolean Is_Delivered;
    private String Order_Added_Date_And_Time;
    private String DeliveryCharge;
    private Boolean Is_Order_Cancelled;
    private Double deductedAmount;

    public String getDeliveryCharge() {
        return DeliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        DeliveryCharge = deliveryCharge;
    }

    public CartModel() {
    }

    public Double getDeductedAmount() {
        return deductedAmount;
    }

    public void setDeductedAmount(Double deductedAmount) {
        this.deductedAmount = deductedAmount;
    }

    public Boolean getIs_Order_Cancelled() {
        return Is_Order_Cancelled;
    }



    public void setIs_Order_Cancelled(Boolean is_Order_Cancelled) {
        this.Is_Order_Cancelled = is_Order_Cancelled;
    }

    protected CartModel(Parcel in) {
        Item_Added_Date = in.readString();
        Product_Cat_Id = in.readString();
        Product_Created_Date = in.readString();
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
        DeliveryCharge = in.readString();
        productDiscount = in.readString();
        currency = in.readString();
        if (in.readByte() == 0) {
            prodctQuantityUnit = null;
        } else {
            prodctQuantityUnit = in.readInt();
        }
        if (in.readByte() == 0) {
            deductedAmount = null;
        } else {
            deductedAmount = in.readDouble();
        }
        Sub_Total = in.readString();
        Order_Id = in.readString();
        byte tmpIS_DELIVERD = in.readByte();
        byte tmpIS_ORDER_CANCELLED = in.readByte();
        Is_Delivered = tmpIS_DELIVERD == 0 ? null : tmpIS_DELIVERD == 1;
        Is_Order_Cancelled = tmpIS_ORDER_CANCELLED == 0 ? null : tmpIS_ORDER_CANCELLED == 1;
        Order_Added_Date_And_Time = in.readString();
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };

    public Boolean getIs_Delivered() {
        return Is_Delivered;
    }

    public void setIs_Delivered(Boolean is_Delivered) {
        this.Is_Delivered = is_Delivered;
    }

    public String getOrder_Added_Date_And_Time() {
        return Order_Added_Date_And_Time;
    }

    public void setOrder_Added_Date_And_Time(String order_Added_Date_And_Time) {
        Order_Added_Date_And_Time = order_Added_Date_And_Time;
    }



    public Boolean getDeliver() {
        return Is_Delivered;
    }

    public void setDeliver(Boolean deliver) {
        Is_Delivered = deliver;
    }


    public String getOrder_Id() {
        return Order_Id;
    }

    public void setOrder_Id(String order_Id) {
        this.Order_Id = order_Id;
    }



    public String getSub_Total() {
        return Sub_Total;
    }

    public void setSub_Total(String sub_Total) {
        Sub_Total = sub_Total;
    }

    public CartModel(ProductModel product, String productQunatity, String Sub_Total, String date) {
//    public CartModel(ProductModel product, String productQunatity, String SubTotal) {
        Product_Cat_Id = product.getProduct_Cat_Id();
        Product_Created_Date = product.getProduct_Created_Date();
        Product_Details = product.getProduct_Details();
        Product_Id = product.getProduct_Id();
        Product_Image_Url = product.getProduct_Image_Url();
        Product_MRP = product.getProduct_MRP();
        Product_Modified_Date = product.getProduct_Modified_Date();
        Product_Name = product.getProduct_Name();
        Product_Offer_Id = product.getProduct_Offer_Id();
        DeliveryCharge = product.getDeliveryCharge();
        Product_Out_Of_Stock = product.getProduct_Out_Of_Stock();
        Product_Quantitiy = product.getProduct_Quantitiy();
        Product_Quantity_Type = product.getProduct_Quantity_Type();
        Product_SP = product.getProduct_SP();
        Product_Sub_Cat_Id = product.getProduct_Sub_Cat_Id();
        productDiscount = product.getProductDiscount();
        currency= product.getCurrency();
        Product_Sub_Cat_Id = product.getProduct_Sub_Cat_Id();
        prodctQuantityUnit = Integer.parseInt(productQunatity);
        this.Sub_Total = Sub_Total;
        this.Item_Added_Date = date;
    }

    public String getItem_Added_Date() {
        return Item_Added_Date;
    }

    public void setItem_Added_Date(String item_Added_Date) {
        Item_Added_Date = item_Added_Date;
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

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getProdctQuantityUnit() {
        return prodctQuantityUnit;
    }

    public void setProdctQuantityUnit(Integer prodctQuantityUnit) {
        this.prodctQuantityUnit = prodctQuantityUnit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Item_Added_Date);
        parcel.writeString(Product_Cat_Id);
        parcel.writeString(Product_Created_Date);
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
        parcel.writeString(DeliveryCharge);
        parcel.writeString(Product_Sub_Cat_Id);
        parcel.writeString(productDiscount);
        parcel.writeString(currency);
        if (prodctQuantityUnit == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(prodctQuantityUnit);
        }
        parcel.writeString(Sub_Total);
        parcel.writeString(Order_Id);
        parcel.writeByte((byte) (Is_Delivered == null ? 0 : Is_Delivered ? 1 : 2));
        parcel.writeString(Order_Added_Date_And_Time);
    }
}
