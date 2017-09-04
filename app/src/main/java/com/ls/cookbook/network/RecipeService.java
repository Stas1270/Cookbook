package com.ls.cookbook.network;

import com.ls.cookbook.data.model.Recipe;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by LS on 03.09.2017.
 */

public interface RecipeService {


    @GET("recipe.json")
    Observable<List<Recipe>> getRecipeList(@Query("auth") String auth);

    @POST("recipe.json")
    @FormUrlEncoded
    Observable<Void> putRecipe(@Query("auth") String auth,
                               @Field("name")String name);

}
