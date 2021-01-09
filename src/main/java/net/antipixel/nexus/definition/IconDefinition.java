package net.antipixel.nexus.definition;

/**
 * Contains data that defines a region icon.
 * This information is loaded directly from a JSON file.
 * @author Antipixel
 */
public class IconDefinition
{
	private int x;
	private int y;
	private int spriteStandard;
	private int spriteHover;

	/**
	 * Gets the X position of the icon
	 * @return the icon X position
	 */
	public int getX()
	{
		return this.x;
	}

	/**
	 * Gets the Y position of the icon
	 * @return the icon Y position
	 */
	public int getY()
	{
		return this.y;
	}

	/**
	 * Gets the standard sprite for the icon
	 * @return the standard sprite ID
	 */
	public int getSpriteStandard()
	{
		return this.spriteStandard;
	}

	/**
	 * Gets the sprite for the hovering state of this icon
	 * @return the hover sprite ID
	 */
	public int getSpriteHover()
	{
		return this.spriteHover;
	}
}
