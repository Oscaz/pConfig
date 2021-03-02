package dev.oscaz.pconfig;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public enum InputType {
	BYTE("&aYou are setting a &fWhole number (-127, 128)&a for the configuration value &f%node%&a.", Byte.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.of(Byte.parseByte(s));
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
			}
	)),
	SHORT("&aYou are setting a &fWhole number (-32768, 32767)&a for the configuration value &f%node%&a.", Short.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.of(Short.parseShort(s));
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
			}
	)),
	INTEGER("&aYou are setting a &fWhole number (-2B, 2B)&a for the configuration value &f%node%&a.", Integer.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.of(Integer.parseInt(s));
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
			}
	)),
	LONG("&aYou are setting a &fWhole number&a for the configuration value &f%node%&a.", Long.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.of(Long.parseLong(s));
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
			}
	)),
	FLOAT("&aYou are setting a &fDecimal&a for the configuration value &f%node%&a.", Float.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.of(Float.parseFloat(s));
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
			}
	)),
	DOUBLE("&aYou are setting a &fDecimal&a for the configuration value &f%node%&a.", Double.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.of(Double.parseDouble(s));
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
			}
	)),
	STRING("&aYou are setting a &fString&a for the configuration value &f%node%&a.", String.class, new InputTypeParser<>(
			(p, s) -> Optional.of(s)
	)),
	CHARACTER("&aYou are setting a &fSingular character&a for the configuration value &f%node%&a.", Character.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.of(s.charAt(0));
				} catch (NullPointerException | IndexOutOfBoundsException e) {
					return Optional.empty();
				}
			}
	)),
	LOCATION("&aYou are setting a &fLocation&a for the configuration value &f%node%&a. Type any message to confirm your current location.", Location.class, new InputTypeParser<>(
			(p, s) -> {
				try {
					return Optional.ofNullable(p.getLocation());
				} catch (NullPointerException e) {
					return Optional.empty();
				}
			}
	));
	
	private final String message;
	private final Class<?> type;
	private final InputTypeParser<?> parser;
	
	<T> InputType(String message, Class<T> type, InputTypeParser<T> parser) {
		this.message = message;
		this.type = type;
		this.parser = parser;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Class<?> getType() {
		return this.type;
	}
	
	<T> InputTypeParser<T> getParser() {
		return (InputTypeParser<T>) parser;
	}
	
	<T> Optional<T> parse(Player player, String val) {
		return (Optional<T>) this.parser.getParser().apply(player, val);
	}
}
