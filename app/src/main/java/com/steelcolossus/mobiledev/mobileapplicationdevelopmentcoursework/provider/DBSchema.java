package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider;

public class DBSchema
{
    // Defines the database name and version
    public static final String DB_NAME = "shoppingdb";
    public static final int DB_VERSION = 4;

    public static final String SHOPPINGLIST_TABLE_NAME = "shoppinglist";
    public static final String SHOPPINGLISTPRODUCT_TABLE_NAME = "shoppinglist_product";
    public static final String PRODUCT_TABLE_NAME = "product";

    // A string that defines the SQL statement for creating the shopping list table
    public static final String SQL_CREATE_SHOPPINGLIST_TABLE = "CREATE TABLE " + SHOPPINGLIST_TABLE_NAME + " (" + ShoppingListContract.ShoppingList._ID + " INTEGER PRIMARY KEY," + ShoppingListContract.ShoppingList.NAME + " TEXT," + ShoppingListContract.ShoppingList.DATE + " INTEGER)";

    // A string that defines the SQL statement for creating the shopping list product table
    public static final String SQL_CREATE_SHOPPINGLISTPRODUCT_TABLE = "CREATE TABLE " + SHOPPINGLISTPRODUCT_TABLE_NAME + " (" + ShoppingListContract.ShoppingListProduct._ID + " INTEGER PRIMARY KEY," + ShoppingListContract.ShoppingListProduct.SHOPPINGLIST_ID + " INTEGER," + ShoppingListContract.ShoppingListProduct.PRODUCT_ID + " INTEGER," + "FOREIGN KEY(" + ShoppingListContract.ShoppingListProduct.SHOPPINGLIST_ID + ") REFERENCES " + SHOPPINGLIST_TABLE_NAME + "(" + ShoppingListContract.ShoppingList._ID + "),FOREIGN KEY(" + ShoppingListContract.ShoppingListProduct.PRODUCT_ID + ") REFERENCES " + PRODUCT_TABLE_NAME + "(" + ShoppingListContract.Product._ID + "))";

    // A string that defines the SQL statement for creating the product table
    public static final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + PRODUCT_TABLE_NAME + " (" + ShoppingListContract.Product._ID + " INTEGER PRIMARY KEY," + ShoppingListContract.Product.TPNB + " INTEGER," + ShoppingListContract.Product.NAME + " TEXT," + ShoppingListContract.Product.DEPARTMENT + " TEXT," + ShoppingListContract.Product.PRICE + " NUMERIC," + ShoppingListContract.Product.IMAGE_URL + " TEXT," + ShoppingListContract.Product.SEARCH_QUERY + " TEXT," + ShoppingListContract.Product.BOUGHT + " INTEGER)";
}
