package com.nathanthomson.gamecollectionweb;

import com.nathanthomson.gamecollectionweb.dto.AddGameRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usergames")
public class UserGameController {

    private final UserGameRepository userGameRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public UserGameController(UserGameRepository userGameRepository, GameRepository gameRepository, UserRepository userRepository){
        this.userGameRepository = userGameRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public UserGame addGameToCollection(@RequestBody AddGameRequest request, HttpSession session){

        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            throw new RuntimeException("Not logged in");
        }

        Optional<Game> existingGame = gameRepository.findByTitleIgnoreCase(request.getTitle());

        Game game;
        if(existingGame.isPresent()){
            game = existingGame.get(); //if game row exists already, use that, else, create new. Same game used for multiple GameUser
        }else{
            game = new Game();
            game.setTitle(request.getTitle()); //create and save new game from title.
            game = gameRepository.save(game);  //saves to repository then loaded back in
        }

        User user = userRepository.findById(userId).orElseThrow();
        //look up user by ID sent in request

        Optional<UserGame> existingUserGame = userGameRepository.findByUserIdAndGameId(userId, game.getId());
        UserGame userGame;

        if(existingUserGame.isPresent()){
            userGame = existingUserGame.get(); //update existing entry instead of making a duplicate
        }else{
            userGame = new UserGame();
            userGame.setUser(user);
            userGame.setGame(game);
        }

        userGame.setRating(request.getRating());
        userGame.setReview(request.getReview());
        userGame.setStatus(request.getStatus());


        return userGameRepository.save(userGame);
    }

    @GetMapping("/user/collection")
    public List<UserGame> getUserCollection(HttpSession session){

        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            throw new RuntimeException("Not logged in");
        }
        return userGameRepository.findByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFromCollection(@PathVariable Long id, HttpSession session){

        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            throw new RuntimeException("Not logged in");
        }

        UserGame userGame = userGameRepository.findById(id).orElseThrow();

        if(!userGame.getUser().getId().equals(userId)){
            throw new RuntimeException("Not your entry to delete");
        }

        userGameRepository.deleteById(id);
    }
}
