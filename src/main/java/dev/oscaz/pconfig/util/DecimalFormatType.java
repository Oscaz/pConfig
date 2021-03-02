package dev.oscaz.pconfig.util;

import java.text.DecimalFormat;

public enum DecimalFormatType {
	
	MONEY(new DecimalFormat("#,###.##")),
	SECONDS(new DecimalFormat("#.#")),
	PERCENTAGE(new DecimalFormat("#.##")),
	LOCATION(new DecimalFormat("#.##"));
	
	private DecimalFormat format;
	
	public String format(final Number value) {
		return this.format.format(value);
	}
	
	private DecimalFormatType(final DecimalFormat format) {
		this.format = format;
	}
	
	public DecimalFormat getFormat() {
		return this.format;
	}
	
}
