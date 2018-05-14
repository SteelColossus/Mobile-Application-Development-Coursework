package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api;

public interface ProductDataCallback
{
    void onProductFoundResponse(ProductDataItem productDataItem);

    void onNoProductsFoundResponse();
}
