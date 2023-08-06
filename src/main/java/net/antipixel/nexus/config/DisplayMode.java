package net.antipixel.nexus.config;

import lombok.AllArgsConstructor;

/**
 * Display options for the nexus menu
 * @author Antipixel
 */
@AllArgsConstructor
public enum DisplayMode
{
	NEXUS_MAP("Map menu"),
	DEFAULT_MENU("Standard menu"),
	REMEMBER_PREVIOUS("Remember previous");

	private String value;

	@Override
	public String toString()
	{
		return this.value;
	}
}
