package com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData;

/**
 * Created by Dream Land on 10/19/2017.
 */

public class ListItems {
    private String name;
    private String id;
    private String mobile;
    private String imageUrl;

    public ListItems(String name, String id, String mobile, String imageUrl) {
        this.name = name;
        this.id = id;
        this.mobile = mobile;
        this.imageUrl = imageUrl;
    }

    public ListItems (String name, String id, String mobile) {
        this.name = name;
        this.id = id;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMobile() {
        return mobile;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}