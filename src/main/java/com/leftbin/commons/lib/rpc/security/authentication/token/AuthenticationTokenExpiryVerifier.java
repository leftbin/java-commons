package com.leftbin.commons.lib.rpc.security.authentication.token;

import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for verifying the expiration of a given authentication token.
 * It uses JWT (JSON Web Tokens) for decoding and checking the expiration time of the token.
 */
@Component
public class AuthenticationTokenExpiryVerifier {
    /**
     * Verifies if the provided access token is expiring within the specified number of minutes.
     *
     * <p> This method decodes the JWT access token to extract the expiration date. It calculates
     * the difference between the current time and the expiration time to determine if the token
     * is set to expire within the provided timeframe. The time difference is calculated in days,
     * hours, and minutes, but only the minute value is compared against the provided value.</p>
     *
     * @param accessToken The JWT access token to be verified.
     * @param minutes     The time in minutes to check if the token is expiring within.
     * @return True if the token is expiring within the given number of minutes, otherwise False.
     */
    public boolean verify(String accessToken, long minutes) {
        // Decode the JWT access token to obtain a DecodedJWT object.
        var decodedJWT = JWT.decode(accessToken);

        // Calculate the time difference between the expiration time of the token and the current time.
        // This difference is in milliseconds.
        var timeDiff = decodedJWT.getExpiresAt().getTime() - new java.util.Date().getTime();

        // Convert the time difference to minutes.
        // The remainder operation ensures we are only considering the minutes part of the time difference.
        long diffInMinutes = (timeDiff / (1000 * 60)) % 60;

        // Convert the time difference to hours, similarly ignoring any full days.
        long diffInHours = (timeDiff / (1000 * 60 * 60)) % 24;

        // Convert the time difference to days. This will ignore any full years.
        long diffInDays = (timeDiff / (1000 * 60 * 60 * 24)) % 365;

        // Check if the token is expiring within the given number of minutes.
        // This is true if the token expires today (no full days until expiration),
        // within the current hour (no full hours until expiration), and within the given number of minutes.
        return diffInDays == 0 && diffInHours == 0 && diffInMinutes < minutes;
    }
}
