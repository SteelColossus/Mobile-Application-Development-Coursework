package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

interface ShoppingListItemContextMenuFunction
{
    boolean onMenuItemClick(int menuItemId, View view, ShoppingListItem shoppingListItem);
}

interface SuggestionButtonsFunction
{
    void onConfirmSuggestionClick(View view, ShoppingListItem shoppingListItem);

    void onRemoveSuggestionClick(View view, ShoppingListItem shoppingListItem);
}

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>
{
    private final ArrayList<ShoppingListItem> dataset;
    private final SparseBooleanArray suggestionMap;
    private final boolean isNew;

    private ShoppingListItemContextMenuFunction menuFunction;
    private SuggestionButtonsFunction suggestionButtonsFunction;

    ShoppingListAdapter(ArrayList<ShoppingListItem> dataset, boolean isNew)
    {
        this.dataset = dataset;
        this.isNew = isNew;

        suggestionMap = new SparseBooleanArray();

        for (ShoppingListItem shoppingListItem : dataset)
        {
            suggestionMap.append(shoppingListItem.getTpnb(), false);
        }
    }

    public ArrayList<ShoppingListItem> getDataset()
    {
        return dataset;
    }

    public void setMenuFunction(ShoppingListItemContextMenuFunction menuFunction)
    {
        this.menuFunction = menuFunction;
    }

    public void setSuggestionButtonsFunction(SuggestionButtonsFunction suggestionButtonsFunction)
    {
        this.suggestionButtonsFunction = suggestionButtonsFunction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Create a new view, using the parent's context and the layout of the shopping list item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product_item, parent, false);
        // Create a new view holder using the created view
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
        final ShoppingListItem shoppingListItem = dataset.get(position);

        // Set the text of all the text views
        holder.setNameText(shoppingListItem.getName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
        String priceText = formatter.format(shoppingListItem.getPrice());

        holder.setPriceText(priceText);

        holder.setImage(shoppingListItem.getImageUrl());

        ImageView overlayImageView = holder.itemView.findViewById(R.id.overlayImageView);
        Button confirmSuggestionButton = holder.itemView.findViewById(R.id.confirmSuggestionButton);
        Button removeSuggestionButton = holder.itemView.findViewById(R.id.removeSuggestionButton);

        boolean isSuggestion = suggestionMap.get(shoppingListItem.getTpnb());

        if (shoppingListItem.isBought())
        {
            overlayImageView.setImageResource(R.color.colorGreyedOut);
            overlayImageView.setVisibility(View.VISIBLE);
        }
        else if (isSuggestion)
        {
            overlayImageView.setImageResource(R.color.colorSuggestion);
            overlayImageView.setVisibility(View.VISIBLE);
        }
        else
        {
            overlayImageView.setVisibility(View.GONE);
        }

        confirmSuggestionButton.setVisibility(isSuggestion ? View.VISIBLE : View.GONE);
        removeSuggestionButton.setVisibility(isSuggestion ? View.VISIBLE : View.GONE);

        if (isSuggestion)
        {
            confirmSuggestionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (suggestionButtonsFunction != null)
                    {
                        changeIsSuggestion(shoppingListItem, false);
                        moveItemToEnd(shoppingListItem);
                        suggestionButtonsFunction.onConfirmSuggestionClick(v, shoppingListItem);
                    }
                }
            });

            removeSuggestionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (suggestionButtonsFunction != null)
                    {
                        removeItem(shoppingListItem);
                        suggestionButtonsFunction.onRemoveSuggestionClick(v, shoppingListItem);
                    }
                }
            });
        }
        else
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!isNew)
                    {
                        shoppingListItem.setBought(!shoppingListItem.isBought());
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(final View v)
                {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.shoppinglist_item_context_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            if (menuFunction != null)
                            {
                                return menuFunction.onMenuItemClick(item.getItemId(), v, shoppingListItem);
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
    }

    @Override
    public int getItemCount()
    {
        return dataset.size();
    }

    public boolean isSuggestion(int tpnb)
    {
        return suggestionMap.get(tpnb);
    }

    public void addItem(ShoppingListItem shoppingListItem)
    {
        addItem(shoppingListItem, false);
    }

    public void addItem(ShoppingListItem shoppingListItem, boolean isSuggestion)
    {
        dataset.add(shoppingListItem);
        suggestionMap.put(shoppingListItem.getTpnb(), isSuggestion);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeItem(ShoppingListItem shoppingListItem)
    {
        int index = dataset.indexOf(shoppingListItem);

        dataset.remove(index);
        suggestionMap.delete(shoppingListItem.getTpnb());
        notifyItemRemoved(index);
    }

    public void changeItem(ShoppingListItem oldShoppingListItem, ShoppingListItem newShoppingListItem)
    {
        int index = dataset.indexOf(oldShoppingListItem);

        dataset.set(index, newShoppingListItem);
        suggestionMap.delete(oldShoppingListItem.getTpnb());
        suggestionMap.put(newShoppingListItem.getTpnb(), false);
        notifyItemChanged(index);
    }

    public void changeIsSuggestion(ShoppingListItem shoppingListItem, boolean isSuggestion)
    {
        int index = dataset.indexOf(shoppingListItem);

        suggestionMap.put(shoppingListItem.getTpnb(), isSuggestion);
        notifyItemChanged(index);
    }

    public void moveItemToEnd(ShoppingListItem shoppingListItem)
    {
        int index = dataset.indexOf(shoppingListItem);

        dataset.remove(index);
        dataset.add(shoppingListItem);
        notifyItemMoved(index, dataset.size() - 1);
    }

    // A view holder used to store the elements of the recycler view
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView productImageView;
        private final TextView nameTextView;
        private final TextView priceTextView;

        ViewHolder(View itemView)
        {
            super(itemView);

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
            Glide.with(productImageView.getContext()).load(url).into(productImageView);
        }
    }
}
