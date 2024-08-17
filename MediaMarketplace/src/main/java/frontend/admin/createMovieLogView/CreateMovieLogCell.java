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

/**
 * Custom ListCell for displaying log records in a log view.
 * <p>This cell format is used to present log entries with a timestamp and message. 
 * The color of the log message changes according to the log level to differentiate 
 * the severity of the log entry.</p>
 */
public class CreateMovieLogCell extends ListCell<LoggerRecord> {
    
    /**
     * Container for laying out and formatting text nodes.
     * <p>The {@link TextFlow} instance organizes {@link Text} nodes for timestamp and message
     * into a single visual representation within the cell.</p>
     */
    private TextFlow textFlow;
    
    /**
     * Displays the log message.
     * <p>The {@link Text} instance for the message is styled based on the log level:
     * red for warnings, green for finer logs, and black for other log levels.</p>
     */
    private Text message;
    
    /**
     * Displays the timestamp of the log entry.
     * <p>The {@link Text} instance for the timestamp provides the date and time when
     * the log entry was created, displayed before the log message.</p>
     */
    private Text timeStamp;
    
    /**
     * Constructs a new {@code CreateMovieLogCell} instance.
     * <p>Initializes the layout and style for displaying log records.</p>
     */
    public CreateMovieLogCell() {
        setStyle("-fx-padding: 0px;");
        
        textFlow = new TextFlow();
        timeStamp = new Text();
        textFlow.getChildren().add(timeStamp);
        message = new Text();
        textFlow.getChildren().add(message);
    }
    
    /**
     * Updates the cell's content based on the provided {@link LoggerRecord}.
     * <p>Sets the timestamp and message text, and applies color coding based on the log level.</p>
     * 
     * @param logRecord The {@link LoggerRecord} to be displayed in the cell.
     */
    private void set(LoggerRecord logRecord) {
        Level level = logRecord.getLevel();
        if(level == Level.WARNING) {
            message.setFill(Color.RED);
        }
        else if(level == Level.FINER) {
            message.setFill(Color.GREEN);
        }
        else {
            message.setFill(Color.BLACK);
        }
        timeStamp.setText(logRecord.getTimestamp() + "  ");
        message.setText(logRecord.getMessage());
    }
    
    /**
     * Resets the cell's content to default values.
     * <p>Clears the timestamp and message texts and sets default color for the message.</p>
     */
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