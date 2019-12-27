package macro.buddy.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import macro.buddy.Util;
import macro.buddy.build.AscendencyTag;
import macro.buddy.build.BuildInfo;
import macro.buddy.build.BuildUtils;
import macro.buddy.build.ClassTag;
import macro.buddy.gems.GemInfo;
import macro.buddy.gems.GemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Macro303 on 2019-Dec-03
 */
public class GemsController implements Initializable {
	private static final Logger LOGGER = LogManager.getLogger(GemsController.class);
	@FXML
	private ComboBox<BuildInfo> buildCombo;
	@FXML
	private TextField buildName;
	@FXML
	private ComboBox<ClassTag> classCombo;
	@FXML
	private ComboBox<AscendencyTag> ascendencyCombo;
	@FXML
	private VBox gemVBox;

	private Stage stage = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildCombo.getItems().setAll(BuildUtils.getBuilds());
		buildCombo.setConverter(new StringConverter<BuildInfo>() {
			@Override
			public String toString(BuildInfo object) {
				return object.getName() + " [" + object.getClassTag() + (object.getAscendency() == AscendencyTag.NONE ? "" : "/" + object.getAscendency().name()) + "]";
			}

			@Override
			public BuildInfo fromString(String string) {
				return null;
			}
		});
		classCombo.getItems().setAll(ClassTag.values());
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setBuild(BuildInfo build) {
		gemVBox.getChildren().clear();
		gemVBox.setFillWidth(true);
		build.getLinks().forEach(it -> {
			HBox linkBox = new HBox();
			linkBox.setSpacing(5.0);
			it.forEach(gem -> {
				Optional<GemInfo> info = GemUtils.getGem(gem);
				String imageFile = getClass().getResource("placeholder[80x80].png").toExternalForm();
				if (info.isPresent()) {
					imageFile = String.format("gems\\inventory\\%s", info.get().getFilename(gem.contains("Vaal")));
					if (!new File(imageFile).exists())
						imageFile = getClass().getResource("placeholder[64x64].png").toExternalForm();
					else
						imageFile = "file:" + imageFile;
				}
				ImageView image = new ImageView(new Image(imageFile));
				image.setFitHeight(80);
				image.setFitWidth(80);
				Label name = new Label(gem);
				name.setWrapText(true);
				name.setPrefWidth(80);
				VBox border = new VBox();
				border.setAlignment(Pos.TOP_CENTER);
				border.getChildren().addAll(image, name);
				linkBox.getChildren().add(border);
			});
			gemVBox.getChildren().add(linkBox);
		});
	}

	public void addBuild() {
		if (buildName.getText() == null || buildName.getText().trim().isEmpty())
			return;
		BuildInfo newInfo = new BuildInfo(buildName.getText(), classCombo.getValue(), ascendencyCombo.getValue(), new ArrayList<>(), new HashMap<>());
		newInfo.save();
		BuildUtils.loadBuilds();
		buildCombo.getItems().setAll(BuildUtils.getBuilds());
		buildCombo.getSelectionModel().select(newInfo);
	}

	public void buildSelection(ActionEvent event) {
		setBuild(buildCombo.getValue());
	}

	public void classSelection(ActionEvent event) {
		ascendencyCombo.setDisable(false);
		ascendencyCombo.getItems().setAll(AscendencyTag.values(classCombo.getValue()));
	}

	public void ascendencySelection(ActionEvent event) {
	}
}