package com.nathanthomson.gamecollectionweb.dto;
import com.nathanthomson.gamecollectionweb.Status;

public class UserGameResponse {

    private Long id;
    private String title;
    private String coverURL;
    private Status status;
    private Integer rating;
    private String review;

    public UserGameResponse(Long id, String title, String coverURL, Status status, Integer rating, String review){
        this.id = id;
        this.title = title;
        this.coverURL = coverURL;
        this.status = status;
        this.rating = rating;
        this.review = review;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
}
