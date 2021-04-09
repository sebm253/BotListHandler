package dev.mlnr.blh.core.internal.utils;

import dev.mlnr.blh.core.api.BotList;

import java.util.Map;

public class Checks {
	private Checks() {}

	public static void check(boolean condition, String message) {
		if (condition)
			throw new IllegalStateException(message);
	}

	public static void notNull(Object o, String name) {
		if (o == null)
			throw new IllegalArgumentException(name + " may not be null");
	}

	public static void notEmpty(Map<?, ?> map, String name) {
		notNull(map, name);
		if (map.isEmpty())
			throw new IllegalArgumentException(name + " may not be empty");
	}

	public static void notEmpty(String string, String name) {
		notNull(string, name);
		if (string.isEmpty())
			throw new IllegalStateException(name + " may not be empty");
	}

	public static void checkListAndToken(BotList botList, String token) {
		notNull(botList, "The bot list");
		checkToken(token);
	}

	public static void checkToken(String token) {
		notEmpty(token, "The bot list token");
	}
}