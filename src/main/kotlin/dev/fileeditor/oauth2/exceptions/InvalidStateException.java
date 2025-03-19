package dev.fileeditor.oauth2.exceptions;

/**
 * Exception is thrown when provided OAuth2 stats is not valid.
 */
public class InvalidStateException extends Exception {
  public InvalidStateException(String message) {
    super(message);
  }
}
