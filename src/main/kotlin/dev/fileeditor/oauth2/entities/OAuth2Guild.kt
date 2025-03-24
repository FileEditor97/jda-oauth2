package dev.fileeditor.oauth2.entities

import dev.fileeditor.oauth2.OAuth2Client
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ISnowflake
import java.util.*

/**
 * OAuth2 representation of a Discord Server/Guild.
 *
 *
 * Note that this is effectively a wrapper for both the Guild info, and
 * the info on the user in the guild represented by the session that got this Guild.
 */
interface OAuth2Guild : ISnowflake {
    /**
     * Gets the underlying [OAuth2Client] that created this OAuth2Guild.
     *
     * @return The OAuth2Client that created this OAuth2Guild.
     */
    val client: OAuth2Client

    /**
     * Gets the Guild's name.
     *
     * @return The Guild's name.
     */
    val name: String

    /**
     * Gets the Guild's icon ID, or `null` if the Guild does not have an icon.
     *
     * @return The Guild's icon ID.
     */
    val iconId: String?

    /**
     * Gets the Guild's icon URL, or `null` if the Guild does not have an icon.
     *
     * @return The Guild's icon URL.
     */
    val iconUrl: String?

    /**
     * Gets the Guild's banner ID, or `null` if the Guild does not have a banner.
     *
     * @return The Guild's banner ID.
     */
    val bannerId: String?

    /**
     * Gets the Guild's banner URL, or `null` if the Guild does not have a banner.
     *
     * @return The Guild's banner URL.
     */
    val bannerUrl: String?

    /**
     * Gets the Session User's raw permission value for the Guild.
     *
     * @return The Session User's raw permission value for the Guild.
     */
    val permissionsRaw: Long

    /**
     * Gets the Session User's [Permissions][net.dv8tion.jda.api.Permission] for the Guild.
     *
     * @return The Session User's Permissions for the Guild.
     */
    val permissions: EnumSet<Permission>

    /**
     * Whether the Session User is the owner of the Guild.
     *
     * @return `true` if the Session User is the owner of
     * the Guild, `false` otherwise.
     */
    val isOwner: Boolean

    /**
     * Whether the Session User has all specified [Permissions][net.dv8tion.jda.api.Permission] in the Guild.
     *
     * @param  perms
     * The Permissions to check for.
     *
     * @return `true` if and only if the Session User has all
     * specified Permissions, `false` otherwise.
     */
    fun hasPermission(vararg perms: Permission): Boolean

    /**
     * @return the approximate count of online members in the guild,
     * or -1 if Scope [GUILDS_MEMBERS][dev.fileeditor.oauth2.Scope.GUILDS_MEMBERS] not set
     */
    val onlineCount: Int

    /**
     * @return the approximate count of total members in the guild,
     * or -1 if Scope [GUILDS_MEMBERS][dev.fileeditor.oauth2.Scope.GUILDS_MEMBERS] not set
     */
    val memberCount: Int
}
