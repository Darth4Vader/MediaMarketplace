package frontend.admin.createMovieLogView;

import java.util.logging.Logger;

import backend.tmdb.CreateMovie;
import frontend.utils.AppUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

/**
 * Controller for managing the creation movie log view.
 * <p>This class sets up and controls a list view to display log messages related to
 * movie creation tasks. It also provides methods to manage the log display and alert dialogs.</p>
 */
public class CreateMovieLoggerControl {

    /**
     * ListView to display the log records.
     * <p>The {@link ListView} is used to present a scrollable list of log records.</p>
     */
    private ListView<LoggerRecord> createMovieLoggerListView;
    
    /**
     * Observable list of log records.
     * <p>The {@link ObservableList} holds the log records to be displayed in the {@link ListView}.</p>
     */
    private ObservableList<LoggerRecord> createMovieLoggerList;
    
    /**
     * Alert dialog for displaying the log information.
     * <p>The {@link Alert} provides feedback to the user about the progress of the movie creation task.</p>
     */
    private Alert alert;

    /**
     * Constructs a {@code CreateMovieLoggerControl} for managing log display.
     * <p>Initializes the list view and logger handler for capturing log records.</p>
     * 
     * @param createMovie The {@link CreateMovie} instance for retrieving the logger.
     */
    public CreateMovieLoggerControl(CreateMovie createMovie) {
        createMovieLoggerListView = new ListView<>();
        createMovieLoggerListView.setSelectionModel(null);
        createMovieLoggerListView.setCellFactory(x -> new CreateMovieLogCell());
        createMovieLoggerList = FXCollections.observableArrayList();
        createMovieLoggerListView.setItems(createMovieLoggerList);
        Logger logger = createMovie.getLogger();
        logger.addHandler(new CreateMovieHandler(createMovieLoggerList));
    }
    
    /**
     * Starts the log view with an associated task.
     * <p>Displays an alert dialog and binds it to the provided task. The log view is
     * cleared and set to be expandable within the alert dialog.</p>
     * 
     * @param task The {@link Task} to monitor and link with the alert dialog.
     */
    public void start(Task<?> task) {
        createAlert(task);
        createMovieLoggerList.clear();
        alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        alert.getDialogPane().setExpandableContent(createMovieLoggerListView);
        alert.getDialogPane().setExpanded(true);
        alert.show();
    }
    
    /**
     * Marks the task as finished and enables the OK button in the alert dialog.
     * <p>This method should be called when the associated task completes successfully.</p>
     */
    public void finishedTask() {
        alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
    }
    
    /**
     * Closes the alert dialog and clears the log records.
     * <p>This method cleans up resources and closes the alert dialog if it is open.</p>
     */
    public void close() {
        createMovieLoggerList.clear();
        closeAlert();
    }
    
    /**
     * Creates or updates the alert dialog based on the provided task.
     * <p>If the alert already exists, it is updated with the new task information. The alert
     * is configured to cancel the task if the dialog is closed.</p>
     * 
     * @param task The {@link Task} associated with the alert dialog.
     */
    private void createAlert(Task<?> task) {
        closeAlert();
        if(this.alert == null)
            this.alert = AppUtils.createAlertOfInformation("Updating Movie", "Starting to Update");
        if(task != null) {
            // If we close the window, we will let the task know, maybe it was before it finished.
            this.alert.setOnCloseRequest(v -> {
                task.cancel();
            });
        }
        else {
            this.alert.setOnCloseRequest(null);
        }
    }
    
    /**
     * Closes the alert dialog if it is open.
     * <p>This method ensures that the alert dialog is properly closed to avoid resource leaks.</p>
     */
    private void closeAlert() {
        if(this.alert != null)
            this.alert.close();
    }
}