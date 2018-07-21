package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseIntArray;

public class ShoppingListOpenHelper extends SQLiteOpenHelper
{
    public ShoppingListOpenHelper(Context context)
    {
        super(context, DBSchema.DB_NAME, null, DBSchema.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DBSchema.SQL_CREATE_SHOPPINGLIST_TABLE);
        db.execSQL(DBSchema.SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(DBSchema.SQL_CREATE_SHOPPINGLISTPRODUCT_TABLE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion <= 3)
        {
            SparseIntArray tpnbMap = new SparseIntArray();

            db.execSQL(DBSchema.SQL_CREATE_SHOPPINGLISTPRODUCT_TABLE);

            Cursor productCursor = db.query(DBSchema.PRODUCT_TABLE_NAME, new String[] { ShoppingListContract.Product._ID, ShoppingListContract.ShoppingListProduct.SHOPPINGLIST_ID, ShoppingListContract.ShoppingListProduct.SEARCH_QUERY, ShoppingListContract.ShoppingListProduct.BOUGHT, ShoppingListContract.Product.TPNB }, null, null, null, null, ShoppingListContract.Product._ID + " DESC");

            while (productCursor.moveToNext())
            {
                int tpnb = productCursor.getInt(4);
                int existingProductId = tpnbMap.get(tpnb, -1);
                int currentProductId = productCursor.getInt(0);

                if (existingProductId >= 0)
                {
                    db.delete(DBSchema.PRODUCT_TABLE_NAME, ShoppingListContract.Product._ID + " = ?", new String[] { Integer.toString(currentProductId) });

                    currentProductId = existingProductId;
                }
                else
                {
                    tpnbMap.put(tpnb, currentProductId);
                }

                ContentValues shoppingListProductValues = new ContentValues();
                shoppingListProductValues.put(ShoppingListContract.ShoppingListProduct.PRODUCT_ID, currentProductId);
                shoppingListProductValues.put(ShoppingListContract.ShoppingListProduct.SHOPPINGLIST_ID, productCursor.getInt(1));
                shoppingListProductValues.put(ShoppingListContract.ShoppingListProduct.SEARCH_QUERY, productCursor.getString(2));
                shoppingListProductValues.put(ShoppingListContract.ShoppingListProduct.BOUGHT, productCursor.getInt(3));

                db.insertWithOnConflict(DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME, null, shoppingListProductValues, SQLiteDatabase.CONFLICT_IGNORE);
            }

            productCursor.close();
        }
        else
        {
            // Temporary
            db.execSQL("DROP TABLE IF EXISTS " + DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DBSchema.PRODUCT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DBSchema.SHOPPINGLIST_TABLE_NAME);
            onCreate(db);
        }
    }
}
