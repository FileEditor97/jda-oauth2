package dev.fileeditor.oauth2.entities;

import dev.fileeditor.oauth2.OAuth2Client;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.EnumSet;

/**
 * OAuth2 representation of a Discord Server/Guild.
 *
 * <p>Note that this is effectively a wrapper for both the Guild info, and
 * the info on the user in the guild represented by the session that got this Guild.
 */
public interface OAuth2Guild extends ISnowflake {
	/**
	 * Gets the underlying {@link OAuth2Client} that created this OAuth2Guild.
	 *
	 * @return The OAuth2Client that created this OAuth2Guild.
	 */
	OAuth2Client getClient();

	/**
	 * Gets the Guild's name.
	 *
	 * @return The Guild's name.
	 */
	String getName();

	/**
	 * Gets the Guild's icon ID, or {@code null} if the Guild does not have an icon.
	 *
	 * @return The Guild's icon ID.
	 */
	String getIconId();

	/**
	 * Gets the Guild's icon URL, or {@code null} if the Guild does not have an icon.
	 *
	 * @return The Guild's icon URL.
	 */
	String getIconUrl();

	/**
	 * Gets the Guild's banner ID, or {@code null} if the Guild does not have a banner.
	 *
	 * @return The Guild's banner ID.
	 */
	String getBannerId();

	/**
	 * Gets the Guild's banner URL, or {@code null} if the Guild does not have a banner.
	 *
	 * @return The Guild's banner URL.
	 */
	String getBannerUrl();

	/**
	 * Gets the Session User's raw permission value for the Guild.
	 *
	 * @return The Session User's raw permission value for the Guild.
	 */
	long getPermissionsRaw();

	/**
	 * Gets the Session User's {@link net.dv8tion.jda.api.Permission Permissions} for the Guild.
	 *
	 * @return The Session User's Permissions for the Guild.
	 */
	EnumSet<Permission> getPermissions();

	/**
	 * Whether the Session User is the owner of the Guild.
	 *
	 * @return {@code true} if the Session User is the owner of
	 *         the Guild, {@code false} otherwise.
	 */
	boolean isOwner();

	/**
	 * Whether the Session User has all specified {@link net.dv8tion.jda.api.Permission Permissions} in the Guild.
	 *
	 * @param  perms
	 *         The Permissions to check for.
	 *
	 * @return {@code true} if and only if the Session User has all
	 *         specified Permissions, {@code false} otherwise.
	 */
	boolean hasPermission(Permission... perms);

	/**
	 * @return the approximate count of online members in the guild, or -1 if flag not set
	 */
	int getOnlineCount();

	/**
	 * @return the approximate count of total members in the guild, or -1 if flag not set
	 */
	int getMemberCount();

}
