package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public class ProductQueryRequest extends GsonRequest<ProductQuery>
{
    private static final Gson gson = new Gson();

    ProductQueryRequest(String url, Response.Listener<ProductQuery> listener, Response.ErrorListener errorListener)
    {
        super(url, ProductQuery.class, TescoRequestHandler.defaultHeaders, listener, errorListener);
    }

    @Override
    protected Response<ProductQuery> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            JsonObject productsObject = jsonObject.getAsJsonObject("uk").getAsJsonObject("ghs").getAsJsonObject("products");

            return Response.success(gson.fromJson(productsObject, getClazz()), HttpHeaderParser.parseCacheHeaders(response));
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
