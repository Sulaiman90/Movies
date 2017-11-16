package com.innovae.movies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieCreditsResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("cast")
    private List<MovieCast> casts;
    @SerializedName("crew")
    private List<MovieCrew> crews;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MovieCast> getCasts() {
        return casts;
    }

    public void setCasts(List<MovieCast> casts) {
        this.casts = casts;
    }

    public List<MovieCrew> getCrews() {
        return crews;
    }

    public void setCrews(List<MovieCrew> crews) {
        this.crews = crews;
    }
}
