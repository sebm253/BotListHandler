package dev.mlnr.blh.core.api;

/**
 * An interface to provide the bot id and the server amount for automatic stats posting.
 */
public interface IBLHUpdater {
	long getBotId();
	long getServerCount();
}