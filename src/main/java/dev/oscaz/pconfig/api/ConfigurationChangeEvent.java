package dev.oscaz.pconfig.api;

public interface ConfigurationChangeEvent<T> {

	ConfigNode<T> getNode();
	
	T getOldValue();
	
	T getNewValue();

}
