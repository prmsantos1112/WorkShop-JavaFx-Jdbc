package graficUserInterFace;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import dbase.DbException;
import graficUserInterFace.Listeners.DataChangeListener;
import graficUserInterFace.Util.Alerts;
import graficUserInterFace.Util.Constraints;
import graficUserInterFace.Util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationExceptions;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;	
	private DepartmentService deptoService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService (DepartmentService deptoService) {
		this.deptoService = deptoService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listeners) {
		dataChangeListeners.add(listeners);
		
	}	
	
	@FXML
	public void onBtSaveAction(ActionEvent events) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null !!");
		}
		if (deptoService == null) {
			throw new IllegalStateException("DeptoService was null !!");
		}
		try {
			entity = getFormData();
			deptoService.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(events).close();
			
		} 
		catch (ValidationExceptions e ) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	private Department getFormData() {
		Department object = new Department();
		
		ValidationExceptions exception = new ValidationExceptions("Validation Error !!");
		
		object.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("Name", "Field Can't be Empty");
		}
		object.setName(txtName.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return object;
	}

	@FXML
	public void onBtCancelAction(ActionEvent events) {
		Utils.currentStage(events).close();
	
	}	

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {		
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 50);
	}
	
	public void updateFormData() {
		
		if (entity == null) {
			throw new IllegalStateException("Entity was Null !!");
		}			
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("Name")) {
			labelErrorName.setText(errors.get("Name"));
		}
	}

}
