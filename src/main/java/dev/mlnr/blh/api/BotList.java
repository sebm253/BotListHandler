package dev.mlnr.blh.api;

public enum BotList {
	TOP_GG("https://top.gg/api/bots/%s/stats", "server_count"),
	BOTLIST_SPACE("https://api.botlist.space/v1/bots/%s", "server_count"),
	DBOATS("https://discord.boats/api/bot/%s", "server_count"),
	DSERVICES("https://api.discordservices.net/bot/%s/stats", "servers"),
	DBOTS_GG("https://discord.bots.gg/api/v1/bots/%s/stats", "guildCount"),
	DBL("https://discordbotlist.com/api/v1/bots/%s/stats", "guilds"),
	DEL("https://api.discordextremelist.xyz/v2/bot/%s/stats", "guildCount");

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