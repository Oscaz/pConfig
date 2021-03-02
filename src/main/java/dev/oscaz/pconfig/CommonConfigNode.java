package dev.oscaz.pconfig;

import dev.oscaz.pconfig.api.ConfigNode;
import dev.oscaz.pconfig.api.ConfigurationChangeEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class CommonConfigNode<T> implements ConfigNode<T>, ConfigurationSerializable {
	
	private transient Set<Consumer<ConfigurationChangeEvent<T>>> listeners;
	
	private final String key;
	private T value;
	private transient T temporaryValue = null;
	
	protected CommonConfigNode(String key, T value) {
		this.listeners = null;
		
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public void set(T value) {
		if (this.listeners != null) {
			ConfigurationChangeEvent<T> event = new CommonConfigurationChangeEvent<>(this, this.get(), value);
			this.listeners.forEach(listener -> listener.accept(event));
		}
		this.value = value;
	}
	
	@Override
	public void setTemporary(T value) {
		this.temporaryValue = value;
	}
	
	@Override
	public @Nullable T getTemporary() {
		return this.temporaryValue;
	}
	
	@Override
	public boolean hasTemporary() {
		return this.temporaryValue != null;
	}
	
	@Override
	public T getReal() {
		return this.value;
	}
	
	@Override
	public T get() {
		return this.temporaryValue == null ? this.value : this.temporaryValue;
	}
	
	@Override
	public Optional<InputType> getInputType() {
		if (this.value.getClass().equals(Byte.class)) return Optional.of(InputType.BYTE);
		if (this.value.getClass().equals(Short.class)) return Optional.of(InputType.SHORT);
		if (this.value.getClass().equals(Integer.class)) return Optional.of(InputType.INTEGER);
		if (this.value.getClass().equals(Long.class)) return Optional.of(InputType.LONG);
		if (this.value.getClass().equals(Float.class)) return Optional.of(InputType.FLOAT);
		if (this.value.getClass().equals(Double.class)) return Optional.of(InputType.DOUBLE);
		if (this.value.getClass().equals(String.class)) return Optional.of(InputType.STRING);
		if (this.value.getClass().equals(Character.class)) return Optional.of(InputType.CHARACTER);
		if (this.value.getClass().equals(Location.class)) return Optional.of(InputType.LOCATION);
		
		return Optional.empty();
	}
	
	@Override
	public void addListener(Consumer<ConfigurationChangeEvent<T>> listener) {
		if (this.listeners == null) this.listeners = Sets.newHashSet();
		this.listeners.add(listener);
	}
	
	@Override
	public String toString() {
		return this.value.toString();
	}
	
	public String toProgrammaticString() {
		return "CommonConfigNode{" +
				", key='" + key + '\'' +
				", value=" + value +
				'}';
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialized = Maps.newHashMap();
		serialized.put(this.key, this.getReal());
		return serialized;
	}
}
