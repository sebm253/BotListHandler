package dev.mlnr.blh.api;

import dev.mlnr.blh.internal.config.AutoPostingConfig;
import dev.mlnr.blh.internal.utils.Checks;
import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BLHBuilder {
	private Map<BotList, String> botLists = new EnumMap<>(BotList.class);

	private JDA jda;
	private long autoPostDelay;
	private TimeUnit autoPostUnit;

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for event based stats updating.</b>
	 * You can add bot lists by using one of provided methods.
	 *
	 * @see #BLHBuilder(Map)
	 */
	public BLHBuilder() {}

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for automatic stats posting.</b>
	 * You can add bot lists by using one of provided methods.
	 *
	 * @param  jda
	 *         The JDA object to get the guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided JDA object is {@code null}
	 *
	 * @see    #BLHBuilder(JDA, Map)
	 */
	public BLHBuilder(@Nonnull JDA jda) {
		Checks.notNull(jda, "The JDA object");

		this.jda = jda;
	}

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for automatic stats posting.</b>
	 * Provided map of bot lists will be used to update stats.
	 *
	 * @param  jda
	 *         The JDA object to get the guild amount from
	 * @param  botLists
	 *         The bot lists map
	 *
	 * @throws IllegalArgumentException
	 *         If the provided JDA object or bot list map is {@code null} or empty
	 */
	public BLHBuilder(@Nonnull JDA jda, @Nonnull Map<BotList, String> botLists) {
		Checks.notNull(jda, "The JDA object");
		Checks.notEmpty(botLists, "The bot lists map");

		this.jda = jda;
		this.botLists = botLists;
	}

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for event based stats updating.</b>
	 * Provided map of bot lists will be used to update stats.
	 *
	 * @param  botLists
	 *         The bot lists map
	 *
	 * @throws IllegalArgumentException
	 *         If the provided bot list map is {@code null} or empty
	 */
	public BLHBuilder(@Nonnull Map<BotList, String> botLists) {
		setBotLists(botLists);
	}

	/**
	 * Adds a bot list to update the stats for.
	 *
	 * @param  botList
	 *         The bot list
	 * @param  token
	 *         The API token for the bot list
	 *
	 * @throws IllegalArgumentException
	 *         If the provided bot list or token is {@code null} or empty
	 */
	public void addBotList(@Nonnull BotList botList, @Nonnull String token) {
		Checks.notNull(botList, "The bot list");
		Checks.notEmpty(token, "The bot list token");

		botLists.put(botList, token);
	}

	/**
	 * Sets the bot list map to update the stats for.
	 *
	 * @param  botLists
	 *         The bot lists map
	 *
	 * @throws IllegalArgumentException
	 *         If the provided bot list map is {@code null} or empty
	 */
	public void setBotLists(@Nonnull Map<BotList, String> botLists) {
		Checks.notEmpty(botLists, "The bot lists map");

		this.botLists = botLists;
	}

	/**
	 * Sets the autoposting delay.
	 * <br><b>This is only for when using a builder for automatic stats posting.</b>
	 *
	 * @param  delay
	 *         The delay to use
	 * @param  unit
	 *         The time unit to use
	 *
	 * @throws IllegalArgumentException
	 *         If no JDA object was set (using other constructor than {@link #BLHBuilder(JDA)} or {@link #BLHBuilder(JDA, Map)})
	 * @throws IllegalStateException
	 *         If the provided delay is less than {@code 1}
	 * @throws IllegalArgumentException
	 *         If the provided unit is {@code null}
	 * @throws IllegalStateException
	 *         If provided unit is smaller than seconds
	 *
	 * @see    #BLHBuilder(JDA)
	 * @see    #BLHBuilder(JDA, Map)
	 */
	public void setAutoPostDelay(long delay, @Nonnull TimeUnit unit) {
		Checks.notNull(this.jda, "The JDA object");
		Checks.check(delay < 1, "The delay cannot be less than 1");
		Checks.notNull(unit, "The time unit");
		Checks.check(unit.ordinal() < TimeUnit.SECONDS.ordinal(), "The time unit cannot be smaller than seconds");

		this.autoPostDelay = delay;
		this.autoPostUnit = unit;
	}

	/**
	 * Builds BotListHandler.
	 *
	 * <br>If you use autoposting, this will start the posting scheduler.
	 * Additionally, if provided JDA object was passed before JDA has been ready, this will wait the provided delay to update the stats for the first time.
	 *
	 * <br>Returned instance can be used to be passed into the {@link BLHEventListener} constructor to use event based updating.
	 *
	 * @throws IllegalArgumentException
	 *         If no bot lists were added
	 *
	 * @return The BotListHandler instance
	 */
	public BotListHandler build() {
		Checks.notEmpty(botLists, "The bot lists map");

		return new BotListHandler(botLists, new AutoPostingConfig(jda, autoPostDelay, autoPostUnit));
	}
}