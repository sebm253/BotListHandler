package dev.mlnr.blh.jda;

import dev.mlnr.blh.core.api.IBLHUpdater;
import dev.mlnr.blh.core.internal.utils.Checks;
import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;

/**
 * An updater class for JDA used for automatic stats posting.
 */
public class BLHJDAUpdater implements IBLHUpdater {
	private final JDA jda;

	/**
	 * Creates a new updater for JDA.
	 *
	 * @param  jda
	 *         The JDA object to get the guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided JDA object is {@code null}
	 */
	public BLHJDAUpdater(@Nonnull JDA jda) {
		Checks.notNull(jda, "The JDA object");

		this.jda = jda;
	}

	@Override
	public long getBotId() {
		return jda.getSelfUser().getIdLong();
	}

	@Override
	public long getServerCount() {
		return jda.getGuildCache().size();
	}
}