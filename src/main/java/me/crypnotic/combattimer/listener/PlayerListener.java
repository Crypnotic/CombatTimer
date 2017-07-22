package me.crypnotic.combattimer.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.crypnotic.combattimer.CombatTimer;
import me.crypnotic.combattimer.api.CombatPlayer;
import me.crypnotic.combattimer.manager.CombatManager;
import me.crypnotic.combattimer.manager.ConfigManager;
import me.crypnotic.combattimer.util.Messenger;

public class PlayerListener implements Listener {

	private ConfigManager configManager;
	private CombatManager combatManager;

	public PlayerListener(CombatTimer plugin) {
		this.configManager = plugin.getConfigManager();
		this.combatManager = plugin.getCombatManager();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		CombatPlayer player = combatManager.get(event.getPlayer().getUniqueId());
		if (player != null) {
			player.getHandle().setHealth(0.0);
			combatManager.remove(player.getUuid());
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		CombatPlayer player = combatManager.get(event.getEntity().getUniqueId());
		if (player != null) {
			combatManager.remove(player.getUuid());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if (damaged instanceof Player) {
			if (damager instanceof Player) {
				if (damager.equals(damaged)) {
					return;
				}
				if (!damaged.hasPermission(configManager.getBypassPermission())) {
					combatManager.setTagged((Player) damaged, configManager.getCombatTime());
				}
				if (!damager.hasPermission(configManager.getBypassPermission())) {
					combatManager.setTagged((Player) damager, configManager.getCombatTime());
				}
				return;
			}
			if (damager instanceof Projectile) {
				Projectile projectile = (Projectile) damager;
				if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
					if (projectile.getShooter().equals(damaged)) {
						return;
					}
					if (!damaged.hasPermission(configManager.getBypassPermission())) {
						combatManager.setTagged((Player) damaged, configManager.getCombatTime());
					}
					if (!((Player) projectile.getShooter()).hasPermission(configManager.getBypassPermission())) {
						combatManager.setTagged((Player) projectile.getShooter(), configManager.getCombatTime());
					}
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		CombatPlayer player = combatManager.get(event.getPlayer().getUniqueId());
		if (player != null) {
			String[] array = event.getMessage().split(" ")[0].split(":");
			String command = array[array.length - 1].replaceFirst("/", "");
			if (!configManager.getCommandWhitelist().contains(command)) {
				Messenger.sendMessage(player.getHandle(), configManager.getMessage("command-blacklisted"));
				event.setCancelled(true);
			}
		}
	}
}