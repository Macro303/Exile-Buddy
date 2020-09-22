package github.macro.ui.items.belts

import github.macro.core.Data
import github.macro.core.items.belts.BuildBelt
import github.macro.core.items.belts.ItemBelt
import github.macro.ui.AbstractItemSelector
import java.io.File

/**
 * Created by Macro303 on 2020-Sep-22
 */
class BeltSelector : AbstractItemSelector<BuildBelt, ItemBelt>() {

	override fun updateSelection(selected: ItemBelt?) {
		selectedItem = BuildBelt(selected ?: Data.getBelt(null))
		var image = selectedItem!!.item.getFile()
		if (selectedItem!!.item.name != "None" && !image.exists())
			image = File(image.parent, "placeholder.png")
		imageUrl = "file:${image.path}"
	}
}