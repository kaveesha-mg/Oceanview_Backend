package com.oceanview.repository;

import com.oceanview.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    /** Case-insensitive match; pass pattern like "^username$" from Pattern.quote(name). */
    @Query("{ 'username' : { $regex: ?0, $options: 'i' } }")
    Optional<User> findByUsernameRegex(String regexPattern);

    boolean existsByUsername(String username);
}
