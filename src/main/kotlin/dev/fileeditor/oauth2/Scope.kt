package dev.fileeditor.oauth2

enum class Scope(@JvmField val text: String) {
    /**
     * For oauth2 bots, this puts the bot in the user's selected guild by default
     */
    BOT("bot"),

    /**
     * Allows /users/@me/connections to return linked third-party accounts
     */
    CONNECTIONS("connections"),

    /**
     * Allows /users/@me without email
     */
    IDENTIFY("identify"),

    /**
     * Enables /users/@me to return an email
     */
    EMAIL("email"),

    /**
     * Allows your app to join users to a group dm
     */
    GDM_JOIN("gdm.join"),

    /**
     * Allows /users/@me/guilds to return basic information about all of a user's guilds
     */
    GUILDS("guilds"),

    /**
     * Allows /guilds/{guild.id}/members/{user.id} to be used for joining users to a guild
     */
    GUILDS_JOIN("guilds.join"),

    /**
     * Allows /users/@me/guilds/{guild.id}/member to return a user's member information in a guild
     */
    GUILDS_MEMBERS("guilds.members.read"),

    /**
     * For local rpc server api access, this allows you to read messages
     * from all client channels (otherwise restricted to channels/guilds your app creates)
     */
    MESSAGES_READ("messages.read"),

    /**
     * Allows your app to update a user's connection and metadata for the app
     */
    ROLE_CONNECTIONS("role_connections.write"),

    /**
     * This generates a webhook that is returned in the oauth token response for authorization code grants
     */
    WEBHOOK_INCOMING("webhook.incoming"),

    /**
     * Allows your app to update permissions for its commands in a guild a user has permissions to
     */
    COMMAND_PERMISSIONS("applications.commands.permissions.update"),

    /**
     * Unknown scope
     */
    UNKNOWN("");

    companion object {
		fun join(vararg scopes: Scope): String {
            return join(false, *scopes)
        }

		fun join(bySpace: Boolean, vararg scopes: Scope): String {
            if (scopes.isEmpty()) {
                return ""
            }
            val builder = StringBuilder(scopes[0].text)
            for (i in 1..<scopes.size) {
                if (bySpace) {
                    builder.append(" ")
                } else {
                    builder.append("%20") // +
                }
                builder.append(scopes[i].text)
            }
            return builder.toString()
        }

		fun from(scope: String): Scope {
            for (s in entries) {
                if (s.text.equals(scope, ignoreCase = true)) {
                    return s
                }
            }
            return UNKNOWN
        }
    }
}
