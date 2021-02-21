package dev.mlnr.blh.internal.objects;

import dev.mlnr.blh.api.BotList;

public class BotListData {
	private final BotList botList;
	private final String token;

	public BotListData(BotList botList, String token) {
		this.botList = botList;
		this.token = token;
	}

	public BotList getBotList() {
		return botList;
	}

	public String getToken() {
		return token;
	}
}