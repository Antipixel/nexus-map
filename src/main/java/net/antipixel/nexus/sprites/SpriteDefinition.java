package net.antipixel.nexus.sprites;

import lombok.Getter;
import net.runelite.client.game.SpriteOverride;

/**
 * Sprite IDs and file names are loaded up into this class directly
 * from a JSON file, creating custom sprites from PNG files
 * @author Antipixel
 */
@Getter
public class SpriteDefinition implements SpriteOverride
{
	private int spriteId;
	private String fileName;
}
