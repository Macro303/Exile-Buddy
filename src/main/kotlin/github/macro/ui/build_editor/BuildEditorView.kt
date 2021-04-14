package github.macro.ui.build_editor

import github.macro.Styles
import github.macro.Utils
import github.macro.Utils.cleanName
import github.macro.core.Ascendency
import github.macro.core.Bandit
import github.macro.core.ClassTag
import github.macro.core.Stage
import github.macro.ui.build_viewer.BuildViewerModel
import github.macro.ui.build_viewer.BuildViewerView
import github.macro.ui.main.MainView
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.ComboBox
import javafx.scene.control.ScrollPane
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Paint
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import javafx.scene.text.TextAlignment
import org.apache.logging.log4j.LogManager
import tornadofx.*

/**
 * Created by Macro303 on 2021-Apr-14.
 */
class BuildEditorView : View("Path of Taurewa") {
    private val model by inject<BuildEditorModel>()

    private var classComboBox: ComboBox<ClassTag> by singleAssign()
    private var ascendencyComboBox: ComboBox<Ascendency> by singleAssign()

    override val root = borderpane {
        prefWidth = Utils.UI_PREF_WIDTH
        prefHeight = Utils.UI_PREF_HEIGHT
        paddingAll = 10.0
        top {
            //region Header Pane
            paddingAll = 5.0
            hbox(spacing = 5.0, alignment = Pos.CENTER) {
                paddingAll = 5.0
                label(text = "Build Editor") {
                    addClass(Styles.title)
                }
                spacer { }
                vbox(spacing = 5.0, alignment = Pos.CENTER) {
                    paddingAll = 5.0
                    textfield(model.titleProperty) {
                        addClass(Styles.subtitle)
                    }
                    hbox(spacing = 5.0, alignment = Pos.CENTER) {
                        paddingAll = 5.0
                        spacer { }
                        textfield(model.versionProperty)
                        separator()
                        classComboBox = combobox(model.classProperty, values = ClassTag.values().toList()) {
                            cellFormat {
                                text = it.cleanName()
                            }
                        }
                        separator()
                        ascendencyComboBox = combobox(model.ascendencyProperty, values = model.classTag.ascendencies.toList()) {
                            cellFormat {
                                text = it.cleanName()
                            }
                        }
                        classComboBox.setOnAction {
                            ascendencyComboBox.items = (classComboBox.selectedItem?.ascendencies?.toList()
                                ?: emptyList()).asObservable()
                            ascendencyComboBox.selectionModel.select(0)
                        }
                        spacer { }
                        button(text = "Add Stage") {
                            addClass(Styles.sizedButton)
                            action {
                                model.stageList.add(Stage(
                                    name = "New Stage",
                                    weapons = emptyList(),
                                    helmet = emptyList(),
                                    bodyArmour = emptyList(),
                                    gloves = emptyList(),
                                    boots = emptyList(),
                                ))
                                val scope = Scope()
                                setInScope(BuildEditorModel(model.saveBuild()), scope)
                                find<BuildEditorView>(scope).openWindow(owner = null, resizable = false)
                                close()
                            }
                        }
                        spacer { }
                        button(text = "Save") {
                            addClass(Styles.sizedButton)
                            action {
                                val scope = Scope()
                                setInScope(BuildViewerModel(model.saveBuild()), scope)
                                find<BuildViewerView>(scope).openWindow(owner = null, resizable = false)
                                close()
                            }
                        }
                    }
                }
            }
            //endregion
        }
        center {
            paddingAll = 5.0
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                //region Gems Tab
                tab(text = "Gems") {
                    tabpane {
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        side = Side.LEFT
                        model.stageList.forEach {
                            tab(it.name) {
                                scrollpane(fitToWidth = true) {
                                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                                    vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
                                    gridpane {
                                        //region Weapon Gems
                                        row {
                                            label("Weapon/s") {
                                                addClass(Styles.subtitle)
                                                gridpaneConstraints {
                                                    columnSpan = 6
                                                    hAlignment = HPos.CENTER
                                                    margin = Insets(2.5)
                                                }
                                            }
                                        }
                                        row {
                                            it.weaponList.forEachIndexed { index, gem ->
                                                borderpane {
                                                    maxWidth = (Utils.UI_PREF_WIDTH - (10 * 6)) / 6
                                                    prefWidth = (Utils.UI_PREF_WIDTH - (20 * 6)) / 6
                                                    minWidth = (Utils.UI_PREF_WIDTH - (30 * 6)) / 6
                                                    paddingAll = 10.0
                                                    center {
                                                        paddingAll = 5.0
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            imageview(
                                                                "file:${gem.getImageFile().path}",
                                                                lazyload = true
                                                            ) {
                                                                if (gem.getImageFile().name != "placeholder.png") {
                                                                    if (Utils.getImageHeight(gem) >= Utils.IMG_SIZE) {
                                                                        fitHeight = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    } else if (Utils.getImageWidth(gem) >= Utils.IMG_SIZE) {
                                                                        fitWidth = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    bottom {
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            label(gem.name) {
                                                                isWrapText = true
                                                                alignment = Pos.CENTER
                                                                textAlignment = TextAlignment.CENTER
                                                                textFillProperty().bind(
                                                                    Paint.valueOf(gem.colour.hex).toProperty()
                                                                )
                                                            }
                                                        }
                                                    }
                                                    style {
                                                        borderColor += box(c(gem.colour.hex))
                                                        borderStyle += BorderStrokeStyle(
                                                            StrokeType.CENTERED,
                                                            StrokeLineJoin.ROUND,
                                                            StrokeLineCap.ROUND,
                                                            0.0,
                                                            0.0,
                                                            listOf(5.0, 5.0)
                                                        )
                                                        borderWidth += box(2.px)
                                                    }
                                                    gridpaneConstraints {
                                                        hAlignment = HPos.CENTER
                                                        margin = Insets(2.5)
                                                    }
                                                }
                                            }
                                        }
                                        //endregion
                                        //region Helmet Gems
                                        row {
                                            label("Helmet") {
                                                addClass(Styles.subtitle)
                                                gridpaneConstraints {
                                                    columnSpan = 6
                                                    hAlignment = HPos.CENTER
                                                    margin = Insets(2.5)
                                                }
                                            }
                                        }
                                        row {
                                            it.helmetList.forEachIndexed { index, gem ->
                                                borderpane {
                                                    maxWidth = (Utils.UI_PREF_WIDTH - (10 * 6)) / 6
                                                    prefWidth = (Utils.UI_PREF_WIDTH - (20 * 6)) / 6
                                                    minWidth = (Utils.UI_PREF_WIDTH - (30 * 6)) / 6
                                                    paddingAll = 10.0
                                                    center {
                                                        paddingAll = 5.0
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            imageview(
                                                                "file:${gem.getImageFile().path}",
                                                                lazyload = true
                                                            ) {
                                                                if (gem.getImageFile().name != "placeholder.png") {
                                                                    if (Utils.getImageHeight(gem) >= Utils.IMG_SIZE) {
                                                                        fitHeight = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    } else if (Utils.getImageWidth(gem) >= Utils.IMG_SIZE) {
                                                                        fitWidth = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    bottom {
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            label(gem.name) {
                                                                isWrapText = true
                                                                alignment = Pos.CENTER
                                                                textAlignment = TextAlignment.CENTER
                                                                textFillProperty().bind(
                                                                    Paint.valueOf(gem.colour.hex).toProperty()
                                                                )
                                                            }
                                                        }
                                                    }
                                                    style {
                                                        borderColor += box(c(gem.colour.hex))
                                                        borderStyle += BorderStrokeStyle(
                                                            StrokeType.CENTERED,
                                                            StrokeLineJoin.ROUND,
                                                            StrokeLineCap.ROUND,
                                                            0.0,
                                                            0.0,
                                                            listOf(5.0, 5.0)
                                                        )
                                                        borderWidth += box(2.px)
                                                    }
                                                    gridpaneConstraints {
                                                        hAlignment = HPos.CENTER
                                                        margin = Insets(2.5)
                                                    }
                                                }
                                            }
                                        }
                                        //endregion
                                        //region Body Armour Gems
                                        row {
                                            label("Body Armour") {
                                                addClass(Styles.subtitle)
                                                gridpaneConstraints {
                                                    columnSpan = 6
                                                    hAlignment = HPos.CENTER
                                                    margin = Insets(2.5)
                                                }
                                            }
                                        }
                                        row {
                                            it.bodyArmourList.forEachIndexed { index, gem ->
                                                borderpane {
                                                    maxWidth = (Utils.UI_PREF_WIDTH - (10 * 6)) / 6
                                                    prefWidth = (Utils.UI_PREF_WIDTH - (20 * 6)) / 6
                                                    minWidth = (Utils.UI_PREF_WIDTH - (30 * 6)) / 6
                                                    paddingAll = 10.0
                                                    center {
                                                        paddingAll = 5.0
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            imageview(
                                                                "file:${gem.getImageFile().path}",
                                                                lazyload = true
                                                            ) {
                                                                if (gem.getImageFile().name != "placeholder.png") {
                                                                    if (Utils.getImageHeight(gem) >= Utils.IMG_SIZE) {
                                                                        fitHeight = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    } else if (Utils.getImageWidth(gem) >= Utils.IMG_SIZE) {
                                                                        fitWidth = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    bottom {
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            label(gem.name) {
                                                                isWrapText = true
                                                                alignment = Pos.CENTER
                                                                textAlignment = TextAlignment.CENTER
                                                                textFillProperty().bind(
                                                                    Paint.valueOf(gem.colour.hex).toProperty()
                                                                )
                                                            }
                                                        }
                                                    }
                                                    style {
                                                        borderColor += box(c(gem.colour.hex))
                                                        borderStyle += BorderStrokeStyle(
                                                            StrokeType.CENTERED,
                                                            StrokeLineJoin.ROUND,
                                                            StrokeLineCap.ROUND,
                                                            0.0,
                                                            0.0,
                                                            listOf(5.0, 5.0)
                                                        )
                                                        borderWidth += box(2.px)
                                                    }
                                                    gridpaneConstraints {
                                                        hAlignment = HPos.CENTER
                                                        margin = Insets(2.5)
                                                    }
                                                }
                                            }
                                        }
                                        //endregion
                                        //region Gloves Gems
                                        row {
                                            label("Gloves") {
                                                addClass(Styles.subtitle)
                                                gridpaneConstraints {
                                                    columnSpan = 6
                                                    hAlignment = HPos.CENTER
                                                    margin = Insets(2.5)
                                                }
                                            }
                                        }
                                        row {
                                            it.glovesList.forEachIndexed { index, gem ->
                                                borderpane {
                                                    maxWidth = (Utils.UI_PREF_WIDTH - (10 * 6)) / 6
                                                    prefWidth = (Utils.UI_PREF_WIDTH - (20 * 6)) / 6
                                                    minWidth = (Utils.UI_PREF_WIDTH - (30 * 6)) / 6
                                                    paddingAll = 10.0
                                                    center {
                                                        paddingAll = 5.0
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            imageview(
                                                                "file:${gem.getImageFile().path}",
                                                                lazyload = true
                                                            ) {
                                                                if (gem.getImageFile().name != "placeholder.png") {
                                                                    if (Utils.getImageHeight(gem) >= Utils.IMG_SIZE) {
                                                                        fitHeight = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    } else if (Utils.getImageWidth(gem) >= Utils.IMG_SIZE) {
                                                                        fitWidth = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    bottom {
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            label(gem.name) {
                                                                isWrapText = true
                                                                alignment = Pos.CENTER
                                                                textAlignment = TextAlignment.CENTER
                                                                textFillProperty().bind(
                                                                    Paint.valueOf(gem.colour.hex).toProperty()
                                                                )
                                                            }
                                                        }
                                                    }
                                                    style {
                                                        borderColor += box(c(gem.colour.hex))
                                                        borderStyle += BorderStrokeStyle(
                                                            StrokeType.CENTERED,
                                                            StrokeLineJoin.ROUND,
                                                            StrokeLineCap.ROUND,
                                                            0.0,
                                                            0.0,
                                                            listOf(5.0, 5.0)
                                                        )
                                                        borderWidth += box(2.px)
                                                    }
                                                    gridpaneConstraints {
                                                        hAlignment = HPos.CENTER
                                                        margin = Insets(2.5)
                                                    }
                                                }
                                            }
                                        }
                                        //endregion
                                        //region Boots Gems
                                        row {
                                            label("Boots") {
                                                addClass(Styles.subtitle)
                                                gridpaneConstraints {
                                                    columnSpan = 6
                                                    hAlignment = HPos.CENTER
                                                    margin = Insets(2.5)
                                                }
                                            }
                                        }
                                        row {
                                            it.bootsList.forEachIndexed { index, gem ->
                                                borderpane {
                                                    maxWidth = (Utils.UI_PREF_WIDTH - (10 * 6)) / 6
                                                    prefWidth = (Utils.UI_PREF_WIDTH - (20 * 6)) / 6
                                                    minWidth = (Utils.UI_PREF_WIDTH - (30 * 6)) / 6
                                                    paddingAll = 10.0
                                                    center {
                                                        paddingAll = 5.0
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            imageview(
                                                                "file:${gem.getImageFile().path}",
                                                                lazyload = true
                                                            ) {
                                                                if (gem.getImageFile().name != "placeholder.png") {
                                                                    if (Utils.getImageHeight(gem) >= Utils.IMG_SIZE) {
                                                                        fitHeight = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    } else if (Utils.getImageWidth(gem) >= Utils.IMG_SIZE) {
                                                                        fitWidth = Utils.IMG_SIZE
                                                                        isPreserveRatio = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    bottom {
                                                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                                                            paddingAll = 5.0
                                                            label(gem.name) {
                                                                isWrapText = true
                                                                alignment = Pos.CENTER
                                                                textAlignment = TextAlignment.CENTER
                                                                textFillProperty().bind(
                                                                    Paint.valueOf(gem.colour.hex).toProperty()
                                                                )
                                                            }
                                                        }
                                                    }
                                                    style {
                                                        borderColor += box(c(gem.colour.hex))
                                                        borderStyle += BorderStrokeStyle(
                                                            StrokeType.CENTERED,
                                                            StrokeLineJoin.ROUND,
                                                            StrokeLineCap.ROUND,
                                                            0.0,
                                                            0.0,
                                                            listOf(5.0, 5.0)
                                                        )
                                                        borderWidth += box(2.px)
                                                    }
                                                    gridpaneConstraints {
                                                        hAlignment = HPos.CENTER
                                                        margin = Insets(2.5)
                                                    }
                                                }
                                            }
                                        }
                                        //endregion
                                    }
                                }
                            }
                        }
                    }
                }
                //endregion
                //region Gear Tab
                tab(text = "Gear") {
                    isDisable = true
                    scrollpane(fitToWidth = true) {
                        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                        gridpane {}
                    }
                }
                //endregion
                //region Other Tab
                tab(text = "Other") {
                    scrollpane(fitToWidth = true) {
                        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                        vbox(spacing = 5.0, alignment = Pos.CENTER) {
                            paddingAll = 5.0
                            //region Bandits
                            label(text = "Kill Bandits"){
                                addClass(Styles.subtitle)
                            }
                            hbox(spacing = 5.0, alignment = Pos.CENTER){
                                paddingAll = 5.0
                                Bandit.values().forEach {
                                    checkbox(text = it.cleanName()){
                                        isSelected = model.banditList.contains(it)
                                        isDisable = true
                                    }
                                }
                            }
                            //endregion
                            //region Ascendency
                            label(text = "Ascendency") {
                                addClass(Styles.subtitle)
                            }
                            hbox(spacing = 5.0, alignment = Pos.CENTER) {
                                paddingAll = 5.0
                                label(model.majorPantheonProperty)
                                separator()
                                label(model.minorPantheonProperty)
                            }
                            //endregion
                            //region Other
                            label(text = "Other Details") {
                                addClass(Styles.subtitle)
                            }
                            textarea(model.detailsProperty)
                            //endregion
                        }
                    }
                }
                //endregion
            }
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            val scope = Scope()
            setInScope(BuildViewerModel(model.originalBuild), scope)
            find<BuildViewerView>(scope).openWindow(owner = null, resizable = false)
            close()
        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
    }
}