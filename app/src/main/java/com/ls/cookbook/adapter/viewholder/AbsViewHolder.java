package com.ls.cookbook.adapter.viewholder;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ls.cookbook.interfaces.OnListItemClickListener;

/**
 * Created by LS on 02.09.2017.
 */

public abstract  class AbsViewHolder<T> extends RecyclerView.ViewHolder {

    protected OnListItemClickListener<T> onListItemClickListener;

    public AbsViewHolder(@NonNull ViewGroup viewGroup, @Nullable OnListItemClickListener<T> onListItemClickListener, @LayoutRes int layoutId) {
        super(initItemView(viewGroup, layoutId));
        this.onListItemClickListener = onListItemClickListener;
    }

    private static View initItemView(ViewGroup viewGroup, @LayoutRes int layoutId) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
    }

    abstract public void onBindView(int position, T object);
}