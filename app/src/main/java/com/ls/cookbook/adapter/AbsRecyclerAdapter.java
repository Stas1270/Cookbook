package com.ls.cookbook.adapter;

/**
 * Created by LS on 02.09.2017.
 */


import android.support.v7.widget.RecyclerView;

import com.ls.cookbook.adapter.viewholder.AbsViewHolder;
import com.ls.cookbook.interfaces.OnListItemClickListener;

import java.util.List;

public abstract class AbsRecyclerAdapter<T, VH extends AbsViewHolder<T>> extends RecyclerView.Adapter<VH> {

    protected List<T> list;
    protected OnListItemClickListener<T> onItemClickListener;

    public void setData(List<T> list) {
        if (list != null) {
            this.list = list;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        final T model = list.get(position);
        final int currentPosition = holder.getAdapterPosition();
        holder.onBindView(currentPosition, model);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public AbsRecyclerAdapter(List<T> list, OnListItemClickListener<T> onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void add(T t) {
        if (list != null && t != null) {
            list.add(t);
            notifyItemInserted(list.size() - 1);
        }
    }
}