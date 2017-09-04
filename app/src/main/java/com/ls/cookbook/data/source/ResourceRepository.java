package com.ls.cookbook.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by LS on 04.09.2017.
 */

public class ResourceRepository implements ResourceSource {

    @Nullable
    private static ResourceRepository INSTANCE = null;

    ResourceSource resourceRemoteDataSource;

    ResourceSource resourceLocalDataSource;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public static ResourceRepository getInstance(@NonNull ResourceSource tasksRemoteDataSource,
                                                 @NonNull ResourceSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ResourceRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    public ResourceRepository(ResourceSource resourceRemoteDataSource, ResourceSource resourceLocalDataSource) {
        this.resourceLocalDataSource = resourceLocalDataSource;
        this.resourceRemoteDataSource = resourceRemoteDataSource;
    }
}
