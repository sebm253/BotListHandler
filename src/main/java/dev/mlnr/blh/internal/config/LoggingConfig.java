package dev.mlnr.blh.internal.config;

public class LoggingConfig {
	private final boolean successLoggingEnabled;
	private final boolean ratelimitedLoggingEnabled;

	public LoggingConfig(boolean successLoggingEnabled, boolean ratelimitedLoggingEnabled) {
		this.successLoggingEnabled = successLoggingEnabled;
		this.ratelimitedLoggingEnabled = ratelimitedLoggingEnabled;
	}

	public boolean isSuccessLoggingEnabled() {
		return successLoggingEnabled;
	}

	public boolean isRatelimitedLoggingEnabled() {
		return ratelimitedLoggingEnabled;
	}
}