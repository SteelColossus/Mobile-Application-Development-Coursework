package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

interface ShoppingListFunction
{
    void run(ShoppingList shoppingList);
}

interface ShoppingListContextMenuFunction
{
    boolean onMenuItemClick(int menuItemId, View view, ShoppingList shoppingList);
}

public class ListOfShoppingListsAdapter extends RecyclerViewAdapter<ListOfShoppingListsAdapter.ViewHolder, ShoppingList>
{
    private final NumberFormat priceFormatter;
    private ShoppingListFunction onClickFunction;
    private ShoppingListContextMenuFunction menuFunction;

    ListOfShoppingListsAdapter(ArrayList<ShoppingList> dataset)
    {
        super(dataset);
        priceFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
    }

    public void setOnClickFunction(ShoppingListFunction onClickFunction)
    {
        this.onClickFunction = onClickFunction;
    }

    public void setMenuFunction(ShoppingListContextMenuFunction menuFunction)
    {
        this.menuFunction = menuFunction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Create a new view, using the parent's context and the layout of the shopping list item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shoppinglist_item, parent, false);
        // Create a new view holder using the created view
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final ShoppingList shoppingList = dataset.get(position);

        // Set the text of all the text views
        holder.setNameText(shoppingList.getName());
        holder.setDateText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(shoppingList.getDate()));
        holder.setNumItemsText(shoppingList.getNumItems() + " items");
        holder.setTotalPriceText(priceFormatter.format(shoppingList.getTotalPrice()));

        holder.itemView.findViewById(R.id.boughtOverlayImageView).setVisibility(shoppingList.isCompleted() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onClickFunction != null)
                {
                    onClickFunction.run(shoppingList);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(final View v)
            {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.shoppinglist_context_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        if (menuFunction != null)
                        {
                            return menuFunction.onMenuItemClick(item.getItemId(), v, shoppingList);
                        }
                        else
                        {
                            return false;
                        }
                    }
                });

                popupMenu.show();

                return true;
            }
        });
    }

    // A view holder used to store the elements of the recycler view
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView nameTextView;
        private final TextView dateTextView;
        private final TextView numItemsTextView;
        private final TextView totalPriceTextView;

        ViewHolder(View itemView)
        {
            super(itemView);

            // Set the different contained views from the base view
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.priceTextView);
            numItemsTextView = itemView.findViewById(R.id.numItemsTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
        }

        void setNameText(String text)
        {
            this.nameTextView.setText(text);
        }

        void setDateText(String text)
        {
            this.dateTextView.setText(text);
        }

        void setNumItemsText(String text)
        {
            this.numItemsTextView.setText(text);
        }

        void setTotalPriceText(String text)
        {
            this.totalPriceTextView.setText(text);
        }
    }
}
