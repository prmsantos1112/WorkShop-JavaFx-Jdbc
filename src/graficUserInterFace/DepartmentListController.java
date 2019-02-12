package graficUserInterFace;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import dbase.DbIntegrityException;
import graficUserInterFace.Listeners.DataChangeListener;
import graficUserInterFace.Util.Alerts;
import graficUserInterFace.Util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService deptoService;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Department> observList;

	@FXML
	public void onBtNewAction(ActionEvent events) {
		Stage parentStage = Utils.currentStage(events);
		Department object = new Department();
		createDialogForm(object, "/graficUserInterFace/DepartmentForm.fxml", parentStage);
	}

	public void setDeptoService(DepartmentService deptoService) {
		this.deptoService = deptoService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		initializaNodes();

	}

	private void initializaNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (deptoService == null) {
			throw new IllegalStateException("Service was Null !!");
		}

		List<Department> list = deptoService.findAll();
		observList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(observList);
		initEditButtons();
		initRemoveButtons();

	}

	private void createDialogForm(Department object, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(object);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading View", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department objetiv, boolean empty) {
				super.updateItem(objetiv, empty);

				if (objetiv == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(event -> createDialogForm(objetiv, "/graficUserInterFace/DepartmentForm.fxml",
						Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department objectiv, boolean empty) {
				super.updateItem(objectiv, empty);

				if (objectiv == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(event -> removeEntity(objectiv));
			}

		});
	}

	private void removeEntity(Department objectiv) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation ", "Are you Sure to Delete ?");

		if (result.get() == ButtonType.OK) {
			if (deptoService == null) {
				throw new IllegalStateException("Service was Null !!");
			}
			
			try {
				deptoService.remove(objectiv);
				updateTableView();
				
			} 
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error Removing Object !!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
