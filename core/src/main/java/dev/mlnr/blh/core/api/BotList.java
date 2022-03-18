package dev.mlnr.blh.core.api;

public enum BotList {
	/**
	 * Bots on Discord
	 *
	 * @see <a href="https://bots.ondiscord.xyz/" target="_blank">Website</a>
	 */
	BOTS_ON_DISCORD("https://bots.ondiscord.xyz/bot-api/bots/%s/guilds", "guildCount"),
	/**
	 * discordlist.space
	 *
	 * @see <a href="https://discordlist.space/" target="_blank">Website</a>
	 */
	DISCORDLIST_SPACE("https://api.discordlist.space/v2/bots/%s", "serverCount"),
	/**
	 * discords.com/bots
	 *
	 * @see <a href="https://discords.com/bots/" target="_blank">Website</a>
	 */
	DISCORDS("https://discords.com/bots/api/bot/%s", "server_count"),
	/**
	 * discordbotlist.com
	 *
	 * @see <a href="https://discordbotlist.com/" target="_blank">Website</a>
	 */
	DBL("https://discordbotlist.com/api/v1/bots/%s/stats", "guilds"),
	/**
	 * discordbots.co
	 *
	 * @see <a href="https://discordbots.co/" target="_blank">Website</a>
	 */
	DBOTS_CO("https://api.discordbots.co/v1/public/bot/%s/stats", "serverCount"),
	/**
	 * discord.bots.gg
	 *
	 * @see <a href="https://discord.bots.gg/" target="_blank">Website</a>
	 */
	DBOTS_GG("https://discord.bots.gg/api/v1/bots/%s/stats", "guildCount"),
	/**
	 * Discord Extreme List
	 *
	 * @see <a href="https://discordextremelist.xyz/" target="_blank">Website</a>
	 */
	DEL("https://api.discordextremelist.xyz/v2/bot/%s/stats", "guildCount"),
	/**
	 * Discord Services
	 *
	 * @see <a href="https://discordservices.net/" target="_blank">Website</a>
	 */
	DSERVICES("https://api.discordservices.net/bot/%s/stats", "servers"),
	/**
	 * Top.gg
	 *
	 * @see <a href="https://top.gg/" target="_blank">Website</a>
	 */
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