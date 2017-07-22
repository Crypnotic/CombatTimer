package me.crypnotic.combattimer.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Messenger {

	public static void sendMessage(CommandSender target, String message) {
		target.sendMessage(color(message));
	}

	public static void sendActionBar(Player player, String text) {
		TextComponent message = new TextComponent(TextComponent.fromLegacyText(color(text)));

		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
	}

	private static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
