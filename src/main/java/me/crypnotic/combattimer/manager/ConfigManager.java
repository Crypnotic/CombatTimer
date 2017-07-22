package me.crypnotic.combattimer.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import me.crypnotic.combattimer.CombatTimer;

public class ConfigManager {

	private CombatTimer plugin;
	private File folder;
	private File file;
	private YamlConfiguration config;

	@Getter
	private String bypassPermission;
	@Getter
	private long combatTime;
	@Getter
	private List<String> commandWhitelist;
	private HashMap<String, String> messages;

	public ConfigManager(CombatTimer plugin, File folder, String name) {
		this.plugin = plugin;
		this.folder = folder;
		this.file = new File(folder, name);
	}

	public boolean init() {
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();

				InputStream input = plugin.getResource(file.getName());
				OutputStream output = new FileOutputStream(file);

				int count;
				byte[] buffer = new byte[1024];
				while ((count = input.read(buffer)) > 0) {
					output.write(buffer, 0, count);
				}

				input.close();
				output.close();
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}
		return reload();
	}

	public void load() {
		this.bypassPermission = config.getString("Permissions.bypass");
		this.combatTime = config.getLong("combattime");
		this.commandWhitelist = config.getStringList("command-whitelist");
		this.messages = new HashMap<String, String>();

		ConfigurationSection section = config.getConfigurationSection("Messages");
		for (String key : section.getKeys(false)) {
			messages.put(key, section.getString(key));
		}
	}

	public boolean reload() {
		try {
			config = YamlConfiguration.loadConfiguration(file);
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public String getMessage(String key, Object... values) {
		String message = messages.get(key);
		for (int i = 0; i < values.length; i++) {
			message.replace("{" + i + "}", values[i].toString());
		}
		return messages.get(key);
	}
}
