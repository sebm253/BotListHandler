package dev.mlnr.blh.core.internal.config;

import dev.mlnr.blh.core.api.IBLHUpdater;

public class AutoPostingConfig {
	private final IBLHUpdater updater;
	private final long initialDelay;
	private final long delay;

	public AutoPostingConfig(IBLHUpdater updater, long initialDelay, long delay) {
		this.updater = updater;
		this.initialDelay = initialDelay;
		this.delay = delay;
	}

	public boolean isAutoPostingEnabled() {
		return updater != null;
	}

	public IBLHUpdater getUpdater() {
		return updater;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public long getDelay() {
		return delay;
	}
}