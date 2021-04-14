package dev.mlnr.blh.jda;

import dev.mlnr.blh.core.api.IBLHUpdater;
import dev.mlnr.blh.core.internal.utils.Checks;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nonnull;

/**
 * An updater class for JDA used for automatic stats posting.
 */
public class BLHJDAUpdater implements IBLHUpdater {
	private JDA jda;
	private ShardManager shardManager;

	/**
	 * Creates a new updater for JDA.
	 *
	 * @param  jda
	 *         The JDA object to get the bot id and the guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided JDA object is {@code null}
	 */
	public BLHJDAUpdater(@Nonnull JDA jda) {
		Checks.notNull(jda, "The JDA object");

		this.jda = jda;
	}

	/**
	 * Creates a new updater for JDA.
	 *
	 * @param  shardManager
	 *         The ShardManager object to get the bot id and the guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided ShardManager object is {@code null}
	 * @throws IllegalStateException
	 *         If the shard 0 of the ShardManager is null
	 */
	public BLHJDAUpdater(@Nonnull ShardManager shardManager) {
		Checks.notNull(shardManager, "The ShardManager object");
		Checks.check(shardManager.getShardById(0) == null, "The shard 0 of the ShardManager may not be null");

		this.shardManager = shardManager;
	}

	@Override
	public long getBotId() {
		long botId;
		if (jda == null) {
			JDA shard = shardManager.getShardById(0);
			Checks.check(shard == null, "The shard 0 of the ShardManager may not be null");
			botId = shard.getSelfUser().getIdLong();
		}
		else {
			botId = jda.getSelfUser().getIdLong();
		}
		return botId;
	}

	@Override
	public long getServerCount() {
		return jda == null ? shardManager.getGuildCache().size() : jda.getGuildCache().size();
	}
}