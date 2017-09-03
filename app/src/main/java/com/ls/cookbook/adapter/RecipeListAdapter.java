package com.ls.cookbook.adapter;

/**
 * Created by LS on 02.09.2017.
 */

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ls.cookbook.R;
import com.ls.cookbook.adapter.viewholder.AbsViewHolder;
import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.interfaces.OnListItemClickListener;

import java.util.List;


public class RecipeListAdapter extends AbsRecyclerAdapter<Recipe, RecipeListAdapter.ViewHolder> {

    public RecipeListAdapter(List<Recipe> list, OnListItemClickListener<Recipe> onItemClickListener) {
        super(list, onItemClickListener);
    }

    @Override
    public RecipeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecipeListAdapter.ViewHolder(parent, onItemClickListener, R.layout.item_drawer);
    }

    class ViewHolder extends AbsViewHolder<Recipe> {
        private final TextView tvTitle;

        public ViewHolder(@NonNull ViewGroup viewGroup, @Nullable OnListItemClickListener<Recipe> onListItemClickListener, @LayoutRes int layoutId) {
            super(viewGroup, onListItemClickListener, layoutId);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
        }

        @Override
        public void onBindView(int position, Recipe object) {
            Recipe recipe = list.get(position);
            final int currentPosition = getAdapterPosition();
            tvTitle.setText(recipe.getName());

            if (!itemView.hasOnClickListeners()) {
                itemView.setOnClickListener(v -> {
                    if (onListItemClickListener != null) {
                        onListItemClickListener.onItemClick(currentPosition, recipe);
                    }
                });
            }
        }
    }
}