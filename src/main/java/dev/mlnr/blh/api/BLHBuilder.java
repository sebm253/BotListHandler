package dev.mlnr.blh.api;

import dev.mlnr.blh.internal.BLHImpl;
import dev.mlnr.blh.internal.objects.AutoPostingConfig;
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

	public BLHBuilder() {}

	public BLHBuilder(JDA jda) {
		this.jda = jda;
	}

	public BLHBuilder(@Nonnull Map<BotList, String> botLists) {
		setBotLists(botLists);
		this.botLists = botLists;
	}

	public void addBotList(@Nonnull BotList botList, @Nonnull String token) {
		Checks.notNull(botList, "The bot list");
		Checks.notEmpty(token, "The bot list token");

		botLists.put(botList, token);
	}

	public void setBotLists(@Nonnull Map<BotList, String> botLists) {
		Checks.notEmpty(botLists, "The bot lists map");
	}

	public void setAutoPostDelay(long delay, @Nonnull TimeUnit unit) {
		Checks.notNull(this.jda, "The JDA object");
		Checks.check(delay < 1, "The delay cannot be less than 1");
		Checks.notNull(unit, "The time unit");
		Checks.check(unit.ordinal() < TimeUnit.SECONDS.ordinal(), "The time unit cannot be smaller than seconds");

		this.autoPostDelay = delay;
		this.autoPostUnit = unit;
	}

	public BotListHandler build() {
		Checks.notEmpty(botLists, "The bot lists map");

		return new BLHImpl(botLists, new AutoPostingConfig(jda, autoPostDelay, autoPostUnit));
	}
}