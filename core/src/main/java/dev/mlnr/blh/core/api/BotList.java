package dev.mlnr.blh.core.api;

public enum BotList {
	BOTS_FOR_DISCORD("https://botsfordiscord.com/api/bot/%s", "server_count"),
	BOTS_ON_DISCORD("https://bots.ondiscord.xyz/bot-api/bots/%s/guilds", "guildCount"),
	DISCORDLIST_SPACE("https://api.discordlist.space/v1/bots/%s", "server_count"),
	DBL("https://discordbotlist.com/api/v1/bots/%s/stats", "guilds"),
	DBOATS("https://discord.boats/api/bot/%s", "server_count"),
	DBOTS_CO("https://api.discordbots.co/v1/public/bot/%s/stats", "serverCount"),
	DBOTS_GG("https://discord.bots.gg/api/v1/bots/%s/stats", "guildCount"),
	DEL("https://api.discordextremelist.xyz/v2/bot/%s/stats", "guildCount"),
	DSERVICES("https://api.discordservices.net/bot/%s/stats", "servers"),
	TOP_GG("https://top.gg/api/bots/%s/stats", "server_count");

	private final String url;
	private final String serversParam;

	BotList(String url, String serversParam) {
		this.url = url;
		this.serversParam = serversParam;
	}

	public String getUrl() {
		return url;
	}

	public String getServersParam() {
		return serversParam;
	}
}