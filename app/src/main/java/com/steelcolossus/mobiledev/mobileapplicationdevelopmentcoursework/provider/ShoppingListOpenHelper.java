package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        // Temporary
        db.execSQL("DROP TABLE IF EXISTS " + DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBSchema.PRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBSchema.SHOPPINGLIST_TABLE_NAME);
        onCreate(db);
    }
}
