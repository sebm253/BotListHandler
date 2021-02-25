package dev.mlnr.blh.api;

import dev.mlnr.blh.internal.utils.Checks;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * A listener which is used to update bot stats when bot is ready or joins or leaves a guild.
 */
public class BLHEventListener extends ListenerAdapter {
	private final BotListHandler botListHandler;

	/**
	 * Constructs a new event listener used to update bot stats.
	 *
	 * <br>The stats will be updated when the bot joins/leaves a guild.
	 * <b>Additionally, if you want to update the stats when the bot is ready, you need to register this listener using the
	 * {@link net.dv8tion.jda.api.JDABuilder#addEventListeners(Object...) JDABuilder#addEventListeners} method.</b>
	 *
	 * @param  botListHandler
	 *         The {@link BotListHandler} instance built by a {@link BLHBuilder}
	 *
	 * @throws IllegalArgumentException
	 *         If the provided {@link BotListHandler} instance is {@code null}
	 * @throws IllegalStateException
	 *         If the provided {@link BotListHandler} instance uses autoposting
	 */
	public BLHEventListener(@Nonnull BotListHandler botListHandler) {
		Checks.notNull(botListHandler, "The BotListHandler instance");

		if (botListHandler.isAutoPostingEnabled())
			throw new IllegalStateException("Can only use event based updating if autoposting is disabled");
		this.botListHandler = botListHandler;
	}

	@Override
	public void onReady(ReadyEvent event) {
		botListHandler.updateAllStats(event.getJDA());
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		botListHandler.updateAllStats(event.getJDA());
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		botListHandler.updateAllStats(event.getJDA());
	}

	// unavailable guilds

	@Override
	public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) {
		if (botListHandler.isUnavailableEventsHandlingEnabled())
			botListHandler.updateAllStats(event.getJDA());
	}

	@Override
	public void onUnavailableGuildLeave(UnavailableGuildLeaveEvent event) {
		if (botListHandler.isUnavailableEventsHandlingEnabled())
			botListHandler.updateAllStats(event.getJDA());
	}
}