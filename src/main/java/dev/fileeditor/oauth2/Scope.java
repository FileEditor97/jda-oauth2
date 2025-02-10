package dev.fileeditor.oauth2;

public enum Scope {
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

	private final String text;

	Scope(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public static boolean contains(Scope[] scopes, Scope target) {
		if (scopes == null || scopes.length == 0 || target == null || target == UNKNOWN) {
			return false;
		}
		for (Scope scope : scopes) {
			if (scope == target) {
				return true;
			}
		}
		return false;
	}

	public static String join(Scope... scopes) {
		return join(false, scopes);
	}

	public static String join(boolean bySpace, Scope... scopes) {
		if (scopes.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder(scopes[0].getText());
		for (int i = 1; i < scopes.length; i++) {
			if (bySpace) {
				builder.append(" ");
			} else {
				builder.append("%20"); // +
			}
			builder.append(scopes[i].getText());
		}
		return builder.toString();
	}

	public static Scope from(String scope) {
		for (Scope s : values()) {
			if (s.text.equalsIgnoreCase(scope)) {
				return s;
			}
		}
		return UNKNOWN;
	}

}
