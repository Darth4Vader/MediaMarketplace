package frontend.admin.createMovieLogView;

import java.util.logging.Level;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class CreateMovieLogCell extends ListCell<LoggerRecord> {
	
	private TextFlow textFlow;
	private Text message;
	private Text timeStamp;
	
	public CreateMovieLogCell() {
		setStyle("-fx-padding: 0px;");
		
		textFlow = new TextFlow();
		timeStamp = new Text();
		textFlow.getChildren().add(timeStamp);
		message = new Text();
		textFlow.getChildren().add(message);
	}
	
	private void set(LoggerRecord logRecord) {
		Level level = logRecord.getLevel();
		if(level == Level.WARNING) {
			message.setFill(Color.RED);
		}
		else
			message.setFill(Color.BLACK);
		timeStamp.setText(logRecord.getTimestamp() + "  ");
		message.setText(logRecord.getMessage());
	}
	
	private void reset() {
		message.setFill(Color.BLACK);
		timeStamp.setText("");
		message.setText("");
	}
	
    @Override
    public void updateItem(LoggerRecord item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
        	setGraphic(null);
            reset();
        }
        else {
            setGraphic(textFlow);
            set(item);
        }
        setAlignment(Pos.CENTER_LEFT);
		setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
    }
	
}
