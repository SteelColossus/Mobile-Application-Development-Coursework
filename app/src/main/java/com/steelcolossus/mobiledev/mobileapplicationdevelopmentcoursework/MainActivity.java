package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider.ShoppingListContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<ShoppingList>>
{
    public static final int LOADER_ID = 1;
    public static final String INTENT_TAG_SHOPPING_LIST_IS_NEW = "new";
    public static final String INTENT_TAG_SHOPPING_LIST_NAME = "name";
    public static final String INTENT_TAG_SHOPPING_LIST_DATA = "shoppingList";
    private static final int NEW_SHOPPING_LIST_REQUEST = 1;
    private static final int EXISTING_SHOPPING_LIST_REQUEST = 2;
    private RecyclerView recyclerView;
    private ListOfShoppingListsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.listOfShoppingListsRecyclerView);

        // Set that the elements of the recycler view will not change in size
        recyclerView.setHasFixedSize(true);
        // Set a linear layout manager for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Context context = this;

        // Set up the adapter
        adapter = new ListOfShoppingListsAdapter(new ArrayList<ShoppingList>());
        adapter.setOnClickFunction(new ShoppingListFunction()
        {
            @Override
            public void run(ShoppingList shoppingList)
            {
                Intent intent = new Intent(context, ShoppingListActivity.class);
                intent.putExtra(INTENT_TAG_SHOPPING_LIST_IS_NEW, false);
                intent.putExtra(INTENT_TAG_SHOPPING_LIST_DATA, shoppingList);
                startActivityForResult(intent, EXISTING_SHOPPING_LIST_REQUEST);
            }
        });

        adapter.setMenuFunction(new ShoppingListContextMenuFunction()
        {
            @Override
            public boolean onMenuItemClick(int menuItemId, View view, ShoppingList shoppingList)
            {
                switch (menuItemId)
                {
                    case R.id.remove:
                        deleteShoppingListFromDatabase(shoppingList);
                        Snackbar.make(view, shoppingList.getName() + " removed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String shoppingListDefaultName = String.format(getString(R.string.shopping_list_name_format_default), adapter.getItemCount() + 1);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                final View dialogView = View.inflate(context, R.layout.dialog_shoppinglist_name, null);

                final EditText shoppingListNameEditText = dialogView.findViewById(R.id.shoppingListNameEditText);

                shoppingListNameEditText.setText(shoppingListDefaultName);

                dialogBuilder.setTitle(R.string.dialog_name_shopping_list_title).setView(dialogView).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent newShoppingListIntent = new Intent(context, ShoppingListActivity.class);
                        newShoppingListIntent.putExtra(INTENT_TAG_SHOPPING_LIST_IS_NEW, true);
                        newShoppingListIntent.putExtra(INTENT_TAG_SHOPPING_LIST_NAME, shoppingListNameEditText.getText().toString());
                        startActivityForResult(newShoppingListIntent, NEW_SHOPPING_LIST_REQUEST);
                    }
                });

                dialogBuilder.create().show();
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.userGuide:
                Intent intent = new Intent(this, UserGuideActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_SHOPPING_LIST_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                ShoppingList result = data.getParcelableExtra(ShoppingListActivity.INTENT_TAG_SHOPPING_LIST_DATA);

                addShoppingListToDatabase(result);
            }
        }
        else if (requestCode == EXISTING_SHOPPING_LIST_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                ShoppingList result = data.getParcelableExtra(ShoppingListActivity.INTENT_TAG_SHOPPING_LIST_DATA);

                changeShoppingListInDatabase(result);
            }
        }
    }

    private void deleteShoppingListFromDatabase(ShoppingList shoppingList)
    {
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(ShoppingListContract.ShoppingList.CONTENT_URI, new String[] { ShoppingListContract.ShoppingList._ID }, ShoppingListContract.ShoppingList._ID + " = ?", new String[] { Integer.toString(shoppingList.getId()) }, ShoppingListContract.ShoppingList.SORT_ORDER_DEFAULT);

        if (cursor != null)
        {
            cursor.moveToFirst();
            String id = Integer.toString(cursor.getInt(0));

            contentResolver.delete(ShoppingListContract.ShoppingListProduct.CONTENT_URI, ShoppingListContract.ShoppingListProduct.SHOPPINGLIST_ID + " = ?", new String[] { id });
            contentResolver.delete(ShoppingListContract.ShoppingList.CONTENT_URI, ShoppingListContract.ShoppingList._ID + " = ?", new String[] { id });

            cursor.close();
        }
    }

    private void addShoppingListToDatabase(ShoppingList shoppingList)
    {
        ContentResolver contentResolver = getContentResolver();

        ContentValues newShoppingList = new ContentValues();

        newShoppingList.put(ShoppingListContract.ShoppingList.NAME, shoppingList.getName());
        newShoppingList.put(ShoppingListContract.ShoppingList.DATE, shoppingList.getDate().getTime());

        Uri shoppingListUri = contentResolver.insert(ShoppingListContract.ShoppingList.CONTENT_URI, newShoppingList);

        if (shoppingListUri != null)
        {
            ArrayList<ShoppingListItem> items = shoppingList.getItems();

            for (ShoppingListItem item : items)
            {
                ContentValues productValues = new ContentValues();

                productValues.put(ShoppingListContract.Product.TPNB, item.getTpnb());
                productValues.put(ShoppingListContract.Product.NAME, item.getName());
                productValues.put(ShoppingListContract.Product.DEPARTMENT, item.getDepartment());
                productValues.put(ShoppingListContract.Product.PRICE, item.getPrice());
                productValues.put(ShoppingListContract.Product.IMAGE_URL, item.getImageUrl());

                Cursor existingProductCursor = contentResolver.query(ShoppingListContract.Product.CONTENT_URI, ShoppingListContract.Product.PROJECTION_ALL, ShoppingListContract.Product.TPNB + " = ?", new String[]{ Integer.toString(item.getTpnb()) }, ShoppingListContract.Product.SORT_ORDER_DEFAULT);

                int productId = -1;

                if (existingProductCursor != null && existingProductCursor.getCount() > 0)
                {
                    contentResolver.update(ShoppingListContract.Product.CONTENT_URI, productValues, ShoppingListContract.Product.TPNB + " = ?", new String[] { Integer.toString(item.getTpnb()) });
                    existingProductCursor.moveToFirst();
                    productId = existingProductCursor.getInt(0);
                }
                else
                {
                    Uri productUri = contentResolver.insert(ShoppingListContract.Product.CONTENT_URI, productValues);

                    if (productUri != null)
                    {
                        productId = Integer.parseInt(productUri.getLastPathSegment());
                    }
                }

                if (existingProductCursor != null)
                {
                    existingProductCursor.close();
                }

                if (productId != -1)
                {
                    ContentValues newShoppingListProduct = new ContentValues();

                    newShoppingListProduct.put(ShoppingListContract.ShoppingListProduct.SHOPPINGLIST_ID, Integer.parseInt(shoppingListUri.getLastPathSegment()));
                    newShoppingListProduct.put(ShoppingListContract.ShoppingListProduct.PRODUCT_ID, productId);
                    newShoppingListProduct.put(ShoppingListContract.ShoppingListProduct.SEARCH_QUERY, item.getSearchQuery());
                    newShoppingListProduct.put(ShoppingListContract.ShoppingListProduct.BOUGHT, item.isBought());

                    contentResolver.insert(ShoppingListContract.ShoppingListProduct.CONTENT_URI, newShoppingListProduct);
                }
            }
        }
    }

    private void changeShoppingListInDatabase(ShoppingList shoppingList)
    {
        deleteShoppingListFromDatabase(shoppingList);
        addShoppingListToDatabase(shoppingList);
    }

    private void updateListVisibility()
    {
        int itemCount = adapter.getItemCount();
        findViewById(R.id.noListsTextView).setVisibility(itemCount > 0 ? View.INVISIBLE : View.VISIBLE);
        recyclerView.setVisibility(itemCount > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @NonNull
    @Override
    public Loader<ArrayList<ShoppingList>> onCreateLoader(int id, @Nullable Bundle args)
    {
        return new ShoppingListLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<ShoppingList>> loader, ArrayList<ShoppingList> data)
    {
        adapter.setItems(data);
        updateListVisibility();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<ShoppingList>> loader)
    {
        adapter.clearItems();
        updateListVisibility();
    }
}
