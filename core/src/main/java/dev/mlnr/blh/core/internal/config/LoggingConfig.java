package dev.mlnr.blh.core.internal.config;

public class LoggingConfig {
	private final boolean successLoggingEnabled;
	private final boolean noUpdateNecessaryLoggingEnabled;
	private final boolean ratelimitedLoggingEnabled;

	public LoggingConfig(boolean successLoggingEnabled, boolean noUpdateNecessaryLoggingEnabled, boolean ratelimitedLoggingEnabled) {
		this.successLoggingEnabled = successLoggingEnabled;
		this.noUpdateNecessaryLoggingEnabled = noUpdateNecessaryLoggingEnabled;
		this.ratelimitedLoggingEnabled = ratelimitedLoggingEnabled;
	}

	public boolean isSuccessLoggingEnabled() {
		return successLoggingEnabled;
	}

	public boolean isNoUpdateNecessaryLoggingEnabled() {
		return noUpdateNecessaryLoggingEnabled;
	}

	public boolean isRatelimitedLoggingEnabled() {
		return ratelimitedLoggingEnabled;
	}
}