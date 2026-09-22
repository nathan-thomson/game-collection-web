package com.nathanthomson.gamecollectionweb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

//https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html

@Entity
public class UserGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user; //one user can have many usergame rows, GTA, Elden RIng, Hollow knight etc

    @NotBlank
    private String title;
    private String coverURL;
    @Enumerated(EnumType.STRING)
    private Status status; //PLAYER or WANT_TO_PLAY

    private Integer rating; //Integer over int allows null, rating is optional
    private String review; //^^^

    public UserGame(){
    }

    public UserGame(User user, Status status){
        this.user = user;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
