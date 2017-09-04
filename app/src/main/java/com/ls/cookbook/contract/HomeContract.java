package com.ls.cookbook.contract;

import com.ls.cookbook.BasePresenter;
import com.ls.cookbook.data.model.Recipe;

import java.util.List;

/**
 * Created by LS on 02.09.2017.
 */

public interface HomeContract {

    interface View {
        void setLoadingIndicator(boolean b);

        void showLoadingError();

        void showRecipeList(List<Recipe> recipeList);

        void showNoRecipes();

        void setPresenter(Presenter presenter);
    }

    interface Presenter extends BasePresenter{

        void getRecipeList(boolean forceUpdate);

        void addRecipe(Recipe recipe);
    }
}
