package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShoppingListOpenHelper extends SQLiteOpenHelper
{
    public static final String SHOPPINGLIST_TABLE_NAME = "shoppinglist";
    public static final String PRODUCT_TABLE_NAME = "product";

    // Defines the database name and version
    private static final String DB_NAME = "shoppingdb";
    private static final int DB_VERSION = 3;

    // A string that defines the SQL statement for creating the shopping list table
    private static final String SQL_CREATE_SHOPPINGLIST_TABLE = "CREATE TABLE " + SHOPPINGLIST_TABLE_NAME + " (" +
            ShoppingListContract.ShoppingList._ID + " INTEGER PRIMARY KEY," +
            ShoppingListContract.ShoppingList.NAME + " TEXT," +
            ShoppingListContract.ShoppingList.DATE + " INTEGER)";

    // A string that defines the SQL statement for creating the product table
    private static final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + PRODUCT_TABLE_NAME + " (" +
            ShoppingListContract.Product._ID + " INTEGER PRIMARY KEY," +
            ShoppingListContract.Product.TPNB + " INTEGER," +
            ShoppingListContract.Product.NAME + " TEXT," +
            ShoppingListContract.Product.DEPARTMENT + " TEXT," +
            ShoppingListContract.Product.PRICE + " NUMERIC," +
            ShoppingListContract.Product.IMAGE_URL + " TEXT," +
            ShoppingListContract.Product.SEARCH_QUERY + " TEXT," +
            ShoppingListContract.Product.BOUGHT + " INTEGER," +
            ShoppingListContract.Product.SHOPPINGLIST_ID + " INTEGER," +
            "FOREIGN KEY(" + ShoppingListContract.Product.SHOPPINGLIST_ID + ") REFERENCES " + SHOPPINGLIST_TABLE_NAME + "(" + ShoppingListContract.ShoppingList._ID + "))";

    public ShoppingListOpenHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_SHOPPINGLIST_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHOPPINGLIST_TABLE_NAME);
        onCreate(db);
    }
}
