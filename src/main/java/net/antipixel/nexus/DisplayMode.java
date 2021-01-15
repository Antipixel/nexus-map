package net.antipixel.nexus;

/**
 * Display options for the nexus menu
 * @author Antipixel
 */
public enum DisplayMode
{
	NEXUS_MAP("Map menu"),
	DEFAULT_MENU("Standard menu"),
	REMEMBER_PREVIOUS("Remember previous");

	private String value;

	DisplayMode(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return this.value;
	}
}
