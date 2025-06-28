package com.sys.kanri.services;

import com.sys.kanri.entities.MUser;

/**
 * Interface for JWT utility service.
 */
public interface IJwtService {
    /**
     * Generates a JWT token for the specified user.
     *
     * @param user the authenticated user
     * @return JWT token string
     */
    String generateToken(MUser user);

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the extracted username
     */
    String extractUsername(String token);

    /**
     * Checks whether the token is valid for the given user.
     *
     * @param token the JWT token
     * @param user the user to validate
     * @return true if valid, false otherwise
     */
    boolean isTokenValid(String token, MUser user);
}
