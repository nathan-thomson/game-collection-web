package com.nathanthomson.gamecollectionweb;

import com.nathanthomson.gamecollectionweb.dto.AddGameRequest;
import com.nathanthomson.gamecollectionweb.dto.UpdateGameRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usergames")
public class UserGameController {

    private final UserGameRepository userGameRepository;
    private final UserRepository userRepository;

    public UserGameController(UserGameRepository userGameRepository, UserRepository userRepository){
        this.userGameRepository = userGameRepository;
        this.userRepository = userRepository;
    }


    //checks if user is logged in, if yes, return that users id
    private Long requireLoggedInUserId(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            throw new RuntimeException("Not logged in");
        }
        return userId;
    }

    @PostMapping
    public UserGame addGameToCollection(@RequestBody @Valid AddGameRequest request, HttpSession session){ //@Valid ensures constrains set up in AddGameRequest are followed

        Long userId = requireLoggedInUserId(session); //check if user logged in, and get userId

        User user = userRepository.findById(userId).orElseThrow();

        UserGame userGame = new UserGame();
        userGame.setUser(user);
        userGame.setTitle(request.getTitle());
        userGame.setCoverURL(request.getCoverURL());
        userGame.setStatus(request.getStatus());
        userGame.setRating(request.getRating());
        userGame.setReview(request.getReview());

        return userGameRepository.save(userGame);
        //look up user by ID sent in request

        }


    @GetMapping("/user/collection")
    public List<UserGame> getUserCollection(HttpSession session){

        Long userId = requireLoggedInUserId(session);
        return userGameRepository.findByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFromCollection(@PathVariable Long id, HttpSession session){

        Long userId = requireLoggedInUserId(session);

        UserGame userGame = userGameRepository.findById(id).orElseThrow();

        if(!userGame.getUser().getId().equals(userId)){
            throw new RuntimeException("Not your entry to delete");
        }

        userGameRepository.deleteById(id);
    }


    //Recieve the DTO, only allows manipulation on rating, review and status,
    //
    @PutMapping("/{id}") //get id from url path, use this as the userGame entry u are updating
    public UserGame updateUserGame(@PathVariable Long id, @RequestBody UpdateGameRequest request, HttpSession session){

        Long userId = requireLoggedInUserId(session);

        UserGame userGame = userGameRepository.findById(id).orElseThrow();

        if(!userGame.getUser().getId().equals(userId)){ //if user associated with this usergame, dosnt match user making request, block
            throw new RuntimeException("Not your entry to edit");
        }

        userGame.setTitle(request.getTitle());
        userGame.setCoverURL(request.getCoverURL());
        userGame.setRating(request.getRating());
        userGame.setReview(request.getReview());
        userGame.setStatus(request.getStatus());

        return userGameRepository.save(userGame);
    }
}
