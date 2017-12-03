package com.innovae.movies.model;

import com.google.gson.annotations.SerializedName;

public class Genre {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String genreName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public Genre(int id, String genreName){
        this.id = id;
        this.genreName = genreName;
    }
}
