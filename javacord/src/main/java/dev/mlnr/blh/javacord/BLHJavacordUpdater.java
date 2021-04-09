package dev.mlnr.blh.javacord;

import dev.mlnr.blh.core.api.IBLHUpdater;
import dev.mlnr.blh.core.internal.utils.Checks;
import org.javacord.api.DiscordApi;

import javax.annotation.Nullable;

/**
 * An updater class for Javacord used for automatic stats posting.
 */
public class BLHJavacordUpdater implements IBLHUpdater {
	private final DiscordApi javacord;

	/**
	 * Creates a new updater for Javacord.
	 *
	 * @param  javacord
	 *         The DiscordApi object to get the bot id and guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided DiscordApi object is {@code null}
	 */
	public BLHJavacordUpdater(@Nullable DiscordApi javacord) {
		Checks.notNull(javacord, "The DiscordApi object");

		this.javacord = javacord;
	}

	@Override
	public long getBotId() {
		return javacord.getClientId();
	}

	@Override
	public long getServerCount() {
		return javacord.getServers().size();
	}
}