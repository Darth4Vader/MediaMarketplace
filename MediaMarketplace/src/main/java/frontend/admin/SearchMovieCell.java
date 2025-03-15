package frontend.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import backend.dtos.CreateMovieDto;
import backend.dtos.MovieDto;
import frontend.AppImageUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Custom ListCell for displaying movie details in a {@link javafx.scene.control.ListView}.
 * <p>This class is responsible for rendering the visual representation of a {@link CreateMovieDto} in
 * a list view. It provides an interface to display movie posters, names, release dates, and synopses.</p>
 */
class SearchMovieCell extends ListCell<CreateMovieDto> {

    /**
     * Container for holding the movie poster and information.
     * <p>This HBox arranges the poster and movie details horizontally.</p>
     */
    private HBox moviePane;

    /**
     * ImageView for displaying the movie poster.
     * <p>This ImageView is used to show the poster of the movie. It is clickable to add the movie to the database.</p>
     */
    private ImageView posterView;

    /**
     * Label displaying the movie name.
     * <p>This Label is used to show the name of the movie in bold text.</p>
     */
    private Label name;

    /**
     * Label displaying the movie release date.
     * <p>This Label shows the release date of the movie in light gray text.</p>
     */
    private Label date;

    /**
     * TextArea displaying the movie synopsis.
     * <p>This TextArea is used to display a summary or synopsis of the movie. It wraps text and is not editable.</p>
     */
    private TextArea textArea;

    /**
     * Controller responsible for managing interactions on the add movie page.
     * <p>This controller is used to add the selected movie to the database when the poster is clicked.</p>
     */
    private AddMoviePageController addMoviePageController;

    /**
     * Constructs a {@link SearchMovieCell} with the specified {@link AddMoviePageController}.
     * 
     * @param addMoviePageController The controller responsible for managing the add movie page interactions.
     */
    public SearchMovieCell(AddMoviePageController addMoviePageController) {
        setStyle("-fx-padding: 0px;");
        this.addMoviePageController = addMoviePageController;
        initializeUIComponents();
    }

    /**
     * Initializes the UI components of the cell.
     * <p>This method sets up the layout and appearance of the movie pane, including the poster view,
     * movie information labels, and text area.</p>
     */
    private void initializeUIComponents() {
        moviePane = new HBox();
        moviePane.setFillHeight(true);
        moviePane.maxHeightProperty().bind(addMoviePageController.movieListView.heightProperty().multiply(0.2));
        
        posterView = new ImageView();
        posterView.setPreserveRatio(true);
        posterView.fitWidthProperty().bind(addMoviePageController.movieListView.widthProperty().multiply(0.25));
        posterView.fitHeightProperty().bind(moviePane.maxHeightProperty());
        posterView.setCursor(Cursor.HAND);
        
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
        
        moviePane.getChildren().add(infoBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        moviePane.setBorder(Border.stroke(Color.BLACK));
    }

    /**
     * Updates the cell with the given movie data.
     * 
     * @param createMovie The {@link CreateMovieDto} to be displayed in the cell.
     */
    private void set(CreateMovieDto createMovie) {
        MovieDto movie = createMovie.getMovieDto(); 
        posterView.setImage(AppImageUtils.loadImageFromClass(movie.getPosterPath()));
        posterView.setOnMouseClicked(e -> {
            addMoviePageController.addMovieToDatabase(createMovie);
        });
        name.setText(movie.getName());
        LocalDate releaseDate = movie.getReleaseDate();
        if (releaseDate != null) {
            date.setText(releaseDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        }
        textArea.setText(movie.getSynopsis());
    }

    /**
     * Resets the UI components to their default state.
     * <p>This method clears the poster image, labels, and text area.</p>
     */
    private void reset() {
        posterView.setImage(null);
        posterView.setOnMouseClicked(null);
        name.setText(null);
        date.setText(null);
        textArea.setText(null);
    }

    /**
     * Updates the visual representation of the cell based on the item and its emptiness state.
     * <p>This method overrides the {@link ListCell#updateItem(Object, boolean)} method to
     * provide custom cell content and styling.</p>
     * 
     * @param item The item to be displayed in the cell.
     * @param empty {@code true} if the cell is empty; {@code false} otherwise.
     */
    @Override
    public void updateItem(CreateMovieDto item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            reset();
        } else {
            setGraphic(moviePane);
            set(item);
        }
        setAlignment(Pos.CENTER_LEFT);
    }
}