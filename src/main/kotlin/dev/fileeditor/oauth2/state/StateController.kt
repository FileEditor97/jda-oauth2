package dev.fileeditor.oauth2.state

/**
 * Implementable state controller, used for registering states
 * and generating redirect URIs using them.
 *
 *
 * Naturally, states should be unique, and attempting to
 * generate a redirect using a previously used state should
 * return `null` instead of a new redirect URI.
 */
interface StateController {
    /**
     * Generates a new state string using the provided redirect URI.
     *
     * @param  redirectUri
     * The redirect URI that will be used with this state.
     *
     * @return The state string.
     */
    fun generateNewState(redirectUri: String): String

    /**
     * Consumes a state to get the corresponding redirect URI.
     *
     *
     * Once this method is called for a specific state, it
     * should return null for all future calls of that same state.
     *
     * @param  state
     * The state.
     *
     * @return The redirect URI, or `null` if the state does not exist.
     */
    fun consumeState(state: String): String?
}
