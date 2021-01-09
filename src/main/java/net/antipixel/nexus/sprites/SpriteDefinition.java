package net.antipixel.nexus.sprites;

import net.runelite.client.game.SpriteOverride;

/**
 * Sprite IDs and file names are loaded up into this class directly
 * from a JSON file, creating custom sprites from PNG files
 * @author Antipixel
 */
public class SpriteDefinition implements SpriteOverride
{
	private int spriteID;
	private String fileName;

	@Override
	public int getSpriteId()
	{
		return this.spriteID;
	}

	@Override
	public String getFileName()
	{
		return this.fileName;
	}
}
