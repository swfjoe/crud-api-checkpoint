package com.example.crudapicheckpoint.Controller;

import com.example.crudapicheckpoint.Model.User;
import com.example.crudapicheckpoint.Repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {
    UserRepository newUserRepository;

    public UserController(UserRepository newUserRepository) {
        this.newUserRepository = newUserRepository;
    }

    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return this.newUserRepository.findAll();
    }

    @PostMapping("/users")
    public User addUserToDatabase(@RequestBody User newUser) {
        return this.newUserRepository.save(newUser);
    }

    @GetMapping("/users/{userId}")
    public Optional<User> getUserById(@PathVariable int userId) {
        return this.newUserRepository.findById(userId);
    }

    @PatchMapping("/users/{userId}")
    public User modifyUserInDatabase(@PathVariable int userId, @RequestBody Map<String, Object> patchMap){
        User oldUser = this.newUserRepository.findById(userId).get();
        patchMap.forEach((key, value) -> {
            switch (key) {
                case "email" -> oldUser.setEmail((String) value);
                case "password" -> oldUser.setPassword((String) value);
                case "authenticated" -> oldUser.setAuthenticated((Boolean) value);
            }
        });
        return this.newUserRepository.save(oldUser);
    }

    @DeleteMapping("/users/{userId}")
    public Map<String, Integer> deleteUserFromDatabase(@PathVariable int userId) {
        this.newUserRepository.deleteById(userId);
        int counter = 0;
        Map<String, Integer> recordCount = new HashMap();
        for (User user : getAllUsers()) {
            counter++;
        }
        recordCount.put("count", counter);
        return recordCount;
    }

    @PostMapping("/users/authenticate")
    public Map<String,Object> returnUserIsAuthenticated(@RequestBody User userToAuthenticate) {
        Map<String, Object> outputMap = new HashMap<>();

        User userToCheck = this.newUserRepository.findFirstUserByEmail(userToAuthenticate.getEmail()).get();
        String passToCheck = userToCheck.getPassword();
        String passToConfirm = userToAuthenticate.getPassword();

        if (passToCheck.equals(passToConfirm)) {
            userToCheck.setAuthenticated(true);
            this.newUserRepository.save(userToCheck);
            outputMap.put("authenticated", true);
            outputMap.put("user", userToCheck);
            return outputMap;
        } else {
            outputMap.put("authenticated", false);
        }
            return outputMap;

    }
}
