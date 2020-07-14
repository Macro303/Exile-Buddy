package github.macro.build_info.equipment

/**
 * Created by Macro303 on 2020-Jan-16.
 */
enum class Slot {
	MAIN_HAND,
	OFF_HAND,
	BOTH_HANDS,
	CHEST,
	BOOTS,
	GLOVES,
	HELMET,
	AMULET,
	BELT,
	RING,
	FLASK;

	companion object {
		fun value(name: String): Slot? = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
	}
}