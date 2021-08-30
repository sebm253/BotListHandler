package dev.mlnr.blh.core.internal.config;

public class LoggingConfig {
	private final boolean successLoggingEnabled;
	private final boolean noUpdateNecessaryLoggingEnabled;
	private final boolean ratelimitedLoggingEnabled;
	private final int errorThreshold;

	public LoggingConfig(boolean successLoggingEnabled, boolean noUpdateNecessaryLoggingEnabled, boolean ratelimitedLoggingEnabled, int errorThreshold) {
		this.successLoggingEnabled = successLoggingEnabled;
		this.noUpdateNecessaryLoggingEnabled = noUpdateNecessaryLoggingEnabled;
		this.ratelimitedLoggingEnabled = ratelimitedLoggingEnabled;
		this.errorThreshold = errorThreshold;
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

	public int getErrorThreshold() {
		return errorThreshold;
	}
}