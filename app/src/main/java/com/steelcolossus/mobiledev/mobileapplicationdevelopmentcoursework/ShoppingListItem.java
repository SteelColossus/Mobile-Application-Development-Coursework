package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.os.Parcel;
import android.os.Parcelable;

import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.ProductQueryItem;

public class ShoppingListItem implements Parcelable
{
    public static final Parcelable.Creator<ShoppingListItem> CREATOR = new Parcelable.Creator<ShoppingListItem>()
    {
        @Override
        public ShoppingListItem createFromParcel(Parcel source)
        {
            return new ShoppingListItem(source);
        }

        @Override
        public ShoppingListItem[] newArray(int size)
        {
            return new ShoppingListItem[size];
        }
    };

    private final int tpnb;
    private final String name;
    private final String department;
    private final float price;
    private final String imageUrl;
    private String searchQuery;
    private boolean bought;

    public ShoppingListItem(int tpnb, String name, String department, float price, String imageUrl)
    {
        this.tpnb = tpnb;
        this.name = name;
        this.department = department;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private ShoppingListItem(Parcel parcel)
    {
        this(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readFloat(), parcel.readString());
        this.searchQuery = parcel.readString();
        this.bought = parcel.readByte() == 1;
    }

    public ShoppingListItem(ProductQueryItem productQueryItem, String searchQuery)
    {
        this(productQueryItem.getTpnb(), productQueryItem.getName(), productQueryItem.getDepartment(), productQueryItem.getPrice(), productQueryItem.getImageUrl());
        this.searchQuery = searchQuery;
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
        return imageUrl;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    public boolean isBought()
    {
        return bought;
    }

    public void setBought(boolean bought)
    {
        this.bought = bought;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ShoppingListItem)
        {
            ShoppingListItem otherShoppingListItem = (ShoppingListItem)obj;

            return tpnb == otherShoppingListItem.getTpnb() && bought == otherShoppingListItem.isBought() && (searchQuery == null || searchQuery.equals(otherShoppingListItem.getSearchQuery()));
        }
        else
        {
            return false;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(tpnb);
        dest.writeString(name);
        dest.writeString(department);
        dest.writeFloat(price);
        dest.writeString(imageUrl);
        dest.writeString(searchQuery);
        dest.writeByte((byte)(bought ? 1 : 0));
    }
}
