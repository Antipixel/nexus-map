package net.antipixel.nexus.definition;

import lombok.Getter;

/**
 * Contains data that defines where a neighbouring region's icon should be placed on a region's page.
 * This information is loaded directly from a JSON file.
 * @author Enriath
 */
@Getter
public class NeighbourDefinition
{
	private int regionId;
	private int x;
	private int y;
}
