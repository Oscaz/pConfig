package dev.oscaz.pconfig;

import dev.oscaz.pconfig.api.RootConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicConfigurationManager {
	
	public static RootConfigManager get(JavaPlugin plugin) {
		return new CommonConfigManager(plugin);
	}
	
	public static RootConfigManager get(JavaPlugin plugin, String fileName) {
		return new CommonConfigManager(plugin, fileName);
	}
	
	public static RootConfigManager get(JavaPlugin plugin, String fileName, String extension) {
		return new CommonConfigManager(plugin, fileName, extension);
	}
	
}
