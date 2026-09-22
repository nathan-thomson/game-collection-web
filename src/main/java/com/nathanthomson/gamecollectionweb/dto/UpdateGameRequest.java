package com.nathanthomson.gamecollectionweb.dto;

import com.nathanthomson.gamecollectionweb.Status;
import jakarta.validation.constraints.NotNull;

public class UpdateGameRequest {

    private String title;
    private String coverURL;
    private Integer rating;
    private String review;
    @NotNull
    private Status status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


}
