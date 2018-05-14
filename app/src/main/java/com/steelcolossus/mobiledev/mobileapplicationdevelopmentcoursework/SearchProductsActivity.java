package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.ProductDataCallback;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.ProductDataItem;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.ProductQueryCallback;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.ProductQueryItem;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.ProductQuery;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.TescoRequestHandler;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.barcode.IntentIntegrator;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.barcode.IntentResult;

import java.util.ArrayList;

interface MenuCompletedCallback
{
    void onMenuCompleted();
}

public class SearchProductsActivity extends AppCompatActivity
{
    private TescoRequestHandler requestHandler;
    private ProductAdapter productAdapter;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SearchView searchView;

    private MenuCompletedCallback menuCompletedCallback = null;

    private String searchQuery;

    private int itemTpnbToReplace = -1;

    public static final String INTENT_TAG_PRODUCT = "product";

    private static final String INSTANCE_STATE_TAG_DATASET = "dataset";
    private static final String INSTANCE_STATE_TAG_SEARCH_TEXT = "searchText";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.productRecyclerView);

        // Set that the elements of the recycler view will not change in size
        recyclerView.setHasFixedSize(true);
        // Set a linear layout manager for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<ProductQueryItem> dataset = new ArrayList<>();

        if (savedInstanceState != null)
        {
            dataset = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_TAG_DATASET);
        }

        // Set up the adapter
        productAdapter = new ProductAdapter(dataset);
        productAdapter.setOnClickFunction(new ProductFunction()
        {
            @Override
            public void run(ProductQueryItem productQueryItem)
            {
                returnProductFromActivity(productQueryItem);
            }
        });
        recyclerView.setAdapter(productAdapter);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        requestHandler = new TescoRequestHandler(this);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        this.searchView = searchView;
        searchView.setIconifiedByDefault(false);
        //searchView.setMaxWidth(searchView.getContext().getResources().getDisplayMetrics().widthPixels);

        if (searchManager != null)
        {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        if (menuCompletedCallback != null)
        {
            menuCompletedCallback.onMenuCompleted();
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString(INSTANCE_STATE_TAG_SEARCH_TEXT, searchView.getQuery().toString());

        outState.putParcelableArrayList(INSTANCE_STATE_TAG_DATASET, productAdapter.getDataset());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        final String searchText = savedInstanceState.getString(INSTANCE_STATE_TAG_SEARCH_TEXT);
        searchQuery = searchText;

        menuCompletedCallback = new MenuCompletedCallback()
        {
            @Override
            public void onMenuCompleted()
            {
                searchView.setQuery(searchText, false);
            }
        };

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.scanner:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        final Context context = this;

        if (scanResult != null)
        {
            setInProgress(true);

            requestHandler.getResultsFromProductDataQuery(scanResult.getContents(), new ProductDataCallback()
            {
                @Override
                public void onProductFoundResponse(final ProductDataItem productDataItem)
                {
                    requestHandler.getResultsFromProductSearchQuery(productDataItem.getDescription(), new ProductQueryCallback()
                    {
                        @Override
                        public void onSuccessResponse(ProductQuery productQuery)
                        {
                            ProductQueryItem testProduct = productQuery.results.get(0);

                            if (testProduct.getName().equals(productDataItem.getDescription()))
                            {
                                returnProductFromActivity(testProduct);
                            }
                            else
                            {
                                productAdapter.setItems(productQuery.results);
                                setInProgress(false);
                            }
                        }
                    });
                }

                @Override
                public void onNoProductsFoundResponse()
                {
                    setInProgress(false);
                    Toast.makeText(context, R.string.no_scan_results_found_message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        String intentAction = intent.getAction();

        if (intentAction != null && intentAction.equals(Intent.ACTION_SEARCH))
        {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);

            if (intent.hasExtra("tpnb"))
            {
                itemTpnbToReplace = intent.getIntExtra("tpnb", -1);
            }

            // Set the search bar text if it hasn't been set - probably from voice search
            if (searchView != null)
            {
                searchView.setQuery(searchQuery, false);
            }
            else
            {
                menuCompletedCallback = new MenuCompletedCallback()
                {
                    @Override
                    public void onMenuCompleted()
                    {
                        searchView.setQuery(searchQuery, false);
                    }
                };
            }

            setInProgress(true);

            requestHandler.getResultsFromProductSearchQuery(searchQuery, new ProductQueryCallback()
            {
                @Override
                public void onSuccessResponse(ProductQuery productQuery)
                {
                    if (productQuery.results.size() > 0)
                    {
                        productAdapter.setItems(productQuery.results);
                    }
                    else
                    {
                        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.no_search_results_found_message, Snackbar.LENGTH_LONG);
                    }

                    setInProgress(false);
                }
            });
        }
    }

    private void setInProgress(boolean inProgress)
    {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }

    private void returnProductFromActivity(ProductQueryItem productQueryItem)
    {
        Intent resultIntent = new Intent();
        ShoppingListItem shoppingListItem = new ShoppingListItem(productQueryItem, searchQuery);
        resultIntent.putExtra(INTENT_TAG_PRODUCT, shoppingListItem);

        if (itemTpnbToReplace != -1)
        {
            resultIntent.putExtra("tpnb", itemTpnbToReplace);
        }

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
