package frontend.admin.createMovieLogView;

import java.util.logging.Logger;

import backend.tmdb.CreateMovie;
import frontend.AppUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

public class CreateMovieLoggerControl {
	
	private ListView<LoggerRecord> createMovieLoggerListView;
	private ObservableList<LoggerRecord> createMovieLoggerList;
	private Alert alert;

	public CreateMovieLoggerControl(CreateMovie createMovie) {
		createMovieLoggerListView = new ListView<>();
		createMovieLoggerListView.setSelectionModel(null);
		createMovieLoggerListView.setCellFactory(x -> new CreateMovieLogCell());
		createMovieLoggerList = FXCollections.observableArrayList();
		createMovieLoggerListView.setItems(createMovieLoggerList);
		Logger logger = createMovie.getLogger();
		logger.addHandler(new CreateMovieHandler(createMovieLoggerList));
	}
	
	public void start() {
		createAlert();
		createMovieLoggerList.clear();
		alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
		alert.getDialogPane().setExpandableContent(createMovieLoggerListView);
		alert.getDialogPane().setExpanded(true);
		alert.show();
	}
	
	public void finishedTask() {
		alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
	}
	
	public void close() {
		createMovieLoggerList.clear();
		closeAlert();
	}
	
	private void createAlert() {
		closeAlert();
		if(this.alert == null)
			this.alert = AppUtils.alertOfInformation("Updating Movie", "Starting to Update");;
	}
	
	private void closeAlert() {
		if(this.alert != null)
			this.alert.close();
	}

}
