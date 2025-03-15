package frontend.utils;

import java.util.ArrayList;
import java.util.List;

import backend.dtos.references.MovieReference;
import frontend.App;
import frontend.AppImageUtils;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * A custom {@link ListCell} for displaying a row of movies in a grid format.
 * <p>
 * This class uses a {@link GridPane} to arrange a series of {@link MovieCell}
 * instances in a single row. Each cell displays a movie's poster and title.
 * </p>
 */
public class MovieTableCellEditor extends ListCell<MovieRow> {
	
    /**
     * A {@link Region} that determines the size of the grid pane.
     * <p>
     * The width of the {@code GridPane} is adjusted based on this {@code Region}.
     * The height of the {@code MovieCell} instances is bound to a percentage of the
     * height of this {@code Region}.
     * </p>
     */
	private Region sizePane;
	
    /**
     * A {@link GridPane} used to layout {@link MovieCell} instances in a row.
     * <p>
     * Each column in the {@code GridPane} represents a {@code MovieCell}. The number
     * of columns is determined by the number of {@code MovieCell} instances to display
     * in the row.
     * </p>
     */
	private final GridPane box;
	
    /**
     * A list of {@link MovieCell} instances used to display movies in a row.
     * <p>
     * Each {@code MovieCell} corresponds to a column in the {@code GridPane}. This list
     * is initialized with a size equal to the number of columns in the {@code GridPane}.
     * </p>
     */
	private final List<MovieCell> cells;
	
    /**
     * Constructs a {@code MovieTableCellEditor} with a specified size pane and
     * the number of cells to display in the row.
     * 
     * @param sizePane the {@code Region} that determines the size of the grid
     *                 pane
     * @param number   the number of {@code MovieCell} instances to create and
     *                 display in the row
     */
	public MovieTableCellEditor(Region sizePane, int number) {
		this.sizePane = sizePane;
		this.box = new GridPane();
		box.setAlignment(Pos.CENTER);
		this.cells = new ArrayList<>();
		for(int i = 0; i < number; i++) {
			ColumnConstraints cols = new ColumnConstraints();
			cols.setPercentWidth(100d / number);
			cols.setHalignment(HPos.CENTER);
			MovieCell cell = new MovieCell();
			box.getColumnConstraints().add(cols);
			this.cells.add(cell);
			this.box.add(cell.b, i, 0);
		}
    	box.prefWidthProperty().bind(sizePane.widthProperty().subtract(40));
		setStyle("-fx-padding: 0px;");
	}
	
    /**
     * Updates the visual representation of the cell based on the provided
     * {@code MovieRow}.
     * <p>
     * If the item is null or empty, clears the content of each cell. Otherwise,
     * populates each {@code MovieCell} with the corresponding {@link MovieReference}
     * from the {@code MovieRow}.
     * </p>
     * 
     * @param item  the {@code MovieRow} to display in this cell
     * @param empty {@code true} if the item is null or empty; {@code false} otherwise
     */
    @Override
    public void updateItem(MovieRow item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            for(MovieCell cell : cells)
            	cell.clean();
        }
        else {
        	List<MovieReference> movies = item.getMovies();
        	for(int i = 0; i < cells.size(); i++) {
        		MovieCell cell = cells.get(i);
        		if(i < movies.size()) {
        			cell.set(movies.get(i));
        		}
        		else {
        			cell.clean();
        		}
        	}
            setGraphic(box);
        }
        setAlignment(Pos.CENTER_LEFT);
    }
    
    /**
     * Class representing a cell for displaying a single movie.
     * <p>
     * Each {@code MovieCell} contains an {@link ImageView} for the movie's poster
     * and a {@link Label} for the movie's name. It handles the click events to
     * navigate to the movie's detailed page.
     * </p>
     */
	private class MovieCell {

	    /**
	     * A {@link VBox} that contains the movie's poster and title.
	     * <p>
	     * This layout node arranges an {@link ImageView} and a {@link Label} vertically.
	     * It is styled and used as a container for the movie details.
	     * </p>
	     */
	    private VBox b;

	    /**
	     * A {@link Label} displaying the movie's title.
	     * <p>
	     * This {@code Label} is used to show the name of the movie below the poster.
	     * It is updated when a new movie is set, and its text is cleared when the cell is cleaned.
	     * </p>
	     */
	    private Label name;

	    /**
	     * An {@link ImageView} used to display the movie's poster image.
	     * <p>
	     * This {@code ImageView} is updated with the movie's poster image when a new movie is set.
	     * It is sized to fit a percentage of the height of the {@link MovieTableCellEditor}'s size pane.
	     * </p>
	     */
	    private ImageView view;
		
        /**
         * Constructs a {@code MovieCell} instance.
         * <p>
         * Initializes the cell with an {@link ImageView} and {@link Label} inside a
         * {@link VBox}. The {@code ImageView} displays the movie poster, and the
         * {@code Label} displays the movie name.
         * </p>
         */
		public MovieCell() {
			b = new VBox();
			b.setAlignment(Pos.TOP_CENTER);
			view = new ImageView();
			view.setPreserveRatio(true);
			view.fitHeightProperty().bind(sizePane.heightProperty().multiply(0.4));
			b.getChildren().add(view);
			name = new Label();
			name.setWrapText(true);
			b.getChildren().add(name);
			b.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
		}
		
        /**
         * Sets the movie to be displayed in this cell.
         * <p>
         * Configures the {@code ImageView} with the movie's poster and the {@code Label}
         * with the movie's name. Also sets an event handler on the cell to navigate to
         * the movie's detailed page when clicked.
         * </p>
         * 
         * @param movie the {@code MovieReference} representing the movie to display
         */
		public void set(MovieReference movie) {
			view.setImage(AppImageUtils.loadImageFromClass(movie.getPosterPath()));
    		name.setText(movie.getName());
    		b.setOnMouseClicked(evt -> {
    			App.getApplicationInstance().enterMoviePage(movie);
    		});
		}
		
        /**
         * Clears the content of this cell.
         * <p>
         * Sets the image and text to null and removes the click event handler.
         * </p>
         */
		public void clean() {
        	view.setImage(null);
        	name.setText(null);
        	b.setOnMouseClicked(null);
		}
	}
}