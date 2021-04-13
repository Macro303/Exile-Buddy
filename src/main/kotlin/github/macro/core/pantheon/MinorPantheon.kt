package github.macro.core.pantheon

/**
 * Created by Macro303 on 2020-Sep-28
 */
enum class MinorPantheon {
	YUGUL,
	RALAKESH,
	SHAKARI,
	ABBERATH,
	TUKOHAMA,
	RYSLATHA,
	GARUKHAN,
	GRUTHKUL;

	companion object {
		fun fromName(name: String?): MinorPantheon? = values().firstOrNull {
			it.name.replace("_", " ").equals(name, ignoreCase = true)
		}
	}
}