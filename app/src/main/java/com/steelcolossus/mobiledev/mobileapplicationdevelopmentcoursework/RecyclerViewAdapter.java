package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public abstract class RecyclerViewAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH>
{
    protected final ArrayList<T> dataset;

    RecyclerViewAdapter(ArrayList<T> dataset)
    {
        this.dataset = dataset;
    }

    @Override
    public int getItemCount()
    {
        return dataset.size();
    }

    public ArrayList<T> getDataset()
    {
        return dataset;
    }

    public int indexOf(T t)
    {
        return dataset.indexOf(t);
    }

    public void addItem(T t)
    {
        dataset.add(t);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeItem(int position)
    {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    public void changeItem(int position, T t)
    {
        dataset.set(position, t);
        notifyItemChanged(position);
    }

    public void clearItems()
    {
        while (getItemCount() > 0)
        {
            removeItem(0);
        }
    }

    public void setItems(ArrayList<T> dataset)
    {
        clearItems();

        for (T t : dataset)
        {
            addItem(t);
        }
    }
}
