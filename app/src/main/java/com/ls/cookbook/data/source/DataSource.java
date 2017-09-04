package com.ls.cookbook.data.source;

import android.support.annotation.NonNull;


import com.ls.cookbook.data.model.Recipe;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * Main entry point for accessing  data.
 * <p>
 */
public interface DataSource {

    Maybe<Recipe> getRecipe(@NonNull String recipeId);

    Observable<Recipe> saveRecipe(@NonNull Recipe recipe);

    void deleteRecipe(@NonNull String id);

    void refreshRecipeList();

    Observable<List<Recipe>> getRecipeList();

}
