package github.macro.ui

import github.macro.Styles
import github.macro.core.IBuildItem
import github.macro.core.IItem
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import org.apache.logging.log4j.LogManager
import tornadofx.*

/**
 * Created by Macro303 on 2020-Sep-22
 */
abstract class AbstractItemSelector<T : IBuildItem, S : IItem> : View() {
	private val model by inject<SelectionModel<T, S>>()

	private var itemCombobox: ComboBox<S> by singleAssign()
	private val imageUrlProperty = SimpleStringProperty()
	protected var imageUrl by imageUrlProperty
	protected var selectedItem: T? = null

	init {
		model.selected = null
		updateSelection(null)
	}

	protected abstract fun updateSelection(selected: S?)

	override val root = borderpane {
		paddingAll = 10.0
		top {
			paddingAll = 5.0
			vbox(spacing = 5.0, alignment = Pos.TOP_CENTER) {
				paddingAll = 5.0
				label(text = "Exile Buddy") {
					addClass(Styles.title)
				}
			}
		}
		center {
			hbox(spacing = 5.0, alignment = Pos.CENTER) {
				paddingAll = 5.0
				imageview(imageUrlProperty, lazyload = true) {
					fitHeight = model.imageHeight
					fitWidth = model.imageWidth
				}
				itemCombobox = combobox<S>(values = model.items.sortedBy { it.name }) {
					promptText = "Select Item"
					hgrow = Priority.ALWAYS
					cellFormat {
						text = it.name
					}
					setOnAction {
						updateSelection(itemCombobox.selectedItem)
					}
				}
				button("Select") {
					isDefaultButton = true
					action {
						model.selected = selectedItem
						close()
					}
				}
				button("None") {
					action {
						model.selected = null
						close()
					}
				}
			}
		}
	}

	companion object {
		private val LOGGER = LogManager.getLogger()
	}
}