package com.swad.taptable.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swad.taptable.resources.UserRole;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Singleton utility class for generating and verifying JWT tokens. Uses HMAC256 signing with a
 * secret key read from the {@code jwt.secret} context parameter. Tokens contain a {@code user_id}
 * claim and expire in 8 hours.
 *
 * @author SWAD Team
 */
public final class JWTUtil {

  /** The name of the cookie used to store the JWT token. */
  public static final String COOKIE_NAME = "auth_token";

  /** Token expiry in seconds (8 hours). */
  public static final int EXPIRY_SECONDS = 8 * 60 * 60;

  /**
   * Signing algorithm initialized from the application secret and reused for token generation.
   */
  private static Algorithm algorithm;

  /**
   * Verifier configured with the current signing algorithm and reused for token validation.
   */
  private static JWTVerifier verifier;

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private JWTUtil() {}

  /**
   * Initializes the JWTUtil with the given secret key. Called once by
   * {@link AppContextListener#contextInitialized} at application startup.
   *
   * @param secret the secret key for signing and verifying JWTs.
   */
  public static void init(final String secret) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("jwt.secret context parameter is missing or blank");
    }
    algorithm = Algorithm.HMAC256(secret);
    verifier = JWT.require(algorithm).build();
  }

  /**
   * Creates a signed JWT containing the given user id and role. Expires in 8 hours.
   *
   * @param userId the authenticated user's id.
   * @param role the authenticated user's role.
   * @return a signed JWT string.
   */
  public static String generateToken(final int userId, final UserRole role) {
    if (algorithm == null) {
      throw new IllegalStateException("JWTUtil.init() has not been called");
    }
    return JWT.create().withClaim("user_id", userId).withClaim("user_role", role.name())
        .withExpiresAt(Instant.now().plus(EXPIRY_SECONDS, ChronoUnit.SECONDS)).sign(algorithm);
  }

  /**
   * Verifies the token signature and expiry.
   *
   * @param token the raw JWT string.
   * @return the decoded token on success.
   * @throws JWTVerificationException if the signature is wrong or the token is expired.
   */
  public static DecodedJWT verify(final String token) throws JWTVerificationException {
    if (verifier == null) {
      throw new IllegalStateException("JWTUtil.init() has not been called");
    }
    return verifier.verify(token);
  }
}
