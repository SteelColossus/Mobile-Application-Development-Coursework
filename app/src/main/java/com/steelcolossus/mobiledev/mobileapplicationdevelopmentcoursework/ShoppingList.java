package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class ShoppingList implements Parcelable
{
    public static final Parcelable.Creator<ShoppingList> CREATOR = new Parcelable.Creator<ShoppingList>()
    {
        @Override
        public ShoppingList createFromParcel(Parcel source)
        {
            return new ShoppingList(source);
        }

        @Override
        public ShoppingList[] newArray(int size)
        {
            return new ShoppingList[size];
        }
    };

    private String name;
    private Date date;
    private ArrayList<ShoppingListItem> items;

    ShoppingList(String name, Date date)
    {
        this(name, date, new ArrayList<ShoppingListItem>());
    }

    ShoppingList(String name, Date date, ArrayList<ShoppingListItem> items)
    {
        this.name = name;
        this.date = date;
        this.items = items;
    }

    private ShoppingList(Parcel parcel)
    {
        this(parcel.readString(), new Date(parcel.readLong()));

        parcel.readTypedList(this.items, ShoppingListItem.CREATOR);
    }

    public ArrayList<ShoppingListItem> getItems()
    {
        return items;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getNumItems()
    {
        return this.items.size();
    }

    public boolean isCompleted()
    {
        for (ShoppingListItem item : items)
        {
            if (!item.isBought())
            {
                return false;
            }
        }

        return true;
    }

    public float getTotalPrice()
    {
        float totalPrice = 0;

        for (ShoppingListItem item : items)
        {
            totalPrice += item.getPrice();
        }

        return totalPrice;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ShoppingList)
        {
            ShoppingList otherShoppingList = (ShoppingList)obj;

            return name.equals(otherShoppingList.getName()) && date.equals(otherShoppingList.getDate()) && items.equals(otherShoppingList.getItems());
        }
        else
        {
            return false;
        }
    }

    public ShoppingList deepCopy()
    {
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return CREATOR.createFromParcel(parcel);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeLong(date.getTime());
        dest.writeTypedList(items);
    }
}
