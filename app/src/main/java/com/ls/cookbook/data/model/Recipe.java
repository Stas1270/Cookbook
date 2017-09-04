package com.ls.cookbook.data.model;

/**
 * Created by LS on 03.09.2017.
 */

public class Recipe {

    private String id;
    private String name;
    private String description;

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

    public Recipe(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
