package dev.oscaz.pconfig.api;

import java.util.Optional;
import java.util.Set;

public interface ConfigManager {
	
	SubConfigNode manageSubNode(String key);
	
	SubConfigNode createSubNode(String key);
	
	Optional<SubConfigNode> getSubNode(String key);
	
	<T> ConfigNode<T> createNode(String key, T value);

	<T> ConfigNode<T> manage(String key, T def);
	
	<T> ConfigNode<T> manage(String key, Class<T> type, T def);
	
	<T> ConfigNode<T> forceManage(String key, T def);
	
	<T> ConfigNode<T> forceManage(String key, Class<T> type, T val);
	
	<T> Optional<ConfigNode<T>> get(String key, Class<T> type);
	
	Set<SubConfigNode> getSubNodes();
	Set<ConfigNode<?>> getNodes();
	
	int getChildren();
	
}
