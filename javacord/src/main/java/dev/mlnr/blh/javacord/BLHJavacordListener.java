package dev.mlnr.blh.javacord;

import dev.mlnr.blh.core.api.BLHBuilder;
import dev.mlnr.blh.core.api.BotListHandler;
import dev.mlnr.blh.core.internal.utils.Checks;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.javacord.api.listener.server.ServerLeaveListener;

import javax.annotation.Nonnull;

/**
 * A Javacord listener which is used to update bot stats when bot joins or leaves a guild.
 */
public class BLHJavacordListener implements ServerJoinListener, ServerLeaveListener {
	private final BotListHandler botListHandler;

	/**
	 * Constructs a new event listener used to update bot stats.
	 *
	 * <br>The stats will be updated when the bot joins/leaves a guild.
	 *
	 * @param  botListHandler
	 *         The {@link BotListHandler} instance built by a {@link BLHBuilder}
	 *
	 * @throws IllegalArgumentException
	 *         If the provided {@link BotListHandler} instance is {@code null}
	 * @throws IllegalStateException
	 *         If the provided {@link BotListHandler} instance uses autoposting
	 */
	public BLHJavacordListener(@Nonnull BotListHandler botListHandler) {
		Checks.notNull(botListHandler, "The BotListHandler instance");
		Checks.check(botListHandler.isAutoPostingEnabled(), "Can only use event based updating if autoposting is disabled");

		this.botListHandler = botListHandler;
	}

	@Override
	public void onServerJoin(ServerJoinEvent event) {
		DiscordApi javacord = event.getApi();
		botListHandler.updateAllStats(javacord.getClientId(), javacord.getServers().size());
	}

	@Override
	public void onServerLeave(ServerLeaveEvent event) {
		DiscordApi javacord = event.getApi();
		botListHandler.updateAllStats(javacord.getClientId(), javacord.getServers().size());
	}
}
