package com.sys.kanri.services;

import com.sys.kanri.entities.MUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * Interface that defines core user management functionalities.
 * Extends {@link UserDetailsService} for Spring Security integration.
 */
public interface IMUserService extends UserDetailsService {

    /**
     * Saves a user to the database.
     *
     * @param user the user entity to be saved
     * @return the saved user entity
     */
    MUser save(MUser user);

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return an Optional containing the user if found
     */
    Optional<MUser> findById(Long id);

    /**
     * Finds a user by email.
     *
     * @param email the user's email address
     * @return an Optional containing the user if found
     */
    Optional<MUser> findByEmail(String email);
    /**
     * Checks whether a username already exists.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);
    /**
     * Checks whether an email address already exists.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
