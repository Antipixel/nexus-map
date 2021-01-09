package net.antipixel.nexus.definition;

/**
 * Contains data that defines a teleport destination.
 * This information is loaded directly from a JSON file.
 * @author Antipixel
 */
public class TeleportDefinition
{
	private String name;
	private String alias;
	private int spriteX;
	private int spriteY;
	private int enabledSprite;
	private int disabledSprite;

	/**
	 * Gets the name of the teleport location
	 * @return the teleport name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Checks if this teleport has an alias defined
	 * @return true if the teleport has an alias, otherwise false
	 */
	public boolean hasAlias()
	{
		return this.alias != null;
	}

	/**
	 * Gets the alias for the teleport, usually specified for
	 * locations on the ancient spellbook as they're listed
	 * using the ancient names for the location
	 * @return the teleport alias
	 */
	public String getAlias()
	{
		return this.alias;
	}

	/**
	 * Gets the X position of the spell sprite
	 * @return the X position
	 */
	public int getSpriteX()
	{
		return this.spriteX;
	}

	/**
	 * Gets the Y position of the spell sprite
	 * @return the Y position
	 */
	public int getSpriteY()
	{
		return this.spriteY;
	}

	/**
	 * Gets the sprite for this teleport in its enabled state
	 * @return the enabled teleport sprite
	 */
	public int getEnabledSprite()
	{
		return this.enabledSprite;
	}

	/**
	 * Gets the sprite for this teleport in its disabled state
	 * @return the disabled teleport sprite
	 */
	public int getDisabledSprite()
	{
		return this.disabledSprite;
	}
}
