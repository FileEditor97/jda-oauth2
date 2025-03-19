package dev.fileeditor.oauth2.entities

import dev.fileeditor.oauth2.OAuth2Client
import dev.fileeditor.oauth2.session.Session
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ImageProxy
import java.awt.Color

/**
 * OAuth2 representation of a Discord User.
 * <br></br>More specifically, this is the User that the session is currently managing when retrieved using
 * [OAuth2Client#getUser][dev.fileeditor.oauth2.OAuth2Client.getUser].
 */
interface OAuth2User {
    /**
     * Gets the underlying [OAuth2Client]
     * that created this OAuth2User.
     *
     * @return The OAuth2Client that created this OAuth2User.
     */
    val client: OAuth2Client

    /**
     * Gets the originating [Session]
     * that is responsible for this OAuth2User.
     *
     * @return The Session responsible for this OAuth2User.
     */
    val session: Session

    val id: String
        /**
         * Gets the user's Snowflake ID as a String.
         *
         * @return The user's Snowflake ID as a String.
         */
        get() = idLong.toString()

    /**
     * Gets the user's Snowflake ID as a `long`.
     *
     * @return The user's Snowflake ID as a `long`.
     */
    val idLong: Long

    /**
     * The username of the User. Length is between 2 and 32 characters (inclusive).
     *
     * @return Never-null String containing the User's username.
     */
    val name: String

    /**
     * The global display name of the user.
     * <br></br>This name is not unique and allows more characters.
     *
     *
     * This name is usually displayed in the UI.
     *
     * @return The global display name or null if unset.
     */
    val globalName: String?

    val effectiveName: String
        /**
         * The name visible in the UI.
         * <br></br>If the [global name][.getGlobalName] is `null`, this returns the [username][.getName] instead.
         *
         * @return The effective display name
         */
        get() = globalName ?: name

    /**
     * Gets the user's email address that is associated with their Discord account.
     *
     *
     * Note that if this user is acquired without the '[email][dev.fileeditor.oauth2.Scope.EMAIL]'
     * OAuth [dev.fileeditor.oauth2.Scope], this will throw a
     * [dev.fileeditor.oauth2.exceptions.MissingScopeException].
     *
     * @return The user's email.
     *
     * @throws dev.fileeditor.oauth2.exceptions.MissingScopeException
     * If the corresponding [session][OAuth2User.getSession] does not have the
     * proper 'email' OAuth2 scope
     */
    val email: String?

    /**
     * Returns `true` if the user's Discord account has been verified via email.
     *
     *
     * This is required to send messages in guilds where certain moderation levels are used.
     *
     * @return `true` if the user has verified their account, `false` otherwise.
     */
    val isVerified: Boolean

    /**
     * Returns `true` if this user has multi-factor authentication enabled.
     *
     *
     * Some guilds require mfa for administrative actions.
     *
     * @return `true` if the user has mfa enabled, `false` otherwise.
     */
    val isMfaEnabled: Boolean

    /**
     * Gets the user's avatar ID, or `null` if they have not set one.
     *
     * @return The user's avatar ID, or `null` if they have not set one.
     */
    val avatarId: String?

    val avatarUrl: String?
        /**
         * The URL for the user's avatar image.
         * If the user has not set an image, this will return null.
         *
         * @return Possibly-null String containing the User avatar url.
         */
        get() = if (avatarId == null) null else String.format(
            AVATAR_URL,
            id,
            avatarId,
            if (avatarId!!.startsWith("a_")) "gif" else "png"
        )

    val avatar: ImageProxy?
        /**
         * Returns an [ImageProxy] for this user's avatar.
         *
         * @return Possibly-null [ImageProxy] of this user's avatar
         *
         * @see .getAvatarUrl
         */
        get() = if (avatarUrl == null) null else ImageProxy(avatarUrl!!)

    val effectiveAvatarUrl: String
        /**
         * The URL for the user's avatar image.
         * If they do not have an avatar set, this will return the URL of their
         * default avatar
         *
         * @return  Never-null String containing the User effective avatar url.
         */
        get() = avatarUrl ?: defaultAvatarUrl

    val defaultAvatarId: String
        /**
         * The Discord ID for this user's default avatar image.
         *
         * @return Never-null String containing the user's default avatar id.
         */
        get() = ((idLong shr 22) % 6).toString()

    val defaultAvatarUrl: String
        /**
         * The URL for the user's default avatar image.
         *
         * @return Never-null String containing the user's default avatar url.
         */
        get() = String.format(DEFAULT_AVATAR_URL, defaultAvatarId)

    /**
     * The Discord id for this user's banner image.
     * If the user has not set a banner, this will return null.
     *
     * @return Possibly-null String containing the [User] banner id.
     */
    val bannerId: String?

    val bannerUrl: String?
        /**
         * The URL for the user's avatar image.
         * If the user has not set an image, this will return null.
         *
         * @return Possibly-null String containing the User avatar url.
         */
        get() = if (bannerId == null) null else String.format(
            BANNER_URL,
            id,
            bannerId,
            if (bannerId!!.startsWith("a_")) "gif" else "png"
        )

    val accentColor: Color
        /**
         * The user's accent color.
         * If the user has not set an accent color, this will return null.
         * The automatically calculated color is not returned.
         * The accent color is not shown in the client if the user has set a banner.
         *
         * @return Possibly-null [java.awt.Color] containing the [User] accent color.
         */
        get() = Color(accentColorRaw)

    /**
     * The raw RGB value of this user's accent color.
     * <br></br>Defaults to [.DEFAULT_ACCENT_COLOR_RAW] if this user's banner color is not available.
     *
     * @return The raw RGB color value or [User.DEFAULT_ACCENT_COLOR_RAW]
     */
    val accentColorRaw: Int

    /**
     * @return The user's chosen language option
     */
    val locale: String

    /**
     * Gets the user as a discord formatted mention:
     * <br></br>`<@SNOWFLAKE_ID> `
     *
     * @return A discord formatted mention of this user.
     */
    fun asMention(): String {
        return "<@$idLong>"
    }

    /**
     * Gets the corresponding [JDA User][net.dv8tion.jda.api.entities.User]
     * from the provided instance of [JDA][net.dv8tion.jda.api.JDA].
     *
     *
     * Note that there is no guarantee that this will not return `null`
     * as the instance of JDA may not have access to the User.
     *
     *
     * For sharded bots, use [OAuth2User.getJDAUser].
     *
     * @param  jda
     * The instance of JDA to get from.
     *
     * @return A JDA User, possibly `null`.
     */
    fun getJDAUser(jda: JDA): User?

    /**
     * Gets the corresponding [JDA User][net.dv8tion.jda.api.entities.User]
     * from the provided [ShardManager][net.dv8tion.jda.api.sharding.ShardManager].
     *
     *
     * Note that there is no guarantee that this will not return `null`
     * as the ShardManager may not have access to the User.
     *
     *
     * For un-sharded bots, use [OAuth2User.getJDAUser].
     *
     * @param  shardManager
     * The ShardManager to get from.
     *
     * @return A JDA User, possibly `null`.
     */
    fun getJDAUser(shardManager: ShardManager): User?

    companion object {
        const val AVATAR_URL: String = "https://cdn.discordapp.com/avatars/%s/%s.%s"

        const val DEFAULT_AVATAR_URL: String = "https://cdn.discordapp.com/embed/avatars/%s.png"

        const val BANNER_URL: String = "https://cdn.discordapp.com/banners/%s/%s.%s"

        const val DEFAULT_ACCENT_COLOR_RAW: Int = 0x1FFFFFFF
    }
}
