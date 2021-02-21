package dev.mlnr.blh.api;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BLHEventListener extends ListenerAdapter {
	private final BotListHandler botListHandler;

	public BLHEventListener(BotListHandler botListHandler) {
		if (botListHandler.isAutoPostingEnabled())
			throw new IllegalStateException("Can only use event based updating if autoposting is disabled");
		this.botListHandler = botListHandler;
	}

	@Override
	public void onReady(ReadyEvent event) {
		botListHandler.updateStats(event.getJDA());
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		botListHandler.updateStats(event.getJDA());
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		botListHandler.updateStats(event.getJDA());
	}

	// unavailable guilds

	@Override
	public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) {
		botListHandler.updateStats(event.getJDA());
	}

	@Override
	public void onUnavailableGuildLeave(UnavailableGuildLeaveEvent event) {
		botListHandler.updateStats(event.getJDA());
	}
}