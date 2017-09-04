package com.ls.cookbook.data.model;

/**
 * Created by LS on 03.09.2017.
 */

public class Recipe {

    private long id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Recipe(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Id = " + id +
                "; name = " + name +
                "; descr = " + description;
    }
}
