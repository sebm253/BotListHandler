package dev.mlnr.blh.core.api;

import dev.mlnr.blh.core.internal.config.AutoPostingConfig;
import dev.mlnr.blh.core.internal.config.LoggingConfig;
import dev.mlnr.blh.core.internal.utils.Checks;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class BotListHandler {
	private final Map<BotList, String> botLists;
	private final long botId;
	private final Predicate<Long> devModePredicate;
	private final boolean unavailableEventsEnabled;
	private final AutoPostingConfig autoPostingConfig;
	private final LoggingConfig loggingConfig;

	private static final Logger logger = LoggerFactory.getLogger(BotListHandler.class);
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	private final OkHttpClient httpClient = new OkHttpClient();

	private final Set<BotList> ratelimitedBotLists;
	private final Set<BotList> unauthorizedBotLists;

	private long previousGuildCount = -1;

	BotListHandler(Map<BotList, String> botListMap, long botId, Predicate<Long> devModePredicate, boolean unavailableEventsEnabled,
	               AutoPostingConfig autoPostingConfig, LoggingConfig loggingConfig) {
		this.botLists = botListMap;
		this.botId = botId;
		this.devModePredicate = devModePredicate;
		this.unavailableEventsEnabled = unavailableEventsEnabled;
		this.autoPostingConfig = autoPostingConfig;
		this.loggingConfig = loggingConfig;
		this.ratelimitedBotLists = EnumSet.noneOf(BotList.class);
		this.unauthorizedBotLists = EnumSet.noneOf(BotList.class);

		if (autoPostingConfig.isAutoPostingEnabled())
			SCHEDULER.scheduleAtFixedRate(() -> updateAllStats(autoPostingConfig.getUpdater()), 0, autoPostingConfig.getDelay(), autoPostingConfig.getUnit());
	}

	/**
	 * Used to add bot lists at runtime.
	 *
	 * @param  botList
	 *         The bot list to add
	 * @param  token
	 *         The token for the bot list
	 *
	 * @throws IllegalArgumentException
	 *         If the provided bot list or token is {@code null} or empty
	 */
	public void addBotList(@Nonnull BotList botList, @Nonnull String token) {
		Checks.checkListAndToken(botList, token);

		botLists.put(botList, token);
	}

	/**
	 * Used to hotswap invalid tokens at runtime.
	 *
	 * @param  botList
	 *         The bot list to replace the token for
	 * @param  newToken
	 *         The new token to use for the provided bot list
	 *
	 * @throws IllegalStateException
	 *         If the bot list hasn't been added
	 * @throws IllegalStateException
	 *         If the provided token is the same as the previous one
	 */
	public void swapToken(@Nonnull BotList botList, @Nonnull String newToken) {
		String previousToken = botLists.get(botList);
		Checks.check(previousToken == null, "The bot list hasn't been added");
		Checks.check(previousToken.equals(newToken), "The new token may not be the same as the previous one");

		addBotList(botList, newToken);
		unauthorizedBotLists.remove(botList); // if the bot list isn't unauthorized, nothing will happen
	}

	// "internal" methods

	/**
	 * Returns whether automatic stats posting is enabled.
	 *
	 * @return Whether automatic stats posting is enabled
	 */
	public boolean isAutoPostingEnabled() {
		return autoPostingConfig.isAutoPostingEnabled();
	}

	/**
	 * Returns the bot id BotListHandler is updating the stats for.
	 *
	 * @return The bot id BotListHandler is updating the stats for
	 */
	public long getBotId() {
		return botId;
	}

	/**
	 * Returns whether handling of unavailable events is enabled.
	 *
	 * <br><b>This only affects the JDA updater.</b>
	 *
	 * @return Whether handling of unavailable events is enabled
	 */
	public boolean isUnavailableEventsHandlingEnabled() {
		return unavailableEventsEnabled;
	}

	void updateAllStats(IBLHUpdater updater) {
		updateAllStats(updater.getBotId(), updater.getServerCount());
	}

	/**
	 * A method to update the stats for all added {@link BotList BotLists}.
	 *
	 * <br><b>If the current saved server count is the same as the provided one, the call will be ignored.</b>
	 *
	 * @param  botId
	 *         The id of the bot to update the stats for
	 * @param  serverCount
	 *         The amount of servers
	 *
	 * @throws IllegalArgumentException
	 *         If the provided server amount is negative
	 */
	public void updateAllStats(long botId, long serverCount) {
		if (devModePredicate.test(botId))
			return;
		Checks.notNegative(botId, "The bot id");
		Checks.notNegative(serverCount, "The server amount");

		if (serverCount == previousGuildCount) {
			logger.info("No stats updating was necessary.");
			return;
		}
		previousGuildCount = serverCount;
		botLists.forEach((botList, token) -> updateStats(botList, token, botId, serverCount, false));
	}

	void updateStats(BotList botList, String token, long botId, long serverCount, boolean retriedRequest) {
		if (ratelimitedBotLists.contains(botList) && !retriedRequest)
			return;
		String botListName = botList.name();
		if (unauthorizedBotLists.contains(botList)) {
			logger.warn("Dropping stats update for bot list {} as the provided token is invalid. " +
					"You can hotswap the token by calling swapToken on the BotListHandler instance.", botListName);
			return;
		}
		JSONObject payload = new JSONObject().put(botList.getServersParam(), serverCount);

		String url = String.format(botList.getUrl(), botId);
		Request.Builder requestBuilder = new Request.Builder().url(url)
				.header("Authorization", token)
				.post(RequestBody.create(payload.toString(), MediaType.parse("application/json")));

		httpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				logger.error("There was an error while updating stats for bot list {}", botListName, e);
			}

			@Override
			public void onResponse(Call call, Response response) {
				response.close();
				if (response.isSuccessful()) {
					if (loggingConfig.isSuccessLoggingEnabled())
						logger.info("Successfully updated stats for bot list {}", botListName);
					ratelimitedBotLists.remove(botList); // if the bot list isn't ratelimited, nothing will happen
				}
				else {
					int code = response.code();
					if (code == 401) {
						logger.error("Failed to update stats for bot list {} as the provided token is invalid. " +
									"You can hotswap the token by calling swapToken on the BotListHandler instance.", botListName);
						unauthorizedBotLists.add(botList);
						return;
					}
					if (code == 429) {
						if (loggingConfig.isRatelimitedLoggingEnabled())
							logger.warn("Failed to update stats for bot list {} as we got ratelimited. Retrying in 15 seconds", botListName);
						ratelimitedBotLists.add(botList);
						SCHEDULER.schedule(() -> updateStats(botList, token, botId, serverCount, true), 15, TimeUnit.SECONDS);
						return;
					}
					logger.error("Failed to update stats for bot list {} with code {}", botListName, code);
				}
			}
		});
	}
}