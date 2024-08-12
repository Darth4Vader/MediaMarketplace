package frontend.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import backend.dto.mediaProduct.MovieDto;
import frontend.AppImageUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

class SearchMovieCell extends  ListCell<MovieDto> {
	
	private HBox moviePane;
	private ImageView posterView;
	private Label name;
	private Label date;
	private TextArea textArea;
	private AddMoviePageController addMoviePageController;
	
	public SearchMovieCell(AddMoviePageController addMoviePageController) {
		setStyle("-fx-padding: 0px;");
		this.addMoviePageController = addMoviePageController;
		moviePane = new HBox();
		moviePane.setFillHeight(true);
		moviePane.maxHeightProperty().bind(addMoviePageController.movieListView.heightProperty().multiply(0.2));
		posterView = new ImageView();
		posterView.setPreserveRatio(true);
		posterView.fitWidthProperty().bind(addMoviePageController.movieListView.widthProperty().multiply(0.25));
		posterView.fitHeightProperty().bind(moviePane.maxHeightProperty());
		posterView.setCursor(Cursor.HAND);
		moviePane.setMinHeight(0);
		
		moviePane.getChildren().add(posterView);
		
		VBox infoBox = new VBox();
		name = new Label();
		name.setStyle("-fx-font-weight: bold");
		infoBox.getChildren().add(name);
		date = new Label();
		date.setTextFill(Color.LIGHTGRAY);
		infoBox.getChildren().add(date);
		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setEditable(false);
		infoBox.getChildren().add(textArea);
		VBox.setVgrow(textArea, Priority.ALWAYS);
		textArea.setMaxHeight(Double.MAX_VALUE);
		
		infoBox.prefHeightProperty().bind(moviePane.heightProperty());
		infoBox.prefHeightProperty().bind(moviePane.heightProperty());
		
		moviePane.getChildren().add(infoBox);
		HBox.setHgrow(infoBox, Priority.ALWAYS);
		
		moviePane.setBorder(Border.stroke(Color.BLUE));
		
		infoBox.setBorder(Border.stroke(Color.GREEN));
	}
	
	private void set(MovieDto movie) {
		posterView.setImage(AppImageUtils.loadImageFromClass(movie.getPosterPath()));
		posterView.setOnMouseClicked(e -> {
			addMoviePageController.addMovieToDatabase(movie);
		});
		name.setText(movie.getMediaName());
		LocalDate releaseDate = movie.getReleaseDate();
		if(releaseDate != null) {
			date.setText(releaseDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
		}
		textArea.setText(movie.getSynopsis());
	}
	
	private void reset() {
		posterView.setImage(null);
		posterView.setOnMouseClicked(null);
		name.setText(null);
		date.setText(null);
		textArea.setText(null);
	}
	
    @Override
    public void updateItem(MovieDto item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            reset();
        }
        else {
            setGraphic(moviePane);
            set(item);
        }
        setAlignment(Pos.CENTER_LEFT);
		setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
    }
}
