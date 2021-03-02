package dev.oscaz.pconfig;

import com.google.common.collect.Maps;
import dev.oscaz.pconfig.api.ConfigNode;
import dev.oscaz.pconfig.api.ConfigurationGUI;
import dev.oscaz.pconfig.api.RootConfigManager;
import dev.oscaz.pconfig.api.SubConfigNode;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommonConfigManager implements RootConfigManager, Listener {
	
	private final JavaPlugin plugin;
	private final InventoryManager inventoryManager;
	private final String fileName;
	private final String fileExtension;
	
	private CommonSubConfigNode rootNode;
	
	CommonConfigManager(JavaPlugin plugin) {
		this(plugin, "configuration");
	}
	
	CommonConfigManager(JavaPlugin plugin, String name) {
		this(plugin, name, "yml");
	}
	
	CommonConfigManager(JavaPlugin plugin, String fileName, String fileExtension) {
		this.plugin = plugin;
		this.inventoryManager = new InventoryManager(this.plugin);
		this.inventoryManager.init();
		this.fileName = fileName;
		this.fileExtension = fileExtension;
		this.rootNode = new CommonSubConfigNode("");
		this.rootNode.kill();
		
		this.load();
		
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public SubConfigNode manageSubNode(String key) {
		return this.rootNode.manageSubNode(key);
	}
	
	@Override
	public SubConfigNode createSubNode(String key) {
		return this.rootNode.createSubNode(key);
	}
	
	@Override
	public Optional<SubConfigNode> getSubNode(String key) {
		return this.rootNode.getSubNode(key);
	}
	
	@Override
	public <T> ConfigNode<T> createNode(String key, T value) {
		return this.rootNode.createNode(key, value);
	}
	
	@Override
	public <T> ConfigNode<T> manage(String key, T def) {
		return this.manage(key, (Class<T>) def.getClass(), def);
	}
	
	@Override
	public <T> ConfigNode<T> manage(String key, Class<T> type, T def) {
		return this.rootNode.manage(key, type, def);
	}
	
	@Override
	public <T> ConfigNode<T> forceManage(String key, T def) {
		return this.forceManage(key, (Class<T>) def.getClass(), def);
	}
	
	@Override
	public <T> Optional<ConfigNode<T>> get(String key, Class<T> type) {
		return this.rootNode.get(key, type);
	}
	
	@Override
	public Set<SubConfigNode> getSubNodes() {
		return this.rootNode.getSubNodes();
	}
	
	@Override
	public Set<ConfigNode<?>> getNodes() {
		return this.rootNode.getNodes();
	}
	
	@Override
	public int getChildren() {
		return this.rootNode.getChildren();
	}
	
	@Override
	public void reload() {
		this.load();
	}
	
	@Override
	public File getFile() {
		try {
			File file = new File(this.plugin.getDataFolder() + File.separator + this.fileName + "." + this.fileExtension);
			
			if (!file.exists()) {
				if (!this.plugin.getDataFolder().exists()) {
					this.plugin.getDataFolder().mkdirs();
				}
				file.createNewFile();
			}
			return file;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void load() {
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(this.getFile());
		
		Map<String, Object> deserialized = Maps.newHashMap();
		configuration.getKeys(false).forEach(rootKey -> {
			deserialized.putAll(this.recursiveDeserialization(configuration, rootKey, rootKey));
		});
		this.recursiveManage(this.rootNode, deserialized);
	}
	
	@Override
	public <T> ConfigNode<T> forceManage(String key, Class<T> type, T val) {
		return this.rootNode.forceManage(key, type, val);
	}
	
	private void recursiveManage(SubConfigNode masterNode, Map<String, Object> map) {
		map.forEach((key, val) -> {
			if (val instanceof Map) {
				this.recursiveManage(masterNode.manageSubNode(key), (Map<String, Object>) val);
			} else {
				this.manage(masterNode, key, val);
			}
		});
	}
	
	private <T> ConfigNode<T> manage(SubConfigNode masterNode, String key, T val) {
		return masterNode.forceManage(key, (Class<T>) val.getClass(), val);
	}
	
	private Map<String, Object> recursiveDeserialization(YamlConfiguration configuration, String path, String key) {
		Map<String, Object> deserialized = Maps.newHashMap();
		if (configuration.isConfigurationSection(path)) { // is a subnode, not regular node!
			ConfigurationSection section = configuration.getConfigurationSection(path);
			Map<String, Object> results = Maps.newHashMap();
			section.getKeys(false).forEach(deepKey -> {
				results.putAll(this.recursiveDeserialization(configuration, section.getCurrentPath() + "." + deepKey, deepKey));
			});
			deserialized.put(key, results);
		} else { // regular node.
			deserialized.put(key, configuration.get(path));
		}
		return deserialized;
	}
	
	@Override
	public void save() {
		try {
			YamlConfiguration configuration = YamlConfiguration.loadConfiguration(this.getFile());
			this.recursiveSerialization(this.rootNode).forEach(configuration::set);
			
			configuration.save(this.getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ConfigurationGUI getGUI() {
		return new CommonConfigurationGUI(this);
	}
	
	@Override
	public JavaPlugin getPlugin() {
		return this.plugin;
	}
	
	@Override
	public InventoryManager getInventoryManager() {
		return this.inventoryManager;
	}
	
	@Override
	public SubConfigNode getRootNode() {
		return this.rootNode;
	}
	
	private Map<String, Object> recursiveSerialization(CommonSubConfigNode node) {
		Map<String, Object> config = Maps.newHashMap();
		node.serialize().forEach((nodeKey, nodeValue) -> {
			if (nodeValue instanceof CommonSubConfigNode) config.put(nodeKey, this.recursiveSerialization((CommonSubConfigNode) nodeValue));
			else if (nodeValue instanceof CommonConfigNode) {
				CommonConfigNode<?> targetNode = (CommonConfigNode<?>) nodeValue;
				
				if (!(targetNode.getReal() instanceof ArrayList) && (targetNode.getReal() instanceof Collection)) {
					ArrayList list = new ArrayList();
					((Iterable<?>) targetNode.getReal()).forEach(list::add);
					config.put(nodeKey, list);
				} else {
					config.put(nodeKey, targetNode.getReal());
				}
				
				
			}
		});
		
		return config;
	}
}
