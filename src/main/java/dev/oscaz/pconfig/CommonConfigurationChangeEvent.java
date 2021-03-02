package dev.oscaz.pconfig;

import dev.oscaz.pconfig.api.ConfigNode;
import dev.oscaz.pconfig.api.ConfigurationChangeEvent;

public class CommonConfigurationChangeEvent<T> implements ConfigurationChangeEvent<T> {
	
	private final ConfigNode<T> node;
	private final T oldValue;
	private final T newValue;
	
	protected CommonConfigurationChangeEvent(ConfigNode<T> node, T oldValue, T newValue) {
		this.node = node;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	@Override
	public ConfigNode<T> getNode() {
		return this.node;
	}
	
	@Override
	public T getOldValue() {
		return this.oldValue;
	}
	
	@Override
	public T getNewValue() {
		return this.newValue;
	}
}
