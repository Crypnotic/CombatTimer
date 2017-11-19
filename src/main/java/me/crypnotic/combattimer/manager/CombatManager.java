package me.crypnotic.combattimer.manager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import me.crypnotic.combattimer.CombatTimer;
import me.crypnotic.combattimer.api.CombatPlayer;
import me.crypnotic.combattimer.task.CombatTask;
import me.crypnotic.combattimer.util.Messenger;

public class CombatManager {

    private CombatTimer plugin;
    private ConfigManager configManager;
    @Getter
    private HashMap<UUID, CombatPlayer> tagged;
    private BukkitTask timer;

    public CombatManager(CombatTimer plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.tagged = new HashMap<UUID, CombatPlayer>();
    }

    public CombatPlayer get(UUID uuid) {
        return tagged.get(uuid);
    }

    public void setTagged(Player player, Long time) {
        UUID uuid = player.getUniqueId();
        if (isTagged(uuid)) {
            get(uuid).setCombatTime(time);
            Messenger.sendActionBar(player, configManager.getMessage("player-reset"));
            checkTimer();
        } else {
            tagged.put(uuid, new CombatPlayer(uuid, time, player.getAllowFlight()));
            Messenger.sendActionBar(player, configManager.getMessage("player-tagged"));
            checkTimer();
        }

        if (configManager.isRestrictFlight() && player.isFlying()) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    public void sendCombatMessage(UUID uuid, String key) {
        String message = configManager.getMessage(key);
        if (message == null || key == null) {
            return;
        }
        message = message.replace("{time}", "" + (tagged.containsKey(uuid) ? tagged.get(uuid).getCombatTime() : 0));

        Messenger.sendMessage(Bukkit.getPlayer(uuid), message);
    }

    public void sendCombatActionBar(UUID uuid, String key) {
        String message = configManager.getMessage(key);
        if (message == null || key == null) {
            return;
        }
        message = message.replace("{time}", "" + (tagged.containsKey(uuid) ? tagged.get(uuid).getCombatTime() : 0));

        Messenger.sendActionBar(Bukkit.getPlayer(uuid), message);
    }

    private void checkTimer() {
        if (timer == null && tagged.size() >= 1) {
            timer = plugin.getServer().getScheduler().runTaskTimer(plugin, new CombatTask(plugin), 0L, 20L);
        }
        if (timer != null && tagged.size() <= 0) {
            timer.cancel();
            timer = null;
        }
    }

    public void remove(UUID uuid) {
        tagged.remove(uuid);
        checkTimer();
    }

    public boolean isTagged(UUID uuid) {
        return tagged.containsKey(uuid);
    }
}
