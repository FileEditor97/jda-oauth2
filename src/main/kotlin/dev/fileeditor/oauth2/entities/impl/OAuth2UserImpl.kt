package dev.fileeditor.oauth2.entities.impl

import dev.fileeditor.oauth2.OAuth2Client
import dev.fileeditor.oauth2.entities.OAuth2User
import dev.fileeditor.oauth2.session.Session
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.sharding.ShardManager

class OAuth2UserImpl(
    override val client: OAuth2Client,
    override val session: Session,
    private val id: Long,
    override val name: String,
    override val globalName: String?,
    override val avatarId: String?,
    override val email: String?,
    override val isVerified: Boolean,
    override val isMfaEnabled: Boolean,
    override val bannerId: String?,
    override val accentColorRaw: Int = OAuth2User.DEFAULT_ACCENT_COLOR_RAW,
    override val locale: String = "en-US"
) : OAuth2User {
    override fun getIdLong(): Long {
        return id
    }

    override fun getJDAUser(jda: JDA): User? {
        return jda.getUserById(idLong)
    }

    override fun getJDAUser(shardManager: ShardManager): User? {
        return shardManager.getUserById(idLong)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is OAuth2UserImpl) return false
        return this === other || this.idLong == other.idLong
    }

    override fun hashCode(): Int {
        return idLong.hashCode()
    }

    override fun toString(): String {
        return "U:$name($idLong)"
    }
}
