package net.antipixel.nexus.config;

import lombok.AllArgsConstructor;

/**
 * Teleport icon name format options
 *
 * ORIGINAL_NAME displays the name as it appears in the vanilla client,
 *               e.g. Kharyrll
 * ALIAS displays the more familiar name for the location, e.g. Canifis
 * BOTH displays the original name with the alias in parenthesis,
 *               e.g. Kharyrll (Canifis)
 * @author Antipixel
 */
@AllArgsConstructor
public enum TeleportNameMode
{
    ORIGINAL_NAME("Original Name"),
    ALIAS("Alternate Name"),
    BOTH("Both Names");

    private final String value;

    @Override
    public String toString()
    {
        return this.value;
    }
}
