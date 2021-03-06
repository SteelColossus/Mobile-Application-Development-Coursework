package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    private static final String INSTANCE_STATE_TAG_SHOPPING_LIST_ITEMS = INTENT_TAG_SHOPPING_LIST_DATA + "Items";
    private ShoppingListAdapter adapter;
    private ShoppingList initialShoppingList;
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
            initialShoppingList = savedInstanceState.getParcelable(INSTANCE_STATE_TAG_SHOPPING_LIST);
        }
        else
        {
            if (isNew)
            {
                initialShoppingList = new ShoppingList(-1, intent.getStringExtra(MainActivity.INTENT_TAG_SHOPPING_LIST_NAME), new Date());
            }
            else
            {
                initialShoppingList = intent.getParcelableExtra(MainActivity.INTENT_TAG_SHOPPING_LIST_DATA);
            }
        }

        if (initialShoppingList != null)
        {
            setTitle(initialShoppingList.getName());
        }

        RecyclerView recyclerView = findViewById(R.id.shoppingListRecyclerView);

        // Set that the elements of the recycler view will not change in size
        recyclerView.setHasFixedSize(true);
        // Set a linear layout manager for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<ShoppingListItem> shoppingListItems;

        if (savedInstanceState != null)
        {
            shoppingListItems = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_TAG_SHOPPING_LIST_ITEMS);
        }
        else
        {
            shoppingListItems = new ArrayList<>(initialShoppingList.getItems());
        }

        // Set up the adapter
        adapter = new ShoppingListAdapter(this, shoppingListItems, isNew);
        recyclerView.setAdapter(adapter);

        if (isNew)
        {
            ProductSuggestionsProvider productSuggestionsProvider = new ProductSuggestionsProvider(getContentResolver());

            for (ShoppingListItem shoppingListItem : productSuggestionsProvider.getSuggestedProducts())
            {
                adapter.addItem(shoppingListItem, true);
            }
        }

        updateViewVisibility();

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

        adapter.setSuggestionButtonsFunction(new SuggestionButtonsFunction()
        {
            @Override
            public void onConfirmSuggestionClick(View view, ShoppingListItem shoppingListItem)
            {
                updateViewVisibility();
                Snackbar.make(view, "Suggestion added", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRemoveSuggestionClick(View view, ShoppingListItem shoppingListItem)
            {
                updateViewVisibility();
                Snackbar.make(view, "Suggestion removed", Snackbar.LENGTH_LONG).show();
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
                        adapter.removeItem(adapter.indexOf(shoppingListItem));
                        updateViewVisibility();
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
        outState.putParcelable(INSTANCE_STATE_TAG_SHOPPING_LIST, initialShoppingList);
        outState.putParcelableArrayList(INSTANCE_STATE_TAG_SHOPPING_LIST_ITEMS, adapter.getDataset());

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

                if (adapter.isSuggestion(result.getTpnb()))
                {
                    adapter.removeItem(result.getTpnb());
                }

                adapter.addItem(result);

                updateViewVisibility();
            }
        }
        else if (requestCode == CHANGE_PRODUCT_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                ShoppingListItem result = data.getParcelableExtra(SearchProductsActivity.INTENT_TAG_PRODUCT);

                int itemTpnbToReplace = data.getIntExtra("tpnb", -1);

                ArrayList<ShoppingListItem> shoppingListItems = adapter.getDataset();

                for (ShoppingListItem oldShoppingListItem : shoppingListItems)
                {
                    if (oldShoppingListItem.getTpnb() == itemTpnbToReplace)
                    {
                        adapter.changeItem(adapter.indexOf(oldShoppingListItem), result);
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

    private void updateViewVisibility()
    {
        int itemCount = adapter.getItemCount();
        findViewById(R.id.noItemsTextView).setVisibility(itemCount > 0 ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.shoppingListRecyclerView).setVisibility(itemCount > 0 ? View.VISIBLE : View.INVISIBLE);

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
        String priceText = formatter.format(getTotalPrice());
        TextView totalPriceTextView = findViewById(R.id.totalPriceTextView);

        totalPriceTextView.setText(priceText);
    }

    private ArrayList<ShoppingListItem> getDatasetWithoutSuggestions()
    {
        ArrayList<ShoppingListItem> shoppingListItems = adapter.getDataset();

        for (ShoppingListItem shoppingListItem : new ArrayList<>(shoppingListItems))
        {
            if (adapter.isSuggestion(shoppingListItem.getTpnb()))
            {
                shoppingListItems.remove(shoppingListItem);
            }
        }

        return shoppingListItems;
    }

    private void showSaveShoppingListDialog(final BackNavigationCallback backNavigationCallback)
    {
        if (!initialShoppingList.getItems().equals(getDatasetWithoutSuggestions()))
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

    private float getTotalPrice()
    {
        float totalPrice = 0;

        for (ShoppingListItem shoppingListItem : adapter.getDataset())
        {
            if (!adapter.isSuggestion(shoppingListItem.getTpnb()))
            {
                totalPrice += shoppingListItem.getPrice();
            }
        }

        return totalPrice;
    }

    private void returnShoppingListFromActivity()
    {
        Intent resultIntent = new Intent();

        if (isNew)
        {
            initialShoppingList.setDate(new Date());
        }

        ArrayList<ShoppingListItem> shoppingListItems;

        if (isNew)
        {
            shoppingListItems = getDatasetWithoutSuggestions();
        }
        else
        {
            shoppingListItems = adapter.getDataset();
        }

        initialShoppingList.setItems(shoppingListItems);

        resultIntent.putExtra(MainActivity.INTENT_TAG_SHOPPING_LIST_DATA, initialShoppingList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
