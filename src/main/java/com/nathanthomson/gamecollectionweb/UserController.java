package com.nathanthomson.gamecollectionweb;

import com.nathanthomson.gamecollectionweb.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public UserResponse register(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword())); //uses bcrypt for password
        User savedUser = userRepository.save(user); //saves details to the user
        return new UserResponse(savedUser.getId(), savedUser.getUsername()); //user response used to prevent returning password back
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody User loginRequest, HttpServletRequest request){
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new RuntimeException("Invalid Credentials"));
        //if no user has this username, throw exception

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Credentials"); //if hashed password input and stored dont match return invalid
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());

        return new UserResponse(user.getId(), user.getUsername());
    }
}
