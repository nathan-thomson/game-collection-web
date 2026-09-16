package com.nathanthomson.gamecollectionweb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameRepository gameRepository;

    public GameController(GameRepository gameRepository){
        this.gameRepository = gameRepository;

    }

    @GetMapping
    public List<Game> getGames(){
        return gameRepository.findAll();
    }

    @GetMapping("/search")
    public List<Game> searchGame(@RequestParam String title){
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }



}
