package com.example.crudapicheckpoint.Repository;

import com.example.crudapicheckpoint.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findFirstUserByEmail (String email);

}
