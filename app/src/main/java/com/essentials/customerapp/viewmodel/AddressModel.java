package com.essentials.customerapp.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressModel implements Parcelable {

    private String createdDate;
    private String modifiedDate;
    private String name;
    private String mobileNumber;
    private String houseNo;
    private String societyName;
    private String addressNickName;
    private String addressId;
    private String pincode;
    private String landmark;
    private boolean selectedAddress;

    public AddressModel() {
    }

    protected AddressModel(Parcel in) {
        createdDate = in.readString();
        modifiedDate = in.readString();
        name = in.readString();
        mobileNumber = in.readString();
        houseNo = in.readString();
        societyName = in.readString();
        addressNickName = in.readString();
        addressId = in.readString();
        pincode = in.readString();
        landmark = in.readString();
        selectedAddress = in.readByte() != 0;
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    public boolean isSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(boolean selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }



    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public AddressModel(String createdDate, String modifiedDate, String name, String mobileNumber,
                        String houseNo, String societyName, String addressNickName, String addressId,
                        String pincode, String landmark, boolean selectedAddress) {
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.houseNo = houseNo;
        this.societyName = societyName;
        this.addressNickName = addressNickName;
        this.pincode = pincode;
        this.landmark = landmark;
        this.addressId = addressId;
        this.selectedAddress = selectedAddress;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getAddressNickName() {
        return addressNickName;
    }

    public void setAddressNickName(String addressNickName) {
        this.addressNickName = addressNickName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(createdDate);
        parcel.writeString(modifiedDate);
        parcel.writeString(name);
        parcel.writeString(mobileNumber);
        parcel.writeString(houseNo);
        parcel.writeString(societyName);
        parcel.writeString(addressNickName);
        parcel.writeString(addressId);
        parcel.writeString(pincode);
        parcel.writeString(landmark);
        parcel.writeByte((byte) (selectedAddress ? 1 : 0));
    }
}
