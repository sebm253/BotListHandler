package dev.mlnr.blh.javacord;

import dev.mlnr.blh.core.api.IBLHUpdater;
import dev.mlnr.blh.core.internal.utils.Checks;
import org.javacord.api.DiscordApi;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * An updater class for Javacord used for automatic stats posting.
 */
public class BLHJavacordUpdater implements IBLHUpdater {
	private final DiscordApi[] discordApis;

	/**
	 * Creates a new updater for Javacord.
	 *
	 * @param  discordApis
	 *         The DiscordApi objects to get the bot id and the guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided DiscordApi array is {@code null}
	 * @throws IllegalStateException
	 *         If the provided DiscordApi array is empty
	 * @throws IllegalArgumentException
	 *         If the provided DiscordApi array contains {@code null}
	 */
	public BLHJavacordUpdater(@Nonnull DiscordApi... discordApis) {
		Checks.notNull(discordApis, "The DiscordApi array");
		Checks.check(discordApis.length == 0, "The DiscordApi array may not be empty");
		Checks.noneNull(discordApis, "The DiscordApi object");

		this.discordApis = discordApis;
	}

	/**
	 * Creates a new updater for Javacord.
	 *
	 * @param  discordApis
	 *         The DiscordApi collection to get the bot id and the guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided DiscordApi collection is {@code null}
	 * @throws IllegalStateException
	 *         If the provided DiscordApi collection is empty
	 * @throws IllegalArgumentException
	 *         If the provided DiscordApi collection contains {@code null}
	 */
	public BLHJavacordUpdater(@Nonnull Collection<DiscordApi> discordApis) {
		Checks.notNull(discordApis, "The DiscordApi collection");
		Checks.check(discordApis.isEmpty(), "The DiscordApi collection may not be empty");
		Checks.noneNull(discordApis, "The DiscordApi object");

		this.discordApis = discordApis.toArray(new DiscordApi[0]);
	}

	@Override
	public long getBotId() {
		return discordApis[0].getYourself().getId();
	}

	@Override
	public long getServerCount() {
		long count = 0;
		for (DiscordApi discordApi : discordApis)
			count += discordApi.getServers().size();
		return count;
	}
}