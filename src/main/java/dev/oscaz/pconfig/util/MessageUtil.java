package dev.oscaz.pconfig.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.stream.Collectors;

public class MessageUtil {
	
	public static void sendMessage(LivingEntity entity, String message) {
		entity.sendMessage(color(message));
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(color(message));
	}
	
	public static void sendMessage(CommandSender sender, List<String> messages) {
		sender.sendMessage(color(messages).toArray(new String[messages.size()]));
	}
	
	private static String color(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static List<String> color(List<String> messages, ChatColor color) {
		return messages.stream().map(str -> color + str).collect(Collectors.toList());
	}
	
	public static List<String> color(List<String> messages) {
		return messages.stream().map(MessageUtil::color).collect(Collectors.toList());
	}
	
}
