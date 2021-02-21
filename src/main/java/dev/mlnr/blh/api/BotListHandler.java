package dev.mlnr.blh.api;

import dev.mlnr.blh.internal.config.AutoPostingConfig;
import dev.mlnr.blh.internal.utils.Checks;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotListHandler {
	private final Map<BotList, String> botLists;
	private final AutoPostingConfig autoPostingConfig;

	private static final Logger logger = LoggerFactory.getLogger(BotListHandler.class);
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	private final OkHttpClient httpClient = new OkHttpClient();

	private long previousGuildCount = -1;

	BotListHandler(Map<BotList, String> botListMap, AutoPostingConfig autoPostingConfig) {
		logger.info("map size {}", botListMap.size());
		this.botLists = botListMap;
		this.autoPostingConfig = autoPostingConfig;

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
	 * Used to hotswap invalid tokens at runtime.
	 *
	 * @param  botList
	 *         The bot list to replace the token for
	 * @param  newToken
	 *         The new token to use for the provided bot list
	 *
	 * @throws IllegalArgumentException
	 *         If the provided bot list or token is {@code null} or empty
	 * @throws IllegalStateException
	 *         If the provided token is the same as the previous one
	 */
	public void swapToken(@Nonnull BotList botList, @Nonnull String newToken) {
		Checks.notNull(botList, "The bot list");
		Checks.notEmpty(newToken, "The bot list token");
		Checks.check(botLists.get(botList).equals(newToken), "The new token may not be the same as the previous one");

		botLists.put(botList, newToken);
	}

	// "internal" methods

	boolean isAutoPostingEnabled() {
		return autoPostingConfig.isAutoPostingEnabled();
	}

	void updateAllStats(JDA jda) {
		long serverCount = jda.getGuildCache().size();
		if (serverCount == previousGuildCount) {
			logger.info("No stats updating was necessary.");
			return;
		}
		previousGuildCount = serverCount;

		botLists.forEach((botList, token) -> updateStats(botList, token, jda, serverCount));
	}

	void updateStats(BotList botList, String token, JDA jda, long serverCount) {
		logger.info("updating for {}", botList.name());
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
				if (response.isSuccessful()) {
					logger.info("Successfully updated stats for bot list {}", botListName);
				}
				else {
					int code = response.code();
					if (code == 401) {
						logger.error("Failed to update stats for bot list {} as the provided token is invalid." +
								"You can hotswap the token by calling swapToken on the BotListHandler instance.", botListName);
						return;
					}
					if (code == 429) {
						logger.error("Failed to update stats for bot list {} as we got ratelimited. Retrying in 15 seconds", botListName);
						SCHEDULER.schedule(() -> updateStats(botList, token, jda, serverCount), 15, TimeUnit.SECONDS);
						return;
					}
					logger.error("Failed to update stats for bot list {} with code {}", botListName, code);
				}
				response.close();
			}
		});
	}
}