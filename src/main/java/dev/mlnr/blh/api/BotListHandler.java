package dev.mlnr.blh.api;

import dev.mlnr.blh.internal.config.AutoPostingConfig;
import dev.mlnr.blh.internal.config.LoggingConfig;
import dev.mlnr.blh.internal.utils.Checks;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
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
	private final Predicate<JDA> devModePredicate;
	private final boolean unavailableEventsEnabled;
	private final AutoPostingConfig autoPostingConfig;
	private final LoggingConfig loggingConfig;

	private static final Logger logger = LoggerFactory.getLogger(BotListHandler.class);
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	private final OkHttpClient httpClient = new OkHttpClient();

	private final Set<BotList> ratelimitedBotLists;

	private long previousGuildCount = -1;

	BotListHandler(Map<BotList, String> botListMap, Predicate<JDA> devModePredicate, boolean unavailableEventsEnabled,
	               AutoPostingConfig autoPostingConfig, LoggingConfig loggingConfig) {
		this.botLists = botListMap;
		this.devModePredicate = devModePredicate;
		this.unavailableEventsEnabled = unavailableEventsEnabled;
		this.autoPostingConfig = autoPostingConfig;
		this.loggingConfig = loggingConfig;
		this.ratelimitedBotLists = EnumSet.noneOf(BotList.class);

		if (isAutoPostingEnabled()) {
			JDA jda = autoPostingConfig.getJDA();
			long delay = autoPostingConfig.getDelay();
			long initialDelay = delay;
			if (jda.getStatus() == JDA.Status.INITIALIZED) // if jda has finished setting up cache for guilds, immediately post the guild count
				initialDelay = 0;
			SCHEDULER.scheduleAtFixedRate(() -> updateAllStats(jda), initialDelay, delay, autoPostingConfig.getUnit());
		}
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
	}

	// "internal" methods

	boolean isAutoPostingEnabled() {
		return autoPostingConfig.isAutoPostingEnabled();
	}

	boolean isUnavailableEventsHandlingEnabled() {
		return unavailableEventsEnabled;
	}

	void updateAllStats(JDA jda) {
		if (devModePredicate.test(jda))
			return;
		long serverCount = jda.getGuildCache().size();
		if (serverCount == previousGuildCount) {
			logger.info("No stats updating was necessary.");
			return;
		}
		previousGuildCount = serverCount;

		botLists.forEach((botList, token) -> updateStats(botList, token, jda, serverCount, false));
	}

	void updateStats(BotList botList, String token, JDA jda, long serverCount, boolean retriedRequest) {
		if (ratelimitedBotLists.contains(botList) && !retriedRequest)
			return;
		String botListName = botList.name();
		DataObject payload = DataObject.empty().put(botList.getServersParam(), serverCount);

		String url = String.format(botList.getUrl(), jda.getSelfUser().getId());
		Request.Builder requestBuilder = new Request.Builder().url(url)
				.header("Authorization", token)
				.post(RequestBody.create(MediaType.parse("application/json"), payload.toString()));

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
						return;
					}
					if (code == 429) {
						if (loggingConfig.isRatelimitedLoggingEnabled())
							logger.warn("Failed to update stats for bot list {} as we got ratelimited. Retrying in 15 seconds", botListName);
						ratelimitedBotLists.add(botList);
						SCHEDULER.schedule(() -> updateStats(botList, token, jda, serverCount, true), 15, TimeUnit.SECONDS);
						return;
					}
					logger.error("Failed to update stats for bot list {} with code {}", botListName, code);
				}
			}
		});
	}
}