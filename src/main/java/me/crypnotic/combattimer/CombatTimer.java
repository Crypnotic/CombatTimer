package me.crypnotic.combattimer;

import org.bstats.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import lombok.Getter;
import me.crypnotic.combattimer.api.CombatPlayer;
import me.crypnotic.combattimer.api.CombatTimerHook;
import me.crypnotic.combattimer.listener.PlayerListener;
import me.crypnotic.combattimer.manager.CombatManager;
import me.crypnotic.combattimer.manager.ConfigManager;
import me.crypnotic.combattimer.util.Messenger;

public class CombatTimer extends JavaPlugin {

	@Getter
	private static CombatTimer instance;
	@Getter
	private ConfigManager configManager;
	@Getter
	private CombatManager combatManager;

	@Override
	public void onLoad() {
		CombatTimer.instance = this;

		this.configManager = new ConfigManager(this, getDataFolder(), "config.yml");
		this.combatManager = new CombatManager(this);

		if (!configManager.init()) {
			getLogger().warning("Failed to initialize Configuration! Shutting down.");
			setEnabled(false);
			return;
		}
	}

	@Override
	public void onEnable() {
		configManager.load();

		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

		if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			if (new CombatTimerHook(this).hook()) {
				getLogger().info("Successfully registered PlaceholderAPI hook.");
			} else {
				getLogger().warning("Failed to register PlaceholderAPI hook.");
			}
		}

		new Metrics(this);

		new SpigetUpdate(this, 22722).setVersionComparator(VersionComparator.SEM_VER)
				.checkForUpdate(new UpdateCallback() {
					@Override
					public void upToDate() {
						/* NOOP */
					}

					@Override
					public void updateAvailable(String version, String url, boolean direct) {
						getLogger().info("------------------------------------");
						getLogger().info("A new version of CombatTimer is available: " + version);
						if (direct) {
							getLogger().info("Download URL: " + url);
						}
						getLogger().info("------------------------------------");
					}
				});
	}

	@Override
	public void onDisable() {
		combatManager.getTagged().clear();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("combattimer.command.ct")) {
				CombatPlayer player = combatManager.get(((Player) sender).getUniqueId());
				if (args.length == 0) {
					if (player != null) {
						combatManager.sendCombatMessage(player.getUuid(), "player-combattime");
					} else {
						Messenger.sendMessage(sender, "You are not currently in combat");
					}
				} else {
					if (sender.hasPermission("combattimer.command.ct.admin")) {
						String task = args[0].toLowerCase();
						if (task.equals("version") || task.equals("ver") || task.equals("v")) {
							Messenger.sendMessage(sender, "Version: " + getDescription().getVersion());
						} else if (task.equals("reload") || task.equals("rl")) {
							configManager.reload();
							configManager.load();
							Messenger.sendMessage(sender, "Config file has been reloaded");
						} else {
							Messenger.sendMessage(sender, "Usage: /ct (help, reload, version)");
						}
					} else {
						Messenger.sendMessage(sender, "&oPermission denied!");
					}
				}
			} else {
				Messenger.sendMessage(sender, "&oPermission denied!");
			}
		} else {
			Messenger.sendMessage(sender, "Only players can use this command!");
		}
		return true;
	}
}
