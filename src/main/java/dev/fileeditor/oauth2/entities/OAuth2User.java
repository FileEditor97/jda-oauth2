package dev.fileeditor.oauth2.entities;

import dev.fileeditor.oauth2.OAuth2Client;
import dev.fileeditor.oauth2.Session.Session;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * OAuth2 representation of a Discord User.
 * <br>More specifically, this is the User that the session is currently managing when retrieved using
 * {@link dev.fileeditor.oauth2.OAuth2Client#getUser(Session) OAuth2Client#getUser}.
 */
public interface OAuth2User {
	String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s.%s";

	String DEFAULT_AVATAR_URL = "https://cdn.discordapp.com/embed/avatars/%s.png";

	String BANNER_URL = "https://cdn.discordapp.com/banners/%s/%s.%s";

	int DEFAULT_ACCENT_COLOR_RAW = 0x1FFFFFFF;

	/**
	 * Gets the underlying {@link OAuth2Client}
	 * that created this OAuth2User.
	 *
	 * @return The OAuth2Client that created this OAuth2User.
	 */
	OAuth2Client getClient();

	/**
	 * Gets the originating {@link Session}
	 * that is responsible for this OAuth2User.
	 *
	 * @return The Session responsible for this OAuth2User.
	 */
	Session getSession();

	/**
	 * Gets the user's Snowflake ID as a String.
	 *
	 * @return The user's Snowflake ID as a String.
	 */
	String getId();

	/**
	 * Gets the user's Snowflake ID as a {@code long}.
	 *
	 * @return The user's Snowflake ID as a {@code long}.
	 */
	long getIdLong();

	/**
	 * The username of the User. Length is between 2 and 32 characters (inclusive).
	 *
	 * @return Never-null String containing the User's username.
	 */
	@NotNull
	String getName();

	/**
	 * The global display name of the user.
	 * <br>This name is not unique and allows more characters.
	 *
	 * <p>This name is usually displayed in the UI.
	 *
	 * @return The global display name or null if unset.
	 */
	@Nullable
	String getGlobalName();

	/**
	 * The name visible in the UI.
	 * <br>If the {@link #getGlobalName() global name} is {@code null}, this returns the {@link #getName() username} instead.
	 *
	 * @return The effective display name
	 */
	@NotNull
	default String getEffectiveName() {
		String globalName = getGlobalName();
		return globalName != null ? globalName : getName();
	}

	/**
	 * Gets the user's email address that is associated with their Discord account.
	 *
	 * <p>Note that if this user is acquired without the '{@link dev.fileeditor.oauth2.Scope#EMAIL email}'
	 * OAuth {@link dev.fileeditor.oauth2.Scope}, this will throw a
	 * {@link dev.fileeditor.oauth2.exceptions.MissingScopeException}.
	 *
	 * @return The user's email.
	 *
	 * @throws dev.fileeditor.oauth2.exceptions.MissingScopeException
	 *         If the corresponding {@link OAuth2User#getSession() session} does not have the
	 *         proper 'email' OAuth2 scope
	 */
	String getEmail();

	/**
	 * Returns {@code true} if the user's Discord account has been verified via email.
	 *
	 * <p>This is required to send messages in guilds where certain moderation levels are used.
	 *
	 * @return {@code true} if the user has verified their account, {@code false} otherwise.
	 */
	boolean isVerified();

	/**
	 * Returns {@code true} if this user has multi-factor authentication enabled.
	 *
	 * <p>Some guilds require mfa for administrative actions.
	 *
	 * @return {@code true} if the user has mfa enabled, {@code false} otherwise.
	 */
	boolean isMfaEnabled();

	/**
	 * Gets the user's avatar ID, or {@code null} if they have not set one.
	 *
	 * @return The user's avatar ID, or {@code null} if they have not set one.
	 */
	String getAvatarId();

	/**
	 * The URL for the user's avatar image.
	 * If the user has not set an image, this will return null.
	 *
	 * @return Possibly-null String containing the User avatar url.
	 */
	@Nullable
	default String getAvatarUrl() {
		String avatarId = getAvatarId();
		return avatarId == null ? null : String.format(AVATAR_URL, getId(), avatarId, avatarId.startsWith("a_") ? "gif" : "png");
	}

	/**
	 * Returns an {@link ImageProxy} for this user's avatar.
	 *
	 * @return Possibly-null {@link ImageProxy} of this user's avatar
	 *
	 * @see    #getAvatarUrl()
	 */
	@Nullable
	default ImageProxy getAvatar() {
		final String avatarUrl = getAvatarUrl();
		return avatarUrl == null ? null : new ImageProxy(avatarUrl);
	}

	/**
	 * The URL for the user's avatar image.
	 * If they do not have an avatar set, this will return the URL of their
	 * default avatar
	 *
	 * @return  Never-null String containing the User effective avatar url.
	 */
	@NotNull
	default String getEffectiveAvatarUrl() {
		String avatarUrl = getAvatarUrl();
		return avatarUrl == null ? getDefaultAvatarUrl() : avatarUrl;
	}

	/**
	 * The Discord ID for this user's default avatar image.
	 *
	 * @return Never-null String containing the user's default avatar id.
	 */
	@NotNull
	String getDefaultAvatarId();

	/**
	 * The URL for the user's default avatar image.
	 *
	 * @return Never-null String containing the user's default avatar url.
	 */
	@NotNull
	default String getDefaultAvatarUrl() {
		return String.format(DEFAULT_AVATAR_URL, getDefaultAvatarId());
	}

	/**
	 * The Discord id for this user's banner image.
	 * If the user has not set a banner, this will return null.
	 *
	 * @return Possibly-null String containing the {@link User User} banner id.
	 */
	@Nullable
	String getBannerId();

	/**
	 * The URL for the user's banner image.
	 * If the user has not set a banner, this will return null.
	 *
	 * @return Possibly-null String containing the {@link User User} banner url.
	 *
	 * @see User#BANNER_URL
	 */
	@Nullable
	String getBannerUrl();

	/**
	 * The user's accent color.
	 * If the user has not set an accent color, this will return null.
	 * The automatically calculated color is not returned.
	 * The accent color is not shown in the client if the user has set a banner.
	 *
	 * @return Possibly-null {@link java.awt.Color} containing the {@link User User} accent color.
	 */
	@Nullable
	Color getAccentColor();

	/**
	 * The raw RGB value of this user's accent color.
	 * <br>Defaults to {@link #DEFAULT_ACCENT_COLOR_RAW} if this user's banner color is not available.
	 *
	 * @return The raw RGB color value or {@link User#DEFAULT_ACCENT_COLOR_RAW}
	 */
	int getAccentColorRaw();

	/**
	 * @return The user's chosen language option
	 */
	String getLocale();

	/**
	 * Gets the user as a discord formatted mention:
	 * <br>{@code <@SNOWFLAKE_ID> }
	 *
	 * @return A discord formatted mention of this user.
	 */
	String getAsMention();

	/**
	 * Gets the corresponding {@link net.dv8tion.jda.api.entities.User JDA User}
	 * from the provided instance of {@link net.dv8tion.jda.api.JDA JDA}.
	 *
	 * <p>Note that there is no guarantee that this will not return {@code null}
	 * as the instance of JDA may not have access to the User.
	 *
	 * <p>For sharded bots, use {@link OAuth2User#getJDAUser(ShardManager)}.
	 *
	 * @param  jda
	 *         The instance of JDA to get from.
	 *
	 * @return A JDA User, possibly {@code null}.
	 */
	User getJDAUser(JDA jda);

	/**
	 * Gets the corresponding {@link net.dv8tion.jda.api.entities.User JDA User}
	 * from the provided {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager}.
	 *
	 * <p>Note that there is no guarantee that this will not return {@code null}
	 * as the ShardManager may not have access to the User.
	 *
	 * <p>For un-sharded bots, use {@link OAuth2User#getJDAUser(JDA)}.
	 *
	 * @param  shardManager
	 *         The ShardManager to get from.
	 *
	 * @return A JDA User, possibly {@code null}.
	 */
	User getJDAUser(ShardManager shardManager);
}
