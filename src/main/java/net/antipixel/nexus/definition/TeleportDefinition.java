package net.antipixel.nexus.definition;

import lombok.Getter;
import net.antipixel.nexus.IntegerBooleanPair;

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
	private boolean isAlt;
	private String alias;
	public int spriteX;
	public int spriteY;
	private int enabledSprite;
	private int disabledSprite;

	/**
	 * Checks if this teleport has an alias defined
	 * @return true if the teleport has an alias, otherwise false
	 */
	public boolean hasAlias()
	{
		return this.alias != null;
	}

	/**
	 * Gets the key to be used to identify this TeleportDefinition, based on enabledSprite and isAlt.
	 * Alternate teleports share the same sprite ID as their primary counterpart,
	 * but are stored under a separate widget in-game, allowing us to identify them this way.
	 * @return The IntegerBooleanPair key
	 */
	public IntegerBooleanPair getKey()
	{
		return new IntegerBooleanPair(this.enabledSprite, this.isAlt);
	}
}
