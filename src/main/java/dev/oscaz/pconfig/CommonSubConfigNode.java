package dev.oscaz.pconfig;

import dev.oscaz.pconfig.api.ConfigNode;
import dev.oscaz.pconfig.api.SubConfigNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class CommonSubConfigNode implements SubConfigNode, ConfigurationSerializable {
	
	private final String key;
	
	// lazily initialise to save the dedicated wam
	private Map<String, CommonSubConfigNode> subnodes;
	private Map<String, CommonConfigNode<?>> nodes;
	
	protected CommonSubConfigNode(String key) {
		this.key = key;
		this.subnodes = null;
		this.nodes = null;
	}
	
	protected void kill() {
		this.subnodes = null;
		this.nodes = null;
	}
	
	@Override
	public SubConfigNode manageSubNode(String key) {
		this.ensureSubMap();
		return this.getSubNode(key).orElseGet(() -> this.createSubNode(key));
	}
	
	@Override
	public SubConfigNode createSubNode(String key) {
		this.ensureSubMap();
		CommonSubConfigNode node = new CommonSubConfigNode(key);
		this.subnodes.put(node.getKey(), node);
		return node;
	}
	
	@Override
	public Optional<SubConfigNode> getSubNode(String key) {
		if (this.subnodes == null) return Optional.empty();
		return Optional.ofNullable(this.subnodes.get(key));
	}
	
	@Override
	public <T> ConfigNode<T> createNode(String key, T value) {
		this.ensureMap();
		
		CommonConfigNode<T> node = new CommonConfigNode<>(key, value);
		this.nodes.put(key, node);
		
		return node;
	}
	
	@Override
	public <T> ConfigNode<T> manage(String key, T def) {
		return this.manage(key, (Class<T>) def.getClass(), def);
	}
	
	@Override
	public <T> ConfigNode<T> manage(String key, Class<T> type, T def) {
		this.ensureMap();
		
		return this.get(key, type).orElseGet(() -> this.createNode(key, def));
	}
	
	@Override
	public <T> ConfigNode<T> forceManage(String key, T def) {
		return this.forceManage(key, (Class<T>) def.getClass(), def);
	}
	
	@Override
	public <T> Optional<ConfigNode<T>> get(String key, Class<T> type) {
		if (this.nodes == null) return Optional.empty();
		ConfigNode node = this.nodes.get(key);
		try {
			if (node == null) return Optional.empty();
			Class<?> nodeType = node.getReal().getClass();
			
			if (type.isAssignableFrom(nodeType)) {
				return Optional.ofNullable((ConfigNode<T>) node);
			} else if ((node.getReal() instanceof Iterable) && Collection.class.isAssignableFrom(type)) {
				Collection collection = (Collection<?>) type.newInstance();
				((Iterable<?>) node.getReal()).forEach(collection::add);
				node.set(collection);
				return Optional.ofNullable((ConfigNode<T>) node);
			} else {
				return Optional.empty();
			}
		} catch (ClassCastException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	@Override
	public Set<SubConfigNode> getSubNodes() {
		return this.subnodes == null ? Sets.newHashSet() : Sets.newHashSet(this.subnodes.values());
	}
	
	@Override
	public Set<ConfigNode<?>> getNodes() {
		return this.nodes == null ? Sets.newHashSet() : Sets.newHashSet(this.nodes.values());
	}
	
	@Override
	public <T> ConfigNode<T> forceManage(String key, Class<T> type, T val) {
		this.ensureSubMap();
		ConfigNode<T> node = this.get(key, type).orElseGet(() -> this.createNode(key, val));
		node.set(val);
		return node;
	}
	
	private void ensureMap() {
		if (this.nodes == null) this.nodes = Maps.newHashMap();
	}
	
	private void ensureSubMap() {
		if (this.subnodes == null) this.subnodes = Maps.newHashMap();
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public int getChildren() {
		int children = 0;
		if (this.nodes != null) children += this.nodes.size();
		if (this.subnodes != null) children += this.subnodes.size();
		return children;
	}
	
	
	@Override
	public Map<String, Object> serialize() {
		if (this.nodes == null && this.subnodes == null) return Maps.newHashMap();
		Map<String, Object> serialized = Maps.newHashMap();
		
		if (this.nodes != null) this.nodes.forEach(serialized::put);
		if (this.subnodes != null) this.subnodes.forEach(serialized::put);
		
		return serialized;
	}
	
	@Override
	public String toString() {
		return "CommonSubConfigNode{" +
				"key='" + key + '\'' +
				", subnodes=" + subnodes +
				", nodes=" + nodes +
				'}';
	}
}
