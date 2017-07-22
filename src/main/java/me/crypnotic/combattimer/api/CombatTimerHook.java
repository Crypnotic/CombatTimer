package me.crypnotic.combattimer.api;

import java.util.Optional;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.crypnotic.combattimer.CombatTimer;

public class CombatTimerHook extends EZPlaceholderHook {

	public CombatTimerHook(CombatTimer plugin) {
		super(plugin, "combattimer");
	}

	@Override
	public String onPlaceholderRequest(Player player, String key) {
		if (key.equals("combattime")) {
			Optional<CombatPlayer> optional = CombatTimerAPI.getCombatPlayer(player.getUniqueId());
			if (optional.isPresent()) {
				return optional.get().getCombatTime().toString();
			}
		}
		return null;
	}
}
