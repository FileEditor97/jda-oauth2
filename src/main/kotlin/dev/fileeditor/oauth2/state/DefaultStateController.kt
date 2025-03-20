package dev.fileeditor.oauth2.state

/**
 * The default [StateController] implementation.
 */
class DefaultStateController : StateController {
    private val states = HashMap<String, String>()

    override fun generateNewState(redirectUri: String): String {
        val state = randomState()
        states[state] = redirectUri
        return state
    }

    override fun consumeState(state: String): String? {
        return states.remove(state)
    }

    companion object {
        private const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        private fun randomState(): String {
            val builder = StringBuilder()
            for (i in 0..9) {
                builder.append(CHARACTERS[(Math.random() * CHARACTERS.length).toInt()])
            }
            return builder.toString()
        }
    }
}
