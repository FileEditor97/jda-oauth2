package dev.fileeditor.oauth2.exceptions

import dev.fileeditor.oauth2.Scope

/**
 * Exception is thrown when an action requires a specific OAuth2 [Scope]
 * that is not provided or missing.
 */
class MissingScopeException(action: String, missing: Scope) :
    RuntimeException("Cannot $action without '$missing' scope!")
