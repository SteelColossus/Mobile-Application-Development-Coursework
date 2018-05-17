package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider.ShoppingListContract;

import java.util.ArrayList;
import java.util.Date;

public class ShoppingListLoader extends AsyncTaskLoader<ArrayList<ShoppingList>>
{
    public ForceLoadContentObserver contentObserver;
    private ArrayList<ShoppingList> dataset;

    public ShoppingListLoader(@NonNull Context context)
    {
        super(context);
    }

    @Nullable
    @Override
    public ArrayList<ShoppingList> loadInBackground()
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        ArrayList<ShoppingList> data = new ArrayList<>();

        Cursor shoppingListCursor = contentResolver.query(ShoppingListContract.ShoppingList.CONTENT_URI, ShoppingListContract.ShoppingList.PROJECTION_ALL, null, null, ShoppingListContract.ShoppingList.SORT_ORDER_DEFAULT);

        if (shoppingListCursor != null)
        {
            while (shoppingListCursor.moveToNext())
            {
                Cursor productCursor = contentResolver.query(ShoppingListContract.Product.CONTENT_URI, ShoppingListContract.Product.PROJECTION_ALL, "SHOPPINGLIST_ID = ?", new String[] { Integer.toString(shoppingListCursor.getInt(0)) }, ShoppingListContract.Product.SORT_ORDER_DEFAULT);
                ArrayList<ShoppingListItem> items = new ArrayList<>();

                if (productCursor != null)
                {
                    while (productCursor.moveToNext())
                    {
                        ShoppingListItem item = new ShoppingListItem(productCursor.getInt(1), productCursor.getString(2), productCursor.getString(3), productCursor.getFloat(4), productCursor.getString(5));
                        item.setSearchQuery(productCursor.getString(6));
                        item.setBought(productCursor.getInt(7) == 1);

                        items.add(item);
                    }

                    productCursor.close();
                }

                ShoppingList shoppingList = new ShoppingList(shoppingListCursor.getString(1), new Date(shoppingListCursor.getLong(2)), items);
                data.add(shoppingList);
            }

            shoppingListCursor.close();
        }

        return data;
    }

    @Override
    public void deliverResult(@Nullable ArrayList<ShoppingList> data)
    {
        if (isReset())
        {
            return;
        }

        dataset = data;

        if (isStarted())
        {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading()
    {
        if (dataset != null)
        {
            deliverResult(dataset);
        }

        if (contentObserver == null)
        {
            contentObserver = new ForceLoadContentObserver();

            getContext().getContentResolver().registerContentObserver(ShoppingListContract.ShoppingList.CONTENT_URI, true, contentObserver);
        }

        if (takeContentChanged() || dataset == null)
        {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading()
    {
        cancelLoad();
    }

    @Override
    protected void onReset()
    {
        onStopLoading();

        if (dataset != null)
        {
            dataset = null;
        }

        if (contentObserver != null)
        {
            getContext().getContentResolver().unregisterContentObserver(contentObserver);
            contentObserver = null;
        }
    }

    @Override
    public void onCanceled(@Nullable ArrayList<ShoppingList> data)
    {
        super.onCanceled(data);
    }
}
