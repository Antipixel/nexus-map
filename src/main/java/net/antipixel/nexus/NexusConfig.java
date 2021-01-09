package net.antipixel.nexus;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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
}
