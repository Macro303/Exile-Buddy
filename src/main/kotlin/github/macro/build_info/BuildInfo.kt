package github.macro.build_info

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import github.macro.Util
import github.macro.build_info.gems.UpdateGem
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.apache.logging.log4j.LogManager
import tornadofx.*
import java.io.File
import java.io.IOException


/**
 * Created by Macro303 on 2020-Jan-13.
 */
@JsonDeserialize(using = BuildDeserializer::class)
@JsonSerialize(using = BuildSerializer::class)
class BuildInfo() : JsonModelAuto {
	val nameProperty = SimpleStringProperty()
	var name by nameProperty

	val classProperty = SimpleObjectProperty<ClassTag>()
	var classTag by classProperty

	val ascendencyProperty = SimpleObjectProperty<Ascendency>()
	var ascendency by ascendencyProperty

	val linksProperty = SimpleListProperty<LinkInfo>()
	var links by linksProperty

	val updatesProperty = SimpleListProperty<UpdateGem>()
	var updates by updatesProperty

	constructor(
		name: String,
		classTag: ClassTag,
		ascendency: Ascendency?,
		links: List<LinkInfo>,
		updates: List<UpdateGem>
	) : this() {
		this.name = name
		this.classTag = classTag
		this.ascendency = ascendency
		this.links = FXCollections.observableList(links)
		this.updates = FXCollections.observableList(updates)
	}

	override fun toString(): String {
		return "BuildInfo(nameProperty=$nameProperty, classProperty=$classProperty, ascendencyProperty=$ascendencyProperty, linksProperty=$linksProperty, updatesProperty=$updatesProperty)"
	}

	fun display(): String = "$name [$classTag${if (ascendency == null) "" else "/" + ascendency.name}]"

	fun save() {
		try {
			val buildFile = File("builds", name.replace(" ", "_") + ".yaml")
			Util.YAML_MAPPER.writeValue(buildFile, this)
		} catch (ioe: IOException) {
			LOGGER.error("Unable to save build: $ioe")
		}
	}

	companion object {
		private val LOGGER = LogManager.getLogger(BuildInfo::class.java)
	}
}