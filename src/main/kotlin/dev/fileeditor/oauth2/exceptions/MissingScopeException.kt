package dev.fileeditor.oauth2.exceptions;

import dev.fileeditor.oauth2.Scope;

/**
 * Exception is thrown when an action requires a specific OAuth2 {@link Scope}
 * that is not provided or missing.
 */
public class MissingScopeException extends RuntimeException {
	private static final String FORMAT = "Cannot %s without '%s' scope!";

	public MissingScopeException(String action, Scope missing) {
		super(String.format(FORMAT, action, missing.getText()));
	}
}
