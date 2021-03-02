package dev.oscaz.pconfig;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class InputTypeParser<T> {
	
	private final BiFunction<Player, String, Optional<T>> parser;
	
	public InputTypeParser(BiFunction<Player, String, Optional<T>> parser) {
		this.parser = parser;
	}
	
	public BiFunction<Player, String, Optional<T>> getParser() {
		return this.parser;
	}
	
}
