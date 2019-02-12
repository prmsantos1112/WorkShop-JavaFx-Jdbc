package graficUserInterFace.Util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent events) {
		return (Stage)((Node)events.getSource()).getScene().getWindow();
		
	}
	
	public static Integer tryParseToInt(String string) {
		try {
			return Integer.parseInt(string);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
}
