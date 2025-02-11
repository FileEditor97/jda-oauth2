package dev.fileeditor.oauth2.entities.impl;

import dev.fileeditor.oauth2.OAuth2Client;
import dev.fileeditor.oauth2.Scope;
import dev.fileeditor.oauth2.session.Session;
import dev.fileeditor.oauth2.entities.OAuth2User;
import dev.fileeditor.oauth2.exceptions.MissingScopeException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class OAuth2UserImpl implements OAuth2User {
	private final OAuth2Client client;
	private final Session session;

	private final long id;
	private final int accentColor;
	private final String name, globalName, avatar, banner, email, locale;
	private final boolean verified, mfaEnabled;

	public OAuth2UserImpl(OAuth2Client client, Session session, long id, String name, String globalName,
						  String avatar, String email, boolean verified, boolean mfaEnabled,
						  String banner, int accentColor, String locale)
	{
		this.client = client;
		this.session = session;
		this.id = id;
		this.name = name;
		this.globalName = globalName;
		this.avatar = avatar;
		this.email = email;
		this.verified = verified;
		this.mfaEnabled = mfaEnabled;
		this.accentColor = accentColor;
		this.banner = banner;
		this.locale = locale;
	}

	@Override
	public OAuth2Client getClient() {
		return client;
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public String getId() {
		return Long.toUnsignedString(id);
	}

	@Override
	public long getIdLong() {
		return id;
	}

	@NotNull
	@Override
	public String getName() {
		return name;
	}

	@Nullable
	@Override
	public String getGlobalName() {
		return globalName;
	}

	@Override
	public String getEmail() {
		if(!Scope.contains(getSession().getScopes(), Scope.EMAIL))
			throw new MissingScopeException("get email for user", Scope.EMAIL);
		return email;
	}

	@Override
	public boolean isVerified() {
		return verified;
	}

	@Override
	public boolean isMfaEnabled() {
		return mfaEnabled;
	}

	@Override
	public String getAvatarId() {
		return avatar;
	}

	@NotNull
	@Override
	public String getDefaultAvatarId() {
		return String.valueOf((id >> 22) % 6);
	}

	@Override
	public String getAvatarUrl() {
		return getAvatarId() == null ? null : "https://cdn.discordapp.com/avatars/" + getId() + "/" + getAvatarId()
			+ (getAvatarId().startsWith("a_") ? ".gif" : ".png");
	}

	@Nullable
	@Override
	public String getBannerId() {
		return banner;
	}

	@Nullable
	@Override
	public String getBannerUrl() {
		return banner == null ? null : String.format(BANNER_URL, Long.toUnsignedString(id), banner, banner.startsWith("a_") ? "gif" : "png");
	}

	@Nullable
	@Override
	public Color getAccentColor() {
		return accentColor == DEFAULT_ACCENT_COLOR_RAW ? null : new Color(accentColor);
	}

	@Override
	public int getAccentColorRaw() {
		return accentColor;
	}

	@Override
	public String getLocale() {
		return locale == null ? "en-US" : locale;
	}

	@Override
	public String getAsMention() {
		return "<@" + id + '>';
	}

	@Override
	public User getJDAUser(JDA jda) {
		return jda.getUserById(id);
	}

	@Override
	public User getJDAUser(ShardManager shardManager) {
		return shardManager.getUserById(id);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof OAuth2UserImpl oUser))
			return false;
		return this == oUser || this.id == oUser.id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public String toString() {
		return "U:" + getName() + '(' + id + ')';
	}

}
