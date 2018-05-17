package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;

interface BackNavigationCallback
{
    void onSaveUnnecessary();

    void onShoppingListSaved();

    void onShoppingListNotSaved();
}

public class ShoppingListActivity extends AppCompatActivity
{
    public static final String INTENT_TAG_SHOPPING_LIST_DATA = MainActivity.INTENT_TAG_SHOPPING_LIST_DATA;
    private static final int CHOOSE_PRODUCT_REQUEST = 1;
    private static final int CHANGE_PRODUCT_REQUEST = 2;
    private static final String INSTANCE_STATE_TAG_SHOPPING_LIST = INTENT_TAG_SHOPPING_LIST_DATA;
    private static final String INSTANCE_STATE_TAG_INITIAL_SHOPPING_LIST = INSTANCE_STATE_TAG_SHOPPING_LIST + "Initial";
    private ShoppingListAdapter adapter;
    private ShoppingList initialShoppingList;
    private ShoppingList shoppingList;
    private boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        isNew = intent.getBooleanExtra(MainActivity.INTENT_TAG_SHOPPING_LIST_IS_NEW, true);

        if (savedInstanceState != null)
        {
            shoppingList = savedInstanceState.getParcelable(INSTANCE_STATE_TAG_SHOPPING_LIST);
            initialShoppingList = savedInstanceState.getParcelable(INSTANCE_STATE_TAG_INITIAL_SHOPPING_LIST);
        }
        else
        {
            if (isNew)
            {
                shoppingList = new ShoppingList(intent.getStringExtra(MainActivity.INTENT_TAG_SHOPPING_LIST_NAME), new Date());
                initialShoppingList = null;
            }
            else
            {
                shoppingList = intent.getParcelableExtra(MainActivity.INTENT_TAG_SHOPPING_LIST_DATA);
                initialShoppingList = shoppingList.deepCopy();
            }
        }

        if (shoppingList != null)
        {
            setTitle(shoppingList.getName());
        }

        RecyclerView recyclerView = findViewById(R.id.shoppingListRecyclerView);

        // Set that the elements of the recycler view will not change in size
        recyclerView.setHasFixedSize(true);
        // Set a linear layout manager for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter
        adapter = new ShoppingListAdapter(shoppingList.getItems(), isNew);
        recyclerView.setAdapter(adapter);

        final Context context = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent chooseProductIntent = new Intent(context, SearchProductsActivity.class);
                startActivityForResult(chooseProductIntent, CHOOSE_PRODUCT_REQUEST);
            }
        });

        Button addListButton = findViewById(R.id.addListButton);
        addListButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                returnShoppingListFromActivity();
            }
        });

        adapter.setMenuFunction(new ShoppingListItemContextMenuFunction()
        {
            @Override
            public boolean onMenuItemClick(int menuItemId, View view, ShoppingListItem shoppingListItem)
            {
                switch (menuItemId)
                {
                    case R.id.remove:
                        adapter.removeItem(shoppingListItem);
                        Snackbar.make(view, shoppingListItem.getName() + " removed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        return true;
                    case R.id.changeItem:
                        Intent changeProductIntent = new Intent(context, SearchProductsActivity.class);
                        changeProductIntent.setAction(Intent.ACTION_SEARCH);
                        changeProductIntent.putExtra("query", shoppingListItem.getSearchQuery());
                        changeProductIntent.putExtra("tpnb", shoppingListItem.getTpnb());
                        startActivityForResult(changeProductIntent, CHANGE_PRODUCT_REQUEST);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(INSTANCE_STATE_TAG_SHOPPING_LIST, shoppingList);
        outState.putParcelable(INSTANCE_STATE_TAG_INITIAL_SHOPPING_LIST, initialShoppingList);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_PRODUCT_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                ShoppingListItem result = data.getParcelableExtra(SearchProductsActivity.INTENT_TAG_PRODUCT);

                adapter.addItem(result);
            }
        }
        else if (requestCode == CHANGE_PRODUCT_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                ShoppingListItem result = data.getParcelableExtra(SearchProductsActivity.INTENT_TAG_PRODUCT);

                int itemTpnbToReplace = data.getIntExtra("tpnb", -1);

                ArrayList<ShoppingListItem> shoppingListItems = adapter.getDataset();

                for (int i = 0; i < shoppingListItems.size(); i++)
                {
                    if (shoppingListItems.get(i).getTpnb() == itemTpnbToReplace)
                    {
                        adapter.changeItem(i, result);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        showSaveShoppingListDialog(new BackNavigationCallback()
        {
            @Override
            public void onSaveUnnecessary()
            {
                onShoppingListNotSaved();
            }

            @Override
            public void onShoppingListSaved()
            {
                returnShoppingListFromActivity();
            }

            @Override
            public void onShoppingListNotSaved()
            {
                ShoppingListActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        showSaveShoppingListDialog(new BackNavigationCallback()
        {
            @Override
            public void onSaveUnnecessary()
            {
                onShoppingListNotSaved();
            }

            @Override
            public void onShoppingListSaved()
            {
                returnShoppingListFromActivity();
            }

            @Override
            public void onShoppingListNotSaved()
            {
                ShoppingListActivity.super.onSupportNavigateUp();
            }
        });

        return false;
    }

    private void showSaveShoppingListDialog(final BackNavigationCallback backNavigationCallback)
    {
        if (initialShoppingList == null || !shoppingList.equals(initialShoppingList))
        {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

            dialogBuilder.setTitle(R.string.dialog_save_shopping_list_title).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    backNavigationCallback.onShoppingListSaved();
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    backNavigationCallback.onShoppingListNotSaved();
                }
            });

            dialogBuilder.create().show();
        }
        else
        {
            backNavigationCallback.onSaveUnnecessary();
        }
    }

    private void returnShoppingListFromActivity()
    {
        Intent resultIntent = new Intent();

        if (isNew)
        {
            shoppingList.setDate(new Date());
        }

        resultIntent.putExtra(MainActivity.INTENT_TAG_SHOPPING_LIST_DATA, shoppingList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}