package dev.oscaz.pconfig.api;

import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

public interface ConfigurationGUI extends InventoryProvider {
	
	void open(Player player);
	
}
