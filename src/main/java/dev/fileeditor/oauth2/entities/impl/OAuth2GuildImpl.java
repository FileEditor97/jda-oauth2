package dev.fileeditor.oauth2.entities.impl;

import dev.fileeditor.oauth2.OAuth2Client;
import dev.fileeditor.oauth2.entities.OAuth2Guild;
import net.dv8tion.jda.api.Permission;

import java.util.EnumSet;

public class OAuth2GuildImpl implements OAuth2Guild {
	private final OAuth2Client client;
	private final long id, permissions;
	private final String name, icon, banner;
	private final boolean owner;
	private final int presenceCount, memberCount;

	public OAuth2GuildImpl(OAuth2Client client, long id, String name, String icon, String banner, boolean owner, long permissions, int presenceCount, int memberCount) {
		this.client = client;
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.banner = banner;
		this.owner = owner;
		this.permissions = permissions;
		this.presenceCount = presenceCount;
		this.memberCount = memberCount;
	}

	@Override
	public OAuth2Client getClient() {
		return client;
	}

	@Override
	public long getIdLong() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getIconId() {
		return icon;
	}

	@Override
	public String getIconUrl() {
		return icon == null ? null : "https://cdn.discordapp.com/icons/" + id + "/" + icon + ".png";
	}

	@Override
	public String getBannerId() {
		return banner;
	}

	@Override
	public String getBannerUrl() {
		return banner == null ? null : "https://cdn.discordapp.com/banners/" + id + "/" + banner + ".png";
	}

	@Override
	public long getPermissionsRaw() {
		return permissions;
	}

	@Override
	public EnumSet<Permission> getPermissions() {
		return Permission.getPermissions(permissions);
	}

	@Override
	public boolean isOwner() {
		return owner;
	}

	@Override
	public boolean hasPermission(Permission... perms) {
		if(isOwner())
			return true;

		long adminPermRaw = Permission.ADMINISTRATOR.getRawValue();
		long permissions = getPermissionsRaw();

		if ((permissions & adminPermRaw) == adminPermRaw)
			return true;

		long checkPermsRaw = Permission.getRaw(perms);

		return (permissions & checkPermsRaw) == checkPermsRaw;
	}

	@Override
	public int getOnlineCount() {
		return presenceCount;
	}

	@Override
	public int getMemberCount() {
		return memberCount;
	}
}
