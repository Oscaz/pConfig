package dev.oscaz.pconfig.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemUtil {
	
	public static ItemStack createItem(Material material, String name, int amount, int data, String... lore) {
		ItemStack item = new ItemStack(material, amount, (short) data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		meta.setLore(color(Lists.newArrayList(lore)));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material material, String name, String... lore) {
		return createItem(material, name, 1, 0, lore);
	}
	
	public static ItemStack createItem(Material material, String name, int amount, String... lore) {
		return createItem(material, name, amount, 1, lore);
	}
	
	public static ItemStack createItem(Material material, String name, int amount, int data, List<String> lore) {
		return createItem(material, name, amount, data, lore.toArray(new String[lore.size()]));
	}
	
	public static ItemStack createItem(Material material, String name, List<String> lore) {
		return createItem(material, name, lore.toArray(new String[lore.size()]));
	}
	
	public static ItemStack modifyItem(ItemStack item, ItemMeta meta) {
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack modifyItem(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		return modifyItem(item, meta);
	}
	
	public static ItemStack modifyItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		meta.setLore(color(lore));
		return modifyItem(item, meta);
	}
	
	private static String color(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	private static List<String> color(List<String> messages, ChatColor color) {
		return messages.stream().map(str -> color + str).collect(Collectors.toList());
	}
	
	private static List<String> color(List<String> messages) {
		return messages.stream().map(ItemUtil::color).collect(Collectors.toList());
	}
	
}
