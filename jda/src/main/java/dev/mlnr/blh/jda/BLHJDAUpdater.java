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
	 */
	public BLHJDAUpdater(@Nonnull JDA jda) {
		this.jda = jda;
	}

	/**
	 * Creates a new updater for JDA.
	 *
	 * @param  shardManager
	 *         The ShardManager object to get the bot id and the guild amount from
	 */
	public BLHJDAUpdater(@Nonnull ShardManager shardManager) {
		Checks.check(shardManager.getShardById(0) == null, "The shard 0 of the ShardManager may not be null");

		this.shardManager = shardManager;
	}

	@Override
	public long getBotId() {
		JDA shard = shardManager.getShardById(0);
		Checks.check(shard == null, "The shard 0 of the ShardManager may not be null");
		return shard.getSelfUser().getIdLong();
	}

	@Override
	public long getServerCount() {
		return jda == null ? shardManager.getGuildCache().size() : jda.getGuildCache().size();
	}
}