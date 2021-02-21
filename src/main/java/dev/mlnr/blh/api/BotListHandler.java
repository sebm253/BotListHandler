package dev.mlnr.blh.api;

import net.dv8tion.jda.api.JDA;

public interface BotListHandler {
	boolean isAutoPostingEnabled();
	void updateStats(JDA jda);
}