package dev.mlnr.blh.core.api;

import dev.mlnr.blh.core.internal.config.AutoPostingConfig;
import dev.mlnr.blh.core.internal.config.LoggingConfig;
import dev.mlnr.blh.core.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * A builder used to build a {@link BotListHandler} instance.
 */
@SuppressWarnings({"FieldHasSetterButNoGetter", "unused"})
public class BLHBuilder {
	private Map<BotList, String> botLists = new EnumMap<>(BotList.class);

	private IBLHUpdater updater;
	private long autoPostDelay;
	private TimeUnit autoPostUnit;

	private boolean successLoggingEnabled = true;
	private boolean ratelimitedLoggingEnabled = true;

	private Predicate<IBLHUpdater> devModePredicate = o -> false;

	private boolean unavailableEventsEnabled = true;

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for event based stats updating or updating by hand.</b>
	 * Bot lists can be added by using one of provided methods.
	 *
	 * @see #BLHBuilder(Map)
	 */
	public BLHBuilder() {}

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for automatic stats posting.</b>
	 * Bot lists can be added by using one of provided methods.
	 *
	 * @param  updater
	 *         The IBLHUpdater instance to get the bot id and guild amount from
	 *
	 * @throws IllegalArgumentException
	 *         If the provided IBLHUpdater instance is {@code null}
	 *
	 * @see    #BLHBuilder(IBLHUpdater, Map)
	 */
	public BLHBuilder(@Nonnull IBLHUpdater updater) {
		Checks.notNull(updater, "The updater instance");

		this.updater = updater;
	}

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for automatic stats posting.</b>
	 * Provided map of bot lists will be used to update stats.
	 *
	 * @param  updater
	 *         The IBLHUpdater instance to get the bot id and guild amount from
	 * @param  botLists
	 *         The bot lists map
	 *
	 * @throws IllegalArgumentException
	 *         If the provided IBLHUpdater instance is {@code null}
	 */
	public BLHBuilder(@Nonnull IBLHUpdater updater, @Nonnull Map<BotList, String> botLists) {
		Checks.notNull(updater, "The updater instance");
		setBotLists(botLists);

		this.updater = updater;
	}

	/**
	 * Creates a BLHBuilder.
	 *
	 * <br><b>This type of builder can only be used for event based stats updating or updating by hand.</b>
	 * Provided map of bot lists will be used to update stats.
	 *
	 * @param  botLists
	 *         The bot lists map
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
	 *
	 * @return This BLHBuilder instance
	 */
	public BLHBuilder addBotList(@Nonnull BotList botList, @Nonnull String token) {
		Checks.checkListAndToken(botList, token);

		botLists.put(botList, token);
		return this;
	}

	/**
	 * Sets the bot list map to update the stats for.
	 *
	 * @param  botLists
	 *         The bot lists map
	 *
	 * @throws IllegalArgumentException
	 *         If the provided bot list map is {@code null} or empty
	 *
	 * @return This BLHBuilder instance
	 */
	public BLHBuilder setBotLists(@Nonnull Map<BotList, String> botLists) {
		Checks.notEmpty(botLists, "The bot lists map");
		botLists.forEach((botList, token) -> Checks.checkToken(token));

		this.botLists = botLists;
		return this;
	}

	/**
	 * Sets the autoposting delay.
	 *
	 * <br><b>This is only for when using a builder for automatic stats posting.</b>
	 *
	 * @param  delay
	 *         The delay to use
	 * @param  unit
	 *         The time unit to use
	 *
	 * @throws IllegalArgumentException
	 *         If no updater instance was set (using other constructor than {@link #BLHBuilder(IBLHUpdater)} or {@link #BLHBuilder(IBLHUpdater, Map)})
	 * @throws IllegalStateException
	 *         If the provided delay is less than {@code 1}
	 * @throws IllegalArgumentException
	 *         If the provided unit is {@code null}
	 * @throws IllegalStateException
	 *         If the provided unit is smaller than seconds
	 *
	 * @see    #BLHBuilder(IBLHUpdater)
	 * @see    #BLHBuilder(IBLHUpdater, Map)
	 *
	 * @return This BLHBuilder instance
	 */
	public BLHBuilder setAutoPostDelay(long delay, @Nonnull TimeUnit unit) {
		Checks.notNull(this.updater, "The updater instance");
		Checks.check(delay < 1, "The delay cannot be less than 1");
		Checks.notNull(unit, "The time unit");
		Checks.check(unit.ordinal() < TimeUnit.SECONDS.ordinal(), "The time unit cannot be smaller than seconds");

		this.autoPostDelay = delay;
		this.autoPostUnit = unit;
		return this;
	}

	/**
	 * Enables/disables logging of successfully updating stats for a bot list.
	 *
	 * <br>Default: {@code true}
	 *
	 * @param  enabled
	 *         Whether successfully updating stats should be logged
	 *
	 * @return This BLHBuilder instance
	 */
	public BLHBuilder setSuccessLoggingEnabled(boolean enabled) {
		this.successLoggingEnabled = enabled;
		return this;
	}

	/**
	 * Enables/disables logging of getting ratelimited.
	 *
	 * <br>Default: {@code true}
	 *
	 * @param  enabled
	 *         Whether getting ratelimited should be logged
	 *
	 * @return This BLHBuilder instance
	 */
	public BLHBuilder setRatelimitedLoggingEnabled(boolean enabled) {
		this.ratelimitedLoggingEnabled = enabled;
		return this;
	}

	/**
	 * Sets the predicate which will be tested for dev mode.
	 *
	 * <br><b>If testing of the predicate evaluates to {@code false}, BotListHandler will keep updating stats.</b>
	 *
	 * @param  predicate
	 *         The predicate to test dev mode for
	 *
	 * @return This BLHBuilder instance
	 */
	public BLHBuilder setDevModePredicate(@Nonnull Predicate<IBLHUpdater> predicate) {
		Checks.notNull(predicate, "The dev mode predicate");

		this.devModePredicate = predicate;
		return this;
	}

	/**
	 * Sets whether handling of join/leave events for unavailable guilds should be enabled.
	 *
	 * <br><b>Discord seems to keep sending one GUILD_DELETE event for an unavailable guild every time the bot starts
	 * resulting in BotListHandler updating the count twice at startup. This only affects the JDA updater.</b>
	 *
	 * <br>Default: {@code true}
	 *
	 * @param  enabled
	 *         Whether handling of join/leave events for unavailable guilds should be enabled
	 *
	 * @return This BLHBuilder instance
	 */
	public BLHBuilder setUnavailableEventsEnabled(boolean enabled) {
		this.unavailableEventsEnabled = enabled;
		return this;
	}

	/**
	 * Builds BotListHandler.
	 *
	 * <br>If autoposting is used, this will start the posting scheduler.
	 * <br>Returned instance can be used to be passed into the constructors of classes implementing {@link IBLHUpdater} to use event based updating or to
	 * add bot lists or hotswap invalid tokens at runtime.
	 *
	 * @throws IllegalArgumentException
	 *         If no bot lists were added
	 *
	 * @return The BotListHandler instance
	 */
	public BotListHandler build() {
		Checks.notEmpty(botLists, "The bot lists map");
		Checks.check(updater != null && autoPostDelay == 0, "The autoposting delay has to be set");

		return new BotListHandler(botLists, devModePredicate, unavailableEventsEnabled, new AutoPostingConfig(updater, autoPostDelay, autoPostUnit),
				new LoggingConfig(successLoggingEnabled, ratelimitedLoggingEnabled));
	}
}