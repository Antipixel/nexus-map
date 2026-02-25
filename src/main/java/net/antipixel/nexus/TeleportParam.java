package net.antipixel.nexus;

import lombok.Getter;

/*
Script to get struct IDs in the dev shell:
subscribe(PostStructComposition.class, ev ->
	log.info("{} - {}", ev.getStructComposition().getId(), ev.getStructComposition().getStringValue(660)));
*/

/**
 * Parameter IDs for the Nexus teleport details struct
 * @author Antipixel
 */
@Getter
public enum TeleportParam
{
	DEFAULT_ORDER_INDEX(659),
	DESTINATION_NAME(660),

	// A unique object ID exists for the Portal Nexus for each possible teleport option,
	// which decides the left-click teleport option. The struct stores the object IDs
	// for this teleport, one for each portal type: Marble, Gilded and Crystalline.
	MARBLE_NEXUS_OBJ_ID(661),
	GILDED_NEXUS_OBJ_ID(662),
	CRYSTALLINE_NEXUS_OBJ_ID(663),

	// Stores the up to 4 items required to unlock this spell in the Portal Nexus,
	// for instance Law Runes, Water Runes, Bananas, etc.
	UNLOCK_REQUIRED_ITEM_ID_1(664),
	UNLOCK_REQUIRED_ITEM_ID_2(665),
	UNLOCK_REQUIRED_ITEM_ID_3(666),
	UNLOCK_REQUIRED_ITEM_ID_4(667),

	// The required quantities for each of the items
	UNLOCK_REQUIRED_ITEM_QTY_1(668),
	UNLOCK_REQUIRED_ITEM_QTY_2(669),
	UNLOCK_REQUIRED_ITEM_QTY_3(670),
	UNLOCK_REQUIRED_ITEM_QTY_4(671),

	// For skill requirements, a skill ID and minimum level for unlocking the spell is stored
	// Typically this is 6 (Magic), these aren't present for teleports without skill requirements
	UNLOCK_REQUIRED_SKILL_ID(672),
	UNLOCK_REQUIRED_SKILL_LEVEL(673),

	// Set to a value of 1 if the teleport is locked behind a quest or activity
	LOCKED_TELEPORT(674),

	// If a larger sprite isn't available, the value is set to the ID of the regular sized sprite
	SPRITE_ID_ACTIVE(675),
	SPRITE_ID_ACTIVE_LARGE(676),
	SPRITE_ID_INACTIVE(677),
	SPRITE_ID_INACTIVE_LARGE(678),

	// For spells with alternative locations, e.g. Varrock and the Grand Exchange,
	// each will contain a struct ID to the other.
	ALTERNATIVE_TELEPORT_STRUCT_ID(679),
	PRIMARY_TELEPORT_STRUCT_ID(680),

	CONFIG_DEFAULT_ORDER_INDEX(681);

	private int id;

	TeleportParam(int id)
	{
		this.id = id;
	}
}
