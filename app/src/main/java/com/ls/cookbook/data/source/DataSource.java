/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ls.cookbook.data.source;

import android.support.annotation.NonNull;


import com.ls.cookbook.data.model.Recipe;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * Main entry point for accessing tasks data.
 * <p>
 */
public interface DataSource {

    Maybe<Recipe> getRecipe(@NonNull String recipeId);

    Observable<Recipe> saveRecipe(@NonNull Recipe recipe);

    void deleteRecipe(@NonNull String id);

    void refreshRecipeList();

    Observable<List<Recipe>> getRecipeList();

}
