package dev.fileeditor.oauth2.entities.impl

import dev.fileeditor.oauth2.OAuth2Client
import dev.fileeditor.oauth2.entities.OAuth2Guild
import net.dv8tion.jda.api.Permission
import java.util.*

class OAuth2GuildImpl(
    override val client: OAuth2Client,
    private val id: Long,
    override val name: String,
    override val iconId: String?,
    override val bannerId: String?,
    override val isOwner: Boolean,
    override val permissionsRaw: Long,
    override val onlineCount: Int,
    override val memberCount: Int
) : OAuth2Guild {
    override fun getIdLong(): Long {
        return id
    }

    override val iconUrl: String?
        get() = if (iconId == null) null else String.format(
            ICON_URL,
            id,
            iconId,
        )

    override val bannerUrl: String?
        get() = if (bannerId == null) null else String.format(
            BANNER_URL,
            id,
            bannerId
        )

    override val permissions: EnumSet<Permission>
        get() =  Permission.getPermissions(permissionsRaw)

    override fun hasPermission(vararg perms: Permission): Boolean {
        if (isOwner) return true

        val adminPermRaw = Permission.ADMINISTRATOR.rawValue
        val permissions = permissionsRaw

        if ((permissions and adminPermRaw) == adminPermRaw) return true

        val checkPermsRaw = Permission.getRaw(*perms)

        return (permissions and checkPermsRaw) == checkPermsRaw
    }

    companion object {
        const val ICON_URL: String = "https://cdn.discordapp.com/avatars/%s/%s.png"

        const val BANNER_URL: String = "https://cdn.discordapp.com/banners/%s/%s.png"
    }
}
