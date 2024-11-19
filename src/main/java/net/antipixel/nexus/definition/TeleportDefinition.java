package net.antipixel.nexus.definition;

import lombok.Getter;

/**
 * Contains data that defines a teleport destination.
 * This information is loaded directly from a JSON file.
 * @author Antipixel
 */
@Getter
public class TeleportDefinition
{
	private int structID;
	private String name;
	private String[] aliases;
	public int spriteX;
	public int spriteY;
	private int enabledSprite;
	private int disabledSprite;

	/**
	 * Checks if this teleport has an alias defined
	 * @return true if the teleport has an alias, otherwise false
	 */
	public boolean hasAliases()
	{
		return this.aliases != null && this.aliases.length > 0;
	}
}
