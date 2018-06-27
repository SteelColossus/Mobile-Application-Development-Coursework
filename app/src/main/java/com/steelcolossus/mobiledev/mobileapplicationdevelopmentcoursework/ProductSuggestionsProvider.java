package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.content.ContentResolver;
import android.database.Cursor;

import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider.ShoppingListContract;

import java.util.HashMap;
import java.util.Map;

public class ProductSuggestionsProvider
{
    private ContentResolver contentResolver;

    public ProductSuggestionsProvider(ContentResolver contentResolver)
    {
        this.contentResolver = contentResolver;
    }

    public Map<ShoppingListItem, Integer> getProductsByTimesBought()
    {
        Map<ShoppingListItem, Integer> boughtProducts = new HashMap<>();

        Cursor productCursor = contentResolver.query(ShoppingListContract.Product.CONTENT_URI, ShoppingListContract.Product.PROJECTION_ALL, null, null, ShoppingListContract.Product.SORT_ORDER_DEFAULT);

        if (productCursor != null)
        {
            while (productCursor.moveToNext())
            {
                Cursor shoppingListProductCursor = contentResolver.query(ShoppingListContract.ShoppingListProduct.CONTENT_URI_DISTINCT_SHOPS, ShoppingListContract.ShoppingListProduct.PROJECTION_ALL, ShoppingListContract.ShoppingListProduct.PRODUCT_ID + " = ?", new String[]{ Integer.toString(productCursor.getInt(0)) }, ShoppingListContract.ShoppingListProduct.SORT_ORDER_DEFAULT);

                if (shoppingListProductCursor != null)
                {
                    if (shoppingListProductCursor.getCount() > 0)
                    {
                        ShoppingListItem shoppingListItem = new ShoppingListItem(productCursor.getInt(1), productCursor.getString(2), productCursor.getString(3), productCursor.getFloat(4), productCursor.getString(5));
                        boughtProducts.put(shoppingListItem, shoppingListProductCursor.getCount());
                    }

                    shoppingListProductCursor.close();
                }
            }

            productCursor.close();
        }

        return boughtProducts;
    }
}
