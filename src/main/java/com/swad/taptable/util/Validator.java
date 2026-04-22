package com.swad.taptable.util;

import java.util.regex.Pattern;

/**
 * Utility class for common input validation.
 *
 * @author SWAD Team
 */
public final class Validator {

  /**
   * Regex pattern for validating email addresses. This pattern checks for a standard email format,
   * ensuring that the email contains a local part, an '@' symbol, and a domain part with a valid
   * TLD.
   */
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  /**
   * Matches passwords that are 8-16 characters long and contain at least one lowercase letter, one
   * uppercase letter, one digit, and one special character. This enforces a strong password policy
   * to enhance security.
   */
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,16}$");

  /**
   * Private constructor to prevent instantiation of this utility class. This class is not meant to
   * be instantiated, as it only contains static methods for validation. Attempting to instantiate
   * it will throw an AssertionError, signaling that this is a misuse of the class.
   */
  private Validator() {
    throw new AssertionError("Validator is a utility class and should not be instantiated.");
  }

  /**
   * Returns {@code true} if the given email address matches the expected format.
   *
   * @param email the email address to validate.
   * @return {@code true} if valid, {@code false} otherwise.
   */
  public static boolean isValidEmail(final String email) {
    return email != null && EMAIL_PATTERN.matcher(email).matches();
  }

  /**
   * Returns {@code true} if the given password satisfies the password policy: 8-16 characters, at
   * least one lowercase, one uppercase, one digit, and one special character.
   *
   * @param password the plain-text password to validate.
   * @return {@code true} if valid, {@code false} otherwise.
   */
  public static boolean isValidPassword(final String password) {
    return password != null && PASSWORD_PATTERN.matcher(password).matches();
  }
}
