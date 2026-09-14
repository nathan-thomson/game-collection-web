package com.nathanthomson.gamecollectionweb;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity //each entity represents 1 database table, use data which is transferable between users, eg, title and image never change, while rating may be different, so that is handled elsewhere
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //create a primary key which increments by 1 (each user has 1 unique id)

    private Long id;
    private Long rawgId;
    private String title;
    private String genre;
    private String coverURL;

    public Game(){ //JPA requires a no-argument constructor
    }

    public Game(Long rawgId, String title, String genre, String coverURL){
        this.rawgId = rawgId;
        this.title = title;
        this.genre = genre;
        this.coverURL = coverURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getRawgId() {
        return rawgId;
    }

    public void setRawgId(long rawgId) {
        this.rawgId = rawgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }
}
