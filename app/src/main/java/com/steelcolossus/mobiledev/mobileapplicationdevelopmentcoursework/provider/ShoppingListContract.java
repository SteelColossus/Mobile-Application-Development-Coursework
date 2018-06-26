package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ShoppingListContract
{
    /**
     * The authority of the shoppinglist provider.
     */
    public static final String AUTHORITY = "com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider";

    /**
     * The content URI for the top-level shoppinglist authority.
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class ShoppingList implements BaseColumns
    {
        /**
         * The name of this type of content.
         */
        public static final String CONTENT_NAME = "shoppinglist";
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ShoppingListContract.CONTENT_URI, CONTENT_NAME);
        /**
         * The name column of the table.
         */
        public static final String NAME = "name";
        /**
         * The date column of the table.
         */
        public static final String DATE = "date";
        /**
         * A projection of all columns in the shopping list table.
         */
        public static final String[] PROJECTION_ALL = { _ID, NAME, DATE };
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = DATE + " DESC";
        /**
         * The base mime type for a shopping list.
         */
        private static final String MIME_TYPE = "vnd.com.steelcolossus." + CONTENT_NAME;
        /**
         * The mime type of a directory of shopping lists.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_TYPE;
        /**
         * The mime type of a single shopping list.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_TYPE;
    }

    public static final class ShoppingListProduct implements BaseColumns
    {
        /**
         * The name of this type of content.
         */
        public static final String CONTENT_NAME = "shoppinglistproduct";
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ShoppingListContract.CONTENT_URI, CONTENT_NAME);
        /**
         * The name column of the table.
         */
        public static final String SHOPPINGLIST_ID = "shoppinglist_id";
        /**
         * The date column of the table.
         */
        public static final String PRODUCT_ID = "product_id";
        /**
         * The search query column of the table.
         */
        public static final String SEARCH_QUERY = "search_query";
        /**
         * The bought column of the table.
         */
        public static final String BOUGHT = "bought";
        /**
         * A projection of all columns in the shopping list table.
         */
        public static final String[] PROJECTION_ALL = { _ID, SHOPPINGLIST_ID, PRODUCT_ID, SEARCH_QUERY, BOUGHT };
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = SHOPPINGLIST_ID + " ASC";
        /**
         * The base mime type for a shopping list.
         */
        private static final String MIME_TYPE = "vnd.com.steelcolossus." + CONTENT_NAME;
        /**
         * The mime type of a directory of shopping lists.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_TYPE;
        /**
         * The mime type of a single shopping list.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_TYPE;
    }

    public static final class Product implements BaseColumns
    {
        /**
         * The name of this type of content.
         */
        public static final String CONTENT_NAME = "product";
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ShoppingListContract.CONTENT_URI, CONTENT_NAME);
        /**
         * The tpnb column of the table.
         */
        public static final String TPNB = "tpnb";
        /**
         * The name column of the table.
         */
        public static final String NAME = "name";
        /**
         * The department column of the table.
         */
        public static final String DEPARTMENT = "department";
        /**
         * The price column of the table.
         */
        public static final String PRICE = "price";
        /**
         * The image url column of the table.
         */
        public static final String IMAGE_URL = "image_url";
        /**
         * A projection of all columns in the shopping list table.
         */
        public static final String[] PROJECTION_ALL = { _ID, TPNB, NAME, DEPARTMENT, PRICE, IMAGE_URL };
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = DEPARTMENT + " ASC";
        /**
         * The base mime type for a product.
         */
        private static final String MIME_TYPE = "vnd.com.steelcolossus." + CONTENT_NAME;
        /**
         * The mime type of a directory of products.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_TYPE;
        /**
         * The mime type of a single product.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_TYPE;
    }
}
