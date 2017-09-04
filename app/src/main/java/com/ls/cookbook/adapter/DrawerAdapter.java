package com.ls.cookbook.adapter;

/**
 * Created by LS on 02.09.2017.
 */

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ls.cookbook.R;
import com.ls.cookbook.adapter.viewholder.AbsViewHolder;
import com.ls.cookbook.interfaces.OnListItemClickListener;

import java.util.List;


public class DrawerAdapter extends AbsRecyclerAdapter<DrawerAdapter.NavigationItems, DrawerAdapter.ViewHolder> {

    private int selectedItemPosition = -1;

    public enum NavigationItems {
        Home("Home"),
        Settings("Settings"),
        Logout("Logout");

        private String itemName;

        NavigationItems(String s) {
            itemName = s;
        }

        public String getItemName() {
            return itemName;
        }
    }

    public DrawerAdapter(List<DrawerAdapter.NavigationItems> list, OnListItemClickListener<DrawerAdapter.NavigationItems> onItemClickListener) {
        super(list, onItemClickListener);
    }

    public void setSelected(int position) {
        if (selectedItemPosition != -1) {
            notifyItemChanged(selectedItemPosition);
        }
        selectedItemPosition = position;
        notifyItemChanged(selectedItemPosition);
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }


    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DrawerAdapter.ViewHolder(parent, onItemClickListener, R.layout.item_drawer);
    }

    class ViewHolder extends AbsViewHolder<DrawerAdapter.NavigationItems> {
        private final TextView tvTitle;

        public ViewHolder(@NonNull ViewGroup viewGroup, @Nullable OnListItemClickListener<DrawerAdapter.NavigationItems> onListItemClickListener, @LayoutRes int layoutId) {
            super(viewGroup, onListItemClickListener, layoutId);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
        }


        @Override
        public void onBindView(int position, DrawerAdapter.NavigationItems object) {
            final DrawerAdapter.NavigationItems drawerItem = list.get(position);
            final int currentPosition = getAdapterPosition();
            tvTitle.setText(drawerItem.getItemName());

            if (selectedItemPosition == position) {
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
            } else {
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
            }

            if (!itemView.hasOnClickListeners()) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onListItemClickListener != null) {
                            onListItemClickListener.onItemClick(currentPosition, drawerItem);
                        }
                    }
                });
            }
        }
    }
}