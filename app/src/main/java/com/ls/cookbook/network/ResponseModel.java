package com.ls.cookbook.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by stanislav.safyanov on 11.09.17.
 */

public class ResponseModel<T> {

    @Expose
    @SerializedName("class_name")
    protected String className ;

    @Expose
    @SerializedName("skip")
    protected int skip ;

    @Expose
    @SerializedName("limit")
    protected int limit ;

    @Expose
    @SerializedName("items")
    protected T items;

    public ResponseModel() {
    }

    public T getItems() {
        return items;
    }

    public void setItems(T items) {
        this.items = items;
    }

    public String getClassName() {
        return className;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }
}
