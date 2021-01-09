package net.antipixel.nexus.definition;

/**
 * Contains data that defines a game region on the menu.
 * This information is loaded directly from a JSON file.
 * @author Antipixel
 */
public class RegionDefinition
{
	private int id;
	private String name;
	private IconDefinition icon;
	private int indexSprite;
	private int mapSprite;

	private TeleportDefinition[] teleportDefinitions;

	/**
	 * Gets the region ID
	 * @return the region ID
	 */
	public int getID()
	{
		return this.id;
	}

	/**
	 * Gets the name of the region
	 * @return the region name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Gets the definition for this regions icon
	 * @return the icon definition
	 */
	public IconDefinition getIcon()
	{
		return this.icon;
	}

	/**
	 * Gets the sprite ID for the map that represents
	 * this region on the menus index page
	 * @return the index sprite
	 */
	public int getIndexSprite()
	{
		return this.indexSprite;
	}

	/**
	 * Gets the sprite ID for this regions map
	 * @return the map sprite ID
	 */
	public int getMapSprite()
	{
		return this.mapSprite;
	}

	/**
	 * Gets the teleport definitions for this region
	 * @return the teleport definitions
	 */
	public TeleportDefinition[] getTeleports()
	{
		return this.teleportDefinitions;
	}

	/**
	 * Checks if this region has teleports defined for it
	 * @return true if teleports are defined, otherwise false
	 */
	public boolean hasTeleports()
	{
		return this.teleportDefinitions.length > 0;
	}
}
