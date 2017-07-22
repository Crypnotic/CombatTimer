package me.crypnotic.combattimer.api;

import java.util.Optional;
import java.util.UUID;

import me.crypnotic.combattimer.CombatTimer;

public class CombatTimerAPI {

	private static CombatTimer getPlugin() {
		return CombatTimer.getInstance();
	}

	public static Optional<CombatPlayer> getCombatPlayer(UUID uuid) {
		return Optional.of(getPlugin().getCombatManager().get(uuid));
	}

	public static boolean isTagged(UUID uuid) {
		return getPlugin().getCombatManager().isTagged(uuid);
	}
}
