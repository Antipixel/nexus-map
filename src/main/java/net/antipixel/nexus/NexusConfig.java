package net.antipixel.nexus;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.chatfilter.ChatFilterType;

/**
 * Config options
 * @author Antipixel
 */
@ConfigGroup("nexusmenu")
public interface NexusConfig extends Config
{
	@ConfigItem(
		keyName = "displayShortcuts",
		name = "Display shortcuts",
		description = "Display the shortcut keys in the right-click menu of each teleport",
		position = 0
	)
	default boolean displayShortcuts()
	{
		return true;
	}

	@ConfigItem(
		keyName = "initialMode",
		name = "Open to",
		description = "Configures which menu will be first displayed when the Nexus menu is opened",
		position = 1
	)
	default DisplayMode initialMode()
	{
		return DisplayMode.NEXUS_MAP;
	}
}
