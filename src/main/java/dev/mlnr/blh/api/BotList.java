package dev.mlnr.blh.api;

public enum BotList {
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