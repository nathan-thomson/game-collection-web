package com.nathanthomson.gamecollectionweb;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//https://docs.spring.io/spring-data/jpa/reference/repositories/definition.html
//JpaRepository<EntityType, IdType> (always follows this structure)

//FOR CONTROLLER: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html

public interface UserGameRepository extends JpaRepository<UserGame, Long> {
    List<UserGame> findByUserId(Long userId, Sort sort); //return list of userGame by id passed in
    List<UserGame> findByUserIdAndStatus(Long userId, Status status);
}
