package com.nathanthomson.gamecollectionweb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTitleContainingIgnoreCase(String title); //spring derives what you want from method name
    Optional<Game> findByTitleIgnoreCase(String title);
}
