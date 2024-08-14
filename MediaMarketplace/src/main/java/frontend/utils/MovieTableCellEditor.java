package frontend.utils;

import java.util.ArrayList;
import java.util.List;

import backend.dto.mediaProduct.MovieReference;
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

public class MovieTableCellEditor extends ListCell<MovieRow> {
	
	private Region sizePane;
	
	private final GridPane box;
	
	private final List<MovieCell> cells;
	
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
		box.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		//Remove the gap
		setStyle("-fx-padding: 0px;");
	}
	
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
		setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
    }
    
	private class MovieCell {
		private VBox b;
		private Label name;
		private ImageView view;
		
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
			b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
		}
		
		public void set(MovieReference movie) {
			view.setImage(AppImageUtils.loadImageFromClass(movie.getPosterPath()));
    		name.setText(movie.getName());
    		b.setOnMouseClicked(evt -> {
    			App.getApplicationInstance().enterMoviePage(movie);
    		});
		}
		
		public void clean() {
        	view.setImage(null);
        	name.setText(null);
        	b.setOnMouseClicked(null);
		}
	}
}
