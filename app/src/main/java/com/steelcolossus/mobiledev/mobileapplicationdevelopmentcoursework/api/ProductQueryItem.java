package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductQueryItem implements Parcelable
{
    public static final Parcelable.Creator<ProductQueryItem> CREATOR = new Parcelable.Creator<ProductQueryItem>()
    {
        @Override
        public ProductQueryItem createFromParcel(Parcel source)
        {
            return new ProductQueryItem(source);
        }

        @Override
        public ProductQueryItem[] newArray(int size)
        {
            return new ProductQueryItem[size];
        }
    };

    private int tpnb;
    private String name;
    private String department;
    private float price;
    private String image;

    public ProductQueryItem(int tpnb, String name, String department, float price, String image)
    {
        this.tpnb = tpnb;
        this.name = name;
        this.department = department;
        this.price = price;
        this.image = image;
    }

    private ProductQueryItem(Parcel parcel)
    {
        this(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readFloat(), parcel.readString());
    }

    public int getTpnb()
    {
        return tpnb;
    }

    public String getName()
    {
        return name;
    }

    public String getDepartment()
    {
        return department;
    }

    public float getPrice()
    {
        return price;
    }

    public String getImageUrl()
    {
        return image;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(tpnb);
        dest.writeString(name);
        dest.writeString(department);
        dest.writeFloat(price);
        dest.writeString(image);
    }
}
