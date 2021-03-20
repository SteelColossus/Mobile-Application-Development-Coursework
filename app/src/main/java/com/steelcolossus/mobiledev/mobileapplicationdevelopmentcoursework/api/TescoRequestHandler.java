package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class TescoRequestHandler
{
    public static final HashMap<String, String> defaultHeaders;
    private static final String SUBSCRIPTION_KEY = "ef5dd24ba6784aad87cea01e1ca68264";
    private static final int QUERY_LIMIT = 50;

    static
    {
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("Ocp-Apim-Subscription-Key", TescoRequestHandler.SUBSCRIPTION_KEY);
    }

    private final RequestQueue requestQueue;

    public TescoRequestHandler(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getResultsFromProductSearchQuery(String query, final ProductQueryCallback callback)
    {
        String url = "https://dev.tescolabs.com/grocery/products/?query=" + query + "&offset=" + 0 + "&limit=" + QUERY_LIMIT;

        ProductQueryRequest request = new ProductQueryRequest(url, new Response.Listener<ProductQuery>()
        {
            @Override
            public void onResponse(ProductQuery response)
            {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    public void getResultsFromProductDataQuery(String gtin, final ProductDataCallback callback)
    {
        String url = "https://dev.tescolabs.com/product/?gtin=" + gtin;

        ProductDataRequest request = new ProductDataRequest(url, new Response.Listener<ProductDataItem>()
        {
            @Override
            public void onResponse(ProductDataItem response)
            {
                if (response != null)
                {
                    callback.onProductFoundResponse(response);
                }
                else
                {
                    callback.onNoProductsFoundResponse();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }
}
