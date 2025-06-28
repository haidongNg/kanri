package com.sys.kanri.services.impl;

import com.sys.kanri.entities.MUser;
import com.sys.kanri.repositories.MUserRepository;
import com.sys.kanri.services.IMUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link IMUserService} providing user management
 * and integration with Spring Security authentication mechanisms.
 */
@Service
@RequiredArgsConstructor
public class MUserServiceImpl implements IMUserService {

    private final MUserRepository mUserRepository;

    /**
     * Saves a user to the database.
     *
     * @param user the user entity to be saved
     * @return the saved user entity
     */
    @Override
    public MUser save(MUser user) {
        return mUserRepository.save(user);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the user ID
     * @return an Optional containing the user if found
     */
    @Override
    public Optional<MUser> findById(Long id) {
        return mUserRepository.findById(id);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the user's email
     * @return an Optional containing the user if found
     */
    @Override
    public Optional<MUser> findByEmail(String email) {
        return mUserRepository.findByEmail(email);
    }

    /**
     * Checks if a username already exists in the system.
     *
     * @param username the username to check
     * @return true if it exists, false otherwise
     */
    @Override
    public boolean existsByUsername(String username) {
        return mUserRepository.existsByUsername(username);
    }

    /**
     * Checks if an email address already exists in the system.
     *
     * @param email the email to check
     * @return true if it exists, false otherwise
     */
    @Override
    public boolean existsByEmail(String email) {
        return mUserRepository.existsByEmail(email);
    }

    /**
     * Loads a user by their username for Spring Security authentication.
     *
     * @param username the username to load
     * @return the user entity implementing UserDetails
     * @throws UsernameNotFoundException if no user is found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return mUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
