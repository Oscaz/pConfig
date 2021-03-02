package dev.oscaz.pconfig.api;

import dev.oscaz.pconfig.InputType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConfigNode<T> extends Supplier<T> {

	String getKey();
	
	T getReal();
	
	void set(T value);
	
	void setTemporary(T value);
	
	@Nullable T getTemporary();
	
	boolean hasTemporary();
	
	Optional<InputType> getInputType();
	
	void addListener(Consumer<ConfigurationChangeEvent<T>> listener);
	
}
