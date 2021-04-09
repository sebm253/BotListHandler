package dev.mlnr.blh.core.internal.config;

import dev.mlnr.blh.core.api.IBLHUpdater;

import java.util.concurrent.TimeUnit;

public class AutoPostingConfig {
	private final IBLHUpdater updater;
	private final long delay;
	private final TimeUnit unit;

	public AutoPostingConfig(IBLHUpdater updater, long delay, TimeUnit unit) {
		this.updater = updater;
		this.delay = delay;
		this.unit = unit;
	}

	public boolean isAutoPostingEnabled() {
		return updater != null;
	}

	public IBLHUpdater getUpdater() {
		return updater;
	}

	public long getDelay() {
		return delay;
	}

	public TimeUnit getUnit() {
		return unit;
	}
}