package com.nathanthomson.gamecollectionweb;

import com.nathanthomson.gamecollectionweb.dto.AddGameRequest;
import com.nathanthomson.gamecollectionweb.dto.UpdateGameRequest;
import com.nathanthomson.gamecollectionweb.dto.UserGameResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/usergames")
public class UserGameController {


    //CONSTRUCTOR INJECTION: SPRING SUPPLIES REPOSITORIES WHEN IT CREATES CONTROLLER
    private final UserGameRepository userGameRepository;
    private final UserRepository userRepository;

    public UserGameController(UserGameRepository userGameRepository, UserRepository userRepository){
        this.userGameRepository = userGameRepository;
        this.userRepository = userRepository;
    }


    //checks if user is logged in, if yes, return that users id, id never comes from client, so no impersonation
    private Long requireLoggedInUserId(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            throw new RuntimeException("Not logged in");
        }
        return userId;
    }


    //Creates a new game entry for logged in user.
    //AddGameRequest + @Valid restricts and validates incoming fields, owner is set from the session
    //Returns a UserGameResponse so frontend gets new id without entire User entity
    @PostMapping
    public UserGameResponse addGameToCollection(@RequestBody @Valid AddGameRequest request, HttpSession session){ //@Valid ensures constrains set up in AddGameRequest are followed

        Long userId = requireLoggedInUserId(session); //check if user logged in, and get userId

        User user = userRepository.findById(userId).orElseThrow();

        UserGame userGame = new UserGame();
        userGame.setUser(user);
        userGame.setTitle(request.getTitle());
        userGame.setCoverURL(request.getCoverURL());
        userGame.setStatus(request.getStatus());
        userGame.setRating(request.getRating());
        userGame.setReview(request.getReview());

        UserGame saved = userGameRepository.save(userGame);
        return new UserGameResponse(saved.getId(), saved.getTitle(), saved.getCoverURL(), saved.getStatus(), saved.getRating(), saved.getReview());
        //look up user by ID sent in request

        }


    //Returns logged in users collection, sorted or filtered by chosen view (enum)
    //CollectionView enum contains allowed options, so clients cant sort by random fields
    //Each entity is converted to a UserGameResponse so no user data is exposed
    @GetMapping("/user/collection")
    public List<UserGameResponse> getUserCollection(@RequestParam(defaultValue = "TITLE_ASC") CollectionView view, HttpSession session){

        Long userId = requireLoggedInUserId(session);

        List<UserGame> games = switch (view){
            case TITLE_ASC -> userGameRepository.findByUserId(userId, Sort.by("title").ascending());
            case TITLE_DESC -> userGameRepository.findByUserId(userId, Sort.by("title").descending());
            case RATING_ASC -> userGameRepository.findByUserId(userId, Sort.by("rating").ascending());
            case RATING_DESC -> userGameRepository.findByUserId(userId, Sort.by("rating").descending());
            case PLAYED -> userGameRepository.findByUserIdAndStatus(userId, Status.PLAYED);
            case WANT_TO_PLAY -> userGameRepository.findByUserIdAndStatus(userId, Status.WANT_TO_PLAY);
        };

        List<UserGameResponse> responses = new ArrayList<>();
        for(UserGame game : games){
            responses.add(new UserGameResponse(game.getId(), game.getTitle(), game.getCoverURL(), game.getStatus(), game.getRating(), game.getReview()));
        }
        return responses;

    }


    //Deletes a game entry, only if it belongs to the logged in user
    //The ownership check compares the entrys owner with the session user before deleting
    @DeleteMapping("/{id}")
    public void deleteFromCollection(@PathVariable Long id, HttpSession session){

        Long userId = requireLoggedInUserId(session);

        UserGame userGame = userGameRepository.findById(id).orElseThrow();

        if(!userGame.getUser().getId().equals(userId)){
            throw new RuntimeException("Not your entry to delete");
        }

        userGameRepository.deleteById(id);
    }


    //updates game entry after same ownership check as delete
    //UpdateGameRequest limits which fiels can change (owner cand be reassigned)
    //saved result is returned as UserGameResponse
    @PutMapping("/{id}") //get id from url path, use this as the userGame entry u are updating
    public UserGameResponse updateUserGame(@PathVariable Long id, @RequestBody @Valid UpdateGameRequest request, HttpSession session){

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

        UserGame saved = userGameRepository.save(userGame);
        return new UserGameResponse(saved.getId(), saved.getTitle(), saved.getCoverURL(), saved.getStatus(), saved.getRating(), saved.getReview());
    }
}
