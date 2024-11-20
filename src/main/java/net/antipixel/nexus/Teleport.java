package net.antipixel.nexus;

import lombok.Getter;
import net.antipixel.nexus.definition.TeleportDefinition;
import net.runelite.api.widgets.Widget;

/**
 * Represents a teleport option on the Nexus menu
 * @author Antipixel
 */
@Getter
public class Teleport
{
	private final TeleportDefinition definition;
	private final Widget widget;
	private final String keyShortcut;

	/**
	 * Creates a new teleport instance
	 * @param definition the teleport definition
	 * @param widget the widget for this teleport option
	 * @param key the keyboard shortcut
	 */
	public Teleport(TeleportDefinition definition, Widget widget, String key)
	{
		this.definition = definition;
		this.widget = widget;
		this.keyShortcut = key;
	}

	/**
	 * Gets the teleport name
	 * @return the teleport name
	 */
	public String getName()
	{
		return this.definition.getName();
	}

	public boolean isAlt()
	{
		return this.definition.isAlt();
	}

	/**
	 * Gets the teleport alias
	 * @return the alternative name for this teleport
	 */
	public String getAlias()
	{
		return this.definition.getAlias();
	}

	/**
	 * Checks if the teleport has an alias
	 * @return true if an alias is defined, otherwise false
	 */
	public boolean hasAlias()
	{
		return this.definition.getAlias() != null;
	}

	/**
	 * Gets the child index of this teleports widget
	 * @return this teleports widget child index
	 */
	public int getChildIndex()
	{
		return this.widget.getIndex();
	}

	/**
	 * Checks if the teleport has a shortcut key set
	 * @return true if a shortcut is set, otherwise false
	 */
	public boolean hasShortcutKey()
	{
		return this.keyShortcut != null;
	}
}
