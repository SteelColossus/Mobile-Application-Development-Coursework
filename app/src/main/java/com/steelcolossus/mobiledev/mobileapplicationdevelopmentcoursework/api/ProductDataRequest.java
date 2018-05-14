package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public class ProductDataRequest extends GsonRequest<ProductDataItem>
{
    private static final Gson gson = new Gson();

    ProductDataRequest(String url, Response.Listener<ProductDataItem> listener, Response.ErrorListener errorListener)
    {
        super(url, ProductDataItem.class, TescoRequestHandler.defaultHeaders, listener, errorListener);
    }

    @Override
    protected Response<ProductDataItem> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            JsonArray productsArray = jsonObject.getAsJsonArray("products");

            ProductDataItem productDataItem = null;

            if (productsArray.size() > 0)
            {
                productDataItem = gson.fromJson(productsArray.get(0), getClazz());
            }

            return Response.success(productDataItem, HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        }
        catch (JsonSyntaxException e)
        {
            return Response.error(new ParseError(e));
        }
    }
}
