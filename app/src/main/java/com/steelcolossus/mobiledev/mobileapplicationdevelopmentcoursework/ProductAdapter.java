package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework.api.ProductQueryItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

interface ProductFunction
{
    void run(ProductQueryItem productQueryItem);
}

public class ProductAdapter extends RecyclerViewAdapter<ProductAdapter.ViewHolder, ProductQueryItem>
{
    private final Activity activity;

    private ProductFunction onClickFunction;

    ProductAdapter(Activity activity, ArrayList<ProductQueryItem> dataset)
    {
        super(dataset);

        this.activity = activity;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Create a new view, using the parent's context and the layout of the shopping list item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product_item, parent, false);
        // Create a new view holder using the created view
        return new ProductAdapter.ViewHolder(activity, v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position)
    {
        final ProductQueryItem productQueryItem = dataset.get(position);

        // Set the text of all the text views
        holder.setNameText(productQueryItem.getName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
        String priceText = formatter.format(productQueryItem.getPrice());

        holder.setPriceText(priceText);

        holder.setImage(productQueryItem.getImageUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onClickFunction != null)
                {
                    onClickFunction.run(productQueryItem);
                }
            }
        });
    }

    public void setOnClickFunction(ProductFunction onClickFunction)
    {
        this.onClickFunction = onClickFunction;
    }

    // A view holder used to store the elements of the recycler view
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final Activity activity;
        private final ImageView productImageView;
        private final TextView nameTextView;
        private final TextView priceTextView;

        ViewHolder(Activity activity, View itemView)
        {
            super(itemView);

            this.activity = activity;

            // Set the different contained views from the base view
            productImageView = itemView.findViewById(R.id.productImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }

        void setNameText(String text)
        {
            this.nameTextView.setText(text);
        }

        void setPriceText(String text)
        {
            this.priceTextView.setText(text);
        }

        void setImage(String url)
        {
            Glide.with(activity).load(url).into(productImageView);
        }
    }
}
