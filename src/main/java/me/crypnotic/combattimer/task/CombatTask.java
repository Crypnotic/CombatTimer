package me.crypnotic.combattimer.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.crypnotic.combattimer.CombatTimer;
import me.crypnotic.combattimer.api.CombatPlayer;
import me.crypnotic.combattimer.manager.CombatManager;

public class CombatTask implements Runnable {

	private CombatManager combatManager;

	public CombatTask(CombatTimer plugin) {
		this.combatManager = plugin.getCombatManager();
	}

	public void run() {
		List<UUID> untagged = new ArrayList<UUID>();
		for (CombatPlayer player : combatManager.getTagged().values()) {
			if (player.getCombatTime() > 0) {
				player.setCombatTime(player.getCombatTime() - 1);

				combatManager.sendCombatActionBar(player.getUuid(), "player-combattime");
			} else {
				combatManager.sendCombatActionBar(player.getUuid(), "player-untagged");
				
				untagged.add(player.getUuid());
			}
		}
		for (UUID uuid : untagged) {
			combatManager.remove(uuid);
		}
		untagged.clear();
	}
}
