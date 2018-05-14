package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.provider.ShoppingListContract;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private static final int NEW_SHOPPING_LIST_REQUEST = 1;
    private static final int EXISTING_SHOPPING_LIST_REQUEST = 2;

    public static final String INTENT_TAG_SHOPPING_LIST_IS_NEW = "new";
    public static final String INTENT_TAG_SHOPPING_LIST_NAME = "name";
    public static final String INTENT_TAG_SHOPPING_LIST_DATA = "shoppingList";

    private RecyclerView recyclerView;
    private ListOfShoppingListsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<ShoppingList> dataset = loadShoppingListsFromContentProvider();

        recyclerView = findViewById(R.id.listOfShoppingListsRecyclerView);

        // Set that the elements of the recycler view will not change in size
        recyclerView.setHasFixedSize(true);
        // Set a linear layout manager for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Context context = this;

        // Set up the adapter
        adapter = new ListOfShoppingListsAdapter(dataset);
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

        adapter.setMenuFunction(new ShoppingListContextMenuFunction() {
            @Override
            public boolean onMenuItemClick(int menuItemId, View view, ShoppingList shoppingList)
            {
                switch (menuItemId)
                {
                    case R.id.remove:
                        adapter.removeItem(shoppingList);
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

        updateListVisibility(adapter.getItemCount());
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

                updateListVisibility(adapter.getItemCount() + 1);
                adapter.addItemToStart(result);
            }
        }
        else if (requestCode == EXISTING_SHOPPING_LIST_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                ShoppingList result = data.getParcelableExtra(ShoppingListActivity.INTENT_TAG_SHOPPING_LIST_DATA);

                adapter.changeItem(result.getName(), result);
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        saveShoppingListsWithContentProvider(adapter.getDataset());
    }

    private ArrayList<ShoppingList> loadShoppingListsFromContentProvider()
    {
        ContentResolver contentResolver = getContentResolver();
        ArrayList<ShoppingList> dataset = new ArrayList<>();

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
                dataset.add(shoppingList);
            }

            shoppingListCursor.close();
        }

        return dataset;
    }

    private void saveShoppingListsWithContentProvider(ArrayList<ShoppingList> dataset)
    {
        ContentResolver contentResolver = getContentResolver();

        contentResolver.delete(ShoppingListContract.Product.CONTENT_URI, null, null);
        contentResolver.delete(ShoppingListContract.ShoppingList.CONTENT_URI, null, null);

        int j = 0;

        for (int i = 0; i < dataset.size(); i++)
        {
            ShoppingList shoppingList = dataset.get(i);

            ContentValues newShoppingList = new ContentValues();

            newShoppingList.put(ShoppingListContract.ShoppingList._ID, i + 1);
            newShoppingList.put(ShoppingListContract.ShoppingList.NAME, shoppingList.getName());
            newShoppingList.put(ShoppingListContract.ShoppingList.DATE, shoppingList.getDate().getTime());

            contentResolver.insert(ShoppingListContract.ShoppingList.CONTENT_URI, newShoppingList);

            ArrayList<ShoppingListItem> items = shoppingList.getItems();

            for (ShoppingListItem item : items)
            {
                ContentValues newProduct = new ContentValues();

                newProduct.put(ShoppingListContract.Product._ID, j + 1);
                newProduct.put(ShoppingListContract.Product.TPNB, item.getTpnb());
                newProduct.put(ShoppingListContract.Product.NAME, item.getName());
                newProduct.put(ShoppingListContract.Product.DEPARTMENT, item.getDepartment());
                newProduct.put(ShoppingListContract.Product.PRICE, item.getPrice());
                newProduct.put(ShoppingListContract.Product.IMAGE_URL, item.getImageUrl());
                newProduct.put(ShoppingListContract.Product.SEARCH_QUERY, item.getSearchQuery());
                newProduct.put(ShoppingListContract.Product.BOUGHT, item.isBought());
                newProduct.put(ShoppingListContract.Product.SHOPPINGLIST_ID, i + 1);

                contentResolver.insert(ShoppingListContract.Product.CONTENT_URI, newProduct);

                j++;
            }
        }
    }

    private void updateListVisibility(int itemCount)
    {
        findViewById(R.id.noListsTextView).setVisibility(itemCount > 0 ? View.INVISIBLE : View.VISIBLE);
        recyclerView.setVisibility(itemCount > 0 ? View.VISIBLE : View.INVISIBLE);
    }
}
