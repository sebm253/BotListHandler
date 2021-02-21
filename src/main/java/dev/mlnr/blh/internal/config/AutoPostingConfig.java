package dev.mlnr.blh.internal.config;

import net.dv8tion.jda.api.JDA;

import java.util.concurrent.TimeUnit;

public class AutoPostingConfig {
	private final JDA jda;
	private final long delay;
	private final TimeUnit unit;

	public AutoPostingConfig(JDA jda, long delay, TimeUnit unit) {
		this.jda = jda;
		this.delay = delay;
		this.unit = unit;
	}

	public boolean isAutoPostingEnabled() {
		return jda != null;
	}

	public JDA getJDA() {
		return jda;
	}

	public long getDelay() {
		return delay;
	}

	public TimeUnit getUnit() {
		return unit;
	}
}