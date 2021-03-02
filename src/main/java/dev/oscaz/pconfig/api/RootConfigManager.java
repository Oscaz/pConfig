package dev.oscaz.pconfig.api;

import fr.minuskube.inv.InventoryManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public interface RootConfigManager extends ConfigManager {
	
	void reload();
	
	File getFile();
	void load();
	void save();
	
	ConfigurationGUI getGUI();
	JavaPlugin getPlugin();
	InventoryManager getInventoryManager();
	SubConfigNode getRootNode();
	
}
