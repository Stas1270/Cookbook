package com.ls.cookbook.data.source.remote;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.data.source.DataSource;

import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE;

    private DatabaseReference mDatabase;

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private RemoteDataSource() {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    //    @Override
    public Observable<Boolean> isAvailableConnect() {
        return RxFirebaseDatabase
                .observeSingleValueEvent(FirebaseDatabase.getInstance().getReference(".info/connected"), Boolean.class)
                .toObservable();
    }

    @Override
    public Observable<List<Recipe>> getRecipeList() {
        return RxFirebaseDatabase
                .observeSingleValueEvent(getRecipeRef().orderByValue(), DataSnapshotMapper.listOf(Recipe.class))
                .toObservable();
    }

    private DatabaseReference getRecipeRef() {
        return mDatabase.child("recipes");
    }

    private DatabaseReference getUserRecipeRef() {
        return mDatabase.child("user-recipes");
    }

    @Override
    public Maybe<Recipe> getRecipe(@NonNull String taskId) {
        return RxFirebaseDatabase
                .observeSingleValueEvent(getRecipeRef().child("name").equalTo("test"), Recipe.class);
//                .toObservable()
//                .firstElement();
    }

    @Override
    public Observable<Recipe> saveRecipe(@NonNull Recipe recipe) {
        return RxFirebaseDatabase.setValue(getRecipeRef().child(String.valueOf(recipe.getId())), recipe).toObservable();
    }

    @Override
    public void deleteRecipe(@NonNull String id) {
    }

    @Override
    public void refreshRecipeList() {

    }
}
