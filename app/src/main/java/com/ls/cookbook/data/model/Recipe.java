package com.ls.cookbook.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LS on 03.09.2017.
 */

public class Recipe {

    @SerializedName("_id")
    private String id;

    private String name;

    private String description;

    @SerializedName("user_id")
    private long userId;

    @SerializedName("created_at")
    private long createdAt;

    public Recipe(String title, String description) {
        name = title;
        this.description = description;
    }

    public Recipe() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Recipe(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = System.currentTimeMillis();

    }

    @Override
    public String toString() {
        return "Id = " + id +
                "; name = " + name +
                "; descr = " + description;
    }
}
