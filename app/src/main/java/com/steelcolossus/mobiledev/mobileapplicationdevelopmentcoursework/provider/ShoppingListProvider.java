package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class ShoppingListProvider extends ContentProvider
{
    private static final int SHOPPINGLIST_LIST = 1;
    private static final int SHOPPINGLIST_ID = 2;
    private static final int PRODUCT_LIST = 3;
    private static final int PRODUCT_ID = 4;
    private static final int SHOPPINGLISTPRODUCT_LIST = 5;
    private static final int SHOPPINGLISTPRODUCT_ID = 6;

    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        URI_MATCHER.addURI(ShoppingListContract.AUTHORITY, ShoppingListContract.ShoppingList.CONTENT_NAME, SHOPPINGLIST_LIST);
        URI_MATCHER.addURI(ShoppingListContract.AUTHORITY, ShoppingListContract.ShoppingList.CONTENT_NAME + "/#", SHOPPINGLIST_ID);
        URI_MATCHER.addURI(ShoppingListContract.AUTHORITY, ShoppingListContract.Product.CONTENT_NAME, PRODUCT_LIST);
        URI_MATCHER.addURI(ShoppingListContract.AUTHORITY, ShoppingListContract.Product.CONTENT_NAME + "/#", PRODUCT_ID);
        URI_MATCHER.addURI(ShoppingListContract.AUTHORITY, ShoppingListContract.ShoppingListProduct.CONTENT_NAME, SHOPPINGLISTPRODUCT_LIST);
        URI_MATCHER.addURI(ShoppingListContract.AUTHORITY, ShoppingListContract.ShoppingListProduct.CONTENT_NAME + "/#", SHOPPINGLISTPRODUCT_ID);
    }

    private ShoppingListOpenHelper shoppingListOpenHelper;

    @Override
    public boolean onCreate()
    {
        shoppingListOpenHelper = new ShoppingListOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        switch (URI_MATCHER.match(uri))
        {
            case SHOPPINGLIST_LIST:
                return ShoppingListContract.ShoppingList.CONTENT_TYPE;
            case SHOPPINGLIST_ID:
                return ShoppingListContract.ShoppingList.CONTENT_ITEM_TYPE;
            case PRODUCT_LIST:
                return ShoppingListContract.Product.CONTENT_TYPE;
            case PRODUCT_ID:
                return ShoppingListContract.Product.CONTENT_ITEM_TYPE;
            case SHOPPINGLISTPRODUCT_LIST:
                return ShoppingListContract.ShoppingListProduct.CONTENT_TYPE;
            case SHOPPINGLISTPRODUCT_ID:
                return ShoppingListContract.ShoppingListProduct.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        SQLiteDatabase db = shoppingListOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri))
        {
            case SHOPPINGLIST_LIST:
                queryBuilder.setTables(DBSchema.SHOPPINGLIST_TABLE_NAME);
                break;
            case SHOPPINGLIST_ID:
                queryBuilder.setTables(DBSchema.SHOPPINGLIST_TABLE_NAME);
                queryBuilder.appendWhere(ShoppingListContract.ShoppingList._ID + " = " + uri.getLastPathSegment());
                break;
            case PRODUCT_LIST:
                queryBuilder.setTables(DBSchema.PRODUCT_TABLE_NAME);
                break;
            case PRODUCT_ID:
                queryBuilder.setTables(DBSchema.PRODUCT_TABLE_NAME);
                queryBuilder.appendWhere(ShoppingListContract.Product._ID + " = " + uri.getLastPathSegment());
                break;
            case SHOPPINGLISTPRODUCT_LIST:
                queryBuilder.setTables(DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME);
                break;
            case SHOPPINGLISTPRODUCT_ID:
                queryBuilder.setTables(DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME);
                queryBuilder.appendWhere(ShoppingListContract.ShoppingListProduct._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        Context context = getContext();

        if (context != null)
        {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        SQLiteDatabase db = shoppingListOpenHelper.getWritableDatabase();
        String table;

        switch (URI_MATCHER.match(uri))
        {
            case SHOPPINGLIST_LIST:
                table = DBSchema.SHOPPINGLIST_TABLE_NAME;
                break;
            case PRODUCT_LIST:
                table = DBSchema.PRODUCT_TABLE_NAME;
                break;
            case SHOPPINGLISTPRODUCT_LIST:
                table = DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }

        long id = db.insert(table, null, values);

        if (id > 0)
        {
            Uri newUri = ContentUris.withAppendedId(uri, id);

            Context context = getContext();

            if (context != null)
            {
                context.getContentResolver().notifyChange(newUri, null);
            }

            return newUri;
        }
        else
        {
            throw new SQLException("Problem while inserting into uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        SQLiteDatabase db = shoppingListOpenHelper.getWritableDatabase();
        int updateCount;
        String table;

        switch (URI_MATCHER.match(uri))
        {
            case SHOPPINGLIST_LIST:
                table = DBSchema.SHOPPINGLIST_TABLE_NAME;
                break;
            case SHOPPINGLIST_ID:
                table = DBSchema.SHOPPINGLIST_TABLE_NAME;

                if (!TextUtils.isEmpty(selection))
                {
                    selection += " AND ";
                }

                selection += ShoppingListContract.ShoppingList._ID + " = " + uri.getLastPathSegment();

                break;
            case PRODUCT_LIST:
                table = DBSchema.PRODUCT_TABLE_NAME;
                break;
            case PRODUCT_ID:
                table = DBSchema.PRODUCT_TABLE_NAME;

                if (!TextUtils.isEmpty(selection))
                {
                    selection += " AND ";
                }

                selection += ShoppingListContract.Product._ID + " = " + uri.getLastPathSegment();

                break;
            case SHOPPINGLISTPRODUCT_LIST:
                table = DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME;
                break;
            case SHOPPINGLISTPRODUCT_ID:
                table = DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME;

                if (!TextUtils.isEmpty(selection))
                {
                    selection += " AND ";
                }

                selection += ShoppingListContract.Product._ID + " = " + uri.getLastPathSegment();

                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        updateCount = db.update(table, values, selection, selectionArgs);

        if (updateCount > 0)
        {
            Context context = getContext();

            if (context != null)
            {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return updateCount;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        SQLiteDatabase db = shoppingListOpenHelper.getWritableDatabase();
        int deleteCount;
        String table;

        switch (URI_MATCHER.match(uri))
        {
            case SHOPPINGLIST_LIST:
                table = DBSchema.SHOPPINGLIST_TABLE_NAME;
                break;
            case SHOPPINGLIST_ID:
                table = DBSchema.SHOPPINGLIST_TABLE_NAME;

                if (!TextUtils.isEmpty(selection))
                {
                    selection += " AND ";
                }

                selection += ShoppingListContract.ShoppingList._ID + " = " + uri.getLastPathSegment();

                break;
            case PRODUCT_LIST:
                table = DBSchema.PRODUCT_TABLE_NAME;
                break;
            case PRODUCT_ID:
                table = DBSchema.PRODUCT_TABLE_NAME;

                if (!TextUtils.isEmpty(selection))
                {
                    selection += " AND ";
                }

                selection += ShoppingListContract.Product._ID + " = " + uri.getLastPathSegment();

                break;
            case SHOPPINGLISTPRODUCT_LIST:
                table = DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME;
                break;
            case SHOPPINGLISTPRODUCT_ID:
                table = DBSchema.SHOPPINGLISTPRODUCT_TABLE_NAME;

                if (!TextUtils.isEmpty(selection))
                {
                    selection += " AND ";
                }

                selection += ShoppingListContract.Product._ID + " = " + uri.getLastPathSegment();

                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        deleteCount = db.delete(table, selection, selectionArgs);

        if (deleteCount > 0)
        {
            Context context = getContext();

            if (context != null)
            {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return deleteCount;
    }
}
